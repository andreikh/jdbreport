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

import jdbreport.model.DetailGroup;
import jdbreport.model.Group;
import jdbreport.model.RootGroup;
import jdbreport.model.RowsGroup;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 *
 */
public class TemplateRootGroup extends RootGroup {

	public TemplateRootGroup() {
		super();
	}

	public Group addGroup(int type) {
		switch (type) {
		case ROW_TITLE: return getTitleGroup(); 
		case ROW_PAGE_HEADER: return getPageHeaderGroup(); 
		case ROW_PAGE_FOOTER: return getPageFooterGroup(); 
		case ROW_FOOTER: return getFooterGroup();
		case GROUP_DETAIL:
			Group group =  createDetailGroup();
			getChildList().add(getChildList().size() - 2, group);
		    return group; 
		case ROW_NONE:
			Group baseGroup =  createBaseGroup();
			getChildList().add(getChildList().size() - 2, baseGroup);
		    return baseGroup; 
		default:
		    return getFirstDetailGroup().addGroup(type); 
		}
	}

	
	private DetailGroup getFirstDetailGroup() {
				for (int i = 2; i < getChildCount() - 1; i++) {
					if (getChild(i).getType() == Group.GROUP_DETAIL) {
							return (DetailGroup) getChild(i);
					}
				}
				DetailGroup group = createDetailGroup();
				getChildList().add(2, group);
				return group;
	}

	public DetailGroup createDetailGroup() {
		return new DetailGroup(this);
	}

	public boolean remove(Object child) {
		if (child instanceof Group) {
			int type = ((Group)child).getType();
			if (type == ROW_TITLE 
					|| type == ROW_PAGE_HEADER
					|| type == ROW_PAGE_FOOTER
					|| type == ROW_FOOTER) {
				return false;
			} 
			int index = getChildList().indexOf(child);
			if (index > 0 && index < getChildList().size() - 1
				&& getChildList().get(index - 1).getType() == Group.ROW_NONE 
						&& getChildList().get(index + 1).getType() == Group.ROW_NONE) {
					mergeGroup((RowsGroup)getChildList().get(index - 1), 
							(RowsGroup)getChildList().get(index + 1)); 
			}
		}
		return getChildList().remove(child);
	}

	private void mergeGroup(RowsGroup group, RowsGroup group2) {
		for (int i = 0; i < group2.getChildCount(); i++) {
			group.addRow(group2.getChild(i));
		}
		getChildList().remove(group2);
	}
	

}
