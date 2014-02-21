/*
 * Created	04.05.2011
 * 
 * JDBReport Generator
 * 
 * Copyright (C) 2011 Andrey Kholmanskih. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, write to the 
 *
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
 */
package jdbreport.model.math;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Reader;

import javax.swing.JComponent;

import jdbreport.model.CellStyle;


/**
 * @author Andrey Kholmanskih
 *
 * @version	1.0 05.05.2011
 */
public class MathML {

	private net.sourceforge.jeuclid.swing.JMathComponent math;  
	
	
	public MathML() {
	}

	public MathML(String xml) {
		setXML(xml);
	}

	public MathML(Reader reader) throws IOException {
		try {
			StringBuffer sb = new StringBuffer();
			char[] buf = new char[1024 * 32];
			int l = 0;
			do {
				l = reader.read(buf);
				if (l > 0) {
					sb.append(buf, 0, l);
				}
			} while (l > 0);
			setXML(sb.toString());
		} finally {
			reader.close();
		}
	}

	public void setXML(String xml)  {
		if (math == null) {
			math = new net.sourceforge.jeuclid.swing.JMathComponent();
		}
		math.setContent(xml.trim());
	}

	public String getXML() {
		if (math != null) {
			return math.getContent();
		}
		return null;
	}

	public JComponent getComponent() {
		return math;
	}

	@SuppressWarnings("deprecation")
	void setStyle(CellStyle style) {
		if (math != null) {
			math.setHorizontalAlignment(style.getHorizontalAlignment());
			math.setVerticalAlignment(style.getVerticalAlignment());
			math.setFont(new Font(style.getFamily(), style.getStyle(), style.getSize()));
			math.setForeground(style.getForegroundColor());
			math.setBackground(style.getBackground());
		}
	}

	public BufferedImage getImage() throws IOException {
		net.sourceforge.jeuclid.converter.Converter converter = net.sourceforge.jeuclid.converter.Converter.getInstance();
		return converter.render(math.getDocument(), math.getParameters());
	}
	

}
