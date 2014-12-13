/*
 * JDBReport Designer
 *
 * Copyright (C) 2008-2014 Andrey Kholmanskih
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
package jdbreport.design.grid;

import javax.swing.text.JTextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @version 1.0 03/09/08
 * @author Andrey Kholmanskih
 *
 */
public class NumericFieldListener implements KeyListener{

	private static final char[] CHARS= {'0', '1', '2', '3', '4' ,'5' ,'6', '7', '8', '9', '+', '-', 'e', 'E', '.' };
	
	public void keyPressed(KeyEvent e) {
				
	}

	public void keyReleased(KeyEvent e) {
				
	}

	public void keyTyped(KeyEvent e) {
		boolean find = false;
		for (int i = 0; i < CHARS.length; i++) {
			if (e.getKeyChar() == CHARS[i]) {
				find = true;
				break;
			}
		}
		if (!find) {
			switch (e.getKeyChar()) {
			case ',':
				e.setKeyChar('.');
				break;
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_TAB:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
				break;
			default:
				e.setKeyChar((char) 0);
				e.consume();
			}
		}
	}

	public double getDouble(KeyEvent e) {
		JTextComponent field = (JTextComponent) e.getSource();
		String t = field.getText();
		double value = 0;
		if (t.length() > 0)
			try {
				value = Double.parseDouble(t);
			} catch (Exception e1){
				if (!"-".equals(t) && !"+".equals(t)) {
					field.setText("0");
				}
			}
		return value;	
	}
	
	public float getFloat(KeyEvent e) {
		return (float)getDouble(e);
	}

	public double getDouble(KeyEvent e, double min, double max) {
		JTextComponent field = (JTextComponent) e.getSource();
		String t = field.getText();
		double value = min;
		if (t.length() > 0)
			try {
				value = Double.parseDouble(t);
				if (value < min) {
					value = min;
					field.setText("" + value); 
				}
				else
					if (value > max) {
						value = max;
						field.setText("" + value); 
					}
			} catch (Exception e1){
				field.setText("" + value); 
			}
		return value;	
	}
	
	public double getDouble(KeyEvent e, double min) {
		return getDouble(e, min, Double.MAX_VALUE);
	}

	public float getFloat(KeyEvent e, float min) {
		return (float) getDouble(e, min, Float.MAX_VALUE);
	}
	
	public int getInteger(KeyEvent e) {
		JTextComponent field = (JTextComponent) e.getSource();
		String t = field.getText();
		int value = 0;
		if (t.length() > 0)
			try {
				value = Integer.parseInt(t);
			} catch (Exception e1){
				if (!"-".equals(t) && !"+".equals(t))
				field.setText("0"); 
			}
		return value;	
	}

	public int getInteger(KeyEvent e, int min, int max) {
		JTextComponent field = (JTextComponent) e.getSource();
		String t = field.getText();
		int value = min;
		if (t.length() > 0)
			try {
				value = Integer.parseInt(t);
				if (value < min) {
					value = min;
					field.setText("" + value); 
				}
				else
					if (value > max) {
						value = max;
						field.setText("" + value); 
					}
			} catch (Exception e1){
				field.setText("" + value); 
			}
		return value;	
	}

	public int getInteger(KeyEvent e, int min) {
		return getInteger(e, min, Integer.MAX_VALUE);
	}
	
}
