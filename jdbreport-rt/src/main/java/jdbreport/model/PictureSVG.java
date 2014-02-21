/**
 * Created	15.10.2010
 *
 */
package jdbreport.model;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.AbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.wmf.tosvg.WMFPainter;
import org.apache.batik.transcoder.wmf.tosvg.WMFRecordStore;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.XMLFilter;
import org.apache.batik.util.SVGConstants;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 1.0 15.10.2010
 */
public class PictureSVG extends Picture {

	public static final String TIFF = "tiff";
	public static final String SVG = "svg";
	public static final String WMF = "wmf";
	public static final int WMF_TRANSCODER_ERROR_BASE = 0xff00;
	public static final int ERROR_NULL_INPUT = WMF_TRANSCODER_ERROR_BASE + 0;
	public static final int ERROR_INCOMPATIBLE_INPUT_TYPE = WMF_TRANSCODER_ERROR_BASE + 1;
	public static final int ERROR_INCOMPATIBLE_OUTPUT_TYPE = WMF_TRANSCODER_ERROR_BASE + 2;

	/**
	 * 
	 */
	public PictureSVG(String format) {
		super(format);
	}

	/**
	 * @param buf
	 */
	public PictureSVG(byte[] buf) {
		this(buf, SVG);
	}

	/**
	 * @param buf
	 * @param format
	 */
	public PictureSVG(byte[] buf, String format) {
		super(buf, format);
/*		if (format.equals(WMF)) {
			try {
				wmfToSvg(buf);
			} catch (TranscoderException e) {
				e.printStackTrace();
				buf = null;
			}
		}*/
	}

	public byte[] getBuf() {
		return buf;
	}

	@Override
	public void setBuf(byte[] buf) {
		super.setBuf(buf);
/*		if (format.equals(WMF)) {
			try {
				wmfToSvg(buf);
			} catch (TranscoderException e) {
				buf = null;
				e.printStackTrace();
			}
		}*/
	}

	public String getXML() {
		return new String(getBuf());
	}

	public Document createDocument() throws IOException {
		String parser = org.apache.batik.util.XMLResourceDescriptor
				.getXMLParserClassName();
		org.apache.batik.dom.svg.SAXSVGDocumentFactory f = new org.apache.batik.dom.svg.SAXSVGDocumentFactory(
				parser);
		return f.createDocument(null, new ByteArrayInputStream(buf));
	}


	protected ImageIcon createIcon(int w, int h) {
		if (buf == null)
			return null;
		icon = null;
		Image image = createImage(w, h, PNG);
		icon = new ImageIcon(image);
		iconWidth = w;
		iconHeight = h;
		return icon;
	}

	public BufferedImage createImage() {
		return createImage(0, 0, PNG);
	}
	
	public BufferedImage createImage(int width, int height, String format) {

		BufferedImage image = null;

		String parser = org.apache.batik.util.XMLResourceDescriptor
				.getXMLParserClassName();
		org.apache.batik.dom.svg.SAXSVGDocumentFactory f = new org.apache.batik.dom.svg.SAXSVGDocumentFactory(
				parser);
		try {
			Document document = f.createDocument(null,
					new ByteArrayInputStream(buf));


			org.apache.batik.transcoder.image.ImageTranscoder t;
			if (JPG.equals(format) || JPEG.equals(format)) {
				t = new org.apache.batik.transcoder.image.JPEGTranscoder();
			} else if (TIFF.equals(format)) {
				t = new org.apache.batik.transcoder.image.TIFFTranscoder();
			} else {
				t = new org.apache.batik.transcoder.image.PNGTranscoder();
			}

			if (width > 0) {
				t.addTranscodingHint(
						org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_WIDTH,
						new Float(width));
			}
			if (height > 0) {
				t.addTranscodingHint(
						org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_HEIGHT,
						new Float(height));
			}

			org.apache.batik.transcoder.TranscoderInput input = new org.apache.batik.transcoder.TranscoderInput(
					document);
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			org.apache.batik.transcoder.TranscoderOutput output = new org.apache.batik.transcoder.TranscoderOutput(
					ostream);

			t.transcode(input, output);
			ByteArrayInputStream is = new ByteArrayInputStream(
					ostream.toByteArray());
			image = ImageIO.read(is);
			is.close();
			ostream.close();
			iconWidth = image.getWidth();
			iconHeight = image.getHeight();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return image;
	}

	public void wmfToSvg(byte[] wmfBuf) throws TranscoderException {
        WMFTranscoder transcoder = new WMFTranscoder();
        TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(wmfBuf));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(out);
        transcoder.transcode(input, output);
        buf = out.toByteArray();
        format = SVG;
	}
	
