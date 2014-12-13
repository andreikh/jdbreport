/*
 *
 * Copyright 2009-2014 Andrey Kholmanskih
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
package jdbreport.view.finder;

import jdbreport.util.GraphicUtil;
import jdbreport.util.finder.FindParams;
import jdbreport.util.finder.Finder;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class FindFieldPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField findField;
	private Finder finder;
	private boolean wrapSearch;
	private int column;

	public FindFieldPanel(Finder finder) {
		super();
		initControls();
		setFinder(finder);
	}
	

	public void requestFocus() {
		super.requestFocus();
		findField.requestFocus();
	}

	public boolean requestFocusInWindow() {
		return findField.requestFocusInWindow();
	}
	
	public void setFont(Font font) {
		super.setFont(font);
		GraphicUtil.setChildFont(this, font);
	}

	@Override
	public synchronized void addKeyListener(KeyListener l) {
		findField.addKeyListener(l);		
	}

	@Override
	public synchronized void removeKeyListener(KeyListener l) {
		findField.removeKeyListener(l);		
	}


	/**
	 * 
	 */
	private void initControls() {
		setLayout(new BorderLayout());
		findField = new JTextField();
		findField.setOpaque(true);
		findField.setFont(getFont());
		findField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (e.isShiftDown())
						findPrev();
					else
						findNext();
				}
			}

		});
		findField.getDocument().addDocumentListener(new DocumentListener() {

			public void insertUpdate(DocumentEvent e) {
				incrementalFind(FindParams.FORWARD);
			}

			public void removeUpdate(DocumentEvent e) {
				incrementalFind(FindParams.FORWARD);
			}

			public void changedUpdate(DocumentEvent e) {
				incrementalFind(FindParams.FORWARD);
			}

		});
		add(findField, BorderLayout.NORTH);
	}


	/**
	 * @param finder
	 *            The finder to set.
	 */
	public void setFinder(Finder finder) {
		this.finder = finder;
	}

	/**
	 * @return Returns the finder.
	 */
	public Finder getFinder() {
		return finder;
	}

	private void findNext() {
		find(FindParams.FORWARD);
	}

	private void findPrev() {
		find(FindParams.BACKWARD);
	}

	private void incrementalFind(int direction) {
		int cp = findField.getCaretPosition();
		findField.setBackground(Color.white);
		if (findField.getText().length() == 0)
			return;
		FindParams findParams = new FindParams(findField.getText(),
				direction, FindParams.SCOPE_SELECTED, true, false,
				isWrapSearch());
		findParams.setColumn(column);
		if (!finder.incrementalFind(findParams)) {
			findField.setBackground(getNotFoundColor());
		}
		if (cp > findField.getText().length()) {
			cp = findField.getText().length();
		}
		findField.setCaretPosition(cp);
	}

	private void find(int direction) {
		int cp = findField.getCaretPosition();
		if (findField.getText().length() == 0) {
			if (!Color.white.equals(findField.getBackground()))
				findField.setBackground(Color.white);
			return;
		}
		FindParams findParams = new FindParams(findField.getText(), direction,
				FindParams.SCOPE_SELECTED, true, false,
				isWrapSearch());
		findParams.setColumn(column);
		if (!finder.find(findParams)) {
			if (!getNotFoundColor().equals(findField.getBackground()))
					findField.setBackground(getNotFoundColor());
		} else
			if (!Color.white.equals(findField.getBackground()))
					findField.setBackground(Color.white);
			
		findField.requestFocus();
		if (cp > findField.getText().length()) {
			cp = findField.getText().length();
		}
		findField.setCaretPosition(cp);
	}

	private static Color getNotFoundColor() {
		return Color.red.brighter();
	}


	public boolean isWrapSearch() {
		return wrapSearch;
	}

	public void setWrapSearch(boolean b) {
		wrapSearch = b;
	}


	public void setText(String text) {
		findField.setText(text);
		if (text.length() > 0) {
			incrementalFind(FindParams.FORWARD);
		}
	}

	public void setColumn(int column) {
		this.column = column;
	}
}
