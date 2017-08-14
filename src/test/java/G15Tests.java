import java.io.IOException;

import org.junit.Test;

public class G15Tests {

	@Test
	public void sampleTest() throws IOException, InterruptedException {

		try (G15Lcd g15 = new G15Lcd()) {
			g15.setLCDBrightness(2);
			LcdPixels lcdPixels = g15.getLcdPixels();
			lcdPixels.resetPixels();
			lcdPixels.render();

			lcdPixels.writeText("Test çí", 0, 0, LcdPixels.ARIAL);
			lcdPixels.writeText("片仮名, カタカナ", 0, 20, LcdPixels.JACKEY);
			lcdPixels.render();
		}
	}
}