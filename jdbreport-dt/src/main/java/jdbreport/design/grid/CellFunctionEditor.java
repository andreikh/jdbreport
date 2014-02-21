/*
 * JDBReport Designer
 * 
 * Copyright (C) 2006-2011 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.design.grid;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JPanel;
import java.awt.HeadlessException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;

import javax.swing.JTextArea;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JToolBar;
import javax.swing.JSplitPane;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JTextField;

import jdbreport.design.model.CellFunctionObject;
import jdbreport.util.HelpDlg;
import jdbreport.util.Utils;

import and.properties.XMLProperties;

/**
 * @version 2.0 24.05.2011
 * @author Andrey Kholmanskih
 * 
 */
public class CellFunctionEditor extends JDialog {

	private static final long serialVersionUID = 1L;

	private static final String POS_X = "fe_pos_x"; //$NON-NLS-1$

	private static final String POS_Y = "fe_pos_y"; //$NON-NLS-1$

	private static final String SIZE_WIDTH = "fe_size_width"; //$NON-NLS-1$

	private static final String SIZE_HEIGHT = "fe_size_height"; //$NON-NLS-1$

	private static final String DIVIDER_HEIGHT = "fe_divider_height"; //$NON-NLS-1$

	private JPanel jContentPane = null;

	private JPanel functionPanel = null;

	private JTextArea functionText = null;

	private JPanel bottomPanel = null;

	private JTextArea beginTextPane = null;

	private JTextArea endTextPane = null;

	private JTextArea jTextArea = null;

	private transient CellFunctionObject cellFunction;

	private JToolBar jToolBar = null;

	private JButton okButton = null;

	private JButton compileButton = null;

	private JButton cancelButton = null;

	private JSplitPane jSplitPane = null;

	private JTextArea consoleText = null;

	private transient PrintStream err;

	private JScrollPane jScrollPane = null;

	private JScrollPane jScrollPane1 = null;

	private JPanel headerPanel = null;

	private JPanel namePanel = null;

	private JLabel jLabel = null;

	private JTextField nameField = null;

	private JLabel jLabel1 = null;

	private JButton helpButton;

	protected boolean ok;

	private XMLProperties properties;

	public CellFunctionEditor(JDialog owner, CellFunctionObject cellFunction,
			XMLProperties properties) throws HeadlessException {
		super(owner, true);
		this.cellFunction = cellFunction;
		this.properties = properties;
		initialize();
		initProperties();
		setTitle(Messages.getString("CellFunctionEditor.Cell_function") + cellFunction.getFunctionName()); //$NON-NLS-1$
		getNameField().setText(cellFunction.getFunctionName());
		getFunctionText().setText(cellFunction.getFunctionBody());
	}

	protected void initProperties() {
		Utils.screenCenter(this);
		Rectangle r = getBounds();
		r.x = properties.getInt(POS_X, r.x);
		r.y = properties.getInt(POS_Y, r.y);
		r.width = properties.getInt(SIZE_WIDTH, r.width);
		r.height = properties.getInt(SIZE_HEIGHT, r.height);
		setBounds(r);
		getJSplitPane().setDividerLocation(
				properties.getInt(DIVIDER_HEIGHT, 200));
	}

