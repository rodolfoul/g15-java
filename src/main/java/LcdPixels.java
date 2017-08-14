import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LcdPixels {
	public static final Font ARIAL;
	public static final Font JACKEY;
	public static final Font FIPPS;
	public static final Font VCR_OSD_MONO;

	private final Map<RenderingHints.Key, Object> fontHints;
	private final G15Lcd g15;

	private byte[] positions = new byte[G15.G15_BUFFER_LEN];

	static {
		ARIAL = loadFont("/PIXEARG_.TTF", 9);
		JACKEY = loadFont("/jackeyfont.ttf", 10);
		FIPPS = loadFont("/Fipps-Regular.otf", 10);
		VCR_OSD_MONO = loadFont("/VCR_OSD_MONO_1.001.ttf", 15);
	}

	LcdPixels(G15Lcd g15) {
		this.g15 = g15;

		Map<RenderingHints.Key, Object> m = new HashMap<>();
		m.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		m.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		m.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		m.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		m.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		m.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		m.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		fontHints = Collections.unmodifiableMap(m);
	}

	private static Font loadFont(String resourceLocation, int fontSize) {
		Font font;
		try (InputStream is = G15Lcd.class.getResourceAsStream(resourceLocation)) {
			font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, fontSize);
		} catch (IOException | FontFormatException e) {
			throw new RuntimeException(e);
		}
		return font;
	}

	public void resetPixels() {
		positions = new byte[positions.length];
	}

	public void setPixel(int x, int y, boolean color) {
		if (x < 0 || x > G15.G15_LCD_WIDTH || y < 0 || y > G15.G15_LCD_HEIGHT) {
			return;
		}

		int byteToSet = (x + y * 160) / 8;
		int bitPosition = (x + y * 160) % 8;

		byte bit = (byte) (0x80 >> bitPosition);
		if (color) {
			positions[byteToSet] |= bit;
		} else {
			positions[byteToSet] &= ~bit;
		}
	}

	public void drawLine(int x0, int y0, int xf, int yf, boolean color) {
		double m = (yf - y0) / (double) (xf - x0);

		for (int x = x0; x <= xf; x++) {
			int y = (int) Math.round(y0 + m * (x - x0));
			setPixel(x, y, color);
		}
	}

	public void writeText(String text, int x, int y, Font font) {
		BufferedImage dummyImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D dummyGraphics = dummyImg.createGraphics();
		dummyGraphics.setFont(font);
		FontMetrics fontCalculator = dummyGraphics.getFontMetrics();

		int width = fontCalculator.stringWidth(text);
		int height = fontCalculator.getHeight();
		dummyGraphics.dispose();

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();

		g2d.setFont(font);
		g2d.setRenderingHints(fontHints);
		FontMetrics fm = g2d.getFontMetrics();
		g2d.setColor(Color.BLACK);
		g2d.drawString(text, 0, fm.getAscent());
		g2d.dispose();

		drawImage(x, y, img);
	}

	public int getWidth() {
		return G15.G15_LCD_WIDTH;
	}


	public int getHeight() {
		return G15.G15_LCD_HEIGHT;
	}

	public void render() {
		g15.renderPixels(positions);
	}

	public void drawImage(int x0, int y0, BufferedImage img) {
		int imageWidth = img.getWidth();
		int lastColumn = x0 + imageWidth;
		if (lastColumn > getWidth()) {
			lastColumn = getWidth();
		}

		int imageHeight = img.getHeight();
		int lastRow = y0 + imageHeight;
		if (lastRow > getHeight()) {
			lastRow = getHeight();
		}

		for (int y = y0; y < lastRow; y++) {
			for (int x = x0; x < lastColumn; x++) {
				Color color = new Color(img.getRGB(x - x0, y - y0), true);
				if (color.getAlpha() < (255 / 2)) {
					continue;
				}
				int redD = color.getRed();
				int greenD = color.getGreen();
				int blueD = color.getBlue();
				double bkDistance = Math.sqrt(redD * redD + greenD * greenD + blueD * blueD);

				redD = 255 - redD;
				greenD = 255 - greenD;
				blueD = 255 - blueD;
				double whiteDistance = Math.sqrt(redD * redD + greenD * greenD + blueD * blueD);

				setPixel(x, y, whiteDistance > bkDistance);
			}
		}
	}
}