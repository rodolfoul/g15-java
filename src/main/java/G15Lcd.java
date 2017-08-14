import java.io.Closeable;
import java.io.IOException;

import com.sun.jna.Native;

public class G15Lcd implements Closeable {
	private static G15 g15 = Native.loadLibrary("g15", G15.class);
	private final LcdPixels lcdPixels = new LcdPixels(this);

	public LcdPixels getLcdPixels() {
		return lcdPixels;
	}

	public G15Lcd() {
		g15.libg15Debug(1);
		int init = g15.initLibG15();

		if (init != G15.G15_NO_ERROR) {
			throw new IllegalStateException("Could not initialize libg15");
		}
	}

	/**
	 * accepts 0, 1 or 2
	 */
	public int setLCDContrast(int level) {
		return g15.setLCDContrast(level);
	}

	int renderPixels(byte[] data) {
		return g15.writePixmapToLCD(data);
	}

	/**
	 * accepts 0, 1 or 2
	 *
	 * @param level
	 * @return
	 */
	int setLCDBrightness(int level) {
		return g15.setLCDBrightness(level);
	}

	@Override
	public void close() throws IOException {
		g15.exitLibG15();
	}
}