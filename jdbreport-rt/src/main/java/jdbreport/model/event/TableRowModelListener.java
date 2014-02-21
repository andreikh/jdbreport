/*
 * Created on 22.09.2004
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2008 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model.event;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ChangeEvent;

/**
 * TableRowModelListener defines the interface for an object that listens to
 * changes in a TableRowModel.
 * 
 * @see TableRowModelEvent
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public interface TableRowModelListener extends java.util.EventListener {
	/** Tells listeners that a row was added to the model. */
	public void rowAdded(TableRowModelEvent e);

	/** Tells listeners that a row was removed from the model. */
	public void rowRemoved(TableRowModelEvent e);

	/** Tells listeners that a row was repositioned. */
	public void rowMoved(TableRowModelEvent e);

	/** Tells listeners that a row was resized. */
	public void rowResized(TableRowModelEvent e);

	/** Tells listeners that a row was moved due to a margin change. */
	public void rowMarginChanged(ChangeEvent e);

	/**
	 * Tells listeners that the selection model of the TableRowModel changed.
	 */
	public void rowSelectionChanged(ListSelectionEvent e);

	/** Tells listeners that a rows was change. */
	public void rowUpdated(TableRowModelEvent e);

}
