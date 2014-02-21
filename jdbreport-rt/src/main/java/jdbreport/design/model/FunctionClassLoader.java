/*
 * JDBReport Generator
 * 
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
 * Andrey Kholmanskih
 * support@jdbreport.com
 */
package jdbreport.design.model;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 *
 */
class FunctionClassLoader extends ClassLoader {

	private CellFunctionObject cell;
	
	public FunctionClassLoader(CellFunctionObject cell) {
		super();
		this.cell = cell;
	}

	public Class<?> findClass(String className) throws ClassNotFoundException {
		 if (className.equals(cell.getFunctionName())) { 
			 byte[] bytes = cell.getCompiledClass();
			 return defineClass(className, bytes, 0, 
	                         bytes.length);
		 }
		return Class.forName(className);
    }

}
