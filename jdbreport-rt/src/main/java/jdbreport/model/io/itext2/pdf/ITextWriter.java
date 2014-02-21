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
package jdbreport.model.io.itext2.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.JTextComponent;

import com.lowagie.text.Element;

import jdbreport.grid.JReportGrid.HTMLReportRenderer;
import jdbreport.model.CellStyle;
import jdbreport.model.ReportBook;
import jdbreport.model.io.ReportWriter;
import jdbreport.model.io.SaveReportException;
import jdbreport.model.io.itext.pdf.Messages;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 2.0 13.04.2011
 * 
 */
public abstract class ITextWriter implements ReportWriter {

	private JTextComponent htmlReportRenderer;
	private static ReportFontMapper fontMapper;
	Map<Object, com.lowagie.text.Font> fonts = new HashMap<Object, com.lowagie.text.Font>();
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
