package com.potalert;

import java.awt.Color;
import lombok.Getter;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

@Getter
public enum AlertType
{
	ATTACK("attack", "Attack", Kind.BOOST, new Skill[]{Skill.ATTACK}, new Color(0x2A4FB0), AlertSound.DRINK,
		ItemID._4DOSE2ATTACK, new int[]{VarbitID.DIVINEATTACK_POTION_TIME},
		"super attack", "attack potion", "attack mix"),

	STRENGTH("strength", "Strength", Kind.BOOST, new Skill[]{Skill.STRENGTH}, new Color(0xE6E6E6), AlertSound.DRINK,
		ItemID._4DOSE2STRENGTH, new int[]{VarbitID.DIVINESTRENGTH_POTION_TIME},
		"super strength", "strength potion", "super str. mix", "strength mix"),

	DEFENCE("defence", "Defence", Kind.BOOST, new Skill[]{Skill.DEFENCE}, new Color(0xD4A93A), AlertSound.DRINK,
		ItemID._4DOSE2DEFENSE, new int[]{VarbitID.DIVINEDEFENCE_POTION_TIME},
		"super defence", "defence potion", "super def. mix", "defence mix"),

	COMBAT("combat", "Super combat", Kind.BOOST, new Skill[]{Skill.ATTACK, Skill.STRENGTH}, new Color(0x1F6B2E), AlertSound.DRINK,
		ItemID._4DOSE2COMBAT, new int[]{VarbitID.DIVINECOMBAT_POTION_TIME},
		"super combat", "combat potion", "combat mix", "elder (", "elder potion"),

	RANGED("ranged", "Ranging", Kind.BOOST, new Skill[]{Skill.RANGED}, new Color(0x2E8FB8), AlertSound.DRINK,
		ItemID._4DOSERANGERSPOTION, new int[]{VarbitID.DIVINERANGE_POTION_TIME},
		"ranging potion", "super ranging", "ranging mix"),

	BASTION("bastion", "Bastion", Kind.BOOST, new Skill[]{Skill.RANGED}, new Color(0xB86B2E), AlertSound.DRINK,
		ItemID._4DOSEBASTION, new int[]{VarbitID.DIVINEBASTION_POTION_TIME},
		"bastion potion", "twisted (", "twisted potion"),

	MAGIC("magic", "Magic", Kind.BOOST, new Skill[]{Skill.MAGIC}, new Color(0xB8925A), AlertSound.DRINK,
		ItemID._4DOSE1MAGIC, new int[]{VarbitID.DIVINEMAGIC_POTION_TIME},
		"magic potion", "magic mix", "forgotten brew", "ancient brew", "ancient mix"),

	BATTLEMAGE("battlemage", "Battlemage", Kind.BOOST, new Skill[]{Skill.MAGIC}, new Color(0x8A5BB8), AlertSound.DRINK,
		ItemID._4DOSEBATTLEMAGE, new int[]{VarbitID.DIVINEBATTLEMAGE_POTION_TIME},
		"battlemage potion", "kodai (", "kodai potion"),

	ZAMORAK("zamorak", "Zamorak brew", Kind.BOOST, new Skill[]{Skill.ATTACK, Skill.STRENGTH}, new Color(0xB8862E), AlertSound.DRINK,
		ItemID._4DOSEPOTIONOFZAMORAK, new int[0],
		"zamorak brew", "zamorak mix"),

	HEART("heart", "Heart", Kind.HEART, new Skill[]{Skill.MAGIC}, new Color(0xC23A6B), AlertSound.DRINK,
		ItemID.IMBUED_HEART, new int[0],
		"imbued heart", "saturated heart"),

	OVERLOAD("overload", "Overload", Kind.TIMED, new Skill[0], new Color(0x4A4A4A), AlertSound.DRINK,
		ItemID.NZONE4DOSEOVERLOADPOTION, new int[0],
		"overload"),

	SALTS("salts", "Smelling salts", Kind.TIMED, new Skill[0], new Color(0xD8C27A), AlertSound.DRINK,
		ItemID.TOA_SUPPLY_STATS_2, new int[0],
		"smelling salts"),

	POISON("poison", "Anti-venom", Kind.TIMED, new Skill[0], new Color(0x1D2B22), AlertSound.DRINK,
		ItemID.ANTIVENOM_4, new int[0],
		"antipoison", "anti-venom", "antidote", "sanfew"),

