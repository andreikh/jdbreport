/*
 * Created on 22.09.2004
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2014 Andrey Kholmanskih
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
	void rowAdded(TableRowModelEvent e);

	/** Tells listeners that a row was removed from the model. */
	void rowRemoved(TableRowModelEvent e);

	/** Tells listeners that a row was repositioned. */
	void rowMoved(TableRowModelEvent e);

	/** Tells listeners that a row was resized. */
	void rowResized(TableRowModelEvent e);

	/** Tells listeners that a row was moved due to a margin change. */
	void rowMarginChanged(ChangeEvent e);

	/**
	 * Tells listeners that the selection model of the TableRowModel changed.
	 */
	void rowSelectionChanged(ListSelectionEvent e);

	/** Tells listeners that a rows was change. */
	void rowUpdated(TableRowModelEvent e);

}
