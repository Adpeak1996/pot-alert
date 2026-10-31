package com.potalert;

import com.google.inject.Provides;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.SpotanimID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.audio.AudioPlayer;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.Filepath;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Pot Alert",
	internalName = "pot-alert",
	legacyDataDirectory = "pot-alert",
	description = "Color-coded edge glow and sound before your potions or prayer run out",
	tags = {"potion", "reminder", "divine", "antivenom", "antipoison", "antifire", "overload", "smelling salts", "stamina", "heart", "prayer", "boost", "afk", "slayer"}
)
public class PotAlertPlugin extends Plugin
{
	private static final int VENOM_VALUE_CUTOFF = -38;
	private static final int POISON_TICK_LENGTH = 30;
	private static final int ANTIFIRE_TICK_LENGTH = 30;
	private static final int SUPER_ANTIFIRE_TICK_LENGTH = 20;
	private static final int OVERLOAD_TICK_LENGTH = 25;
	private static final int[] OVERLOAD_VARBITS = {
		VarbitID.NZONE_OVERLOAD_POTION_EFFECTS, VarbitID.RAIDS_OVERLOAD_TIMER, VarbitID.DEADMAN_OVERLOAD_POTION_EFFECTS
	};

	private static final long PREVIEW_MILLIS = 5000;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private PotAlertConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ConfigManager configManager;

	@Inject
	private EdgeGlowOverlay overlay;

	@Inject
	private ItemManager itemManager;

	@Inject
	private AudioPlayer audioPlayer;

	private NavigationButton navButton;
	private PotAlertPanel panel;
	private Filepath soundDir;

	private final Map<AlertType, AlertState> states = new EnumMap<>(AlertType.class);
	private final Map<Integer, Color> potionColors = new ConcurrentHashMap<>();
	private final Set<Integer> loadingColors = ConcurrentHashMap.newKeySet();

	private int lastCombatTick = -1000;
	private int nextPoisonTick = -1;
	private int nextAntifireTick = -1;
	private int nextSuperAntifireTick = -1;
	private int nextOverloadTick = -1;
	private boolean venomPhase;
	private boolean waitingForFirstFight = true;
	private int enhanceEndTick = -1;
	private int adrenalineEndTick = -1;

	private volatile List<Color> activeColors = Collections.emptyList();

	private volatile Color previewColor;
	private volatile long previewUntil;

	private static class AlertState
	{
		boolean armed;

		int firedTick = -1;

		boolean firedForExpiry;

		boolean running;

		boolean timerSeen;

		boolean expiryPending;

		boolean divineRunning;

		int noArmUntil;

		AlertState(AlertType type)
		{
			armed = type.getKind() != AlertType.Kind.BOOST;
		}
	}

	PotAlertConfig getConfig()
	{
		return config;
	}

	Filepath getSoundDir()
	{
		return soundDir;
	}

