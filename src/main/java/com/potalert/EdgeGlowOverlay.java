package com.potalert;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

/** Draws a pulsing glow around the edges of the game screen in the color of the pot you need */
class EdgeGlowOverlay extends Overlay
{
	private final Client client;
	private final PotAlertPlugin plugin;
	private final PotAlertConfig config;

	@Inject
	EdgeGlowOverlay(Client client, PotAlertPlugin plugin, PotAlertConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D g)
	{
		List<Color> colors = plugin.getDisplayColors();
		if (colors.isEmpty())
		{
			return null;
		}

		long now = System.currentTimeMillis();
		int period = Math.max(200, config.pulseSpeed());
		// With more than one alert up, the colors take turns, one per pulse
		Color base = colors.get((int) ((now / period) % colors.size()));

		double phase = (now % period) / (double) period;
		double pulse = 0.5 - 0.5 * Math.cos(2 * Math.PI * phase);
		int alpha = (int) (config.glowOpacity() * 2.55 * (0.25 + 0.75 * pulse));

		int x = client.getViewportXOffset();
		int y = client.getViewportYOffset();
		int w = client.getViewportWidth();
		int h = client.getViewportHeight();
		int t = Math.min(config.glowThickness(), Math.min(w, h) / 2);
		if (w <= 0 || h <= 0 || t <= 0)
		{
			return null;
		}

		Color solid = new Color(base.getRed(), base.getGreen(), base.getBlue(), Math.max(0, Math.min(255, alpha)));
		Color clear = new Color(base.getRed(), base.getGreen(), base.getBlue(), 0);

		Paint old = g.getPaint();

		g.setPaint(new GradientPaint(x, y, solid, x, y + t, clear));
		g.fillRect(x, y, w, t);

		g.setPaint(new GradientPaint(x, y + h, solid, x, y + h - t, clear));
		g.fillRect(x, y + h - t, w, t);

		g.setPaint(new GradientPaint(x, y, solid, x + t, y, clear));
		g.fillRect(x, y, t, h);

		g.setPaint(new GradientPaint(x + w, y, solid, x + w - t, y, clear));
		g.fillRect(x + w - t, y, t, h);

		g.setPaint(old);
		return null;
	}
}
