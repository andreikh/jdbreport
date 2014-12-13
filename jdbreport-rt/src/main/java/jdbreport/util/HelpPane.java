/*
 * JDBReport Generator
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
package jdbreport.util;

import java.awt.GridBagLayout;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URL;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class HelpPane extends JPanel {

	private static final long serialVersionUID = 1L;

	static final String WINDOW_STATE = "help_window_state"; //$NON-NLS-1$

	static final String POS_X = "help_pos_x"; //$NON-NLS-1$

	static final String POS_Y = "help_pos_y"; //$NON-NLS-1$

	static final String SIZE_WIDTH = "help_size_width"; //$NON-NLS-1$

	static final String SIZE_HEIGHT = "help_size_height"; //$NON-NLS-1$

	private JScrollPane scrollPane;
	private JEditorPane editorPane;
	private JPanel statusPanel;
	private JPanel topPanel;

	/**
	 * This is the default constructor
	 */
	public HelpPane() {
		super();
		initialize();
	}

	public void setUrl(URL url) {
		try {
			if (url == null)
				throw new IOException(Messages.getString("HelpPane.0")); //$NON-NLS-1$
			getEditorPane().setPage(url);
		} catch (IOException e) {
			e.printStackTrace();
			editorPane.setText(e.getMessage());
		}
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLayout(new BorderLayout());
		add(getTopPanel(), BorderLayout.NORTH);
		add(getStatusPanel(), BorderLayout.SOUTH);
		add(getScrollPane(), BorderLayout.CENTER);

	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getEditorPane());
		}
		return scrollPane;
	}

	/**
	 * This method initializes topPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new JPanel();
			topPanel.setLayout(new GridBagLayout());
		}
		return topPanel;
	}

	/**
	 * This method initializes statusPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getStatusPanel() {
		if (statusPanel == null) {
			statusPanel = new JPanel();
			statusPanel.setLayout(new GridBagLayout());
		}
		return statusPanel;
	}

	/**
	 * This method initializes editorPane
	 * 
	 * @return javax.swing.JEditorPane
	 */
	private JEditorPane getEditorPane() {
		if (editorPane == null) {
			editorPane = new JEditorPane();
			editorPane.setContentType("text/html"); //$NON-NLS-1$
			editorPane.setEditable(false);
			editorPane.addHyperlinkListener(createHyperLinkListener());
		}
		return editorPane;
	}

	public HyperlinkListener createHyperLinkListener() {

		return new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (e instanceof HTMLFrameHyperlinkEvent) {
						((HTMLDocument) editorPane.getDocument())
								.processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent) e);
					} else {
						try {
							editorPane.setPage(e.getURL());
						} catch (IOException ioe) {
							Utils.showError("IOE: " + ioe); //$NON-NLS-1$
						}
					}
				}
			}

		};
	}

}
