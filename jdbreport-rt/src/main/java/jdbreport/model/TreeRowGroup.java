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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import jdbreport.design.model.CellObject;

/**
 * @author Andrey Kholmanskih
 * @version 2.0 11.05.2011
 */
public abstract class TreeRowGroup extends AbstractGroup implements GroupsGroup {

    private List<Group> childList;

    public TreeRowGroup(GroupsGroup parent) {
        super(parent);
    }

    public RowsGroup getGroup(TableRow row) {
        if (row.isNull())
            return null;
        for (int i = 0; i < getChildCount(); i++) {
            RowsGroup group = getChild(i).getGroup(row);
            if (group != null)
                return group;
        }
        return null;
    }

    public abstract int addGroup(int index, Group group);

    protected Group findGroup(int type) {
        for (int i = 0; i < getChildList().size(); i++) {
            if ((getChild(i)).getType() == type) {
                return getChild(i);
            }
        }
        return null;
    }

    public int getChildCount() {
        if (childList == null)
            return 0;
        else
            return childList.size();
    }

    public boolean remove(Object child) {
        boolean result = getChildList().remove(child);
        if (getChildCount() == 0 && getParent() != null) {
            getParent().remove(this);
        }
        return result;
    }

    public int getRowCount() {
        int count = getChildCount();
        if (count == 0)
            return 0;
        if (!isVisible()) {
            for (int i = 0; i < count; i++) {
                if (getChild(i).getRowCount() > 0)
                    return 1;
            }
            return 0;
        }
        int result = 0;
        for (int i = 0; i < count; i++) {
            result += getChild(i).getRowCount();
        }
        return result;
    }

    public Group getChild(int index) {
        if (index >= 0 && index < getChildCount())
            return getChildList().get(index);
        return null;
    }

    public TableRow getFirstGroupRow() {
        if (getChildCount() == 0)
            return null;
        int i = 0;
        while (i < getChildCount()
                && getChildList().get(i).getChildCount() == 0) {
            i++;
        }
        if (i < getChildCount())
            return getChildList().get(i).getFirstGroupRow();
        return null;
    }

