import java.io.IOException;

import org.junit.Test;

public class G15Tests {

	@Test
	public void sampleTest() throws IOException, InterruptedException {

		try (G15Lcd g15 = new G15Lcd()) {
			g15.setLCDBrightness(2);
			G15Lcd.LcdPixels lcdPixels = g15.getLcdPixels();
			lcdPixels.resetPixels();
			lcdPixels.render();

			lcdPixels.writeText("片仮名, カタカナ Test");
			lcdPixels.render();
		}
	}
}