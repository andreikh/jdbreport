/**
 * Created	15.10.2010
 *
 */
package jdbreport.model;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 1.0 28.10.2010
 */
public class PictureFactory {

	private static Boolean enableSVG;

	public static boolean isEnableSVG() {
		if (enableSVG == null) {
			try {
				Class.forName("org.apache.batik.dom.svg.SAXSVGDocumentFactory");
				enableSVG = true;
			} catch (Throwable e) {
				enableSVG = false;
			}
		}
		return enableSVG;
	}

	public static Picture createPicture(String format) {
		if (format != null) {
			if (format.equalsIgnoreCase("svg")) {
				if (isEnableSVG()) {
					return new PictureSVG(format);
				} else {
					return null;
				}
			} else if (format.equalsIgnoreCase("wmf")) {
				if (isEnableSVG()) {
					return new PictureWMF(format);
				} else {
					return null;
				}
			}
		}
		return new Picture(format);
	}
}
