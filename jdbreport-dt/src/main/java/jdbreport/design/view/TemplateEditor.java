/*
 * JDBReport Designer
 * 
 * Copyright (C) 2006-2014 Andrey Kholmanskih
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
