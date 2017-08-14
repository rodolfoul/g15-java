import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface G15 extends Library {

	int G15_NO_ERROR = 0;
	int G15_ERROR_OPENING_USB_DEVICE = 1;
	int G15_ERROR_WRITING_PIXMAP = 2;
	int G15_ERROR_TIMEOUT = 3;
	int G15_ERROR_READING_USB_DEVICE = 4;
	int G15_ERROR_TRY_AGAIN = 5;
	int G15_ERROR_WRITING_BUFFER = 6;
	int G15_ERROR_UNSUPPORTE = 7;

	int G15_LCD_WIDTH = 160;
	int G15_LCD_HEIGHT = 43;

	int G15_BUFFER_LEN = 0x03e0;

	/**
	 * this one return G15_NO_ERROR on success, something
	 * else otherwise (for instance G15_ERROR_OPENING_USB_DEVICE
	 *
	 * @return
	 */
	int initLibG15();

	/**
	 * re-initialise a previously unplugged keyboard ie ENODEV was returned at some point
	 */
	int re_initLibG15();

	int exitLibG15();

	int writePixmapToLCD(byte[] data);

	/**
	 * accepts 0, 1 or 2
	 * @param level
	 * @return
	 */
	int setLCDContrast(int level);

	/**
	 * accepts 0, 1 or 2
	 * @param level
	 * @return
	 */
	int setLCDBrightness(int level);

	/**
	 * enable or disable debugging
	 */
	void libg15Debug(int option);

	/**
	 * Please be warned
	 * the g15 sends two different usb msgs for each key press
	 * but only one of these two is used here. Since we do not want to wait
	 * longer than timeout we will return on any msg recieved. in the good
	 * case you will get G15_NO_ERROR and ORd keys in pressed_keys
	 * in the bad case you will get G15_ERROR_TRY_AGAIN -> try again
	 * <p>
	 * int getPressedKeys(unsigned int *pressed_keys, unsigned int timeout);
	 */

	int getPressedKeys(Pointer pressed_keys, int timeout);
}