	@Provides
	PotAlertConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PotAlertConfig.class);
	}

	@Override
	protected void startUp()
	{
		resetState();
		try
		{
			soundDir = getPluginDirectory();
			soundDir.createDirectories();
		}
		catch (Exception e)
		{
			log.warn("Pot Alert couldn't open its sound folder", e);
			soundDir = null;
		}
		clearMissingCustomSounds();
		overlayManager.add(overlay);

		BufferedImage icon = ImageUtil.loadImageResource(PotAlertPlugin.class, "panel_icon.png");
		navButton = NavigationButton.builder()
			.tooltip("Pot Alert")
			.icon(icon)
			.priority(8)
			.panel(panel = new PotAlertPanel(this, configManager))
			.build();
		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		clientToolbar.removeNavigation(navButton);
		states.clear();
		activeColors = Collections.emptyList();
		previewUntil = 0;
	}

	private void clearMissingCustomSounds()
	{
		for (AlertType type : AlertType.values())
		{
			String key = type.getKey() + "CustomFile";
			String file = configManager.getConfiguration(PotAlertConfig.GROUP, key);
			Filepath path = customSoundFile(file);
			if (file != null && !file.isEmpty() && (path == null || !path.exists()))
			{
				configManager.setConfiguration(PotAlertConfig.GROUP, key, "");
			}
		}
	}

	private void resetState()
	{
		for (AlertType type : AlertType.values())
		{
			states.put(type, new AlertState(type));
		}
		activeColors = Collections.emptyList();
		nextPoisonTick = -1;
		nextAntifireTick = -1;
		nextSuperAntifireTick = -1;
		nextOverloadTick = -1;
		venomPhase = false;
		enhanceEndTick = -1;
		adrenalineEndTick = -1;
		waitingForFirstFight = true;
	}

	List<Color> getDisplayColors()
	{
		Color preview = previewColor;
		if (preview != null && System.currentTimeMillis() < previewUntil)
		{
			return Collections.singletonList(preview);
		}
		return activeColors;
	}

	void preview(AlertType type)
	{
		previewUntil = 0;
		clientThread.invokeLater(() ->
		{
			if (client.getGameState() != GameState.LOGGED_IN)
			{
				return;
			}
			Integer potId = findPots().get(type);
			if (potId != null && (isSharedPot(potId) || type == AlertType.PRAYER))
			{
				potId = null;
			}
			int id = potId != null ? potId : type.getSampleItemId();

			Color ready = loadedColor(type, id);
			if (ready != null)
			{
				startPreview(type, ready);
				return;
			}

			AsyncBufferedImage img = itemManager.getImage(id);
			img.onLoaded(() ->
			{
				Color c = PotionColorSampler.sample(img);
				if (c != null)
				{
					potionColors.put(id, c);
				}
				Color loaded = loadedColor(type, id);
				startPreview(type, loaded != null ? loaded : configColor(type));
			});
		});
	}

	private void startPreview(AlertType type, Color color)
	{
		previewColor = color;
		previewUntil = System.currentTimeMillis() + PREVIEW_MILLIS;
		playSound(type);
	}

	private boolean isOpen(int componentId)
	{
		Widget widget = client.getWidget(componentId);
		return widget != null && !widget.isHidden();
	}

	private boolean isSharedPot(int potId)
	{
		String name = client.getItemDefinition(potId).getName().toLowerCase();
		int count = 0;
		for (AlertType t : AlertType.values())
		{
			if (t.matches(name))
			{
				count++;
			}
		}
		return count > 1;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (PotAlertConfig.GROUP.equals(event.getGroup()) && event.getKey().endsWith("CustomFile") && panel != null)
		{
			SwingUtilities.invokeLater(panel::loadPickers);
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		GameState gs = event.getGameState();
		if (gs == GameState.LOGIN_SCREEN || gs == GameState.HOPPING)
		{
			resetState();
		}
		else if (gs == GameState.LOGGED_IN)
		{
			for (AlertType type : AlertType.values())
			{
				requestColor(type.getSampleItemId());
			}
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		if (!event.getHitsplat().isMine())
		{
			return;
		}
		boolean onMe = event.getActor() == client.getLocalPlayer();
		if (onMe && !config.hitsCountAsCombat())
		{
			return;
		}
		lastCombatTick = client.getTickCount();
		waitingForFirstFight = false;
	}

	@Subscribe
	public void onGraphicChanged(GraphicChanged event)
	{
		Player me = client.getLocalPlayer();
		Actor actor = event.getActor();
		if (me != null && actor != null && actor == me.getInteracting() && actor.hasSpotAnim(SpotanimID.FAILEDSPELL_IMPACT))
		{
			lastCombatTick = client.getTickCount();
			waitingForFirstFight = false;
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.SPAM)
		{
			return;
		}

		String message = Text.removeTags(event.getMessage());
		int tick = client.getTickCount();
		if (message.startsWith("You drink some of"))
		{
			waitingForFirstFight = false;
		}
		if (message.startsWith("You drink some of your") && message.contains("prayer enhance"))
		{
			enhanceEndTick = tick + secondsToTicks(290);
		}
		else if (message.equals("Your prayer enhance effect has worn off."))
		{
			enhanceEndTick = -1;
		}
		else if (message.contains("reducing the energy cost of your special attacks"))
		{
			adrenalineEndTick = tick + secondsToTicks(150);
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		int tick = client.getTickCount();
		int value = event.getValue();
		int varbit = event.getVarbitId();

		if (event.getVarpId() == VarPlayerID.POISON)
		{
			nextPoisonTick = nextTimerTick(nextPoisonTick, value, tick, POISON_TICK_LENGTH);
		}
		else if (varbit == VarbitID.ANTIFIRE_POTION)
		{
			nextAntifireTick = nextTimerTick(nextAntifireTick, value, tick, ANTIFIRE_TICK_LENGTH);
		}
		else if (varbit == VarbitID.SUPER_ANTIFIRE_POTION)
		{
			nextSuperAntifireTick = nextTimerTick(nextSuperAntifireTick, value, tick, SUPER_ANTIFIRE_TICK_LENGTH);
		}
		else if (varbit == VarbitID.RAIDS_CLIENT_INDUNGEON && value == 0)
		{
			enhanceEndTick = -1;
		}
		else if (varbit == VarbitID.TOA_MIDRAIDLOOT_ENERGY_ACTIVE && value == 0)
		{
			adrenalineEndTick = -1;
		}
		else if (varbit == VarbitID.NZONE_OVERLOAD_POTION_EFFECTS
			|| varbit == VarbitID.RAIDS_OVERLOAD_TIMER
			|| varbit == VarbitID.DEADMAN_OVERLOAD_POTION_EFFECTS)
		{
			nextOverloadTick = nextTimerTick(nextOverloadTick, Math.max(0, value), tick, OVERLOAD_TICK_LENGTH);
		}
	}

	private static int nextTimerTick(int current, int value, int tick, int length)
	{
		if (value == 0)
		{
			return -1;
		}
		return current - tick <= 0 ? tick + length : current;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		try
		{
			tickAlerts();
		}
		catch (Exception e)
		{
			log.warn("Pot Alert tick failed", e);
		}
	}

	private void tickAlerts()
	{
		Player me = client.getLocalPlayer();
		if (me == null || states.isEmpty())
		{
			return;
		}

		int tick = client.getTickCount();
		boolean reallyInCombat = tick - lastCombatTick <= secondsToTicks(config.combatTimeout());
		boolean combatOk = !config.onlyInCombat() || reallyInCombat;
		int warnTicks = secondsToTicks(config.warnSeconds());
		int glowTicks = secondsToTicks(config.alertDuration());
		int remindTicks = secondsToTicks(config.remindEvery());
		boolean banking = isOpen(InterfaceID.Bankmain.UNIVERSE) || isOpen(InterfaceID.BankDepositbox.UNIVERSE)
			|| isOpen(InterfaceID.GeOffers.UNIVERSE);
		Map<AlertType, Integer> pots = findPots();

		List<Color> colors = new ArrayList<>();
		Set<Integer> soundedPots = new HashSet<>();
		Set<Integer> glowingPots = new HashSet<>();
		for (AlertType type : AlertType.values())
		{
			AlertState state = states.get(type);
			if (!isEnabled(type))
			{
				state.firedTick = -1;
				continue;
			}

			boolean needs = needsSip(type, state, warnTicks, tick);
			boolean expiry = state.expiryPending;
			if (!needs && !expiry && tick >= state.noArmUntil)
			{
				state.armed = true;
			}

			Integer potId = pots.get(type);
			if (potId != null)
			{
				requestColor(potId);
			}
			else if (warnWithoutPot(type))
			{
				potId = type.getSampleItemId();
			}
			if (potId == null)
			{
				state.expiryPending = false;
				continue;
			}

			boolean heldForLogin = waitingForFirstFight && !state.timerSeen
				&& (type.getKind() != AlertType.Kind.PRAYER || !config.prayerOnLogin());
			boolean trigger = (state.armed && needs) || expiry;
			boolean remind = remindTicks > 0 && needs && state.firedTick >= 0 && tick - state.firedTick >= remindTicks;

			if ((trigger || remind) && combatOk && !heldForLogin)
			{
				state.armed = false;
				state.expiryPending = false;
				if (banking)
				{
					state.firedTick = tick - glowTicks;
				}
				else
				{
					state.firedTick = tick;
					state.firedForExpiry = expiry && !needs;
					if (soundedPots.add(potId))
					{
						playSound(type);
					}
				}
			}

			boolean glowing = state.firedTick >= 0 && tick - state.firedTick < glowTicks
				&& (needs || state.firedForExpiry);
			if (glowing && glowingPots.add(potId))
			{
				int colorId = type == AlertType.PRAYER ? type.getSampleItemId() : potId;
				colors.add(resolveColor(type, colorId));
			}
		}
		activeColors = colors;
	}

	private boolean needsSip(AlertType type, AlertState state, int warnTicks, int tick)
	{
		switch (type.getKind())
		{
			case PRAYER:
				return client.getBoostedSkillLevel(Skill.PRAYER) <= config.prayerThreshold();

			case HEART:
				return client.getVarbitValue(VarbitID.IMBUED_HEART_TIMER) == 0 && statIsLow(type);

			case TIMED:
			{
				int poison = client.getVarpValue(VarPlayerID.POISON);
				if (type == AlertType.POISON && poison > 0)
				{
					venomPhase = false;
					state.running = false;
					state.expiryPending = false;
					return config.poisonWhenPoisoned();
				}

				int remaining = timedRemaining(type, poison);
				boolean running = remaining > 0;
				if (state.running && !running && (type == AlertType.POISON || type == AlertType.ANTIFIRE))
				{
					state.expiryPending = true;
				}
				if (running)
				{
					state.expiryPending = false;
					state.timerSeen = true;
				}
				state.running = running;
				return running && remaining <= warnTicks;
			}

			default:
			{
				int divineLeft = 0;
				for (int varbit : type.getDivineVarbits())
				{
					divineLeft = Math.max(divineLeft, client.getVarbitValue(varbit));
				}
				if (state.divineRunning && divineLeft == 0)
				{
					state.armed = false;
					state.noArmUntil = tick + 5;
				}
				state.divineRunning = divineLeft > 0;
				boolean divineEnding = divineLeft > 0 && divineLeft <= warnTicks;
				return statIsLow(type) || divineEnding;
			}
		}
	}

	private boolean statIsLow(AlertType type)
	{
		int level = configLevel(type);
		for (Skill skill : type.getSkills())
		{
			int boosted = client.getBoostedSkillLevel(skill);
			if (level > 0 ? boosted <= level : boosted - client.getRealSkillLevel(skill) <= autoBoost(type))
			{
				return true;
			}
		}
		return false;
	}

	private Skill lowestSkill(AlertType type)
	{
		Skill lowest = type.getSkills()[0];
		for (Skill skill : type.getSkills())
		{
			if (client.getBoostedSkillLevel(skill) < client.getBoostedSkillLevel(lowest))
			{
				lowest = skill;
			}
		}
		return lowest;
	}

	private int timedRemaining(AlertType type, int poisonValue)
	{
		switch (type)
		{
			case POISON:
				return poisonValue > 0 ? -1 : poisonRemaining(poisonValue);
			case ANTIFIRE:
				return antifireRemaining();
			case OVERLOAD:
				return overloadRemaining();
			case SALTS:
			{
				int salts = client.getVarbitValue(VarbitID.TOA_MIDRAIDLOOT_STATS_TIMER);
				return salts > 0 ? salts * 25 : -1;
			}
			case PRAYER_REGEN:
			{
				int regen = client.getVarbitValue(VarbitID.PRAYER_REGENERATION_POTION_TIMER);
				return regen > 0 ? regen * 12 : -1;
			}
			case REMEDY:
			{
				int remedy = client.getVarbitValue(VarbitID.STATRENEWAL_POTION_TIMER);
				return remedy > 0 ? remedy * 25 : -1;
			}
			case ENHANCE:
				return endTickRemaining(enhanceEndTick);
			case ADRENALINE:
				return endTickRemaining(adrenalineEndTick);
			case STAMINA:
				return client.getVarbitValue(VarbitID.STAMINA_ACTIVE) == 1
					? Math.max(1, client.getVarbitValue(VarbitID.STAMINA_DURATION) * 10) : -1;
			default:
				return -1;
		}
	}

	private int poisonRemaining(int value)
	{
		if (value == 0)
		{
			venomPhase = false;
			return -1;
		}

		int base = Math.max(0, nextPoisonTick - client.getTickCount());
		if (value < VENOM_VALUE_CUTOFF)
		{
			venomPhase = true;
			return base + Math.abs((value + 1 - VENOM_VALUE_CUTOFF) * POISON_TICK_LENGTH);
		}
		if (venomPhase)
		{
			return -1;
		}
		return base + Math.abs((value + 1) * POISON_TICK_LENGTH);
	}

	private int endTickRemaining(int endTick)
	{
		if (endTick < 0)
		{
			return -1;
		}
		int remaining = endTick - client.getTickCount();
		return remaining > 0 ? remaining : -1;
	}

	private int antifireRemaining()
	{
		int tick = client.getTickCount();
		int remaining = -1;

		int antifire = client.getVarbitValue(VarbitID.ANTIFIRE_POTION);
		if (antifire > 0)
		{
			remaining = Math.max(remaining, Math.max(0, nextAntifireTick - tick) + (antifire - 1) * ANTIFIRE_TICK_LENGTH);
		}

		int superAntifire = client.getVarbitValue(VarbitID.SUPER_ANTIFIRE_POTION);
		if (superAntifire > 0)
		{
			remaining = Math.max(remaining, Math.max(0, nextSuperAntifireTick - tick) + (superAntifire - 1) * SUPER_ANTIFIRE_TICK_LENGTH);
		}

		return remaining == 0 ? 1 : remaining;
	}

	private int overloadRemaining()
	{
		int value = 0;
		for (int varbit : OVERLOAD_VARBITS)
		{
			value = Math.max(value, client.getVarbitValue(varbit));
		}
		if (value <= 0)
		{
			return -1;
		}
		int remaining = Math.max(0, nextOverloadTick - client.getTickCount()) + (value - 1) * OVERLOAD_TICK_LENGTH;
		return remaining == 0 ? 1 : remaining;
	}

	private Map<AlertType, Integer> findPots()
	{
		Map<AlertType, Integer> found = new EnumMap<>(AlertType.class);
		ItemContainer inv = client.getItemContainer(InventoryID.INV);
		if (inv == null)
		{
			return found;
		}

		for (Item item : inv.getItems())
		{
			if (item.getId() <= 0)
			{
				continue;
			}
			String name = client.getItemDefinition(item.getId()).getName().toLowerCase();
			for (AlertType type : AlertType.values())
			{
				if (!found.containsKey(type) && type.matches(name))
				{
					found.put(type, item.getId());
				}
			}
		}
		return found;
	}

	private boolean warnWithoutPot(AlertType type)
	{
		switch (type)
		{
			case POISON: return config.poisonNoPot();
			case ANTIFIRE: return config.antifireNoPot();
			default: return false;
		}
	}

	private static int autoBoost(AlertType type)
	{
		switch (type)
		{
			case RANGED:
			case BASTION: return 4;
			case MAGIC:
			case BATTLEMAGE: return 2;
			case HEART: return 3;
			default: return 5;
		}
	}

	private boolean isEnabled(AlertType type)
	{
		switch (type)
		{
			case ATTACK: return config.attackEnabled();
			case STRENGTH: return config.strengthEnabled();
			case DEFENCE: return config.defenceEnabled();
			case COMBAT: return config.combatEnabled();
			case RANGED: return config.rangedEnabled();
			case BASTION: return config.bastionEnabled();
			case MAGIC: return config.magicEnabled();
			case BATTLEMAGE: return config.battlemageEnabled();
			case HEART: return config.heartEnabled();
			case OVERLOAD: return config.overloadEnabled();
			case SALTS: return config.saltsEnabled();
			case POISON: return config.poisonEnabled();
			case ANTIFIRE: return config.antifireEnabled();
			case PRAYER_REGEN: return config.prayerregenEnabled();
			case ENHANCE: return config.enhanceEnabled();
			case ADRENALINE: return config.adrenalineEnabled();
			case REMEDY: return config.remedyEnabled();
			case STAMINA: return config.staminaEnabled();
			case PRAYER: return config.prayerEnabled();
			default: return false;
		}
	}

	private int configLevel(AlertType type)
	{
		switch (type)
		{
			case ATTACK: return config.attackLevel();
			case STRENGTH: return config.strengthLevel();
			case DEFENCE: return config.defenceLevel();
			case COMBAT: return config.combatLevel();
			case RANGED: return config.rangedLevel();
			case BASTION: return config.bastionLevel();
			case MAGIC: return config.magicLevel();
			case BATTLEMAGE: return config.battlemageLevel();
			case HEART: return config.heartLevel();
			default: return 0;
		}
	}

	private boolean usePotionColor(AlertType type)
	{
		switch (type)
		{
			case ATTACK: return config.attackUsePotionColor();
			case STRENGTH: return config.strengthUsePotionColor();
			case DEFENCE: return config.defenceUsePotionColor();
			case COMBAT: return config.combatUsePotionColor();
			case RANGED: return config.rangedUsePotionColor();
			case BASTION: return config.bastionUsePotionColor();
			case MAGIC: return config.magicUsePotionColor();
			case BATTLEMAGE: return config.battlemageUsePotionColor();
			case HEART: return config.heartUsePotionColor();
			case OVERLOAD: return config.overloadUsePotionColor();
			case SALTS: return config.saltsUsePotionColor();
			case POISON: return config.poisonUsePotionColor();
			case ANTIFIRE: return config.antifireUsePotionColor();
			case PRAYER_REGEN: return config.prayerregenUsePotionColor();
			case ENHANCE: return config.enhanceUsePotionColor();
			case ADRENALINE: return config.adrenalineUsePotionColor();
			case REMEDY: return config.remedyUsePotionColor();
			case STAMINA: return config.staminaUsePotionColor();
			case PRAYER: return config.prayerUsePotionColor();
			default: return true;
		}
	}

	private Color configColor(AlertType type)
	{
		switch (type)
		{
			case ATTACK: return config.attackColor();
			case STRENGTH: return config.strengthColor();
			case DEFENCE: return config.defenceColor();
			case COMBAT: return config.combatColor();
			case RANGED: return config.rangedColor();
			case BASTION: return config.bastionColor();
			case MAGIC: return config.magicColor();
			case BATTLEMAGE: return config.battlemageColor();
			case HEART: return config.heartColor();
			case OVERLOAD: return config.overloadColor();
			case SALTS: return config.saltsColor();
			case POISON: return config.poisonColor();
			case ANTIFIRE: return config.antifireColor();
			case PRAYER_REGEN: return config.prayerregenColor();
			case ENHANCE: return config.enhanceColor();
			case ADRENALINE: return config.adrenalineColor();
			case REMEDY: return config.remedyColor();
			case STAMINA: return config.staminaColor();
			case PRAYER: return config.prayerColor();
			default: return type.getDefaultColor();
		}
	}

	AlertSound configSound(AlertType type)
	{
		switch (type)
		{
			case ATTACK: return config.attackSound();
			case STRENGTH: return config.strengthSound();
			case DEFENCE: return config.defenceSound();
			case COMBAT: return config.combatSound();
			case RANGED: return config.rangedSound();
			case BASTION: return config.bastionSound();
			case MAGIC: return config.magicSound();
			case BATTLEMAGE: return config.battlemageSound();
			case HEART: return config.heartSound();
			case OVERLOAD: return config.overloadSound();
			case SALTS: return config.saltsSound();
			case POISON: return config.poisonSound();
			case ANTIFIRE: return config.antifireSound();
			case PRAYER_REGEN: return config.prayerregenSound();
			case ENHANCE: return config.enhanceSound();
			case ADRENALINE: return config.adrenalineSound();
			case REMEDY: return config.remedySound();
			case STAMINA: return config.staminaSound();
			case PRAYER: return config.prayerSound();
			default: return AlertSound.OFF;
		}
	}

	String customFile(AlertType type)
	{
		switch (type)
		{
			case ATTACK: return config.attackCustomFile();
			case STRENGTH: return config.strengthCustomFile();
			case DEFENCE: return config.defenceCustomFile();
			case COMBAT: return config.combatCustomFile();
			case RANGED: return config.rangedCustomFile();
			case BASTION: return config.bastionCustomFile();
			case MAGIC: return config.magicCustomFile();
			case BATTLEMAGE: return config.battlemageCustomFile();
			case HEART: return config.heartCustomFile();
			case OVERLOAD: return config.overloadCustomFile();
			case SALTS: return config.saltsCustomFile();
			case POISON: return config.poisonCustomFile();
			case ANTIFIRE: return config.antifireCustomFile();
			case PRAYER_REGEN: return config.prayerregenCustomFile();
			case ENHANCE: return config.enhanceCustomFile();
			case ADRENALINE: return config.adrenalineCustomFile();
			case REMEDY: return config.remedyCustomFile();
			case STAMINA: return config.staminaCustomFile();
			case PRAYER: return config.prayerCustomFile();
			default: return "";
		}
	}

	private Color resolveColor(AlertType type, int potId)
	{
		Color c = loadedColor(type, potId);
		if (c != null)
		{
			return c;
		}
		requestColor(potId);
		return configColor(type);
	}

	private Color loadedColor(AlertType type, int potId)
	{
		if (!usePotionColor(type))
		{
			return configColor(type);
		}
		Color sampled = potionColors.get(potId);
		if (sampled == null)
		{
			return null;
		}
		return config.brightenDark() ? brighten(sampled) : sampled;
	}

	private void requestColor(int potId)
	{
		if (potionColors.containsKey(potId) || !loadingColors.add(potId))
		{
			return;
		}
		AsyncBufferedImage img = itemManager.getImage(potId);
		img.onLoaded(() ->
		{
			Color c = PotionColorSampler.sample(img);
			if (c != null)
			{
				potionColors.put(potId, c);
			}
			loadingColors.remove(potId);
		});
	}

	static Color brighten(Color c)
	{
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		float minBrightness = 0.6f;
		if (hsb[2] >= minBrightness)
		{
			return c;
		}
		return Color.getHSBColor(hsb[0], hsb[1], minBrightness);
	}

	private void playSound(AlertType type)
	{
		int volume = config.volume();
		if (volume <= 0)
		{
			return;
		}

		String custom = customFile(type);
		Filepath file = customSoundFile(custom);
		if (file != null && file.exists())
		{
			try
			{
				float gain = (float) (20 * Math.log10(volume / 100.0));
				audioPlayer.play(file, gain);
				return;
			}
			catch (Exception e)
			{
				log.warn("Pot Alert couldn't play {}", file, e);
			}
		}

		AlertSound sound = configSound(type);
		if (sound != AlertSound.OFF)
		{
			client.playSoundEffect(sound.getSoundId(), Math.max(1, volume * 127 / 100));
		}
	}

	Filepath customSoundFile(String name)
	{
		if (soundDir == null || name == null || name.trim().isEmpty())
		{
			return null;
		}
		String clean = name.trim();
		clean = clean.substring(Math.max(clean.lastIndexOf('/'), clean.lastIndexOf('\\')) + 1);
		if (!clean.toLowerCase().endsWith(".wav"))
		{
			clean += ".wav";
		}
		try
		{
			return soundDir.joinSegment(clean);
		}
		catch (IllegalArgumentException e)
		{
			return null;
		}
	}

	private static int secondsToTicks(int seconds)
	{
		return (int) Math.ceil(seconds / 0.6);
	}
}