	private static class WMFTranscoder extends AbstractTranscoder implements
			SVGConstants {
		
		
		public void transcode(TranscoderInput input, TranscoderOutput output)
				throws TranscoderException {
			DataInputStream is = getCompatibleInput(input);

			WMFRecordStore currentStore = new WMFRecordStore();
			try {
				currentStore.read(is);
			} catch (IOException e) {
				throw new TranscoderException(e);
			}

			WMFPainter painter = new WMFPainter(currentStore, 1);

			DOMImplementation domImpl = SVGDOMImplementation
					.getDOMImplementation();
			Document doc = domImpl.createDocument(
					SVGConstants.SVG_NAMESPACE_URI, SVGConstants.SVG_SVG_TAG,
					null);

			SVGGraphics2D svgGenerator = new SVGGraphics2D(doc);

			painter.paint(svgGenerator);

			int vpX = (int) currentStore.getVpX();
			int vpY = (int) currentStore.getVpY();
			int vpW = currentStore.getVpW();
			int vpH = currentStore.getVpH();
			svgGenerator.setSVGCanvasSize(new Dimension(vpW, vpH));

			Element svgRoot = svgGenerator.getRoot();
			svgRoot.setAttributeNS(null, SVG_VIEW_BOX_ATTRIBUTE, "" + vpX + " "
					+ vpY + " " + vpW + " " + vpH);

			writeSVGToOutput(svgGenerator, svgRoot, output);
		}

		/**
		 * Writes the SVG content held by the svgGenerator to the
		 * <tt>TranscoderOutput</tt>.
		 */
		private void writeSVGToOutput(SVGGraphics2D svgGenerator,
				Element svgRoot, TranscoderOutput output)
				throws TranscoderException {
			XMLFilter xmlFilter = output.getXMLFilter();
			if (xmlFilter != null) {
				handler.fatalError(new TranscoderException(""
						+ ERROR_INCOMPATIBLE_OUTPUT_TYPE));
			}

			Document doc = output.getDocument();
			if (doc != null) {
				handler.fatalError(new TranscoderException(""
						+ ERROR_INCOMPATIBLE_OUTPUT_TYPE));
			}

			try {
				OutputStream os = output.getOutputStream();
				if (os != null) {
					svgGenerator.stream(svgRoot, new OutputStreamWriter(os));
					return;
				}

				Writer wr = output.getWriter();
				if (wr != null) {
					svgGenerator.stream(svgRoot, wr);
					return;
				}

				String uri = output.getURI();
				if (uri != null) {
					try {
						URL url = new URL(uri);
						URLConnection urlCnx = url.openConnection();
						os = urlCnx.getOutputStream();
						svgGenerator
								.stream(svgRoot, new OutputStreamWriter(os));
						return;
					} catch (MalformedURLException e) {
						handler.fatalError(new TranscoderException(e));
					} catch (IOException e) {
						handler.fatalError(new TranscoderException(e));
					}
				}
			} catch (IOException e) {
				throw new TranscoderException(e);
			}

			throw new TranscoderException("" + ERROR_INCOMPATIBLE_OUTPUT_TYPE);

		}

		/**
		 * Checks that the input is one of URI or an <tt>InputStream</tt>
		 * returns it as a DataInputStream
		 */
		private DataInputStream getCompatibleInput(TranscoderInput input)
				throws TranscoderException {
			if (input == null) {
				handler.fatalError(new TranscoderException(""
						+ ERROR_NULL_INPUT));
			}

			InputStream in = input.getInputStream();
			if (in != null) {
				return new DataInputStream(new BufferedInputStream(in));
			}

			String uri = input.getURI();
			if (uri != null) {
				try {
					URL url = new URL(uri);
					in = url.openStream();
					return new DataInputStream(new BufferedInputStream(in));
				} catch (MalformedURLException e) {
					handler.fatalError(new TranscoderException(e));
				} catch (IOException e) {
					handler.fatalError(new TranscoderException(e));
				}
			}

			handler.fatalError(new TranscoderException(""
					+ ERROR_INCOMPATIBLE_INPUT_TYPE));
			return null;
		}
	}
}
