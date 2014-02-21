/*
 * RootGroup.java
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
