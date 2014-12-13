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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import jdbreport.model.CellStyle;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.DefaultFontMapper;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 3.0 13.12.2014
 * 
 */
public class ReportFontMapper extends DefaultFontMapper {

	private static java.util.List<String> fontPathList = new ArrayList<>();
	private static String defaultFont;
	private static String systemFontPath;
	
	public static void addFontPath(String path) {
		path = path.trim();
		if (fontPathList.indexOf(path) < 0 && path.length() > 0) {
			fontPathList.add(path);
		}
	}

	public static void setFontPaths(Collection<String> fontPaths) {
		fontPathList.clear();
		if (fontPaths != null) {
			fontPaths.forEach(jdbreport.model.io.pdf.itext2.ReportFontMapper::addFontPath);
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
		File files[] = file.listFiles();
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
				} catch (Exception ignored) {
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
