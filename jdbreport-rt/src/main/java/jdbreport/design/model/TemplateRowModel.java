/*
 * Created on 15.03.2005
 *
 * JDBReport Generator
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
package jdbreport.design.model;

import java.util.ArrayList;
import java.util.Iterator;

import jdbreport.model.BaseRowGroup;
import jdbreport.model.DetailGroup;
import jdbreport.model.Group;
import jdbreport.model.GroupsGroup;
import jdbreport.model.ReportRowModel;
import jdbreport.model.RootGroup;
import jdbreport.model.RowsGroup;
import jdbreport.model.TableRow;
import jdbreport.model.TreeRowGroup;
import jdbreport.model.event.TableRowModelEvent;

/**
 * @version 3.0 13.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class TemplateRowModel extends ReportRowModel {

	private static final long serialVersionUID = 1974898737188203078L;

	static private TemplateNullRow templateNullRow = new TemplateNullRow();

	public TemplateRowModel() {
		super();
		setCanHideGroup(true);
	}

	@Override
	public TableRow createTableRow() {
		return new TemplateRow(getColCount());
	}

	protected void updateHeaderValue() {
		int i = 1;
		Iterator<RowsGroup> it = getRootGroup().getAllGroupIterator();
		while (it.hasNext()) {
			RowsGroup group = it.next();
			if (group.getChildCount() > 0) {
				TableRow row = group.getChild(0);
				row.setHeaderValue("" + i++ + " " + group.getHeaderValue());
				for (int n = 1; n < group.getChildCount(); n++) {
					group.getChild(n).setHeaderValue("" + i++);
				}
			}
		}
		setDirtyHeader(false);
	}

	@Override
	protected RootGroup createRootGroup() {
		return new TemplateRootGroup();
	}

	@Override
    public void clearPageHeader(int startRow) {
	}

	@Override
	public void updatePages(int startRow, int pageHeight) {
	}

	private static class TypeTableRow {
		public TableRow row;
		public int index;

		public TypeTableRow(TableRow row, int index) {
			this.row = row;
			this.index = index;
		}
	}

	/**
	 * Sets type of the selected rows
	 * 
	 * @param selectedRows
	 *            - the selected rows
	 * @param type
	 *            - the row's type
	 */
	public void setRowType(int[] selectedRows, int type) {
		if (selectedRows.length == 0)
			return;
		ArrayList<TypeTableRow> list = new ArrayList<>();
		TableRow row;
		BaseRowGroup destGroup;
		int first;

		if (findLikeGroup(selectedRows, type))
			return;

		switch (type) {
		case Group.ROW_GROUP_HEADER:
		case Group.ROW_GROUP_FOOTER:
			if (findDetailRowGroup(selectedRows, type))
				return;
		case Group.ROW_DETAIL:
			if (findDetailGroup(selectedRows, type))
				return;
			findNoneGroup(selectedRows, type);
			return;
		case Group.ROW_NONE:
		default:
			destGroup = (BaseRowGroup) getRootGroup().getGroup(type);
			if (destGroup == null)
				destGroup = (BaseRowGroup) getRootGroup().addGroup(type);
			first = getGroupRowIndex(destGroup);
		}
		disableSpan();
		try {//todo
			int n = 0;
			for (int selectedRow : selectedRows) {
				row = getRow(selectedRows[n]);
				BaseRowGroup srcGroup = (BaseRowGroup) getRootGroup().getGroup(
						row);
				if (srcGroup.getType() != type) {
					list.add(new TypeTableRow(row, selectedRows[n]));
					removeRows(1, selectedRows[n]);
				} else
					n++;
			}
			for (int i = 0; i < list.size(); i++) {
				int index;
				TypeTableRow typeRow = list.get(i);
				if (typeRow.index < first)
					index = i;
				else
					index = -1;
				addRow(destGroup, index, typeRow.row);
			}
		} finally {
			enableSpan();
		}

		updateCellChild(destGroup);
	}

	/**
	 * @param selectedRows rows number array
	 * @param type group type
	 */
	private boolean findLikeGroup(int[] selectedRows, int type) {
		BaseRowGroup destGroup = null;
		int index = 0;
		for (int i = 0; i < selectedRows.length; i++) {
			Group group = getGroup(selectedRows[i]);
			if (group.getType() == type) {
				destGroup = (BaseRowGroup) group;
				index = i;
				break;
			}
		}
		if (selectedRows[0] > 0) {
			Group group = getGroup(selectedRows[0] - 1);
			if (group.getType() == type) {
				destGroup = (BaseRowGroup) group;
				index = -1;
			}
		}
		if (selectedRows[selectedRows.length - 1] + 1 < getRowCount()) {
			Group group = getGroup(selectedRows[selectedRows.length - 1] + 1);
			if (group.getType() == type) {
				destGroup = (BaseRowGroup) group;
				index = selectedRows.length;
			}
		}
		if (destGroup != null) {
			moveRowsToGroup(destGroup, selectedRows, index);
			return true;
		}
		return false;
	}

	private void moveRowsToGroup(RowsGroup destGroup, int[] rows, int index) {
		if (index > 0)
			for (int i = index - 1; i >= 0; i--) {
				TableRow tableRow = rowList.get(rows[i]);
				Group group = getGroup(tableRow);
				group.remove(tableRow);
				destGroup.addRow(0, tableRow);
			}
		for (int i = index + 1; i < rows.length; i++) {
			TableRow tableRow = rowList.get(rows[i]);
			Group group = getGroup(tableRow);
			if (group != destGroup) {
				group.remove(tableRow);
				destGroup.addRow(tableRow);
			}
		}
		hideGroup(getRootGroup());
		showGroup(getRootGroup());
		updateHeaderValue();
	}

	private boolean findDetailRowGroup(int[] selectedRows, int type) {
		int findType = Group.ROW_DETAIL;
		DetailGroup destGroup = null;
		for (int selectedRow : selectedRows) {
			Group group = getGroup(selectedRow);
			if (group.getType() == findType) {
				destGroup = (DetailGroup) group.getParent();
				break;
			}
		}
		if (selectedRows[0] > 0) {
			Group group = getGroup(selectedRows[0] - 1);
			if (group.getType() == findType) {
				destGroup = (DetailGroup) group.getParent();
			}
		}
		if (selectedRows[selectedRows.length - 1] + 1 < getRowCount()) {
			Group group = getGroup(selectedRows[selectedRows.length - 1] + 1);
			if (group.getType() == findType) {
				destGroup = (DetailGroup) group.getParent();
			}
		}
		if (destGroup != null) {
			moveRowsToDetailGroup(destGroup, selectedRows, type);
			return true;
		}
		return false;
	}

	private void moveRowsToDetailGroup(DetailGroup destGroup, int[] rows,
			int type) {
		BaseRowGroup newGroup = (BaseRowGroup) destGroup.addGroup(type);
		if (type == Group.ROW_GROUP_HEADER || type == Group.ROW_DETAIL)
			for (int row : rows) {
				TableRow tableRow = rowList.get(row);
				Group group = getGroup(tableRow);
				group.remove(tableRow);
				newGroup.addRow(tableRow);
			}
		else if (type == Group.ROW_GROUP_FOOTER)
			for (int i = rows.length - 1; i >= 0; i--) {
				TableRow tableRow = rowList.get(rows[i]);
				Group group = getGroup(tableRow);
				group.remove(tableRow);
				newGroup.addRow(0, tableRow);
			}
		hideGroup(getRootGroup());
		showGroup(getRootGroup());
		updateHeaderValue();
	}

	private boolean findDetailGroup(int[] selectedRows, int type) {
		DetailGroup destGroup = null;
		int findType = Group.GROUP_DETAIL;
		for (int selectedRow : selectedRows) {
			Group group = getGroup(selectedRow).getParent();
			if (group.getType() == findType) {
				destGroup = (DetailGroup) group;
				break;
			}
		}
		if (selectedRows[0] > 0 && type == Group.ROW_GROUP_FOOTER) {
			Group group = getGroup(selectedRows[0] - 1).getParent();
			if (group.getType() == findType) {
				destGroup = (DetailGroup) group;
			}
		}
		if (selectedRows[selectedRows.length - 1] + 1 < getRowCount()
				&& type == Group.ROW_GROUP_HEADER) {
			Group group = getGroup(selectedRows[selectedRows.length - 1] + 1)
					.getParent();
			if (group.getType() == findType) {
				destGroup = (DetailGroup) group;
			}
		}
		if (destGroup == null) {
			destGroup = (DetailGroup) getRootGroup().addGroup(Group.GROUP_DETAIL);
		}
		if (destGroup != null) {
			moveRowsToDetailGroup(destGroup, selectedRows, type);
			return true;
		}
		return false;
	}

	private boolean findNoneGroup(int[] selectedRows, int type) {
		DetailGroup destGroup = null;
		Group group = getGroup(selectedRows[0]);
		if (group.getType() == Group.ROW_TITLE
				|| group.getType() == Group.ROW_PAGE_HEADER
				|| selectedRows[0] == 0) {
			if (((RootGroup) group.getParent()).getDetailGroups().size() > 0) {
				destGroup =  ((RootGroup) group.getParent()).getDetailGroups().get(0);
			} 
		} else {
			group = getGroup(selectedRows[selectedRows.length - 1]);
			if (group.getType() == Group.ROW_FOOTER
					|| group.getType() == Group.ROW_PAGE_FOOTER
					|| selectedRows[selectedRows.length - 1] == getRowCount() - 1) {
				destGroup = ((RootGroup) group.getParent()).getDetailGroups()
				.get(((RootGroup)group.getParent()).getDetailGroups().size() - 1);
			} else {
				TableRow tableRow = getRow(selectedRows[0]);
				group = getGroup(tableRow);
				splitGroup((RowsGroup) group, tableRow);
				group = getGroup(tableRow);
				int index = group.getParent().getChildIndex(group);
				destGroup = ((RootGroup) group.getParent()).createDetailGroup();
				((RootGroup) group.getParent()).addGroup(index, destGroup);
			}
		}
		if (destGroup != null) {
			moveRowsToDetailGroup(destGroup, selectedRows, type);
			return true;
		}
		return false;
	}

	protected TableRow getNullRow() {
		return templateNullRow;
	}

	public void moveDraggedRow(int rowIndex, int newIndex) {
		if (rowIndex != newIndex && rowIndex >= 0 && newIndex >= 0) {
			TableRow tableRow = rowList.remove(rowIndex);
			if (newIndex < 0 || newIndex >= rowList.size()) {
				rowList.add(tableRow);
			} else
				rowList.add(newIndex, tableRow);
		}
		fireRowMoved(new TableRowModelEvent(this, rowIndex, newIndex));
	}

	public void moveRow(int rowIndex, int newIndex) {
		if (newIndex >= rowList.size()) {
			newIndex = rowList.size() - 1;
		} else if (newIndex < 0) {
			newIndex = 0;
		}
		if (rowIndex != newIndex) {
			TableRow tableRow = rowList.get(rowIndex);
			if (tableRow == null) {
				return;
			}
			Group group = getGroup(tableRow);
			if (group != null && isCollapse(group)) {
				if (newIndex > rowIndex) {
					newIndex++;
				}
				moveGroup((Group) collapsedGroup(group), (int) newIndex);
				return;
			}
			Group otherGroup = getGroup(newIndex);
			// if (group != otherGroup) return;
			disableSpan();
			try {
				rowList.remove(rowIndex);
				if (group != null) {
					group.remove(tableRow);
				}
				setDirtyHeader(true);
				int newIndexGroup = otherGroup.getChildIndex(getRow(newIndex));
				rowList.add(newIndex, tableRow);
				((BaseRowGroup) otherGroup).addRow(newIndexGroup, tableRow);

			} finally {
				enableSpan();
			}
		}
		fireRowMoved(new TableRowModelEvent(this, rowIndex, newIndex));
	}

	private Group collapsedGroup(Group group) {
		Group grp = group;
		while (grp != null && grp.isVisible()) {
			grp = grp.getParent();
		}
		if (grp != null)
			return grp;
		else if (group != null && group.getRowCount() == 1) {
			if (group.getParent() != null
					&& group.getParent().getType() == Group.GROUP_DETAIL
					&& group.getParent().getRowCount() == 1) {
				return group.getParent();
			}
			return group;
		} else
			return null;

	}

	private void moveGroup(Group group, int newRowIndex) {
		setDirtyHeader(true);
		if (newRowIndex <= 0 || newRowIndex >= rowList.size()) {
			moveGroup(group, newRowIndex, getRootGroup());
			return;
		}
		TableRow row = getRow(newRowIndex);
		Group targetGroup = getGroup(row);
		if (targetGroup != null) {
			disableSpan();
			try {
				int newGroupIndex;
				switch (targetGroup.getType()) {
				case Group.ROW_TITLE:
				case Group.ROW_PAGE_HEADER:
					removeGroup(group);
					newGroupIndex = getRootGroup().getChildIndex(
							getRootGroup().getGroup(Group.ROW_PAGE_HEADER)) + 1;
					addGroup(getRootGroup(), newGroupIndex,
							group);
					break;
				case Group.GROUP_DETAIL:
					removeGroup(group);
					newGroupIndex = getRootGroup().getChildIndex(targetGroup) + 1;
					addGroup(getRootGroup(), newGroupIndex,
							group);
					break;
				case Group.ROW_FOOTER:
				case Group.ROW_PAGE_FOOTER:
					removeGroup(group);
					newGroupIndex = getRootGroup().getChildIndex(
							getRootGroup().getGroup(targetGroup.getType()));
					addGroup(getRootGroup(), newGroupIndex,
							group);
					break;
				case Group.ROW_DETAIL:
				case Group.ROW_GROUP_HEADER:
				case Group.ROW_GROUP_FOOTER:
					TreeRowGroup parentGroup = (TreeRowGroup) targetGroup
							.getParent();
					removeGroup(group);
					if (group.getType() == Group.GROUP_DETAIL)
						newGroupIndex = parentGroup.getChildCount() - 1;
					else
						newGroupIndex = parentGroup.getChildCount();
					addGroup(parentGroup, newGroupIndex, group);
					break;
				case Group.ROW_NONE:
					TreeRowGroup pGroup = (TreeRowGroup) targetGroup
							.getParent();
					removeGroup(group);
					if (pGroup == getRootGroup()) {
						targetGroup = getGroup(row);
						splitGroup((RowsGroup) targetGroup, row);
						newGroupIndex = getRootGroup().getChildIndex(
								targetGroup) + 1;
					} else {
						newGroupIndex = pGroup.getChildCount();
					}
					addGroup(pGroup, newGroupIndex, group);
					break;
				}
			} finally {
				enableSpan();
			}
			fireRowUpdated();
		}

	}


	private void splitGroup(RowsGroup targetGroup, TableRow row) {
		TreeRowGroup parent = (TreeRowGroup) targetGroup.getParent();
		int index = targetGroup.getChildIndex(row);
		if (index >= targetGroup.getChildCount() - 1)
			return;
		if (index == 0)
			index = 1;
		RowsGroup newGroup = new BaseRowGroup(parent, Group.ROW_NONE);
		for (int i = targetGroup.getChildCount() - 1; i >= index; i--) {
			TableRow tableRow = targetGroup.remove(i);
			newGroup.addRow(0, tableRow);
		}
		parent.addGroup(parent.getChildIndex(targetGroup) + 1, newGroup);
	}

	int insertDetailGroup(int rowIndex) {
		GroupsGroup detailGroup;
		if (rowIndex >= 0) {
			TableRow tableRow = getRow(rowIndex);
			Group group = getGroup(tableRow);
			if (group.getParent().getType() == Group.GROUP_DETAIL)
				detailGroup = (GroupsGroup) group.getParent().addGroup(
						Group.GROUP_DETAIL);
			else
				switch (group.getType()) {
				case Group.ROW_NONE:
					splitGroup((RowsGroup) group, tableRow);
					int groupIndex = group.getParent().getChildIndex(group) + 1;
					detailGroup = getRootGroup()
							.createDetailGroup();
					getRootGroup().addGroup(groupIndex,
							detailGroup);
					break;
				case Group.ROW_TITLE:
				case Group.ROW_PAGE_HEADER:
					detailGroup = getRootGroup()
							.createDetailGroup();
					getRootGroup().addGroup(2, detailGroup);
					break;
				default:
					detailGroup = (GroupsGroup) getRootGroup().addGroup(
							Group.GROUP_DETAIL);
				}
		} else
			detailGroup = (GroupsGroup) getRootGroup().addGroup(
					Group.GROUP_DETAIL);
		RowsGroup rowGroup = (RowsGroup) detailGroup
				.addGroup(Group.ROW_GROUP_HEADER);
		int index = addRow(rowGroup, 0);
		rowGroup = (RowsGroup) detailGroup.addGroup(Group.ROW_DETAIL);
		addRow(rowGroup, 0);
		rowGroup = (RowsGroup) detailGroup.addGroup(Group.ROW_GROUP_FOOTER);
		addRow(rowGroup, 0);
		return index;
	}

	/**
	 * 
	 * @param count
	 *            the rows count
	 * @param ind
	 *            the row's index
	 * @param type
	 *            the group's type
	 * @return the inserting row's index
	 */
	public int addRows(int count, int ind, int type) {
		int index = ind;
		if (count <= 0) {
			return index;
		}
		disableSpan();
		try {
			if (index < 0) {
				index = getRowCount();
			}
			TableRow tableRow = getRow(index);
			RowsGroup group = getGroup(tableRow);
			if (group == null || group.getType() != type) {
				switch (type) {
				case Group.ROW_NONE:
					if (group != null
							&& group.getParent().getType() == Group.GROUP_DETAIL) {
						group = (RowsGroup) group.getParent().addGroup(type);
					} else {
						group = (RowsGroup) getRootGroup().addGroup(type);
						moveGroup(group, index);
					}
					break;
				case Group.ROW_GROUP_HEADER:
				case Group.ROW_DETAIL:
				case Group.ROW_GROUP_FOOTER:
					if (group != null
							&& group.getParent().getType() == Group.GROUP_DETAIL) {
						group = (RowsGroup) group.getParent().addGroup(type);
					} else {
						if (group != null && group.getType() == Group.ROW_NONE) {
							splitGroup(group, tableRow);
							int groupIndex = group.getParent().getChildIndex(
									group) + 1;
							DetailGroup detailGroup = getRootGroup()
									.createDetailGroup();
							 getRootGroup().addGroup(
									 groupIndex, detailGroup);
							group = (RowsGroup) detailGroup.addGroup(type);
						} else {
							group = (RowsGroup) getRootGroup().addGroup(type);
						}
					}
					break;
				default:
					group = (RowsGroup) getRootGroup().addGroup(type);
					break;
				}
				if (group != null) {
					index = group.getChildCount();
				} else {
					return -1;
				}
			} else {
				index = group.getChildIndex(getRow(index));
			}
			Group gr = group;
			while (gr != null) {
				setVisibleGroup(gr, true);
				gr = gr.getParent();
			}
			for (int i = 0; i < count; i++) {
				index = addRow(group, index);
			}
		} finally {
			enableSpan();
		}
		return index - count + 1;
	}

}
