package moe.thisis.aether.bokuseru.engine.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class FontTexture {

	public static class CharInfo {

		private final int startX;

		private final int width;

		/** Set character info
		 * @param startX	Starting X value
		 * @param width	Width
		 */
		public CharInfo(final int startX, final int width) {
			this.startX = startX;
			this.width = width;
		}

		/** Get start X
		 * @return Starting X value
		 */
		public int getStartX() {
			return startX;
		}

		/** Get width
		 * @return	Width
		 */
		public int getWidth() {
			return width;
		}
	}

	private static final String IMAGE_FORMAT = "png";

	private final Font font;

	private final String charSetName;

	private final Map<Character, CharInfo> charMap;

	private Texture texture;

	private int height;

	private int width;

	/** Font Texture Constructor
	 * @param font	Font
	 * @param charSetName	Character set name
	 * @throws Exception
	 */
	public FontTexture(final Font font, final String charSetName) throws Exception {
		this.font = font;
		this.charSetName = charSetName;
		charMap = new HashMap<>();

		buildTexture();
	}

	private void buildTexture() throws Exception {
		// Get the font metrics for each character for the selected font by
		// using image
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2D = img.createGraphics();
		g2D.setFont(font);
		FontMetrics fontMetrics = g2D.getFontMetrics();

		final String allChars = getAllAvailableChars(charSetName);
		width = 0;
		height = 0;
		for (final char c : allChars.toCharArray()) {
			// Get the size for each character and update global image size
			final CharInfo charInfo = new CharInfo(width, fontMetrics.charWidth(c));
			charMap.put(c, charInfo);
			width += charInfo.getWidth();
			height = Math.max(height, fontMetrics.getHeight());
		}
		g2D.dispose();

		// Create the image associated to the charset
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g2D = img.createGraphics();
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setFont(font);
		fontMetrics = g2D.getFontMetrics();
		g2D.setColor(Color.WHITE);
		g2D.drawString(allChars, 0, fontMetrics.getAscent());
		g2D.dispose();

		// Dump image to a byte buffer
		InputStream is;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			ImageIO.write(img, FontTexture.IMAGE_FORMAT, out);
			out.flush();
			is = new ByteArrayInputStream(out.toByteArray());
		}

		texture = new Texture(is);
	}

	/** Get all available characters
	 * @param charsetName	Character set
	 * @return	Available characters
	 */
	private String getAllAvailableChars(final String charsetName) {
		final CharsetEncoder ce = Charset.forName(charsetName).newEncoder();
		final StringBuilder result = new StringBuilder();
		for (char c = 0; c < Character.MAX_VALUE; c++) {
			if (ce.canEncode(c)) {
				result.append(c);
			}
		}
		return result.toString();
	}

	/** Get character info
	 * @param c Character
	 * @return Character info
	 */
	public CharInfo getCharInfo(final char c) {
		return charMap.get(c);
	}

	/**
	 * @return	Height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return	Texture
	 */
	public Texture getTexture() {
		return texture;
	}

	/**
	 * @return	Width
	 */
	public int getWidth() {
		return width;
	}
}
