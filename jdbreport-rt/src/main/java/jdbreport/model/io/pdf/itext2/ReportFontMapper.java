/*
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
package jdbreport.model.io.pdf.itext2;

import java.io.File;
import java.io.IOException;

import jdbreport.model.CellStyle;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.DefaultFontMapper;
import jdbreport.model.io.pdf.ReportFont;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 3.0 13.12.2014
 * 
 */
public class ReportFontMapper extends DefaultFontMapper {

	public ReportFontMapper() {
		insertDirectory("fonts/");
		for (String path : ReportFont.getFontPaths()) {
			insertDirectory(path);
		}
	}
	
	public com.lowagie.text.Font styleToPdf(CellStyle style) {
		try {
			java.awt.Font font = new java.awt.Font(style.getFamily(), style.getStyle(), style.getSize());
			BaseFont baseFont = awtToPdf(font);
			if (baseFont != null) {
				return new com.lowagie.text.Font(baseFont, 0.0f + (float)style.getSize(), style.getStyle(), style.getForegroundColor());
			}
		} catch (Exception e) {
			throw new ExceptionConverter(e);
		}

		return null;
	}

	public BaseFont awtToPdf(String fontName) {
		try {
			BaseFontParameters p = getBaseFontParameters(fontName);
			if (p != null) {
				return BaseFont.createFont(p.fontName, BaseFont.IDENTITY_H,
						p.embedded, p.cached, p.ttfAfm, p.pfb);
			}
		} catch (DocumentException | IOException e) {
			throw new ExceptionConverter(e);
		}
		return null;
	}

	public BaseFont awtToPdf(java.awt.Font font) {
		BaseFont baseFont = awtToPdf(font.getFontName());
		if (baseFont == null) {
			try {
				baseFont = getDefaultBaseFont();
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}			
		if (baseFont == null) {
			baseFont = super.awtToPdf(font);
		}
		return baseFont;
	}

	public int insertDirectory(String dir) {
		File file = new File(dir);
		if (!file.exists() || !file.isDirectory())
			return 0;
		File[] files = file.listFiles();
		if (files == null)
			return 0;
		int count = 0;
		for (File file1 : files) {
			file = file1;
			if (file.isDirectory()) {
				count += insertDirectory(file.getPath());
			} else {
				String name = file.getPath().toLowerCase();
				try {
					if (name.endsWith(".ttf") || name.endsWith(".otf")
							|| name.endsWith(".afm")) {
						Object[] allNames = BaseFont.getAllFontNames(file
								.getPath(), BaseFont.IDENTITY_H, null);
						insertNames(allNames, file.getPath());
						++count;
					} else if (name.endsWith(".ttc")) {
						String[] ttcs = BaseFont.enumerateTTCNames(file
								.getPath());
						for (int j = 0; j < ttcs.length; ++j) {
							String nt = file.getPath() + "," + j;
							Object[] allNames = BaseFont.getAllFontNames(nt,
									BaseFont.IDENTITY_H, null);
							insertNames(allNames, nt);
						}
						++count;
					}
				} catch (Exception ignored) {
				}
			}
		}
		return count;
	}

	private BaseFont getDefaultBaseFont() throws DocumentException, IOException {
		if (ReportFont.getDefaultFont() != null) {
			return BaseFont.createFont(ReportFont.getDefaultFont(), BaseFont.IDENTITY_H,
					BaseFont.EMBEDDED);
		}
		return null;
	}


}
