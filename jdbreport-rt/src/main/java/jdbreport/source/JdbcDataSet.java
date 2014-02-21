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
package jdbreport.source;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import jdbreport.model.ReportException;

import and.dbcomp.DataSetParams;
import and.dbcomp.DbSource;
import and.dbcomp.Params;

/**
 * @version 2.0 07.02.2011
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
		if (!preparedQuery.toUpperCase().startsWith("SELECT ")) { //$NON-NLS-1$
			preparedQuery = null;
			throw new ReportException(MessageFormat.format(Messages
					.getString("JdbcDataSet.1"), getQuery(), getId())); //$NON-NLS-1$
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
			e.printStackTrace();
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
			e.printStackTrace();
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

	public Collection<String> getColumnNames() throws ReportException {
		open();
		Collection<String> result = new ArrayList<String>();
		try {
			for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
				result.add(rs.getMetaData().getColumnName(i + 1));
			}
		} catch (SQLException e) {
			throw new ReportException(e);
		}
		return result;
	}

	public DataSetParams getParams() throws ReportException  {
		if (params == null) {
			prepare();
		}
		return params;
	}

	public Object getCurrentObject() {
		return null;
	}

	public boolean hasNext() {
		return !eof;
	}
}
