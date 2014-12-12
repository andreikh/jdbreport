/*
 * JDBReport Designer
 * 
 * Copyright (C) 2006-2010 Andrey Kholmanskih. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, write to the 
 * 
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
 */
package jdbreport.design.view;

import java.io.File;
import javax.swing.WindowConstants;

import jdbreport.design.model.TemplateBook;
import jdbreport.util.Utils;
import jdbreport.view.DefaultErrorHandler;
import jdbreport.view.ReportEditor;
import jdbreport.view.ReportEditorPane;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class TemplateEditor extends ReportEditor {

	private static final long serialVersionUID = 1L;


	/**
	 * 
	 * @return the TemplateBook for this editor
	 */
	public TemplateBook getTemplateBook() {
		return (TemplateBook) getReportPane().getReportBook();
	}

	public TemplatePane getTemplatePane() {
		return (TemplatePane) getReportPane();
	}


	protected ReportEditorPane createClientPanel() {
		return new TemplatePane(properties);
	}

	protected String getLogoImage() {
		return "/jdbreport/design/resources/logo.png"; 
	}

	public static void main(String[] args) {
		Utils.errorHandler = DefaultErrorHandler.getInstance();
		TemplateEditor re = new TemplateEditor();
		re.getReportPane().changeLookAndFeel();
		re.setCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		if (args.length > 0) {
			File file = new File(args[0]);
			if (file.exists())
				re.open(new File(args[0]));
		}
		re.setVisible(true);
	}

}
