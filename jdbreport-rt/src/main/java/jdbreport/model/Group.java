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

import java.util.Iterator;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public interface Group extends Iterable<TableRow> {

	public static final String typeNames[] = {
			"", Messages.getString("Group.1"), Messages.getString("Group.2"), Messages.getString("Group.3"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			Messages.getString("Group.4"), Messages.getString("Group.5"), Messages.getString("Group.6"), Messages.getString("Group.7"), Messages.getString("Group.8") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	public static final int ROW_NONE = 0;
	public static final int ROW_TITLE = 1;
	public static final int ROW_PAGE_HEADER = 2;
	public static final int ROW_GROUP_HEADER = 3;
	public static final int ROW_DETAIL = 4;
	public static final int ROW_GROUP_FOOTER = 5;
	public static final int ROW_PAGE_FOOTER = 6;
	public static final int ROW_FOOTER = 7;
	public static final int GROUP_DETAIL = 8;

	public int getType();

	public String getTypeName();

	public int getChildCount();

	public RowsGroup getGroup(TableRow row);

	public GroupsGroup getParent();

	public int getChildIndex(Object child);

	public boolean remove(Object child);

	public int getLevel();

	/**
	 * Returns all TableRow objects in group
	 * 
	 * @return all TableRow objects
	 */
	public int getRowCount();

	public TableRow getFirstGroupRow();

	public boolean isVisible();

	public void setVisible(boolean visible);

	/**
	 * Returns all rows
	 */
	public Iterator<TableRow> iterator();

	/**
	 * Returns all visible rows
	 * 
	 * @return all visible rows
	 */
	public Iterator<TableRow> getVisibleRowIterator();

	/**
	 * Returns group's height in pixels
	 * 
	 * @return the group's height in pixels
	 */
	public int getHeight();

	/**
	 * Calculates the grand total by column and all rows
	 * 
	 * @param func -
	 *            the total functions (sum, min, max, avg)
	 * @param column -
	 *            the column's number
	 * @return the grand total by column
	 */
	public double getTotalResult(int func, int column);

	/**
	 * Removes all groups and rows from group
	 * 
	 */
	public void clear();

	public int[] getIndexPath();

	public Group[] getPath();

	public Object getChild(int index);
}
