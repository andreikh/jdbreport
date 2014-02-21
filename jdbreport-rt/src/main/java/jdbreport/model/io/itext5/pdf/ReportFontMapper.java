/*
 * Copyright (C) 2009-2011 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model.io.itext5.pdf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import jdbreport.model.CellStyle;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.DefaultFontMapper;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 2.0 19.04.2011
 * 
 */
public class ReportFontMapper extends DefaultFontMapper {

	private static java.util.List<String> fontPathList = new ArrayList<String>();
	private static String defaultFont;
	private static String systemFontPath;
	
	public static void addFontPath(String path) {
		path = path.trim();
		if (fontPathList.indexOf(path) < 0 && path != null && path.length() > 0) {
			fontPathList.add(path);
		}
	}

	public static void setFontPaths(Collection<String> fontPaths) {
		fontPathList.clear();
		if (fontPaths != null) {
			for (String path : fontPaths) {
				addFontPath(path);
			}
		}
	}

	private static String getSystemFontPath() {
		String osName = System.getProperties().getProperty("os.name");
		String path;
		if (osName.startsWith("Windows")) {
			path = System.getenv("SystemRoot");
			if (path != null) {
				path += "\\fonts\\";
			} else {
				path = System.getenv("windir");
				if (path != null) {
					path += "\\fonts\\";
				} else
					path = "c:\\windows\\fonts\\";
			}
		}else {
			path = "/usr/share/fonts/";
		}
		File file = new File(path);
		if (file.exists()) {
			return path;
		}
		return "";
	}
	
	public static Collection<String> getFontPaths() {
		if (fontPathList.size() == 0){
			if (systemFontPath == null) {
				systemFontPath = getSystemFontPath();
			}
			if (systemFontPath.length() > 0) {
				fontPathList.add(systemFontPath);
			}
		}
		return Collections.unmodifiableCollection(fontPathList);
	}


	public static String getDefaultFont() {
		return defaultFont;
	}
	
	public static void setDefaultFont(String fontName) {
		defaultFont = fontName != null && fontName.length() > 0 ? fontName : null;
	}
	
	public ReportFontMapper() {
		insertDirectory("fonts/");
		for (String path : getFontPaths()) {
			insertDirectory(path);
		}
	}
	
	public com.itextpdf.text.Font styleToPdf(CellStyle style) {
		try {
			java.awt.Font font = new java.awt.Font(style.getFamily(), style.getStyle(), style.getSize());
			BaseFont baseFont = awtToPdf(font);
			if (baseFont != null) {
				return new com.itextpdf.text.Font(baseFont, (float)style.getSize(), style.getStyle(), new BaseColor(style.getForegroundColor()));
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
		} catch (DocumentException e) {
			throw new ExceptionConverter(e);
		} catch (IOException e) {
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
		File files[] = file.listFiles();
		if (files == null)
			return 0;
		int count = 0;
		for (int k = 0; k < files.length; ++k) {
			file = files[k];
			if (file.isDirectory()) {
				count += insertDirectory(file.getPath());
			} else {
				String name = file.getPath().toLowerCase();
				try {
					if (name.endsWith(".ttf") || name.endsWith(".otf")
							|| name.endsWith(".afm")) {
						Object allNames[] = BaseFont.getAllFontNames(file
								.getPath(), BaseFont.IDENTITY_H, null);
						insertNames(allNames, file.getPath());
						++count;
					} else if (name.endsWith(".ttc")) {
						String ttcs[] = BaseFont.enumerateTTCNames(file
								.getPath());
						for (int j = 0; j < ttcs.length; ++j) {
							String nt = file.getPath() + "," + j;
							Object allNames[] = BaseFont.getAllFontNames(nt,
									BaseFont.IDENTITY_H, null);
							insertNames(allNames, nt);
						}
						++count;
					}
				}

				catch (Exception e) {
				}
			}
		}
		return count;
	}

	private BaseFont getDefaultBaseFont() throws DocumentException, IOException {
		if (getDefaultFont() != null) {
			return BaseFont.createFont(getDefaultFont(), BaseFont.IDENTITY_H,
					BaseFont.EMBEDDED);
		}
		return null;
	}


}
