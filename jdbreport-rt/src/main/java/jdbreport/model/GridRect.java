/*
 * Created on 16.01.2005
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2005-2014 Andrey Kholmanskih
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