	protected void saveProperties() {
		Rectangle r = getBounds();
		properties.put(POS_X, r.x);
		properties.put(POS_Y, r.y);
		properties.put(SIZE_WIDTH, r.width);
		properties.put(SIZE_HEIGHT, r.height);
		properties.put(DIVIDER_HEIGHT, getJSplitPane().getDividerLocation());
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(500, 450);
		this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setContentPane(getJContentPane());
		this.setTitle(Messages
				.getString("CellFunctionEditor.Cell_function_title")); //$NON-NLS-1$
		this.addWindowListener(new WindowAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent
			 * )
			 */
			@Override
			public void windowClosed(WindowEvent e) {
				saveProperties();
			}

		});
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJToolBar(), java.awt.BorderLayout.NORTH);
			jContentPane.add(getJSplitPane(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes functionPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getFunctionPanel() {
		if (functionPanel == null) {
			functionPanel = new JPanel();
			functionPanel.setLayout(new BorderLayout());
			functionPanel.add(getHeaderPanel(), java.awt.BorderLayout.NORTH);
			functionPanel.add(getEndTextPane(), java.awt.BorderLayout.SOUTH);
			functionPanel.add(getJTextArea2(), java.awt.BorderLayout.WEST);
			functionPanel.add(getJScrollPane1(), java.awt.BorderLayout.CENTER);
		}
		return functionPanel;
	}

	/**
	 * This method initializes functionText
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getFunctionText() {
		if (functionText == null) {
			functionText = new JTextArea();
			functionText.setTabSize(2);
			functionText.setFont(getFont());
		}
		return functionText;
	}

	/**
	 * This method initializes bottomPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.setLayout(new BorderLayout());
			bottomPanel.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		}
		return bottomPanel;
	}

	/**
	 * This method initializes beginTextPane
	 * 
	 * @return javax.swing.JTextPane
	 */
	private JTextArea getBeginTextPane() {
		if (beginTextPane == null) {
			beginTextPane = new JTextArea();
			beginTextPane.setEditable(false);
			beginTextPane.setFont(getFont());
			beginTextPane.setBackground(java.awt.SystemColor.controlHighlight);
			beginTextPane
					.setText("public void run()  throws ReportException {"); //$NON-NLS-1$
		}
		return beginTextPane;
	}

	/**
	 * This method initializes endTextPane
	 * 
	 * @return javax.swing.JTextPane
	 */
	private JTextArea getEndTextPane() {
		if (endTextPane == null) {
			endTextPane = new JTextArea();
			endTextPane.setEditable(false);
			endTextPane.setBorder(null);
			endTextPane.setBackground(java.awt.SystemColor.controlHighlight);
			endTextPane.setText("  }\n}"); //$NON-NLS-1$
		}
		return endTextPane;
	}

	/**
	 * This method initializes jTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea2() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setEnabled(false);
			jTextArea.setBackground(java.awt.SystemColor.controlHighlight);
			jTextArea.setText("    "); //$NON-NLS-1$
		}
		return jTextArea;
	}

	protected boolean apply() {
		PrintStream oldErr = System.err;
		System.setErr(getErrStream());
		try {
			try {
				cellFunction.setFunctionName(getNameField().getText());
				cellFunction.setFunctionBody(getFunctionText().getText());
				cellFunction.doCompile();
				getConsoleText().setText(""); //$NON-NLS-1$
			} catch (Throwable ex) {
				ex.printStackTrace();
				return false;
			}
		} finally {
			err.close();
			err = null;
			System.setErr(oldErr);
			getConsoleText().setCaretPosition(0);
		}
		return true;
	}

	private PrintStream getErrStream() {
		if (err == null) {
			OutputStream errStream = new OutputStream() {
				StringBuffer buf = new StringBuffer();

				@Override
				public void write(int b) throws IOException {
					buf.append((char) b);
				}

				public void close() {
					getConsoleText().setText(buf.toString());
					buf.setLength(0);
				}

			};
			err = new PrintStream(errStream);
		}
		return err;
	}

	/**
	 * This method initializes jToolBar
	 * 
	 * @return javax.swing.JToolBar
	 */
	private JToolBar getJToolBar() {
		if (jToolBar == null) {
			jToolBar = new JToolBar();
			jToolBar.setFloatable(false);
			jToolBar.add(getOkButton());
			jToolBar.add(getCompileButton());
			jToolBar.add(getCancelButton());
			jToolBar.addSeparator();
			jToolBar.add(getHelpButton());
		}
		return jToolBar;
	}

	private JButton getHelpButton() {
		if (helpButton == null) {
			helpButton = new JButton();
			helpButton.setIcon(new ImageIcon(getClass().getResource(
					"/jdbreport/resources/help.gif"))); //$NON-NLS-1$
			helpButton.setToolTipText(Messages
					.getString("CellFunctionEditor.0")); //$NON-NLS-1$
			helpButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showHelp();
				}
			});
		}
		return helpButton;
	}

	protected void showHelp() {
		URL url = getClass().getResource(
				"/doc/jdbreport/design/model/CellFunction.html"); //$NON-NLS-1$
		new HelpDlg(this, url);
	}

	/**
	 * This method initializes okButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setIcon(new ImageIcon(getClass().getResource(
					"/jdbreport/resources/ok.gif")));//$NON-NLS-1$
			okButton.setToolTipText(Messages
					.getString("CellFunctionEditor.Save")); //$NON-NLS-1$
			okButton.addActionListener(new java.awt.event.ActionListener() {

				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (apply()) {
						ok = true;
						CellFunctionEditor.this.setVisible(false);
					}
				}
			});
		}
		return okButton;
	}

	public boolean isOk() {
		return ok;
	}

	/**
	 * This method initializes compileButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCompileButton() {
		if (compileButton == null) {
			compileButton = new JButton();
			compileButton.setIcon(new ImageIcon(getClass().getResource(
					"/jdbreport/design/resources/compile.gif"))); //$NON-NLS-1$
			compileButton.setToolTipText(Messages
					.getString("CellFunctionEditor.Compile")); //$NON-NLS-1$
			compileButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							apply();
						}

					});
		}
		return compileButton;
	}

	/**
	 * This method initializes cancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setIcon(new ImageIcon(getClass().getResource(
					"/jdbreport/resources/del.gif"))); //$NON-NLS-1$
			cancelButton.setToolTipText(Messages
					.getString("CellFunctionEditor.Close")); //$NON-NLS-1$
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					CellFunctionEditor.this.setVisible(false);
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
			jSplitPane.setDividerSize(6);
			jSplitPane.setDividerLocation(200);
			jSplitPane.setTopComponent(getFunctionPanel());
			jSplitPane.setBottomComponent(getBottomPanel());
		}
		return jSplitPane;
	}

	/**
	 * This method initializes consoleText
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getConsoleText() {
		if (consoleText == null) {
			consoleText = new JTextArea();
			consoleText.setFont(new Font("Monospaced", Font.PLAIN, 11));
		}
		return consoleText;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getConsoleText());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jScrollPane1
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getFunctionText());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes headerPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getHeaderPanel() {
		if (headerPanel == null) {
			headerPanel = new JPanel();
			headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
			headerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(
					0, 0, 0, 0));
			headerPanel.add(getNamePanel(), null);
			headerPanel.add(getBeginTextPane(), null);
		}
		return headerPanel;
	}

	/**
	 * This method initializes namePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getNamePanel() {
		if (namePanel == null) {
			jLabel1 = new JLabel();
			jLabel1.setText(" implements CellFunction {"); //$NON-NLS-1$
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(java.awt.FlowLayout.LEFT);
			jLabel = new JLabel();
			jLabel.setText("class "); //$NON-NLS-1$
			namePanel = new JPanel();
			namePanel.setBackground(java.awt.SystemColor.controlHighlight);
			namePanel.setLayout(flowLayout);
			namePanel.add(jLabel, null);
			namePanel.add(getNameField(), null);
			namePanel.add(jLabel1, null);
		}
		return namePanel;
	}

	/**
	 * This method initializes nameField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getNameField() {
		if (nameField == null) {
			nameField = new JTextField();
			nameField.setPreferredSize(new java.awt.Dimension(150, 20));
		}
		return nameField;
	}

}
