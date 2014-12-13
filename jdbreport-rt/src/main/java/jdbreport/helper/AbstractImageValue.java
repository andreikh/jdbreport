/*
 * Created	13.02.2009
 *
/*
 * AbstractImageValue.java
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
package jdbreport.helper;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import jdbreport.model.AbstractValue;
import jdbreport.model.ReportModel;
import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.ResourceWriter;
import jdbreport.model.io.SaveReportException;

import jdbreport.util.xml.XMLCoder;
import jdbreport.util.xml.XMLParser;
import jdbreport.util.xml.XMLReaderHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Andrey Kholmanskih
 * @version 3.0 12.12.2014
 */
public abstract class AbstractImageValue<E> extends AbstractValue<E> {

    private static final long serialVersionUID = 1L;
    private String format;
    protected ImageIcon icon;

    public abstract void setValue(Image image);

    public Icon getIcon() {
        return icon;
    }

    public Image getImage() {
        return icon.getImage();
    }

    public void setImageFormat(String format) {
        this.format = format;
    }

    public String getImageFormat() {
        return format;
    }

    public boolean write(PrintWriter writer, ReportModel model, int row, int column) {
        RenderedImage image;
        Image img = getImage();
        if (img instanceof RenderedImage)
            image = (RenderedImage) img;
        else {
            Icon icon = getIcon();
            image = new BufferedImage(getIcon().getIconWidth(), getIcon()
                    .getIconHeight(), BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = ((BufferedImage) image).createGraphics();
            icon.paintIcon(null, g, 0, 0);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            String format = getImageFormat();
            if (format == null || !checkImageWriterFormat(format)) {
                format = "png";
            }
            if (ImageIO.write(image, format, stream)) {
                String iconStr = new String(XMLCoder.base64Encode(stream
                        .toByteArray()));
                String params = "type=\"" + format + "\" ";
                writer.print("<value class=\"");
                writer.print(getClass().getName());
                writer.println("\">");
                writer.print("<img " + params + " >");
                writer.print(iconStr);
                writer.println("</img>");
                writer.println("</value>");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean checkImageWriterFormat(String format) {
        String[] formats = ImageIO.getWriterFormatNames();
        for (String format1 : formats) {
            if (format.equals(format1)) {
                return true;
            }
        }
        return false;
    }

    public boolean startElement(String name, Attributes attributes)
            throws SAXException {
        return name.equals("img");
    }

    public void endElement(String name, StringBuffer value) throws SAXException {
        if (name.equals("img")) {
            if (value.length() > 0) {
                try {
                    Image image = ImageIO.read(new ByteArrayInputStream(
                            XMLCoder.base64Decode(value.toString().trim()
                                    .getBytes())));
                    setValue(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        if (name.equals("value")) {
            getHandler().popHandler(name);
            handler = null;
        }
    }

    public Image getAsImage() {
        return getImage();
    }

    public boolean write(PrintWriter writer, ReportModel model, int row,
                         int column, ResourceWriter resourceWriter, String format) throws SaveReportException {
        return write(writer, model, row, column, format);
    }

    public XMLParser createParser(XMLReaderHandler handler,
                                  ResourceReader resourceReader) {
        return createParser(handler);
    }

}
