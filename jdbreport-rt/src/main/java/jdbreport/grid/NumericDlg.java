/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2011 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.grid;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import jdbreport.model.Units;
import jdbreport.util.Utils;

import and.util.Utilities;

/**
 * @version 2.0 13.03.2011
 * @author Andrey Kholmanskih
 * 
 */
public class NumericDlg extends JDialog {

	private static final long serialVersionUID = 1L;
	public final static int MM = 0;
	public final static int PINCH = 1;
	public final static int PX = 2;

	public enum Orientation {
		Vertical, Horizontal
	}

	public final static int CANCEL = 0;
	public final static int OK = 1;

	private final static int DECIMAL = 2;

	private Units unit = Units.PT;
	private JPanel jContentPane = null;
	private JPanel topPanel = null;
	private JPanel buttonPanel = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private JPanel centerPanel = null;
	private JLabel jLabel = null;
	private JTextField valueField = null;
	private JLabel jLabel1 = null;
	private JComboBox unitsBox = null;
	private int result = CANCEL;
	private int unitInd;
	private double value;
	private Orientation orientation;

	/**
	 * 
	 * @param owner
	 * @param title
	 * @param value
	 *            numeric value in pixels
	 * @param unitInd
	 * @param orientation
	 * @throws HeadlessException
	 */
	public NumericDlg(Frame owner, String title, double value, int unitInd,
			Orientation orientation) throws HeadlessException {
		super(owner, title, true);
		this.unitInd = PX;
		initialize();
		this.orientation = orientation;
		saveValue(value, PX);
		valueField.setText("" + value); //$NON-NLS-1$
		if (unitInd != PX) {
			getUnitsBox().setSelectedIndex(unitInd);
			changeUnit();
		}
	}

	public NumericDlg(Dialog owner, String title, double value, int unitInd,
			Orientation orientation) throws HeadlessException {
		super(owner, title, true);
		this.unitInd = PX;
		initialize();
		this.orientation = orientation;
		saveValue(value, PX);
		valueField.setText("" + value); //$NON-NLS-1$
		if (unitInd != PX) {
			getUnitsBox().setSelectedIndex(unitInd);
			changeUnit();
		}
	}
	
	public int getUnits() {
		return unitInd;
	}

	/**
	 * @param value
	 * @param unitInd
	 */
	private void saveValue(double value, int unitInd) {
		switch (unitInd) {
		case PX:
			unit = Units.PT;
			if (this.orientation == Orientation.Vertical)
				this.value = unit.setYPixels((int) value);
			else
				this.value = unit.setXPixels((int) value);
			break;
		case MM:
			unit = Units.MM;
			this.value = unit.setValue(value);
			break;
		case PINCH:
			unit = Units.INCH;
			this.value = unit.setValue(value);
			break;
		}
	}

	public NumericDlg(Dialog owner, String title) throws HeadlessException {
		super(owner, title, true);
		initialize();
		Utils.screenCenter(this, owner);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 100);
		this.setContentPane(getJContentPane());
		Utils.screenCenter(this, getOwner());
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
			jContentPane.add(getTopPanel(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes topPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new JPanel();
			topPanel.setLayout(new BorderLayout());
			topPanel.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
			topPanel.add(getCenterPanel(), java.awt.BorderLayout.CENTER);
		}
		return topPanel;
	}

	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getOkButton());
			buttonPanel.add(getCancelButton());
		}
		return buttonPanel;
	}

	/**
	 * This method initializes okButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText(Messages.getString("NumericDlg.1")); //$NON-NLS-1$
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String s = valueField.getText();
					if (s != null) {
						saveValue(Double.parseDouble(s), unitInd);
					} else
						value = 0;
					setVisible(false);
					result = OK;
				}
			});
		}
		return okButton;
	}

	/**
	 * This method initializes cancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText(Messages.getString("NumericDlg.2")); //$NON-NLS-1$
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
					result = CANCEL;
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes centerPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getCenterPanel() {
		if (centerPanel == null) {
			jLabel1 = new JLabel();
			jLabel1.setText(Messages.getString("NumericDlg.3")); //$NON-NLS-1$
			jLabel = new JLabel();
			jLabel.setText(Messages.getString("NumericDlg.4")); //$NON-NLS-1$
			centerPanel = new JPanel();
			centerPanel.add(jLabel);
			centerPanel.add(getValueField());
			centerPanel.add(jLabel1);
			centerPanel.add(getUnitsBox());
		}
		return centerPanel;
	}

	/**
	 * This method initializes valueField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getValueField() {
		if (valueField == null) {
			valueField = new JTextField();
			valueField.setPreferredSize(new java.awt.Dimension(60, 19));
			valueField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
			valueField.setInputVerifier(new InputVerifier() {

				@Override
				public boolean verify(JComponent input) {
					try {
						String s = ((JTextField) input).getText();
						if (s.length() > 0) {
							Double.parseDouble(s);
							return true;
						}
						return true;
					} catch (Exception e) {
						JOptionPane
								.showMessageDialog(
										NumericDlg.this,
										Messages.getString("NumericDlg.5"), Messages.getString("NumericDlg.6"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
						return false;
					}
				}

			});
		}
		return valueField;
	}

	/**
	 * This method initializes unitsBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getUnitsBox() {
		if (unitsBox == null) {
			unitsBox = new JComboBox();
			unitsBox.addItem(Messages.getString("NumericDlg.0")); //$NON-NLS-1$
			unitsBox.addItem(Messages.getString("NumericDlg.7")); //$NON-NLS-1$
			unitsBox.addItem(Messages.getString("NumericDlg.8")); //$NON-NLS-1$
			if (unitInd < unitsBox.getItemCount())
				unitsBox.setSelectedIndex(unitInd);
			unitsBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					changeUnit();
				}

			});
		}
		return unitsBox;
	}

	private void changeUnit() {
		double v = 0;
		String s = valueField.getText();
		if (s.trim().length() > 0) {
			v = Double.parseDouble(s);
		}
		saveValue(v, unitInd);
		unitInd = unitsBox.getSelectedIndex();
		switch (unitInd) {
		case PX:
			unit = Units.PT;
			if (orientation == Orientation.Vertical)
				v = unit.getYPixels(value);
			else
				v = unit.getXPixels(value);
			break;
		case MM:
			unit = Units.MM;
			v = unit.getValue(value);
			break;
		case PINCH:
			unit = Units.INCH;
			v = unit.getValue(value);
			break;
		}
		valueField.setText("" + Utilities.round(v, DECIMAL)); //$NON-NLS-1$
	}

	public int getResult() {
		return result;
	}

	public double getValue(int unitInd) {
		if (unitInd == PX) {
			if (this.orientation == Orientation.Vertical)
				return Units.PT.getYPixels(value);
			else
				return Units.PT.getXPixels(value);
		} else
			return unit.getValue(value);
	}
}
