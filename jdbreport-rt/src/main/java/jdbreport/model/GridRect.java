/*
 * Created on 16.01.2005
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2005-2008 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model;

import java.io.Serializable;

/**
 * Coordinates for selected cells
 * 
 * @version 1.1 03/09/08
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class GridRect implements Serializable{

	private static final long serialVersionUID = 1L;
	private int leftcol;
	private int toprow;
	private int rightcol;
	private int bottomrow;

	public GridRect(int toprow, int leftcol, int bottomrow, int rightcol) {
		this.toprow = toprow;
		this.leftcol = leftcol;
		this.bottomrow = bottomrow;
		this.rightcol = rightcol;
	}

	public int getTopRow() {
		return toprow;
	}

	public int getLeftCol() {
		return leftcol;
	}

	public int getBottomRow() {
		return bottomrow;
	}

	public int getRightCol() {
		return rightcol;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + bottomrow;
		result = PRIME * result + leftcol;
		result = PRIME * result + rightcol;
		result = PRIME * result + toprow;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final GridRect other = (GridRect) obj;
		if (bottomrow != other.bottomrow)
			return false;
		if (leftcol != other.leftcol)
			return false;
		if (rightcol != other.rightcol)
			return false;
		if (toprow != other.toprow)
			return false;
		return true;
	}

}
