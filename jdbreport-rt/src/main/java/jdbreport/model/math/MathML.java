/*
 * Created	04.05.2011
 * 
 * JDBReport Generator
 * 
 * Copyright (C) 2011-2014 Andrey Kholmanskih
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
 * @version	3.0 13.12.2014
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
			StringBuilder sb = new StringBuilder();
			char[] buf = new char[1024 * 32];
			int l;
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
