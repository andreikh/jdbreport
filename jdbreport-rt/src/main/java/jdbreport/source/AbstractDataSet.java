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
package jdbreport.source;

import java.util.logging.Logger;

import jdbreport.model.ReportException;
import and.dbcomp.DataSetParams;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public abstract class AbstractDataSet implements ReportDataSet {

	protected static final Logger logger = Logger.getLogger(AbstractDataSet.class
			.getName());

	private String id;

	protected AbstractDataSet() {
	}

	public AbstractDataSet(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public boolean next() throws ReportException {
		return false;
	}

	public DataSetParams getParams() throws ReportException {
		return null;
	}

	public String getMasterId() {
		return null;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