    public Group getGroup(int type) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildList().get(i).getType() == type) {
                return getChildList().get(i);
            }
        }
        return null;
    }

    public int getHeight() {
        int result = 0;
        Iterator<Group> it = getGroupIterator();
        while (it.hasNext()) {
            result += it.next().getHeight();
        }
        return result;
    }

    protected RowsGroup createRowGroup(int type) {
        return new BaseRowGroup(this, type);
    }

    protected List<Group> getChildList() {
        if (childList == null) {
            childList = new ArrayList<>();
        }
        return childList;
    }

    public int getChildIndex(Object child) {
        return getChildList().indexOf(child);
    }

    public Iterator<TableRow> iterator() {
        return new RowIterator(getChildList());
    }

    public Iterator<TableRow> getVisibleRowIterator() {
        return new VisibleRowIterator(getChildList());
    }

    public Iterator<Group> getGroupIterator() {
        return getChildList().iterator();
    }

    public Iterator<RowsGroup> getAllGroupIterator() {
        return new AllGroupIterator(getChildList());
    }

    public double getTotalResult(int func, int column) {
        double result = 0;
        switch (func) {
            case CellObject.AF_SUM:
                result = 0;
                break;
            case CellObject.AF_MAX:
                result = Double.MIN_VALUE;
                break;
            case CellObject.AF_MIN:
                result = Double.MAX_VALUE;
                break;
        }
        for (int i = 0; i < getChildList().size(); i++) {
            int type = getChild(i).getType();
            if (type == ROW_DETAIL || type == GROUP_DETAIL || type == ROW_NONE) {
                switch (func) {
                    case CellObject.AF_SUM:
                        result += getChild(i)
                                .getTotalResult(func, column);
                        break;
                    case CellObject.AF_MAX:
                        result = Math.max(result, getChild(i)
                                .getTotalResult(func, column));
                        break;
                    case CellObject.AF_MIN:
                        result = Math.min(result, getChild(i)
                                .getTotalResult(func, column));
                        break;
                }
            }
        }
        return result;
    }

    private static class RowIterator implements Iterator<TableRow> {

        List<Group> list;
        int index = 0;
        Iterator<TableRow> it;
        Object current;

        public RowIterator(List<Group> list) {
            this.list = list;
        }

        public boolean hasNext() {
            if (it != null && it.hasNext())
                return true;
            int i = index;
            while (i < list.size()) {
                Object child = list.get(i);
                Iterator<TableRow> tempIt = ((Group) child).iterator();
                if (tempIt.hasNext()) {
                    return true;
                }
                index = i;
                i++;
            }
            return false;
        }

        public TableRow next() {
            if (it != null) {
                if (it.hasNext()) {
                    current = it.next();
                    return (TableRow) current;
                } else
                    it = null;
            }
            while (index < list.size()) {
                current = list.get(index++);
                it = ((Group) current).iterator();
                if (it.hasNext()) {
                    current = it.next();
                    return (TableRow) current;
                } else
                    it = null;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            if (it != null) {
                it.remove();
                return;
            }
            if (current != null) {
                index--;
                list.remove(index);
                current = null;
            } else
                throw new IllegalStateException();
        }

    }

    private class VisibleRowIterator implements Iterator<TableRow> {

        List<Group> list;
        int index = 0;
        Iterator<TableRow> it;
        Object current;

        public VisibleRowIterator(List<Group> list) {
            this.list = list;
        }

        public boolean hasNext() {
            if (!isVisible() && current != null)
                return false;
            if (it != null && it.hasNext())
                return true;
            int i = index;
            while (i < list.size()) {
                Group child = list.get(i);
                Iterator<TableRow> tempIt = child.getVisibleRowIterator();
                if (tempIt.hasNext()) {
                    return true;
                }
                index = i;
                i++;
            }
            return false;
        }

        public TableRow next() {
            if (!isVisible() && current != null)
                throw new NoSuchElementException();
            if (it != null) {
                if (it.hasNext()) {
                    current = it.next();
                    return (TableRow) current;
                } else
                    it = null;
            }
            while (index < list.size()) {
                Group group = list.get(index++);
                it = group.getVisibleRowIterator();
                if (it.hasNext()) {
                    current = it.next();
                    return (TableRow) current;
                } else
                    it = null;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            if (it != null) {
                it.remove();
                return;
            }
            if (current != null) {
                index--;
                list.remove(index);
                current = null;
            } else
                throw new IllegalStateException();
        }

    }

    private static class AllGroupIterator implements Iterator<RowsGroup> {

        List<Group> list;
        int index = 0;
        Iterator<RowsGroup> it;
        Group current;

        public AllGroupIterator(List<Group> list) {
            this.list = list;
        }

        public boolean hasNext() {
            if (it != null && it.hasNext())
                return true;
            int i = index;
            while (i < list.size()) {
                Object child = list.get(i);
                if (child instanceof RowsGroup) {
                    return true;
                }
                if (child instanceof GroupsGroup) {
                    Iterator<RowsGroup> tempIt = ((GroupsGroup) child)
                            .getAllGroupIterator();
                    if (tempIt.hasNext()) {
                        return true;
                    }
                }
                index = i;
                i++;
            }
            return false;
        }

        public RowsGroup next() {
            if (it != null) {
                if (it.hasNext()) {
                    current = it.next();
                    return (RowsGroup) current;
                } else
                    it = null;
            }
            while (index < list.size()) {
                current = list.get(index++);
                if (current instanceof RowsGroup)
                    return (RowsGroup) current;
                if (current instanceof GroupsGroup) {
                    it = ((GroupsGroup) current).getAllGroupIterator();
                    if (it.hasNext()) {
                        current = it.next();
                        return (RowsGroup) current;
                    } else
                        it = null;
                }
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            if (it != null) {
                it.remove();
                return;
            }
            if (current != null) {
                index--;
                list.remove(index);
                current = null;
            } else
                throw new IllegalStateException();
        }

    }

}
