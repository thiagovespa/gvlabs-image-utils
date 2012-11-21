package org.gvlabs.image.utils.filter;

import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MergeFilterTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException {

		File fileResult = new File("mergeResult.jpg");

		BufferedImage i1 = ImageIO.read(new File("1.jpg"));
		BufferedImage i2 = ImageIO.read(new File("2.jpg"));

		MergeFilter ic = new MergeFilter(i1);

		RenderedImage bImage = ic.applyTo(i2);

		ImageIO.write(bImage, "jpg", fileResult);
		assertTrue(fileResult.exists());
	}

}
