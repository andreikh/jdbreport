/*
 * Created on 01.03.2005
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
package jdbreport.model.event;

import java.util.EventListener;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public interface ReportListListener extends EventListener {

	/** Tells listeners that a report was added to the list. */
	public void reportAdded(ReportListEvent e);

	/** Tells listeners that a report was removed from the list. */
	public void reportRemoved(ReportListEvent e);

	/** Tells listeners that a row was repositioned. */
	public void reportMoved(ReportListEvent e);

}
