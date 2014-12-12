/*
 * Created on 03.06.2005
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
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
 */
package jdbreport.source;

import java.util.ArrayList;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class Params extends ArrayList<Object> implements DataSetParams {

	private static final long serialVersionUID = 1L;

	class Param {
		String name;

		Object value;

		int type;

		private boolean old;

		public Param(String aname) {
			if (aname.toUpperCase().indexOf("OLD_") == 0) {
				name = aname.substring(4);
				old = true;
			} else
				name = aname;
		}

		public boolean isOld() {
			return old;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Param))
				return false;
			Param other = (Param) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

	}

	public String prepareParams(String asql) {
		int i = 1, l;
		char quote = '\0';
		StringBuffer sql = new StringBuffer(asql);
		clear();
		while (i < sql.length()) {
			if (sql.charAt(i) == '\"' || sql.charAt(i) == '\'') {
				if (quote == sql.charAt(i))
					quote = '\0';
				else if (quote == '\0')
					quote = sql.charAt(i);
			} else if (quote == '\0' && sql.charAt(i) == ':') {
				l = i + 1;
				while (l < sql.length()) {
					char c = sql.charAt(l);
					if (c != ',' && c != ' ' && c != ')' && c != '+'
							&& c != '=' && c != '-' && c != '/' && c != '*') {
						l++;
					} else
						break;
				}
				add(new Param(sql.substring(i + 1, l).trim()));
				sql.replace(i, l, "?");
			}
			i++;
		}
		return sql.toString();
	}

	public Object getValue(int ind) {
		return ((Param) get(ind)).value;
	}

	public String getName(int ind) {
		return ((Param) get(ind)).name;
	}

	public boolean isOld(int ind) {
		return ((Param) get(ind)).isOld();
	}

	public void setValue(int index, Object val) {
		((Param) get(index)).value = val;
	}

	public void setValue(String name, Object value) {
		for (int i = 0; i < size(); i++) {
			if (((Param) get(i)).name.equals(name)) {
				setValue(i, value);
				return;
			}
		}
	}

	public void setSQLType(int index, int type) {
		((Param) get(index)).type = type;
	}

	public int getSQLType(int index) {
		return ((Param) get(index)).type;
	}
}
