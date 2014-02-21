/*
 * StringMetrics.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2011 Andrey Kholmanskih. All rights reserved.
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

/**
 * @version 2.0 14.03.2011
 * @author Andrey Kholmanskih
 * 
 */
public interface StringMetrics {

	void setStyle(CellStyle style);

	int charsWidth(char[] data, int off, int len);

	char[] toViewCharArray(Cell cell);

}