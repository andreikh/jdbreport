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