	ANTIFIRE("antifire", "Antifire", Kind.TIMED, new Skill[0], new Color(0x8A3B6E), AlertSound.DRINK,
		ItemID._4DOSE3ANTIDRAGON, new int[0],
		"antifire"),

	PRAYER_REGEN("prayerregen", "Prayer regen", Kind.TIMED, new Skill[0], new Color(0x5FB8E0), AlertSound.DRINK,
		ItemID._4DOSE1PRAYER_REGENERATION, new int[0],
		"prayer regeneration"),

	ENHANCE("enhance", "Prayer enhance", Kind.TIMED, new Skill[0], new Color(0x9B59D0), AlertSound.DRINK,
		ItemID.RAIDS_VIAL_PRAYER_WEAK_4, new int[0],
		"prayer enhance"),

	ADRENALINE("adrenaline", "Adrenaline", Kind.TIMED, new Skill[0], new Color(0xD9534F), AlertSound.DRINK,
		ItemID.TOA_SUPPLY_ENERGY_2, new int[0],
		"liquid adrenaline"),

	REMEDY("remedy", "Menaphite", Kind.TIMED, new Skill[0], new Color(0xC9A227), AlertSound.DRINK,
		ItemID._4DOSESTATRENEWAL, new int[0],
		"menaphite remedy"),

	STAMINA("stamina", "Stamina", Kind.TIMED, new Skill[0], new Color(0x9C6B3C), AlertSound.DRINK,
		ItemID._4DOSESTAMINA, new int[0],
		"stamina"),

	GOADING("goading", "Goading", Kind.TIMED, new Skill[0], new Color(0xC0392B), AlertSound.DRINK,
		ItemID._4DOSEGOADING, new int[0],
		"goading"),

	FISHING("fishing", "Fishing", Kind.SKILL, new Skill[]{Skill.FISHING}, new Color(0x3A7BD5), AlertSound.DRINK,
		ItemID._4DOSEFISHERSPOTION, new int[0],
		"fishing potion", "fishing mix"),

	HUNTER("hunter", "Hunter", Kind.SKILL, new Skill[]{Skill.HUNTER}, new Color(0x7A9A3A), AlertSound.DRINK,
		ItemID._4DOSEHUNTING, new int[0],
		"hunter potion"),

	AGILITY("agility", "Agility", Kind.SKILL, new Skill[]{Skill.AGILITY}, new Color(0x3AB8A0), AlertSound.DRINK,
		ItemID._4DOSE1AGILITY, new int[0],
		"agility potion", "agility mix"),

	PRAYER("prayer", "Prayer", Kind.PRAYER, new Skill[]{Skill.PRAYER}, new Color(0x2FC4A8), AlertSound.PRAYER_DRAINED,
		ItemID._4DOSEPRAYERRESTORE, new int[0],
		"prayer potion", "prayer mix", "super restore", "sanfew", "revitalisation (", "revitalisation potion", "tears of elidinis", "blessed crystal scarab", "egniol");

	enum Kind
	{
		BOOST, SKILL, HEART, TIMED, PRAYER
	}

	private final String key;
	private final String label;
	private final Kind kind;
	private final Skill[] skills;
	private final Color defaultColor;
	private final AlertSound defaultSound;
	private final int sampleItemId;
	private final int[] divineVarbits;
	private final String[] keywords;

	AlertType(String key, String label, Kind kind, Skill[] skills, Color defaultColor, AlertSound defaultSound,
		int sampleItemId, int[] divineVarbits, String... keywords)
	{
		this.key = key;
		this.label = label;
		this.kind = kind;
		this.skills = skills;
		this.defaultColor = defaultColor;
		this.defaultSound = defaultSound;
		this.sampleItemId = sampleItemId;
		this.divineVarbits = divineVarbits;
		this.keywords = keywords;
	}

	private static final String[] EXCLUDED = {"(unf)", "cat antipoison", "potion set", "sigil of"};

	public boolean matches(String lowerName)
	{
		for (String x : EXCLUDED)
		{
			if (lowerName.contains(x))
			{
				return false;
			}
		}
		for (String k : keywords)
		{
			if (lowerName.contains(k))
			{
				return true;
			}
		}
		return false;
	}
}
