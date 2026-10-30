# Pot Alert

Pot Alert glows the edge of your screen in the color of the potion you need, and plays a sound, when it's time to sip. It's built for AFK slayer and bossing, where it's easy to forget to repot.

## What it watches

| Type | Pots | Alerts when |
|---|---|---|
| Boosts | Attack, Strength, Defence, Super combat, Ranging, Bastion, Magic, Battlemage (regular, super, divine, mixes, and CoX versions) | Your stat drops to your **Resip at level** |
| Timed pots | Divines, Overload, Smelling salts, Anti-venom / antipoison, Antifire, Prayer regeneration, Prayer enhance, Liquid adrenaline, Menaphite remedy, Stamina | A set number of seconds before they run out |
| Heart | Imbued and saturated heart | It's off cooldown and your Magic is low |
| Prayer | Prayer pots, super restores, sanfew, revitalisation | Your prayer drops to **Alert at prayer** |

## How it behaves

- Only alerts if the matching pot is in your inventory. Anti-venom and antifire can warn even when you have none left, so you know to teleport out.
- In combat, it repeats until you sip. Out of combat, you get a short heads-up and then it goes quiet.
- Combat starts when a hit lands or you get hit, not when you click an NPC.
- Quiet while the bank, deposit box or Grand Exchange is open.
- After logging in, only prayer can alert until you fight or drink something. The prayer one can be turned off too.

## Glow and sound

- The glow uses the actual color of the pot in your inventory. Dark pots like anti-venom are brightened so they still show up, and any pot can use a color you pick instead.
- Every pot has its own sound, chosen from the game's own sound effects, or Off for glow only.
- You can add your own `.wav` files from the Pot Alert sidebar tab and pick one for any pot.

## Sidebar tab

The Pot Alert tab has a quick guide, a **Test** button to preview any pot's glow and sound, the custom sound picker, and a reset button.
