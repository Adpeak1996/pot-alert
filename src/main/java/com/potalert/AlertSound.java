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
	BOOP("Boop", 2266),
	COIN_TINKLE("Coin tinkle", 3924),
	COINS("Coins jingle", 2114),
	EAT("Eat", 2393),
	ERROR("Error", 2394),
	FANFARE("Fanfare", 2930),
	DINGALING("GE ding", 3925),
	PING("Ping", 933),
	SIP("Potion sip", 2390),
	PRAYER_OFF("Prayer off", 2673),
	QUIZ_CORRECT("Quiz correct", 2309),
	BOING("Splash boing", 227),
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
