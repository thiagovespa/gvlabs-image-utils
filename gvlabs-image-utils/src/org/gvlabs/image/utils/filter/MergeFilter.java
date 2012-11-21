package org.gvlabs.image.utils.filter;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

/**
 * Merge images
 * 
 * @author Thiago Galbiatti Vespa
 * @version 3.0
 */
public class MergeFilter implements ImageFilter {
	protected double weigth;
	protected Image original;
	protected ColorModel cm;

	protected int resultArray[];

	/**
	 * Construtor
	 * 
	 * @param original
	 *            original image
	 */
	public MergeFilter(Image original) {
		cm = null;
		this.original = original;
		weigth = 0.5;
		resultArray = null;
	}

	/**
	 * Set the weight to apply the filter
	 * 
	 * @param weigth
	 *            weight to apply the filter
	 */
	public void setWeight(double weigth) {
		this.weigth = weigth;
	}

	/**
	 * Do the merge of the original image and the toMerge image
	 * 
	 * @param toMerge
	 *            image to merge with the original
	 * @return merged image
	 */
	public Image merge(Image toMerge) {

		int wid1 = original.getWidth(null);
		int wid2 = toMerge.getWidth(null);
		int hgt1 = original.getHeight(null);
		int hgt2 = toMerge.getHeight(null);

		int resultWid = Math.max(wid1, wid2);
		int resultHgt = Math.max(hgt1, hgt2);

		cm = ColorModel.getRGBdefault();

		resultArray = new int[resultWid * resultHgt];

		int[] p1 = new int[resultWid * resultHgt];
		int[] p2 = new int[resultWid * resultHgt];

		PixelGrabber pg1 = new PixelGrabber(original, 0, 0, wid1, hgt1, p1, 0,
				resultWid);
		try {
			pg1.grabPixels();
		} catch (Exception ie1) {
		}

		PixelGrabber pg2 = new PixelGrabber(toMerge, 0, 0, wid2, hgt2, p2, 0,
				resultWid);
		try {
			pg2.grabPixels();
		} catch (Exception ie2) {
		}

		int y, x, rp, rpi;
		int red1, red2, redr;
		int green1, green2, greenr;
		int blue1, blue2, bluer;
		int alpha1, alpha2, alphar;
		double wgt1, wgt2;

		// Merge
		for (y = 0; y < resultHgt; y++) {
			for (x = 0; x < resultWid; x++) {
				rpi = y * resultWid + x;
				rp = 0;
				blue1 = p1[rpi] & 0x00ff;
				blue2 = p2[rpi] & 0x00ff;
				green1 = (p1[rpi] >> 8) & 0x00ff;
				green2 = (p2[rpi] >> 8) & 0x00ff;
				red1 = (p1[rpi] >> 16) & 0x00ff;
				red2 = (p2[rpi] >> 16) & 0x00ff;
				alpha1 = (p1[rpi] >> 24) & 0x00ff;
				alpha2 = (p2[rpi] >> 24) & 0x00ff;

				wgt1 = weigth * (alpha1 / 255.0);
				wgt2 = (1.0 - weigth) * (alpha2 / 255.0);

				redr = (int) (red1 * wgt1 + red2 * wgt2);
				redr = (redr < 0) ? (0) : ((redr > 255) ? (255) : (redr));
				greenr = (int) (green1 * wgt1 + green2 * wgt2);
				greenr = (greenr < 0) ? (0) : ((greenr > 255) ? (255)
						: (greenr));
				bluer = (int) (blue1 * wgt1 + blue2 * wgt2);
				bluer = (bluer < 0) ? (0) : ((bluer > 255) ? (255) : (bluer));
				alphar = 255;

				rp = (((((alphar << 8) + (redr & 0x0ff)) << 8) + (greenr & 0x0ff)) << 8)
						+ (bluer & 0x0ff);

				resultArray[rpi] = rp;
			}
		}

		// TODO: Optimize
		Image ret;
		MemoryImageSource mis;
		if (resultArray == null) {
			return null;
		}
		mis = new MemoryImageSource(resultWid, resultHgt, cm, resultArray, 0,
				resultWid);
		ret = Toolkit.getDefaultToolkit().createImage(mis);
		resultArray = null;
		return ret;

	}

	@Override
	public BufferedImage applyTo(BufferedImage src) {
		Image merged = this.merge(src);
		BufferedImage bImage = new BufferedImage(merged.getWidth(null),
				merged.getHeight(null), BufferedImage.TYPE_INT_RGB);

		Graphics2D bImageGraphics = bImage.createGraphics();

		bImageGraphics.drawImage(merged, null, null);

		return bImage;
	}
}
