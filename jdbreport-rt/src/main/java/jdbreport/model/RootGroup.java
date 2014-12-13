/*
 * RootGroup.java
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

package jdbreport.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @version 2.0 26.07.2011
 * @author Andrey Kholmanskih
 * 
 */
public abstract class RootGroup extends TreeRowGroup {

	public RootGroup() {
		super(null);
		getChildList().add(new BaseRowGroup(this, ROW_TITLE));
		getChildList().add(new BaseRowGroup(this, ROW_PAGE_HEADER));
		getChildList().add(new BaseRowGroup(this, ROW_PAGE_FOOTER));
		getChildList().add(new BaseRowGroup(this, ROW_FOOTER));
	}

	public int getType() {
		return -1;
	}

	public String getTypeName() {
		return "RootGroup";
	}

	@Override
	public RowsGroup getGroup(TableRow row) {
		RowsGroup result = super.getGroup(row);
		if (result == null) {
			result = getTitleGroup();
		}
		return result;
	}

	public RowsGroup getTitleGroup() {
		return (RowsGroup) getChildList().get(0);
	}

	public RowsGroup getFooterGroup() {
		return (RowsGroup) getChildList().get(getChildList().size() - 1);
	}

	public RowsGroup getPageHeaderGroup() {
		return (RowsGroup) getChildList().get(1);
	}

	public RowsGroup getPageFooterGroup() {
		return (RowsGroup) getChildList().get(getChildList().size() - 2);
	}

	protected Group createBaseGroup() {
		return new BaseRowGroup(this, ROW_NONE);
	}

	public abstract DetailGroup createDetailGroup();

	public List<DetailGroup> getDetailGroups() {
		List<DetailGroup> list = new ArrayList<DetailGroup>();
		for (int i = 0; i < getChildCount() - 1; i++) {
			if (getChild(i).getType() == Group.GROUP_DETAIL) {
				list.add((DetailGroup) getChild(i));
			}
		}
		return list;
	}

	/**
	 * 
	 * @return list of groups
	 * @since 1.4
	 */
	public List<Group> getBodyGroups() {
		List<Group> list = new ArrayList<Group>();
		for (int i = 0; i < getChildCount() - 1; i++) {
			int type = getChild(i).getType();
			if (type == Group.GROUP_DETAIL || type == Group.ROW_NONE) {
				list.add( getChild(i));
			}
		}
		return list;
	}
	
	public int addGroup(int index, Group group) {
		int type = group.getType();
		if (type == ROW_TITLE || type == ROW_FOOTER || type == ROW_PAGE_FOOTER
				|| type == ROW_PAGE_HEADER) {
			RowsGroup oldGroup = (RowsGroup) findGroup(type);
			if (oldGroup == null) {
				index = getIndexGroup(type);
				getChildList().add(index, group);
				((AbstractGroup) group).setParent(this);
			} else {
				if (oldGroup != group) {
					for (Iterator<TableRow> it = group.iterator(); it.hasNext();) {
						oldGroup.addRow(it.next());
						it.remove();
					}
				}
			}
		} else if (type == GROUP_DETAIL || type == ROW_NONE) {
			if (index < 2)
				index = 2;
			else if (index > getChildCount() - 2)
				index = getChildCount() - 2;
			getChildList().add(index, group);
			((AbstractGroup) group).setParent(this);
		} else {
			Group oldGroup = getChild(index);
			if (!(oldGroup instanceof RowsGroup)) {
				oldGroup = new BaseRowGroup(this, ROW_NONE);
				index = getIndexGroup(ROW_NONE);
				getChildList().add(index, oldGroup);
			}
			if (oldGroup != group) {
				for (Iterator<TableRow> it = group.iterator(); it.hasNext();) {
					((RowsGroup) oldGroup).addRow(it.next());
					it.remove();
				}
			}
		}
		return index;
	}

	private int getIndexGroup(int type) {
		switch (type) {
		case ROW_TITLE:
			return 0;
		case ROW_PAGE_HEADER:
			return 1;
		case ROW_PAGE_FOOTER:
			return getChildList().size() - 2;
		case ROW_FOOTER:
			return getChildList().size() - 1;
		case ROW_NONE:
			return getChildList().size() - 1;
		}
		return -1;
	}

	public void clear() {
		for (int i = getChildList().size() - 1; i >= 0; i--) {
			Group group = getChild(i);
			if (group.getType() == Group.GROUP_DETAIL
					|| group.getType() == Group.ROW_NONE) {
				getChildList().remove(i);
			}
			group.clear();
		}
	}

}
