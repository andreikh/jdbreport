/*
 * Copyright (C) 2006-2008 Andrey Kholmanskih. All rights reserved.
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
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
 */
package jdbreport.view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class JRotateChooser extends JPanel {

	public static final String ANGLE = "angle";

	private static final long serialVersionUID = 1L;

	private int angle;
	private RotateLabel label;
	private JSpinner rotateSpinner;

	public JRotateChooser() {
		this(0);
	}

	public JRotateChooser(int angle) {
		super(new BorderLayout());
		initComponents();
		this.setAngle(angle);
	}

	protected void initComponents() {
		JPanel spinnerPanel = new JPanel();
		spinnerPanel
				.add(new JLabel(Messages.getString("JRotateChooser.angle"))); //$NON-NLS-1$
		rotateSpinner = new JSpinner();
		rotateSpinner.setPreferredSize(new Dimension(50, 20));
		spinnerPanel.add(rotateSpinner);
		((SpinnerNumberModel) rotateSpinner.getModel())
				.setMaximum(new Comparable<Integer>() {

					public int compareTo(Integer arg0) {
						return arg0 == null ? 360 : 360 - arg0;
					}

				});
		((SpinnerNumberModel) rotateSpinner.getModel())
				.setMinimum(new Comparable<Integer>() {

					public int compareTo(Integer arg0) {
						return arg0 == null ? 0 : 0 - arg0;
					}

				});
		this.add(spinnerPanel, BorderLayout.NORTH);
		label = new RotateLabel(Messages.getString("JRotateChooser.text")); //$NON-NLS-1$
		label.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(label, BorderLayout.CENTER);
		label.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				mouseAction(e);
			}

		});
		label.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				mouseAction(e);
			}

		});

		label.addMouseWheelListener(new MouseWheelListener() {

			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() != 0) {
					int old = angle;
					setAngle(old + e.getWheelRotation() * -2);
					firePropertyChange(JRotateChooser.ANGLE, old, angle);
				}
			}

		});

		rotateSpinner.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				int old = angle;
				angle = ((Integer) rotateSpinner.getValue()).intValue();
				if (angle == 360) {
					angle = 0;
					rotateSpinner.setValue(new Integer(0));
				}
				label.repaint();
				firePropertyChange(JRotateChooser.ANGLE, old, angle);
			}

		});

	}

	/**
	 * @param angle
	 *            The angle to set.
	 */
	public void setAngle(int angle) {
		if (angle < 0)
			angle = 360 + angle;
		else if (angle > 360)
			angle -= 360;
		if (this.angle != angle) {
			this.angle = angle;
			rotateSpinner.setValue(new Integer(angle));
			label.repaint();
		}
	}

	/**
	 * @return Returns the angle.
	 */
	public int getAngle() {
		return angle;
	}

	private void mouseAction(MouseEvent e) {
		int old = angle;
		double x = e.getPoint().x - label.getWidth() / 2;
		double y = label.getHeight() / 2 - e.getPoint().y;
		int a = (int) Math.round(Math.toDegrees(Math.atan(x / y)));
		setAngle(y < 0 ? 270 - a : 90 - a);
		firePropertyChange(JRotateChooser.ANGLE, old, angle);
	}

	private class RotateLabel extends JLabel {

		private static final long serialVersionUID = 1L;

		public RotateLabel(String text) {
			super(text);
		}

		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			final int incr = 1;
			final int w = 6;
			int d = Math.min(getWidth(), getHeight());
			int x = getWidth() / 2 - d / 2 + incr;
			int y = getHeight() / 2 - d / 2 + incr;
			d -= incr * 2;
			Color oldColor = g2.getColor();
			g2.drawArc(x, y, d, d, 0, 360);
			x++;
			y++;
			d -= 2;
			boolean black = true;
			for (int start = 0; start < 360; start += 10) {
				if (black)
					g2.setColor(Color.BLACK);
				else
					g2.setColor(Color.lightGray);
				black = !black;
				g2.fillArc(x, y, d, d, start, 10);
			}
			g2.setColor(Color.RED);
			g2.fillArc(x, y, d, d, getAngle() - 2, 8);
			x += w;
			y += w;
			d -= w * 2;
			g2.setColor(Color.WHITE);
			g2.fillArc(x, y, d, d, 0, 360);
			g2.setColor(oldColor);
			g2.drawArc(x, y, d, d, 0, 360);
			g2.rotate(Math.toRadians(360 - getAngle()), getWidth() / 2,
					getHeight() / 2);
			super.paint(g2);
		}

	}

}
