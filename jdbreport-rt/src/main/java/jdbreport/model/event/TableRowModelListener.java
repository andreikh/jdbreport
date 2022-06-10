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
	void rowAdded(TableRowModelEvent e);

	void rowRemoved(TableRowModelEvent e);

	void rowMoved(TableRowModelEvent e);

	void rowResized(TableRowModelEvent e);

	void rowMarginChanged(ChangeEvent e);

	void rowSelectionChanged(ListSelectionEvent e);

	void rowUpdated(TableRowModelEvent e);

}
