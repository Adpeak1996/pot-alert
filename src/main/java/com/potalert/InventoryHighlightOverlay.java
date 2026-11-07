package com.potalert;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

/** Outlines the pot you need in your inventory, pulsing along with the screen glow */
class InventoryHighlightOverlay extends WidgetItemOverlay
{
	private final PotAlertPlugin plugin;
	private final PotAlertConfig config;
	private final ItemManager itemManager;

	@Inject
	InventoryHighlightOverlay(PotAlertPlugin plugin, PotAlertConfig config, ItemManager itemManager)
	{
		this.plugin = plugin;
		this.config = config;
		this.itemManager = itemManager;
		showOnInventory();
	}

	@Override
	public void renderItemOverlay(Graphics2D g, int itemId, WidgetItem item)
	{
		Color color = plugin.getHighlights().get(itemId);
		if (color == null)
		{
			return;
		}

		// Lighter than the pot itself so it doesn't blend into the liquid, and never fades out completely
		int alpha = (int) (255 * (0.6 + 0.4 * (plugin.glowStrength() - 0.25) / 0.75));
		Color outline = new Color(lighten(color.getRed()), lighten(color.getGreen()), lighten(color.getBlue()),
			Math.max(0, Math.min(255, alpha)));
		BufferedImage image = itemManager.getItemOutline(itemId, item.getQuantity(), outline);
		Rectangle bounds = item.getCanvasBounds();
		int x = (int) bounds.getX();
		int y = (int) bounds.getY();

		// Thicker outlines are the same outline drawn shifted a pixel or two in each direction
		int spread = Math.max(1, Math.min(3, config.outlineThickness())) - 1;
		for (int d = 1; d <= spread; d++)
		{
			g.drawImage(image, x - d, y, null);
			g.drawImage(image, x + d, y, null);
			g.drawImage(image, x, y - d, null);
			g.drawImage(image, x, y + d, null);
		}
		g.drawImage(image, x, y, null);
	}

	private static int lighten(int channel)
	{
		return channel + (255 - channel) * 45 / 100;
	}
}
