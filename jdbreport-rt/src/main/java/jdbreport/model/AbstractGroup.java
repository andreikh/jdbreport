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
package jdbreport.model;


/**
 * @version 1.1 03/09/08
 * 
 * @author Andrey Kholmanskih
 * 
 */
public abstract class AbstractGroup implements Group {

	private boolean visible = true;
	private GroupsGroup parent;
	
	public AbstractGroup(GroupsGroup parent) {
		this.parent = parent;
	}
	
	public static String rowTypeToString(int type) {
		return Group.typeNames[type];
	}

	public static int stringToRowType(String type) {
		for (int i = 0; i < Group.typeNames.length; i++) {
			if (Group.typeNames[i].equals(type)) {
				return i;
			}
		}
		return 0;
	}
	
	public int getLevel() {
		int result = -1;
		Group group = this;
		do {
			 group = group.getParent();
			result ++;
		}while (group != null);
		return result;
	}


	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public GroupsGroup getParent() {
		return parent;
	}
	
	public void setParent(GroupsGroup parent) {
		this.parent = parent;
	}

	/* (non-Javadoc)
	 * @see jdbreport.model.Group#getIndexPath()
	 */
	public int[] getIndexPath() {
		int count = getLevel();
		int[] result = new int[count];
		int index = count - 1;
		Group g = this;
		while (g.getParent() != null) {
			result[index--] = g.getParent().getChildIndex(g); 
			g = g.getParent();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see jdbreport.model.Group#getPath()
	 */
	public Group[] getPath() {
		int count = getLevel();
		Group[] result = new Group[count];
		int index = count - 1;
		Group g = this;
		while (g.getParent() != null) {
			result[index--] = g; 
			g = g.getParent();
		}
		return result;
	}
	
}
