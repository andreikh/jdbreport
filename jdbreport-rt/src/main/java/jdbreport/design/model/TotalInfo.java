/*
 * TotalInfo.java
 *
 * JDBReport Generator
 * 
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

package jdbreport.design.model;

import java.util.ArrayList;
import java.util.List;

import jdbreport.model.Cell;
import jdbreport.model.Group;
import jdbreport.model.RowsGroup;
import jdbreport.util.Utils;

/**
 * Object used for calculation of outcomes
 * 
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
class TotalInfo {

	private int totalFunction;
	private double value;
	private int column;
	private int count;
	private RowsGroup group;
	private List<Cell> targetCells = new ArrayList<>();
	private int decimal;

	/**
	 * 
	 * @param totalFunction
	 *            kind of function
	 * @param column
	 *            the column which meanings are used to calculate
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
	 * @param cell Cell
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
			return Utils.round(value, decimal);
		} else if (totalFunction == CellObject.AF_AVG) {
			return count == 0 ? 0 : Utils.round(value / count, decimal + 1);
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
	 * @param value double value
	 * @return decimal
	 * @since 2.0
	 */
	private int getDecimal(double value) {
		long v = (long)value;
		if (value - v != 0.0) {
			String s = String.format("%f", value);
			int i = s.indexOf(Utils.getDecimalSeparator());
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
	 * @return column
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Returns the group where the result is counted
	 * 
	 * @return group
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