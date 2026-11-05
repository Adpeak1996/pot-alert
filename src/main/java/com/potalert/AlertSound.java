package com.potalert;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlertSound
{
	DRINK("Potion gulp", 2401),
	PRAYER_DRAINED("Prayer drained", 2672),

	ANVIL("Anvil tink", 3790),
	BANK_BELL("Bank bell", 2022),
	BELL("Bell", 2192),
	BIRD_CALL("Bird call", 2624),
	BOOP("Boop", 2266),
	CHARGE("Charge spell", 1651),
	COIN_TINKLE("Coin tinkle", 3924),
	COINS("Coins jingle", 2114),
	DRAGON_BREATH("Dragon breath", 585),
	EAT("Eat", 2393),
	ENERGY_DRAIN("Energy drain", 2713),
	ERROR("Error", 2394),
	EXPLODING_VIAL("Exploding vial", 1486),
	FANFARE("Fanfare", 2930),
	FISHING_CAST("Fishing cast", 2600),
	ZAMORAK_FLAMES("Flames of Zamorak", 1655),
	DINGALING("GE ding", 3925),
	GROWL("Growl", 2067),
	HEAL("Heal", 166),
	JUMP("Jump", 2461),
	PING("Ping", 933),
	SIP("Potion sip", 2390),
	PRAYER_BOOST("Prayer boost", 2671),
	PRAYER_OFF("Prayer off", 2673),
	PRAYER_RECHARGE("Prayer recharge", 2674),
	QUIZ_CORRECT("Quiz correct", 2309),
	ROAR("Roar", 2252),
	SALT("Salt sprinkle", 2719),
	SHIELD_HIT("Shield hit", 258),
	SNAKE_HISS("Snake hiss", 799),
	SPIDER("Spider", 1279),
	BOING("Splash boing", 227),
	STONE_ARMOUR("Stone armour", 31),
	VWOOP("Teleport", 200),
	UNLOCK("Unlock", 2419),
	WHISTLE("Whistle", 1569),

	OFF("Off", -1);

	private final String label;
	private final int soundId;

	@Override
	public String toString()
	{
		return label;
	}
}
