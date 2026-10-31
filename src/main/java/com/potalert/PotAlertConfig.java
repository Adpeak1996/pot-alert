package com.potalert;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(PotAlertConfig.GROUP)
public interface PotAlertConfig extends Config
{
	String GROUP = "potalert";

	@ConfigSection(name = "General", description = "When alerts fire", position = 0)
	String generalSection = "general";

	@ConfigItem(keyName = "onlyInCombat", name = "Only in combat",
		description = "Only warn while you're fighting", section = generalSection, position = 0)
	default boolean onlyInCombat()
	{
		return false;
	}

	@ConfigItem(keyName = "hitsCountAsCombat", name = "Getting hit counts as combat",
		description = "When off, only your own attacks count, so running past monsters won't set off warnings", section = generalSection, position = 1)
	default boolean hitsCountAsCombat()
	{
		return false;
	}

	@Range(min = 1, max = 60)
	@Units(Units.SECONDS)
	@ConfigItem(keyName = "combatTimeout", name = "Combat timeout",
		description = "How long after your last attack you still count as in combat", section = generalSection, position = 2)
	default int combatTimeout()
	{
		return 10;
	}

	@Range(min = 0, max = 120)
	@Units(Units.SECONDS)
	@ConfigItem(keyName = "warnSeconds", name = "Warn before expiry",
		description = "How many seconds before a timed pot runs out to warn you", section = generalSection, position = 3)
	default int warnSeconds()
	{
		return 20;
	}

	@Range(min = 1, max = 60)
	@Units(Units.SECONDS)
	@ConfigItem(keyName = "alertDuration", name = "Warning glow",
		description = "How long the screen glows for each warning. It stops early once you sip", section = generalSection, position = 4)
	default int alertDuration()
	{
		return 8;
	}

	@Range(min = 0, max = 300)
	@Units(Units.SECONDS)
	@ConfigItem(keyName = "remindEvery", name = "Remind again every",
		description = "How often to repeat the warning until you drink. Leave at 0 for just one warning", section = generalSection, position = 5)
	default int remindEvery()
	{
		return 0;
	}

	@Range(min = 0, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "volume", name = "Volume",
		description = "Alert volume. Plays even if your in-game sound effects are muted", section = generalSection, position = 6)
	default int volume()
	{
		return 70;
	}

	@ConfigSection(name = "Edge glow", description = "How the screen edge glow looks", position = 1)
	String glowSection = "glow";

	@ConfigItem(keyName = "brightenDark", name = "Brighten dark potions",
		description = "Lightens dark potion colors (anti-venom, overload) so they show up on dark backgrounds. Keeps the same hue", section = glowSection, position = 0)
	default boolean brightenDark()
	{
		return true;
	}

	@Range(min = 5, max = 200)
	@Units(Units.PIXELS)
	@ConfigItem(keyName = "glowThickness", name = "Thickness",
		description = "How far the glow reaches in from the edge", section = glowSection, position = 1)
	default int glowThickness()
	{
		return 100;
	}

	@Range(min = 10, max = 100)
	@Units(Units.PERCENT)
	@ConfigItem(keyName = "glowOpacity", name = "Max opacity",
		description = "How strong the glow gets at the peak of each pulse", section = glowSection, position = 2)
	default int glowOpacity()
	{
		return 75;
	}

	@Range(min = 200, max = 3000)
	@Units(Units.MILLISECONDS)
	@ConfigItem(keyName = "pulseSpeed", name = "Pulse speed",
		description = "Time for one pulse. With more than one alert up, colors take turns each pulse", section = glowSection, position = 3)
	default int pulseSpeed()
	{
		return 1500;
	}

	@ConfigSection(name = "Attack", description = "Super attack, divine super attack, attack pots", position = 2, closedByDefault = true)
	String attackSection = "attack";

	@ConfigItem(keyName = "attackEnabled", name = "Enabled", description = "", section = attackSection, position = 0)
	default boolean attackEnabled()
	{
		return true;
	}

	@Range(min = 0, max = 150)
	@ConfigItem(keyName = "attackLevel", name = "Resip at level",
		description = "Alert when your Attack drops to this level. Set to 0 for automatic (a few levels above your base)", section = attackSection, position = 1)
	default int attackLevel()
	{
		return 112;
	}

	@ConfigItem(keyName = "attackUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = attackSection, position = 2)
	default boolean attackUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "attackColor", name = "Color", description = "Used when 'Use potion color' is off", section = attackSection, position = 3)
	default Color attackColor()
	{
		return AlertType.ATTACK.getDefaultColor();
	}

