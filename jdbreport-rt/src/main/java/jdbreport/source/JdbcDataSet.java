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

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;

import jdbreport.model.ReportException;

/**
 * @version 3.1 15.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class JdbcDataSet extends  AbstractDataSet {

	private String id;

	private String query;

	private String masterId;

	private DbSource source;

	private ResultSet rs;

	private PreparedStatement stat;

	private boolean eof = true;

	private Params params;

	private String preparedQuery;

	private Map<Object, Object> vars;
	private Collection<String> columns;

	public JdbcDataSet() {
		super();
	}

	public JdbcDataSet(String id) {
		super(id);
	}

	public void setVars(Map<Object, Object> vars) {
		this.vars = vars;
	}

	public boolean next() throws ReportException {
		if (isEof())
			return false;
		try {
			eof = !rs.next();
			return !eof;
		} catch (SQLException e) {
			eof = true;
			throw new ReportException(e);
		}
	}

	public boolean isEof() {
		return eof;
	}

	public Object getValue(int index) throws ReportException {
		try {
			return rs.getObject(index);
		} catch (SQLException e) {
			throw new ReportException(e);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param query
	 *            The query to set.
	 */
	public void setQuery(String query) {
		this.query = query.trim();
		preparedQuery = null;
	}

	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}

	public DbSource getSource() {
		return source;
	}

	public void setSource(DbSource source) {
		if (this.source != source) {
			this.source = source;
			preparedQuery = null;
			stat = null;
			rs = null;
		}
	}

	private void prepareParams() throws ReportException {
		params = new Params();
		preparedQuery = params.prepareParams(getQuery());
		if (!preparedQuery.toUpperCase().startsWith("SELECT ")) {
			preparedQuery = null;
			throw new ReportException(MessageFormat.format(Messages
					.getString("JdbcDataSet.invalid_query"), getQuery(), getId()));
		}
		initParams();
	}

	private void initParams() throws ReportException {
		if (vars != null) {
			for (int i = 0; i < params.size(); i++) {
				Object value = vars.get(params.getName(i));
				if (value != null) {
					params.setValue(i, value);
				}
			}
		}
	}

	private void prepare() throws ReportException {
		if (stat != null)
			return;
		try {
			if (preparedQuery == null)
				prepareParams();
			stat = source.getConnection().prepareStatement(preparedQuery);
			ParameterMetaData md = stat.getParameterMetaData();
			for (int i = 0; i < getParams().size(); i++) {
				getParams().setSQLType(i, md.getParameterType(i + 1));
			}
			columns = new HashSet<>();
			try {
				for (int i = 0; i < stat.getMetaData().getColumnCount(); i++) {
					columns.add(stat.getMetaData().getColumnName(i + 1));
				}
			} catch (SQLException e) {
				throw new ReportException(e);
			}
		} catch (Exception e) {
			throw new ReportException(e);
		}

	}

	public void open() throws ReportException {
		if (rs == null) {
			reopen();
		}
	}

	public boolean reopen() throws ReportException {
		prepare();
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			rs = null;
		}
		try {
			ParameterMetaData metaData = stat.getParameterMetaData();
			for (int i = 0; i < getParams().size(); i++) {
				Object value = getParams().getValue(i);
				if (value == null)
					stat.setNull(i + 1, getParams().getSQLType(i));
				else {
					if (metaData.getParameterType(i + 1) == Types.DATE && value instanceof String) {
						try {
							Date d = DateFormat.getDateInstance().parse((String)value);
							stat.setDate(i + 1, new java.sql.Date(d.getTime()));
						} catch (Exception e) {
							stat.setObject(i + 1, value);
						}
					} else {
						stat.setObject(i + 1, value);
					}
				}
			}
			rs = stat.executeQuery();
			eof = !rs.next();
			return !eof;
		} catch (SQLException e) {
			throw new ReportException(e);
		}
	}

	public void close() {
		if (rs == null)
			return;
		try {
			rs.close();
			stat.close();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.getMessage());
		} finally { 
			stat = null;
			rs = null;
			eof = true;
		}
	}

	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
		if (this.masterId != null && this.masterId.length() == 0) {
			this.masterId = null;
		}
	}

	public Object getValue(String name) throws ReportException {
		try {
			return eof ? null : rs.getObject(name);
		} catch (SQLException e) {
			throw new ReportException(e);
		}
	}

	@Override
	public Object getValue(Object current, String name) throws ReportException {
		if (current == null) return null;
		return ((Map)current).get(name);
	}

	@Override
	public boolean containsKey(String name) {
		return columns.contains(name);
	}

	public Collection<String> getColumnNames() throws ReportException {
		open();
		return columns;
	}

	public DataSetParams getParams() throws ReportException  {
		if (params == null) {
			prepare();
		}
		return params;
	}

	public Object getCurrentObject() throws ReportException {
		if (columns == null)
			open();
		HashMap<String, Object> map = new HashMap<>();
		for (String column : columns) {
			map.put(column, getValue(column));
		}
		return map;
	}

	public boolean hasNext() {
		if (preparedQuery == null)
			try {
				open();
			} catch (ReportException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		return !eof;
	}
}
