/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2012 Andrey Kholmanskih. All rights reserved.
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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class JdbcReportSource extends DbSource implements
		Iterable<JdbcDataSet>, Cloneable {

	private static final long serialVersionUID = 1L;
	private List<JdbcDataSet> dsList;

	public JdbcReportSource() {
		this("Default");
		setAutoCommit(false);
	}

	public JdbcReportSource(String name) {
		super();
		setAlias(name);
		dsList = new ArrayList<>();
	}

	@Override
	public Object clone() {
		JdbcReportSource source = null;
		try {
			source = (JdbcReportSource) super.clone();
			source.setConnection(null);
			source.dsList = new ArrayList<>();
			for (JdbcDataSet ds : dsList) {
				JdbcDataSet newDs = (JdbcDataSet) ds.clone();
				newDs.setSource(source);
				source.dsList.add(newDs);
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return source;
	}

	@Override
	public void setConnection(Connection connection) {
		super.setConnection(connection);
		for (JdbcDataSet ds : getDataSetList()) {
			ds.close();
		}
	}

	public int getDataSetCount() {
		return dsList.size();
	}

	public void add(JdbcDataSet ds) {
		ds.setSource(this);
		dsList.add(ds);
	}

	public JdbcDataSet remove(int index) {
		JdbcDataSet ds = dsList.remove(index);
		ds.setSource(null);
		return ds;
	}

	public void remove(JdbcDataSet ds) {
		ds.setSource(null);
		dsList.remove(ds);
	}

	public JdbcDataSet getDataSet(int index) {
		return dsList.get(index);
	}

	public Iterator<JdbcDataSet> iterator() {
		return dsList.iterator();
	}

	public List<JdbcDataSet> getDataSetList() {
		return dsList;
	}
}
