/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2010 Andrey Kholmanskih. All rights reserved.
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
 * @version 1.4 17.04.2010
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class RootRowGroup extends RootGroup {

	public RootRowGroup() {
		super();
	}

	public DetailGroup createDetailGroup() {
		return new CustomDetailGroup(this);
	}

	public Group addGroup(int type) {
		switch (type) {
		case ROW_TITLE:
			return getTitleGroup();
		case ROW_PAGE_HEADER:
			return getPageHeaderGroup();
		case ROW_PAGE_FOOTER:
			return getPageFooterGroup();
		case ROW_FOOTER:
			return getFooterGroup();
		case GROUP_DETAIL:
			Group group =  createDetailGroup();
			getChildList().add(getChildList().size() - 2, group);
		    return group; 
		case ROW_NONE:
			Group noneGroup = getChildList().get(getChildList().size() - 2);
			if (noneGroup.getType() != type) {
				noneGroup =  createBaseGroup();
				getChildList().add(getChildList().size() - 2, noneGroup);
			}
			return noneGroup;
		default:
			return getDetailGroup().addGroup(type);
		}
	}

	public boolean remove(Object child) {
		return false;
	}

	public DetailGroup getDetailGroup() {
		for (int i = 0; i < getChildList().size(); i++) {
			if (getChild(i).getType() == Group.GROUP_DETAIL) {
				return (DetailGroup) getChild(i);
			}
		}
		DetailGroup group = createDetailGroup();
		getChildList().add(getChildList().size() - 2, group);
		return group;
	}

	@Override
	protected RowsGroup createRowGroup(int type) {
		return  new RowsGroup(this, type);
	}

	static class CustomDetailGroup extends DetailGroup {

		public CustomDetailGroup(GroupsGroup parent) {
			super(parent);
//			getChildList().add(new BaseRowGroup(this, ROW_NONE));
		}

		
		@Override
		protected RowsGroup createRowGroup(int type) {
			return  new RowsGroup(this, type);
		}


		@Override
		public Group addGroup(int type) {
			if ((type != ROW_GROUP_HEADER || !isRepeateHeader()) && type != GROUP_DETAIL) {
				type = ROW_NONE; 
			}
			
			Group group;
			if (type == GROUP_DETAIL) {
				group = new CustomDetailGroup(this);
				getChildList().add(group);
				return group;
			} else if (type == ROW_GROUP_HEADER) {
				group = createRowGroup(type);
				getChildList().add(0, group);
				return group;
			} else {
				for (int i = getChildList().size() - 1; i >= 0 ; i--) {
					int childType = getChild(i).getType(); 
					if (childType == type) {
						return getChild(i);
					} else  if (childType != type) {
						group = createRowGroup(type);
						getChildList().add(group);
						return group;
					}
				}
				group = createRowGroup(type);
				getChildList().add(group);
				return group;
			}
			
		}

	}

	public static class RowsGroup extends BaseRowGroup {

		public RowsGroup(GroupsGroup parent, int type) {
			super(parent, type);
		}
		
		public int getLevel() {
			return 1;
		}
		
	}
}
