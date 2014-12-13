/*
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
package jdbreport.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @version 3.0 13.12.2014
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
				.add(new JLabel(Messages.getString("JRotateChooser.angle")));
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

		label.addMouseWheelListener(e -> {
            if (e.getWheelRotation() != 0) {
                int old = angle;
                setAngle(old + e.getWheelRotation() * -2);
                firePropertyChange(JRotateChooser.ANGLE, old, angle);
            }
        });

		rotateSpinner.addChangeListener(e -> {
            int old = angle;
            angle = (Integer) rotateSpinner.getValue();
            if (angle == 360) {
                angle = 0;
                rotateSpinner.setValue(0);
            }
            label.repaint();
            firePropertyChange(JRotateChooser.ANGLE, old, angle);
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
			rotateSpinner.setValue(angle);
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
