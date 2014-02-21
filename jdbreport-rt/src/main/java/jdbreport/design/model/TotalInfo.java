/*
 * TotalInfo.java
 *
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

package jdbreport.design.model;

import java.util.ArrayList;
import java.util.List;

import and.util.Utilities;

import jdbreport.model.Cell;
import jdbreport.model.Group;
import jdbreport.model.RowsGroup;

/**
 * Object used for calculation of outcomes
 * 
 * @version 2.0 22.04.2011
 * @author Andrey Kholmanskih
 * 
 */
class TotalInfo {

	private int totalFunction;
	private double value;
	private int column;
	private int count;
	private RowsGroup group;
	private List<Cell> targetCells = new ArrayList<Cell>();
	private int decimal;

	/**
	 * 
	 * @param totalFunction
	 *            kind of function
	 * @param column
	 *            the column which meanings are used to calculate
     * @param row
	 *            the row which meanings are used to calculate	             
	 * @param group
	 *            the group where the result is counted
	 */
	public TotalInfo(int totalFunction, int column, RowsGroup group) {
		this.totalFunction = totalFunction;
		value = 0;
		count = 0;
		this.column = column;
		this.group = group;
	}

	/**
	 * Adds a cell, where the result of calculation will be written
	 * 
	 * @param cell
	 */
	public void addTargetCell(Cell cell) {
		if (targetCells.indexOf(cell) < 0) {
			targetCells.add(cell);
		}
	}

	public void copyValue() {
		for (Cell cell : targetCells) {
			cell.setValue(getValue());
		}
	}

	/**
	 * Returns the current value
	 */
	public Number getValue() {
		if (totalFunction == CellObject.AF_SUM) {
			if (decimal == 0) {
				return Math.round(value);
			}
			return Utilities.round(value, decimal);
		} else if (totalFunction == CellObject.AF_AVG) {
			return count == 0 ? 0 : Utilities.round(value / count, decimal + 1);
		}  
		return value;
	}

	/**
	 * It includes a new meaning in calculation
	 * 
	 * @param value
	 *            the new meaning
	 */
	public void incValue(double value) {
		if (count == 0) {
			decimal = getDecimal(value);
			this.value = value;
		} else
			switch (totalFunction) {
			case CellObject.AF_SUM:
				decimal = Math.max(decimal, getDecimal(value));
				this.value += value;
				break;
			case CellObject.AF_MAX:
				this.value = Math.max(this.value, value);
				break;
			case CellObject.AF_MIN:
				this.value = Math.min(this.value, value);
				break;
			case CellObject.AF_AVG:
				decimal = Math.max(decimal, getDecimal(value));
				this.value += value;
				break;
			}
		count++;
	}

	/**
	 * 
	 * @param value
	 * @return
	 * @since 2.0
	 */
	private int getDecimal(double value) {
		long v = (long)value;
		if (value - v != 0.0) {
			String s = String.format("%f", value);
			int i = s.indexOf(Utilities.getDecimalSeparator());
			if (i >= 0) {
				int l = s.length() - 1;
				while (l > 0) {
					if (s.charAt(l) != '0') {
						break;
					}
					l--;
				}
				return l - i;
			}
		}
		return 0;
	}

	public void resetValue() {
		value = 0;
		count = 0;
		decimal = 0;
		targetCells.clear();
	}

	/**
	 * Returns the column which meanings are used to calculate
	 * 
	 * @return
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Returns the group where the result is counted
	 * 
	 * @return
	 */
	public RowsGroup getGroup() {
		return group;
	}

	/**
	 * Transfer of the final meaning to the cells and setting to zero the
	 * current meaning
	 * 
	 * @param targetGroup
	 *            the group where the result is counted
	 * @return             
	 */
	public boolean moveToTarget(Group targetGroup) {
		if (targetGroup != null
				&& (group == targetGroup || group.getParent() == targetGroup)) {
			copyValue();
			resetValue();
			return true;
		}
		return false;
	}

	public List<Cell> getTargetCells() {
		return targetCells;
	}

}