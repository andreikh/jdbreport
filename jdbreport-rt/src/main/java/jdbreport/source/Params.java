/*
 * Created on 03.06.2005
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
package jdbreport.source;

import java.util.ArrayList;

/**
 * @version 3.0 13.12.2014
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
		StringBuilder sql = new StringBuilder(asql);
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
