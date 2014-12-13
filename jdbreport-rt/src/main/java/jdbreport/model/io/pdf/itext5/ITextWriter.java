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
package jdbreport.model.io.pdf.itext5;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.JTextComponent;

import com.itextpdf.text.Element; 

import jdbreport.grid.JReportGrid.HTMLReportRenderer;
import jdbreport.model.CellStyle;
import jdbreport.model.ReportBook;
import jdbreport.model.io.ReportWriter;
import jdbreport.model.io.SaveReportException;
import jdbreport.model.io.pdf.Messages;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 2.0 14.04.2011
 * 
 */
public abstract class ITextWriter implements ReportWriter {

	private JTextComponent htmlReportRenderer;
	private static ReportFontMapper fontMapper;
	Map<Object, com.itextpdf.text.Font> fonts = new HashMap<Object, com.itextpdf.text.Font>();
	List<CellStyle> textStyles = new ArrayList<CellStyle>();

	/**
	 * @since 2.0
	 */
	synchronized public static void initFontMapper() {
		ReportFontMapper mapper = new ReportFontMapper();
		fontMapper = mapper;
	}
	
	protected ReportFontMapper getFontMapper() {
		if (fontMapper == null) {
			initFontMapper();
		}
		return fontMapper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.io.ReportWriter#save(java.io.Writer,
	 * jdbreport.model.ReportBook)
	 */
	public void save(Writer writer, ReportBook reportBook)
			throws SaveReportException {
		throw new SaveReportException(Messages.getString("ITextWriter.not_supported")); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.io.ReportWriter#save(java.io.File,
	 * jdbreport.model.ReportBook)
	 */
	public void save(File file, ReportBook reportBook)
			throws SaveReportException {
		try {
			save(new FileOutputStream(file), reportBook);
		} catch (IOException e) {
			throw new SaveReportException(e);
		}
	}

	protected int toPdfHAlignment(int horizontalAlignment) {
		switch (horizontalAlignment) {
		case CellStyle.LEFT:
			return Element.ALIGN_LEFT;
		case CellStyle.RIGHT:
			return Element.ALIGN_RIGHT;
		case CellStyle.CENTER:
			return Element.ALIGN_CENTER;
		case CellStyle.JUSTIFY:
			return Element.ALIGN_JUSTIFIED;
		}
		return Element.ALIGN_LEFT;
	}

	protected int toPdfVAlignment(int verticalAlignment) {
		switch (verticalAlignment) {
		case CellStyle.TOP:
			return Element.ALIGN_TOP;
		case CellStyle.BOTTOM:
			return Element.ALIGN_BOTTOM;
		case CellStyle.CENTER:
			return Element.ALIGN_CENTER;
		}
		return Element.ALIGN_TOP;
	}


	protected JTextComponent getHTMLReportRenderer() {
		if (htmlReportRenderer == null) {
			htmlReportRenderer = new HTMLReportRenderer();
		}
		return htmlReportRenderer;
	}


	protected int roundAngle(int angle) {
		if (angle > 45 && angle <= 135) {
			return 90;
		} else if (angle > 135 && angle <= 225) {
			return 180;
		} else if (angle > 225 && angle <= 315) {
			return 270;
		}
		return 0;
	}

	public String write(String fileName, Object resource) throws SaveReportException {
		throw new SaveReportException(Messages.getString("ITextWriter.not_supported"));  //$NON-NLS-1$
	}

}
