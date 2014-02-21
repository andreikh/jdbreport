/*
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jdbreport.design.model.CellObject;
import jdbreport.design.model.GroupKey;
import jdbreport.design.model.KeyComparator;
import jdbreport.design.model.TemplateReportCell;
import jdbreport.source.BufferedDataSet;

/**
 * @version 2.0 11.05.2011
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class DetailGroup extends TreeRowGroup {

	private List<GroupKey> keyList;

	private boolean validIndexes = false;

	private int firstDetailGroup;

	private int lastDetailGroup;

	private int minRowCount = 0;

	private int maxRowCount = 0;

	private int genRowCount;

	private Map<String, BufferedDataSet> dsList;

	private boolean repeateHeader = false;

	public DetailGroup(GroupsGroup parent) {
		super(parent);
	}

	public boolean isRepeateHeader() {
		return repeateHeader;
	}

	public void setRepeateHeader(boolean repeate) {
		this.repeateHeader = repeate;
	}

	public Group addGroup(int type) {
		Group group = null;
		if (type == ROW_DETAIL || type == ROW_GROUP_HEADER
				|| type == ROW_GROUP_FOOTER || type == ROW_NONE) {
			group = findGroup(type);
			if (group == null) {
				group = createRowGroup(type);
				int index = getIndexGroup(type);
				getChildList().add(index, group);
			}
		} else if (type == GROUP_DETAIL) {
			group = new DetailGroup(this);
			int index = getIndexGroup(type);
			getChildList().add(index, group);
		}
		validIndexes = false;
		return group;
	}

	public int addGroup(int index, Group group) {
		int type = group.getType();
		if (type == ROW_DETAIL || type == ROW_GROUP_HEADER
				|| type == ROW_GROUP_FOOTER || type == ROW_NONE) {
			RowsGroup oldGroup = (RowsGroup) findGroup(type);
			if (oldGroup == null) {
				index = getIndexGroup(type);
				getChildList().add(index, group);
				((AbstractGroup) group).setParent(this);
			} else {
				for (Iterator<TableRow> it = group.iterator(); it.hasNext();) {
					oldGroup.addRow(it.next());
					it.remove();
				}
			}
		} else if (type == GROUP_DETAIL) {
			getChildList().add(index, group);
			((AbstractGroup) group).setParent(this);
		} else {
			Group oldGroup = getChild(index);
			if (!(oldGroup instanceof RowsGroup)) {
				oldGroup = new BaseRowGroup(this, ROW_NONE);
				index = getIndexGroup(ROW_NONE);
				getChildList().add(index, oldGroup);
			}
			for (Iterator<TableRow> it = group.iterator(); it.hasNext();) {
				((RowsGroup) oldGroup).addRow(it.next());
				it.remove();
			}
		}
		return index;
	}

	/**
	 * Returns a position for an insert of group of the set type
	 * 
	 * @param type
	 *            type of group
	 * @return index for an insert
	 */
	private int getIndexGroup(int type) {
		if (type == GROUP_DETAIL) {
			for (int i = 0; i < getChildList().size(); i++) {
				if (getChild(i).getType() == ROW_GROUP_FOOTER) {
					return i;
				}
			}

		} else {
			if (type == ROW_GROUP_FOOTER || type == ROW_NONE) {
				return getChildList().size();
			} else {
				for (int i = 0; i < getChildList().size(); i++) {
					if (getChild(i).getType() >= type) {
						return i;
					}
				}
			}
		}
		return getChildList().size();
	}

	public void addRow(int index, TableRow row) {
		if (getChildCount() == 0) {
			getChildList().add(new BaseRowGroup(this, ROW_DETAIL));
		}
		((RowsGroup) getChild(0)).addRow(index, row);
		validIndexes = false;
	}

	public boolean addKey(GroupKey groupKey) {
		if (keyList == null) {
			keyList = new ArrayList<GroupKey>();
		}
		return keyList.add(groupKey);
	}

	public void clearKeys() {
		if (keyList == null)
			return;
		keyList.clear();
	}

	public GroupKey getKey(int index) {
		if (keyList != null && index >= 0 && index < keyList.size())
			return keyList.get(index);
		return null;
	}

	public int getKeyCount() {
		return keyList == null ? 0 : keyList.size();
	}

	public int getType() {
		return GROUP_DETAIL;
	}

	public int getFirstDetailGroup() {
		if (!validIndexes) {
			recalcIndexes();
		}
		return firstDetailGroup;
	}

	private void recalcIndexes() {
		firstDetailGroup = 0;
		lastDetailGroup = getChildCount() - 1;
		for (int i = 0; i < getChildCount(); i++) {
			int t = ((Group) getChild(i)).getType();
			if (t == Group.ROW_GROUP_HEADER) {
				firstDetailGroup = i + 1;
			} else {
				if (t == Group.ROW_GROUP_FOOTER)
					lastDetailGroup = Math.min(i - 1, lastDetailGroup);
			}
		}
		validIndexes = true;
	}

	public int getLastDetailGroup() {
		if (!validIndexes) {
			recalcIndexes();
		}
		return lastDetailGroup;
	}

	public RowsGroup getHeaderGroup() {
		for (int i = 0; i < getChildList().size(); i++) {
			if (getChild(i).getType() == ROW_GROUP_HEADER) {
				return (RowsGroup) getChild(i);
			}
		}
		return null;
	}

	public RowsGroup getFooterGroup() {
		for (int i = getChildList().size() - 1; i >= 0; i--) {
			if (getChild(i).getType() == ROW_GROUP_FOOTER) {
				return (RowsGroup) getChild(i);
			}
		}
		return null;
	}

	public boolean isEof() {
		if (dsList == null)
			return true;
		for (BufferedDataSet ds : dsList.values()) {
			if (!ds.isEof()) {
				return false;
			}
		}
		DetailGroup childGroup = (DetailGroup) findGroup(Group.GROUP_DETAIL);
		if (childGroup != null) {
			return childGroup.isEof();
		}
		return true;
	}

	private void resetOldCellValue() {
		for (TableRow row : this) {
			if (row.getGroup().getType() == Group.ROW_DETAIL) {
				for (Cell cell : row) {
					if (!cell.isNull() && !cell.isChild())
						((TemplateReportCell) cell).setOldValue(null);
				}
			}
		}
	}

	public void replaceDataSet(BufferedDataSet ds) throws ReportException {
		if (dsList == null) {
			dsList = new HashMap<String, BufferedDataSet>();
		}
		dsList.remove(ds.getId());
		Map<String, BufferedDataSet> map = new HashMap<String, BufferedDataSet>();
		map.put(ds.getId(), ds);
		for (int i = 0; i < getChildCount(); i++) {
			if (getChild(i).getType() == ROW_DETAIL) {
				updateDS((RowsGroup) getChild(i), map);
			} else if (getChild(i).getType() == GROUP_DETAIL) {
				((DetailGroup) getChild(i)).replaceDataSet(ds);
			}
		}

	}

	public void updateDataSet(Map<String, BufferedDataSet> map)
			throws ReportException {
		resetOldCellValue();
		if (map == null)
			return;
		if (dsList == null) {
			dsList = new HashMap<String, BufferedDataSet>();
		}
		dsList.clear();
		for (int i = 0; i < getChildCount(); i++) {
			if (getChild(i).getType() == ROW_DETAIL) {
				updateDS((RowsGroup) getChild(i), map);
			} else if (getChild(i).getType() == GROUP_DETAIL) {
				((DetailGroup) getChild(i)).updateDataSet(map);
			}
		}
		genRowCount = 0;
	}

	private void updateDS(RowsGroup group, Map<String, BufferedDataSet> map)
			throws ReportException {
		Iterator<TableRow> it = group.iterator();
		while (it.hasNext()) {
			TableRow row = it.next();
			for (int c = 0; c < row.getColCount(); c++) {
				CellObject cell = (CellObject) row.getCellItem(c);
				try {
					String[] dsIDs = cell.getDataSetIds();
					if (dsIDs != null) {
						for (String dsID : dsIDs) {
							if (dsID == null)
								break;
							BufferedDataSet ds = map.get(dsID);
							if (ds != null) {
								String[] fields = cell.getFieldNames(dsID);
								if (fields != null) {
									for (String field : fields) {
										if (ds.findColumn(rootFieldName(field))) {
											if (!dsList.containsKey(dsID))
												dsList.put(dsID, ds);
											break;
										}
									}
								}
							}
						}
					}
				} catch (Exception e) {
					String message = "Error in group - " + group.getTypeName()
							+ ", row - " + group.getChildIndex(row)
							+ ", column - " + c;
					throw new ReportException(message, e);
				}
			}
		}
	}

	private String rootFieldName(String fieldName) {
		if (fieldName == null)
			return null;
		int i = fieldName.indexOf('.');
		if (i > 0) {
			return fieldName.substring(0, i);
		} else
			return fieldName;
	}

	public String getTypeName() {
		return rowTypeToString(getType());
	}

	public boolean changeGroupKey(KeyComparator keyComparator)
			throws ReportException {
		for (int i = 0; i < getKeyCount(); i++) {
			if (!keyComparator.compare(getKey(i)))
				return true;
		}
		return false;
	}

	public void initGroupKey(KeyComparator keyComparator)
			throws ReportException {
		for (int i = 0; i < getKeyCount(); i++) {
			keyComparator.init(getKey(i));
		}
	}

	public Map<String, BufferedDataSet> getDsList() {
		return dsList;
	}

	public void clear() {
		for (int i = 0; i < getChildList().size(); i++) {
			getChild(i).clear();
		}
		getChildList().clear();
	}

	/**
	 * 
	 * Number of current row in current group of detailing Current value
	 * VAR_ROW.
	 * 
	 * @return current row
	 * @since 1.3
	 */
	public Integer getCurrentRow() {
		return genRowCount;
	}

	/**
	 * 
	 * @since 1.3
	 */
	public void incGenRowCount() {
		genRowCount++;
	}

	/**
	 * Returns the maximum number of rows in the group
	 * @return maximum number of rows
	 * @since 1.3
	 */
	public int getMaxRowCount() {
		return maxRowCount;
	}

	/**
	 * Sets the maximum number of rows in the group
	 * 
	 * @param count maximum number of rows
	 * @since 1.3
	 */
	public void setMaxRowCount(int count) {
		this.maxRowCount = count;
	}

	/**
	 * 
	 * @return minimum number of rows
	 * @since 1.3
	 */
	public int getMinRowCount() {
		return minRowCount;
	}

	/**
	 * Sets the minimum number of rows in the group
	 * 
	 * @param count
	 * @since 1.3
	 */
	public void setMinRowCount(int count) {
		this.minRowCount = count;
	}

	/**
	 * Tests for the maximum number of rows
	 * @return true if the number of rows is equal to or 
	 * greater than the maximum
	 * @since 1.3
	 */
	public boolean maxRowLimit() {
		return maxRowCount > 0 && genRowCount >= maxRowCount;
	}

	/**
	 * Tests for the minimum number of rows
	 * @return true if the number of rows is less than the minimum
	 * @since 1.3
	 */
	public boolean minRowLimit() {
		return minRowCount > 0 && genRowCount < minRowCount;
	}


}
