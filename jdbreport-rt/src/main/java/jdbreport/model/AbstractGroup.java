/*
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

	@Override
	public String toString() {
		if (getType() >= 0 && getType() < typeNames.length) {
			return typeNames[getType()];
		}
		return "";
	}
}