	@ConfigItem(keyName = "attackSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = attackSection, position = 4)
	default AlertSound attackSound()
	{
		return AlertType.ATTACK.getDefaultSound();
	}

	@ConfigItem(keyName = "attackCustomFile", name = "Custom file", description = "", section = attackSection, position = 5, hidden = true)
	default String attackCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Strength", description = "Super strength, divine super strength, strength pots", position = 3, closedByDefault = true)
	String strengthSection = "strength";

	@ConfigItem(keyName = "strengthEnabled", name = "Enabled", description = "", section = strengthSection, position = 0)
	default boolean strengthEnabled()
	{
		return true;
	}

	@Range(min = 0, max = 150)
	@ConfigItem(keyName = "strengthLevel", name = "Resip at level",
		description = "Alert when your Strength drops to this level. Set to 0 for automatic (a few levels above your base)", section = strengthSection, position = 1)
	default int strengthLevel()
	{
		return 112;
	}

	@ConfigItem(keyName = "strengthUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = strengthSection, position = 2)
	default boolean strengthUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "strengthColor", name = "Color", description = "Used when 'Use potion color' is off", section = strengthSection, position = 3)
	default Color strengthColor()
	{
		return AlertType.STRENGTH.getDefaultColor();
	}

	@ConfigItem(keyName = "strengthSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = strengthSection, position = 4)
	default AlertSound strengthSound()
	{
		return AlertType.STRENGTH.getDefaultSound();
	}

	@ConfigItem(keyName = "strengthCustomFile", name = "Custom file", description = "", section = strengthSection, position = 5, hidden = true)
	default String strengthCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Defence", description = "Super defence, divine super defence, defence pots", position = 4, closedByDefault = true)
	String defenceSection = "defence";

	@ConfigItem(keyName = "defenceEnabled", name = "Enabled", description = "", section = defenceSection, position = 0)
	default boolean defenceEnabled()
	{
		return true;
	}

	@Range(min = 0, max = 150)
	@ConfigItem(keyName = "defenceLevel", name = "Resip at level",
		description = "Alert when your Defence drops to this level. Set to 0 for automatic (a few levels above your base)", section = defenceSection, position = 1)
	default int defenceLevel()
	{
		return 112;
	}

	@ConfigItem(keyName = "defenceUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = defenceSection, position = 2)
	default boolean defenceUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "defenceColor", name = "Color", description = "Used when 'Use potion color' is off", section = defenceSection, position = 3)
	default Color defenceColor()
	{
		return AlertType.DEFENCE.getDefaultColor();
	}

	@ConfigItem(keyName = "defenceSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = defenceSection, position = 4)
	default AlertSound defenceSound()
	{
		return AlertType.DEFENCE.getDefaultSound();
	}

	@ConfigItem(keyName = "defenceCustomFile", name = "Custom file", description = "", section = defenceSection, position = 5, hidden = true)
	default String defenceCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Super combat", description = "Super combat, divine super combat, combat pots, CoX elder pots", position = 5, closedByDefault = true)
	String combatSection = "combat";

	@ConfigItem(keyName = "combatEnabled", name = "Enabled", description = "", section = combatSection, position = 0)
	default boolean combatEnabled()
	{
		return true;
	}

	@Range(min = 0, max = 150)
	@ConfigItem(keyName = "combatLevel", name = "Resip at level",
		description = "Alert when your Attack or Strength drops to this level. Set to 0 for automatic (a few levels above your base)", section = combatSection, position = 1)
	default int combatLevel()
	{
		return 112;
	}

	@ConfigItem(keyName = "combatUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = combatSection, position = 2)
	default boolean combatUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "combatColor", name = "Color", description = "Used when 'Use potion color' is off", section = combatSection, position = 3)
	default Color combatColor()
	{
		return AlertType.COMBAT.getDefaultColor();
	}

	@ConfigItem(keyName = "combatSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = combatSection, position = 4)
	default AlertSound combatSound()
	{
		return AlertType.COMBAT.getDefaultSound();
	}

	@ConfigItem(keyName = "combatCustomFile", name = "Custom file", description = "", section = combatSection, position = 5, hidden = true)
	default String combatCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Ranging", description = "Ranging and divine ranging pots", position = 6, closedByDefault = true)
	String rangedSection = "ranged";

	@ConfigItem(keyName = "rangedEnabled", name = "Enabled", description = "", section = rangedSection, position = 0)
	default boolean rangedEnabled()
	{
		return true;
	}

	@Range(min = 0, max = 150)
	@ConfigItem(keyName = "rangedLevel", name = "Resip at level",
		description = "Alert when your Ranged drops to this level. Set to 0 for automatic (a few levels above your base)", section = rangedSection, position = 1)
	default int rangedLevel()
	{
		return 108;
	}

	@ConfigItem(keyName = "rangedUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = rangedSection, position = 2)
	default boolean rangedUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "rangedColor", name = "Color", description = "Used when 'Use potion color' is off", section = rangedSection, position = 3)
	default Color rangedColor()
	{
		return AlertType.RANGED.getDefaultColor();
	}

	@ConfigItem(keyName = "rangedSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = rangedSection, position = 4)
	default AlertSound rangedSound()
	{
		return AlertType.RANGED.getDefaultSound();
	}

	@ConfigItem(keyName = "rangedCustomFile", name = "Custom file", description = "", section = rangedSection, position = 5, hidden = true)
	default String rangedCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Bastion", description = "Bastion, divine bastion, and CoX twisted pots", position = 7, closedByDefault = true)
	String bastionSection = "bastion";

	@ConfigItem(keyName = "bastionEnabled", name = "Enabled", description = "", section = bastionSection, position = 0)
	default boolean bastionEnabled()
	{
		return true;
	}

	@Range(min = 0, max = 150)
	@ConfigItem(keyName = "bastionLevel", name = "Resip at level",
		description = "Alert when your Ranged drops to this level. Set to 0 for automatic (a few levels above your base)", section = bastionSection, position = 1)
	default int bastionLevel()
	{
		return 108;
	}

	@ConfigItem(keyName = "bastionUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = bastionSection, position = 2)
	default boolean bastionUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "bastionColor", name = "Color", description = "Used when 'Use potion color' is off", section = bastionSection, position = 3)
	default Color bastionColor()
	{
		return AlertType.BASTION.getDefaultColor();
	}

	@ConfigItem(keyName = "bastionSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = bastionSection, position = 4)
	default AlertSound bastionSound()
	{
		return AlertType.BASTION.getDefaultSound();
	}

	@ConfigItem(keyName = "bastionCustomFile", name = "Custom file", description = "", section = bastionSection, position = 5, hidden = true)
	default String bastionCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Magic", description = "Magic, divine magic, super magic, ancient and forgotten brew", position = 8, closedByDefault = true)
	String magicSection = "magic";

	@ConfigItem(keyName = "magicEnabled", name = "Enabled", description = "", section = magicSection, position = 0)
	default boolean magicEnabled()
	{
		return true;
	}

	@Range(min = 0, max = 150)
	@ConfigItem(keyName = "magicLevel", name = "Resip at level",
		description = "Alert when your Magic drops to this level. Set to 0 for automatic (a few levels above your base)", section = magicSection, position = 1)
	default int magicLevel()
	{
		return 101;
	}

	@ConfigItem(keyName = "magicUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = magicSection, position = 2)
	default boolean magicUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "magicColor", name = "Color", description = "Used when 'Use potion color' is off", section = magicSection, position = 3)
	default Color magicColor()
	{
		return AlertType.MAGIC.getDefaultColor();
	}

	@ConfigItem(keyName = "magicSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = magicSection, position = 4)
	default AlertSound magicSound()
	{
		return AlertType.MAGIC.getDefaultSound();
	}

	@ConfigItem(keyName = "magicCustomFile", name = "Custom file", description = "", section = magicSection, position = 5, hidden = true)
	default String magicCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Battlemage", description = "Battlemage, divine battlemage, and CoX kodai pots", position = 9, closedByDefault = true)
	String battlemageSection = "battlemage";

	@ConfigItem(keyName = "battlemageEnabled", name = "Enabled", description = "", section = battlemageSection, position = 0)
	default boolean battlemageEnabled()
	{
		return true;
	}

	@Range(min = 0, max = 150)
	@ConfigItem(keyName = "battlemageLevel", name = "Resip at level",
		description = "Alert when your Magic drops to this level. Set to 0 for automatic (a few levels above your base)", section = battlemageSection, position = 1)
	default int battlemageLevel()
	{
		return 101;
	}

	@ConfigItem(keyName = "battlemageUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = battlemageSection, position = 2)
	default boolean battlemageUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "battlemageColor", name = "Color", description = "Used when 'Use potion color' is off", section = battlemageSection, position = 3)
	default Color battlemageColor()
	{
		return AlertType.BATTLEMAGE.getDefaultColor();
	}

	@ConfigItem(keyName = "battlemageSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = battlemageSection, position = 4)
	default AlertSound battlemageSound()
	{
		return AlertType.BATTLEMAGE.getDefaultSound();
	}

	@ConfigItem(keyName = "battlemageCustomFile", name = "Custom file", description = "", section = battlemageSection, position = 5, hidden = true)
	default String battlemageCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Imbued / saturated heart", description = "Alerts when your heart is off cooldown and your Magic is low", position = 10, closedByDefault = true)
	String heartSection = "heart";

	@ConfigItem(keyName = "heartEnabled", name = "Enabled", description = "", section = heartSection, position = 0)
	default boolean heartEnabled()
	{
		return true;
	}

	@Range(min = 0, max = 150)
	@ConfigItem(keyName = "heartLevel", name = "Resip at level",
		description = "Alert when your Magic drops to this level and your heart is ready. Set to 0 for automatic (a few levels above your base)", section = heartSection, position = 1)
	default int heartLevel()
	{
		return 104;
	}

	@ConfigItem(keyName = "heartUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = heartSection, position = 2)
	default boolean heartUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "heartColor", name = "Color", description = "Used when 'Use potion color' is off", section = heartSection, position = 3)
	default Color heartColor()
	{
		return AlertType.HEART.getDefaultColor();
	}

	@ConfigItem(keyName = "heartSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = heartSection, position = 4)
	default AlertSound heartSound()
	{
		return AlertType.HEART.getDefaultSound();
	}

	@ConfigItem(keyName = "heartCustomFile", name = "Custom file", description = "", section = heartSection, position = 5, hidden = true)
	default String heartCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Overload", description = "NMZ, raids and blighted overloads", position = 11, closedByDefault = true)
	String overloadSection = "overload";

	@ConfigItem(keyName = "overloadEnabled", name = "Enabled", description = "", section = overloadSection, position = 0)
	default boolean overloadEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "overloadUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = overloadSection, position = 2)
	default boolean overloadUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "overloadColor", name = "Color", description = "Used when 'Use potion color' is off", section = overloadSection, position = 3)
	default Color overloadColor()
	{
		return AlertType.OVERLOAD.getDefaultColor();
	}

	@ConfigItem(keyName = "overloadSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = overloadSection, position = 4)
	default AlertSound overloadSound()
	{
		return AlertType.OVERLOAD.getDefaultSound();
	}

	@ConfigItem(keyName = "overloadCustomFile", name = "Custom file", description = "", section = overloadSection, position = 5, hidden = true)
	default String overloadCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Smelling salts", description = "Tombs of Amascut smelling salts", position = 12, closedByDefault = true)
	String saltsSection = "salts";

	@ConfigItem(keyName = "saltsEnabled", name = "Enabled", description = "", section = saltsSection, position = 0)
	default boolean saltsEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "saltsUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = saltsSection, position = 2)
	default boolean saltsUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "saltsColor", name = "Color", description = "Used when 'Use potion color' is off", section = saltsSection, position = 3)
	default Color saltsColor()
	{
		return AlertType.SALTS.getDefaultColor();
	}

	@ConfigItem(keyName = "saltsSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = saltsSection, position = 4)
	default AlertSound saltsSound()
	{
		return AlertType.SALTS.getDefaultSound();
	}

	@ConfigItem(keyName = "saltsCustomFile", name = "Custom file", description = "", section = saltsSection, position = 5, hidden = true)
	default String saltsCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Anti-poison / venom", description = "Antipoison, antidote, anti-venom, sanfew", position = 13, closedByDefault = true)
	String poisonSection = "poison";

	@ConfigItem(keyName = "poisonEnabled", name = "Enabled", description = "", section = poisonSection, position = 0)
	default boolean poisonEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "poisonNoPot", name = "Warn with none left",
		description = "Still alert when your protection is running out even if you have none left, so you know to teleport out", section = poisonSection, position = 1)
	default boolean poisonNoPot()
	{
		return true;
	}

	@ConfigItem(keyName = "poisonWhenPoisoned", name = "Alert if poisoned",
		description = "Also alert if you actually get poisoned or venomed and have a cure on you", section = poisonSection, position = 1)
	default boolean poisonWhenPoisoned()
	{
		return true;
	}

	@ConfigItem(keyName = "poisonUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = poisonSection, position = 2)
	default boolean poisonUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "poisonColor", name = "Color", description = "Used when 'Use potion color' is off", section = poisonSection, position = 3)
	default Color poisonColor()
	{
		return AlertType.POISON.getDefaultColor();
	}

	@ConfigItem(keyName = "poisonSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = poisonSection, position = 4)
	default AlertSound poisonSound()
	{
		return AlertType.POISON.getDefaultSound();
	}

	@ConfigItem(keyName = "poisonCustomFile", name = "Custom file", description = "", section = poisonSection, position = 5, hidden = true)
	default String poisonCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Antifire", description = "Antifire, super antifire, extended versions", position = 14, closedByDefault = true)
	String antifireSection = "antifire";

	@ConfigItem(keyName = "antifireEnabled", name = "Enabled", description = "", section = antifireSection, position = 0)
	default boolean antifireEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "antifireNoPot", name = "Warn with none left",
		description = "Still alert when your protection is running out even if you have none left, so you know to teleport out", section = antifireSection, position = 1)
	default boolean antifireNoPot()
	{
		return true;
	}

	@ConfigItem(keyName = "antifireUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = antifireSection, position = 2)
	default boolean antifireUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "antifireColor", name = "Color", description = "Used when 'Use potion color' is off", section = antifireSection, position = 3)
	default Color antifireColor()
	{
		return AlertType.ANTIFIRE.getDefaultColor();
	}

	@ConfigItem(keyName = "antifireSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = antifireSection, position = 4)
	default AlertSound antifireSound()
	{
		return AlertType.ANTIFIRE.getDefaultSound();
	}

	@ConfigItem(keyName = "antifireCustomFile", name = "Custom file", description = "", section = antifireSection, position = 5, hidden = true)
	default String antifireCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Prayer regeneration", description = "Prayer regeneration potions", position = 15, closedByDefault = true)
	String prayerregenSection = "prayerregen";

	@ConfigItem(keyName = "prayerregenEnabled", name = "Enabled", description = "", section = prayerregenSection, position = 0)
	default boolean prayerregenEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "prayerregenUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = prayerregenSection, position = 2)
	default boolean prayerregenUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "prayerregenColor", name = "Color", description = "Used when 'Use potion color' is off", section = prayerregenSection, position = 3)
	default Color prayerregenColor()
	{
		return AlertType.PRAYER_REGEN.getDefaultColor();
	}

	@ConfigItem(keyName = "prayerregenSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = prayerregenSection, position = 4)
	default AlertSound prayerregenSound()
	{
		return AlertType.PRAYER_REGEN.getDefaultSound();
	}

	@ConfigItem(keyName = "prayerregenCustomFile", name = "Custom file", description = "", section = prayerregenSection, position = 5, hidden = true)
	default String prayerregenCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Prayer enhance", description = "Chambers of Xeric prayer enhance", position = 16, closedByDefault = true)
	String enhanceSection = "enhance";

	@ConfigItem(keyName = "enhanceEnabled", name = "Enabled", description = "", section = enhanceSection, position = 0)
	default boolean enhanceEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "enhanceUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = enhanceSection, position = 2)
	default boolean enhanceUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "enhanceColor", name = "Color", description = "Used when 'Use potion color' is off", section = enhanceSection, position = 3)
	default Color enhanceColor()
	{
		return AlertType.ENHANCE.getDefaultColor();
	}

	@ConfigItem(keyName = "enhanceSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = enhanceSection, position = 4)
	default AlertSound enhanceSound()
	{
		return AlertType.ENHANCE.getDefaultSound();
	}

	@ConfigItem(keyName = "enhanceCustomFile", name = "Custom file", description = "", section = enhanceSection, position = 5, hidden = true)
	default String enhanceCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Liquid adrenaline", description = "Tombs of Amascut liquid adrenaline", position = 17, closedByDefault = true)
	String adrenalineSection = "adrenaline";

	@ConfigItem(keyName = "adrenalineEnabled", name = "Enabled", description = "", section = adrenalineSection, position = 0)
	default boolean adrenalineEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "adrenalineUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = adrenalineSection, position = 2)
	default boolean adrenalineUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "adrenalineColor", name = "Color", description = "Used when 'Use potion color' is off", section = adrenalineSection, position = 3)
	default Color adrenalineColor()
	{
		return AlertType.ADRENALINE.getDefaultColor();
	}

	@ConfigItem(keyName = "adrenalineSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = adrenalineSection, position = 4)
	default AlertSound adrenalineSound()
	{
		return AlertType.ADRENALINE.getDefaultSound();
	}

	@ConfigItem(keyName = "adrenalineCustomFile", name = "Custom file", description = "", section = adrenalineSection, position = 5, hidden = true)
	default String adrenalineCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Menaphite remedy", description = "Menaphite remedy", position = 18, closedByDefault = true)
	String remedySection = "remedy";

	@ConfigItem(keyName = "remedyEnabled", name = "Enabled", description = "", section = remedySection, position = 0)
	default boolean remedyEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "remedyUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = remedySection, position = 2)
	default boolean remedyUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "remedyColor", name = "Color", description = "Used when 'Use potion color' is off", section = remedySection, position = 3)
	default Color remedyColor()
	{
		return AlertType.REMEDY.getDefaultColor();
	}

	@ConfigItem(keyName = "remedySound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = remedySection, position = 4)
	default AlertSound remedySound()
	{
		return AlertType.REMEDY.getDefaultSound();
	}

	@ConfigItem(keyName = "remedyCustomFile", name = "Custom file", description = "", section = remedySection, position = 5, hidden = true)
	default String remedyCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Stamina", description = "Stamina potions and mixes", position = 19, closedByDefault = true)
	String staminaSection = "stamina";

	@ConfigItem(keyName = "staminaEnabled", name = "Enabled", description = "", section = staminaSection, position = 0)
	default boolean staminaEnabled()
	{
		return true;
	}

	@ConfigItem(keyName = "staminaUsePotionColor", name = "Use potion color",
		description = "Glow in the color of the pot in your inventory. Turn off to use the color below", section = staminaSection, position = 2)
	default boolean staminaUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "staminaColor", name = "Color", description = "Used when 'Use potion color' is off", section = staminaSection, position = 3)
	default Color staminaColor()
	{
		return AlertType.STAMINA.getDefaultColor();
	}

	@ConfigItem(keyName = "staminaSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = staminaSection, position = 4)
	default AlertSound staminaSound()
	{
		return AlertType.STAMINA.getDefaultSound();
	}

	@ConfigItem(keyName = "staminaCustomFile", name = "Custom file", description = "", section = staminaSection, position = 5, hidden = true)
	default String staminaCustomFile()
	{
		return "";
	}

	@ConfigSection(name = "Prayer", description = "Prayer pots, super restores, sanfew, CoX revitalisation", position = 20, closedByDefault = true)
	String prayerSection = "prayer";

	@ConfigItem(keyName = "prayerEnabled", name = "Enabled", description = "", section = prayerSection, position = 0)
	default boolean prayerEnabled()
	{
		return true;
	}

	@Range(min = 1, max = 99)
	@ConfigItem(keyName = "prayerThreshold", name = "Alert at prayer",
		description = "Alert when prayer points drop to this", section = prayerSection, position = 1)
	default int prayerThreshold()
	{
		return 20;
	}

	@ConfigItem(keyName = "prayerOnLogin", name = "Alert on login",
		description = "Alert right when you log in if your prayer is low. Off waits until you fight or drink a pot", section = prayerSection, position = 1)
	default boolean prayerOnLogin()
	{
		return true;
	}

	@ConfigItem(keyName = "prayerUsePotionColor", name = "Use potion color",
		description = "Glow prayer potion teal for every prayer and restore pot. Turn off to use the color below", section = prayerSection, position = 2)
	default boolean prayerUsePotionColor()
	{
		return true;
	}

	@ConfigItem(keyName = "prayerColor", name = "Color", description = "Used when 'Use potion color' is off", section = prayerSection, position = 3)
	default Color prayerColor()
	{
		return AlertType.PRAYER.getDefaultColor();
	}

	@ConfigItem(keyName = "prayerSound", name = "Sound", description = "Off for glow only. A custom sound picked in the Pot Alert sidebar overrides this", section = prayerSection, position = 4)
	default AlertSound prayerSound()
	{
		return AlertType.PRAYER.getDefaultSound();
	}

	@ConfigItem(keyName = "prayerCustomFile", name = "Custom file", description = "", section = prayerSection, position = 5, hidden = true)
	default String prayerCustomFile()
	{
		return "";
	}
}
