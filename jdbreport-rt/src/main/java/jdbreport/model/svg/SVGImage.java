/*
 * Created 13.12.2009
 *
 * JDBReport Generator
 *
 * Copyright (C) 2009-2014 Andrey Kholmanskih
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package jdbreport.model.svg;

import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.logging.Logger;

/**
 * @author Andrey Kholmanskih
 * @version 3.0 13.12.2014
 */
public class SVGImage {

    private static final Logger logger = Logger.getLogger(SVGImage.class.getName());

    private static Boolean enableSVG;

    public static boolean isEnableSVG() {
        if (enableSVG == null) {
            try {
                Class.forName("org.apache.batik.anim.dom.SAXSVGDocumentFactory");
                SVGValue.registerValue();
                enableSVG = true;
            } catch (Throwable e) {
                enableSVG = false;
            }
            logger.info("SVG " + (enableSVG ? "is supported" : "is not supported"));
        }

        return enableSVG;
    }

    private String xml;
    private BufferedImage image;
    private int iconWidth;
    private int iconHeight;
    private String imageFormat = "png";

    public SVGImage() {

    }

    public SVGImage(String xml) {
        this.xml = xml;
    }

    public SVGImage(File file) throws IOException {
        this(new FileReader(file));
    }

    public SVGImage(Reader reader) throws IOException {
        try {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[1024 * 32];
            int l;
            do {
                l = reader.read(buf);
                if (l > 0) {
                    sb.append(buf, 0, l);
                }
            } while (l > 0);
            xml = sb.toString();
        } finally {
            reader.close();
        }
    }

    public void setXML(String xml) {
        this.xml = xml;
        image = null;
    }

    public String getXML() {
        return xml;
    }

    public Image getImage() {
        return getImage(0, 0, "png");
    }

    public Image getImage(String format) {
        return getImage(0, 0, format);
    }

    public Document createDocument() throws IOException {
        String parser = org.apache.batik.util.XMLResourceDescriptor
                .getXMLParserClassName();
        org.apache.batik.anim.dom.SAXSVGDocumentFactory f = new org.apache.batik.anim.dom.SAXSVGDocumentFactory(
                parser);
        StringReader reader = new StringReader(xml);
        return f.createDocument(null, reader);
    }

    public Image getImage(int width, int height, String format) {
        if (xml == null)
            return null;
        if (image == null || width != iconWidth || height != iconHeight || !imageFormat.equals(format)) {

            if (format != null) {
                imageFormat = format;
            }

            String parser = org.apache.batik.util.XMLResourceDescriptor
                    .getXMLParserClassName();
            org.apache.batik.anim.dom.SAXSVGDocumentFactory f = new org.apache.batik.anim.dom.SAXSVGDocumentFactory(
                    parser);
            try {
                StringReader reader = new StringReader(xml);
                Document document = f.createDocument(null, reader);

                iconWidth = width;
                iconHeight = height;

                org.apache.batik.transcoder.image.ImageTranscoder t;
                switch (imageFormat) {
                    case "jpg":
                    case "jpeg":
                        t = new org.apache.batik.transcoder.image.JPEGTranscoder();
                        break;
                    case "tiff":
                        t = new org.apache.batik.transcoder.image.TIFFTranscoder();
                        break;
                    default:
                        t = new org.apache.batik.transcoder.image.PNGTranscoder();
                        break;
                }

                if (width > 0) {
                    t
                            .addTranscodingHint(
                                    org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_WIDTH,
                                    (float) width);
                }
                if (height > 0) {
                    t
                            .addTranscodingHint(
                                    org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_HEIGHT,
                                    (float) height);
                }

                org.apache.batik.transcoder.TranscoderInput input = new org.apache.batik.transcoder.TranscoderInput(
                        document);
                ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                org.apache.batik.transcoder.TranscoderOutput output = new org.apache.batik.transcoder.TranscoderOutput(
                        ostream);

                t.transcode(input, output);
                ByteArrayInputStream is = new ByteArrayInputStream(ostream
                        .toByteArray());
                image = ImageIO.read(is);
                is.close();
                ostream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return image;
    }
}
