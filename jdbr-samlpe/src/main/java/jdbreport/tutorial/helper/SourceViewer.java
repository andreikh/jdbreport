package jdbreport.tutorial.helper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FontMetrics;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import java.io.*;

import javax.swing.JEditorPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

import jdbreport.util.Utils;

public class SourceViewer extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JTextPane sourcePane = null;

	private String sourcePath;

	private JScrollPane scrollPane;

	/**
	 * This is the default constructor
	 */
	public SourceViewer(String sourcePath) {
		super();
		this.sourcePath = sourcePath;
		initialize();
		setVisible(true);
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(800, 555);
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
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
			jContentPane.add(getScrollPane(), BorderLayout.CENTER);  // Generated
		}
		return jContentPane;
	}

	private Component getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getSourcePane());
		}

		return scrollPane;
	}

	/**
	 * This method initializes sourcePane	
	 * 	
	 * @return javax.swing.JEditorPane	
	 */
	private JEditorPane getSourcePane() {
		if (sourcePane == null) {
			sourcePane = new JTextPane();
			setTabs(sourcePane, 4);
			sourcePane.setEditable(false);
			try {
				Reader reader = new InputStreamReader(getClass().getResourceAsStream(sourcePath), "UTF-8");
				char[] cbuf = new char[1024 * 10];
				int l = reader.read(cbuf);
				String source = new String(cbuf, 0, l);
				sourcePane.setText(source);
				sourcePane.select(0, 0);
			} catch (IOException e) {
				Utils.showError(e);
			}
		}
		return sourcePane;
	}

	public void setTabs( JTextPane textPane, int charactersPerTab)
	{
		FontMetrics fm = textPane.getFontMetrics( textPane.getFont() );
		int charWidth = fm.charWidth( 'w' );
		int tabWidth = charWidth * charactersPerTab;
 
		TabStop[] tabs = new TabStop[10];
 
		for (int j = 0; j < tabs.length; j++)
		{
			int tab = j + 1;
			tabs[j] = new TabStop( tab * tabWidth );
		}
 
		TabSet tabSet = new TabSet(tabs);
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setTabSet(attributes, tabSet);
		int length = textPane.getDocument().getLength();
		textPane.getStyledDocument().setParagraphAttributes(0, length, attributes, true);
	}	
	
}
