package com.potalert;

import java.awt.Color;
import java.awt.image.BufferedImage;

final class PotionColorSampler
{
	private static final int HUE_BUCKETS = 12;

	private PotionColorSampler()
	{
	}

	static Color sample(BufferedImage img)
	{
		long[] r = new long[HUE_BUCKETS];
		long[] g = new long[HUE_BUCKETS];
		long[] b = new long[HUE_BUCKETS];
		int[] count = new int[HUE_BUCKETS];

		long allR = 0, allG = 0, allB = 0;
		int allCount = 0;
		long lightR = 0, lightG = 0, lightB = 0;
		int lightCount = 0;

		float[] hsb = new float[3];
		for (int y = img.getHeight() * 35 / 100; y < img.getHeight(); y++)
		{
			for (int x = 0; x < img.getWidth(); x++)
			{
				int argb = img.getRGB(x, y);
				if ((argb >>> 24) < 200)
				{
					continue;
				}

				int pr = (argb >> 16) & 0xFF;
				int pg = (argb >> 8) & 0xFF;
				int pb = argb & 0xFF;
				Color.RGBtoHSB(pr, pg, pb, hsb);

				if (hsb[2] < 0.06f)
				{
					continue;
				}

				allR += pr;
				allG += pg;
				allB += pb;
				allCount++;

				if (hsb[1] < 0.25f && hsb[2] >= 0.55f)
				{
					lightR += pr;
					lightG += pg;
					lightB += pb;
					lightCount++;
				}

				if (hsb[1] >= 0.25f && hsb[2] >= 0.12f)
				{
					int bucket = Math.min(HUE_BUCKETS - 1, (int) (hsb[0] * HUE_BUCKETS));
					r[bucket] += pr;
					g[bucket] += pg;
					b[bucket] += pb;
					count[bucket]++;
				}
			}
		}

		int best = -1;
		for (int i = 0; i < HUE_BUCKETS; i++)
		{
			if (count[i] > 0 && (best == -1 || count[i] > count[best]))
			{
				best = i;
			}
		}

		if (best != -1 && count[best] >= 25 && count[best] * 10 >= allCount * 3)
		{
			return new Color((int) (r[best] / count[best]), (int) (g[best] / count[best]), (int) (b[best] / count[best]));
		}
		if (lightCount * 10 >= allCount * 3)
		{
			return new Color((int) (lightR / lightCount), (int) (lightG / lightCount), (int) (lightB / lightCount));
		}
		if (allCount > 0)
		{
			return new Color((int) (allR / allCount), (int) (allG / allCount), (int) (allB / allCount));
		}
		return null;
	}
}
