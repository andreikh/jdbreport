/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2009 Andrey Kholmanskih. All rights reserved.
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
 * 
 */
package jdbreport.print;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JPanel;

/**
 * @version 2.0 12.12.2009
 * @author Andrey Kholmanskih
 * 
 */
class PreviewContainer extends JPanel {

	private static final long serialVersionUID = 1L;

	protected int H_GAP = 16;

	protected int V_GAP = 10;

	public Dimension getPreferredSize() {
		int n = getComponentCount();
		if (n == 0)
			return new Dimension(H_GAP, V_GAP);
		Component comp = getComponent(0);
		Dimension dc = comp.getPreferredSize();
		int w = dc.width;
		Dimension dp = getParent().getSize();
		int nCol = Math.max((dp.width - H_GAP) / (w + H_GAP), 1);
		int nRow = n / nCol;
		if (nRow * nCol < n)
			nRow++;
		
		int nc = 0;
		int wc = 0;
		int wc1 = 0;
		int hc = 0;
		int hc1 = 0;
		for (int i = 0; i < n; i++) {
			comp = getComponent(i);
			dc = comp.getPreferredSize();
			
			wc1 += dc.getWidth() + H_GAP;
			hc1 = Math.max(hc1, (int)dc.getHeight() + V_GAP);
			nc++;
			if (nc >= nCol) {
				nc = 0;
				wc = Math.max(wc, wc1);
				wc1 = 0;
				hc += hc1;
				hc1 = 0;
			}
		}
		wc = Math.max(wc, wc1);
		hc += hc1;
		
		int ww = wc + H_GAP;
		int hh = hc + V_GAP;
		
		Insets ins = getInsets();
		return new Dimension(ww + ins.left + ins.right, hh + ins.top
				+ ins.bottom);
	}

	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

}