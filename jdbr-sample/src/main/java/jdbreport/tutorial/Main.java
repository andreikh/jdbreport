package jdbreport.tutorial;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import java.util.Vector;

import jdbreport.design.view.TemplateEditor;
import jdbreport.model.io.LoadReportException;
import jdbreport.tutorial.helper.SourceViewer;
import jdbreport.util.Utils;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel buttonPanel;

	private JList<SampleItem> tutorialList = null;

	private JButton runButton = null;

	private JButton templateButton = null;

	private JButton sourceButton = null;

	/**
	 * This is the default constructor
	 */
	public Main() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(300, 329);
		this.setLocation(100, 100);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setContentPane(getJContentPane());
		this.setTitle("JDBReport Sample");
		jdbreport.helper.ColorValue.registerValue();
		jdbreport.helper.ImageValue.registerValue();
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
			jContentPane.add(getTutorialList(), BorderLayout.CENTER);
			jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getRunButton(), null); 
			buttonPanel.add(getTemplateButton(), null); 
			buttonPanel.add(getSourceButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes tutorialList
	 * 
	 * @return javax.swing.JList
	 */
	private JList<SampleItem> getTutorialList() {
		if (tutorialList == null) {
			tutorialList = new JList<>();
			Vector<SampleItem> samples = new Vector<>();
			samples.add(new ExpressionReport());
			samples.add(new SimpleDBReport());
			samples.add(new IterableReport());
			samples.add(new CellFunctionReport());
			samples.add(new ImageColorReport());
			samples.add(new VariablesReport());
			samples.add(new VarAsParamReport());
			samples.add(new GroupedReport());
			samples.add(new LinkedDSReport("linkedds.jdbr", "Linked DataSet"));
			samples
					.add(new LinkedDSReport("linkedds2.jdbr",
							"Linked DataSet 2"));
			samples.add(new ManyDetailsReport());
			samples.add(new NotRepeatedReport());
			samples.add(new TwoDataSetReport());
			samples.add(new SubDetailReport());
			samples.add(new TotalsReport());
			tutorialList.setListData(samples);
			tutorialList.setSelectedIndex(0);
			tutorialList.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() > 1) {
						run();
					}
				}
				
			});
		}
		return tutorialList;
	}

	private void run() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			getSelectedItem().run();
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}
	}
	/**
	 * This method initializes runButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRunButton() {
		if (runButton == null) {
			runButton = new JButton();
			runButton.setText("Run"); 
			runButton.addActionListener(e -> run());
		}
		return runButton;
	}

	SampleItem getSelectedItem() {
		return tutorialList.getSelectedValue();
	}

	/**
	 * This method initializes templateButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getTemplateButton() {
		if (templateButton == null) {
			templateButton = new JButton();
			templateButton.setText("Template");
			templateButton
					.addActionListener(e -> {
						TemplateEditor te = new TemplateEditor();
						try {
							te.getReportBook().open(
									getSelectedItem().getTemplateURL());
							te.setVisible(true);
						} catch (LoadReportException e1) {
							Utils.showError(e1);
						}
					});
		}
		return templateButton;
	}

	/**
	 * This method initializes sourceButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSourceButton() {
		if (sourceButton == null) {
			sourceButton = new JButton();
			sourceButton.setText("Source"); 
			sourceButton.addActionListener(e -> new SourceViewer(getSelectedItem().getSource()));
		}
		return sourceButton;
	}

	public static void main(String[] args) {
		Main mainFrame = new Main();
		mainFrame.setVisible(true);
	}

} 
