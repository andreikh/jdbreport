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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.event.EventListenerList;

import jdbreport.model.ReportException;
import jdbreport.util.Utils;

import and.dbcomp.DataSetParams;

/**
 * @version 2.0 21.01.2011
 * @author Andrey Kholmanskih
 * 
 */
public class BufferedDataSet implements MasterDataSet, DataSetListener {

	private ReportDataSet ds;

	private Map<String, Object> values = new HashMap<String, Object>();

	private boolean inCashe = false;

	private boolean eof = false;

	private boolean dsEof = false;

	protected EventListenerList listenerList = new EventListenerList();

	private boolean opened;

	private Map<String, Object> linkedParams;

	private boolean cursorChange;

	private Object currentObject;

	private Map<Object, Object> vars;

	public BufferedDataSet(ReportDataSet ds) {
		super();
		this.ds = ds;
	}

	public BufferedDataSet(ReportDataSet ds, Map<Object, Object> vars) {
		this(ds);
		this.vars = vars;
	}

	public void setVars(Map<Object, Object> vars) {
		this.vars = vars;
	}

	public Map<Object, Object> getVars() {
		return vars;
	}

	public void open() throws ReportException {
		if (isOpened())
			return;
		initValues();
		dsEof = !ds.hasNext();
		eof = !readToCashe();
		setOpened(true);
		fireDataSetCursorChanged();
	}

	/**
	 * @throws ReportException
	 */
	private void initValues() throws ReportException {
		if (values.size() == 0)
			for (String name : ds.getColumnNames()) {
				values.put(name, null);
			}
	}

	public boolean reopen() throws ReportException {
		inCashe = false;
//		dsEof = false;
		dsEof = !ds.reopen();
		initValues();
		eof = !readToCashe();
		setOpened(true);
		cursorChange = true;
		fireDataSetCursorChanged();
		return !eof;
	}

	private void setOpened(boolean b) {
		this.opened = b;
	}

	private boolean isOpened() {
		return opened;
	}

	private boolean readToCashe() {
		if (inCashe)
			return !eof;
		if (dsEof) {
			return false;
		}
		inCashe = true;
		try {
			Iterator<String> it = values.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				values.put(key, ds.getValue(key));
			}
			currentObject = ds.getCurrentObject();
			dsEof = !ds.next();
			return true;
		} catch (ReportException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean next() throws ReportException {
		if (!isOpened())
			throw new ReportException(MessageFormat.format(Messages
					.getString("BufferedDataSet.0"), getId())); //$NON-NLS-1$
		cursorChange = true;
		inCashe = false;
		eof = !readToCashe();
		if (!eof) {
			fireDataSetCursorChanged();
		}
		return !eof;
	}

	private String getFirstToken(String name) {
		int p = name.indexOf('.');
		if (p > 0) {
			return name.substring(0, p);
		}
		return name;
	}

	public Object getValue(String name) throws ReportException {
		return values.get(name);
	}

	public boolean findColumn(String name) {
		return values.containsKey(name);
	}

	public String getId() {
		return ds.getId();
	}

	public Object clone() {
		try {
			BufferedDataSet newDataSet = (BufferedDataSet) super.clone();
			newDataSet.ds = (ReportDataSet) this.ds.clone();
			newDataSet.values = new HashMap<String, Object>();
			return newDataSet;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Collection<String> getColumnNames() throws ReportException {
		return ds.getColumnNames();
	}

	public Object getNextValue(String name) throws ReportException {
		if (!dsEof)
			return ds.getValue(getFirstToken(name));
		else
			return null;
	}

	public boolean isDsEof() {
		return dsEof;
	}

	public boolean isEof() {
		return eof;
	}

	public void addDataSetListener(DataSetListener listener) {
		listenerList.add(DataSetListener.class, listener);
	}

	public void removeDataSetListener(DataSetListener listener) {
		listenerList.remove(DataSetListener.class, listener);
	}

	public void fireDataSetCursorChanged() {
		Object[] listeners = listenerList.getListenerList();
		if (listeners.length == 0)
			return;
		CursorChangedEvent e = new CursorChangedEvent(this);
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == DataSetListener.class) {
				((DataSetListener) listeners[i + 1]).cursorChange(e);
			}
		}
	}

	public DataSetParams getParams() throws ReportException {
		return ds.getParams();
	}

	private boolean validateParams(MasterDataSet masterDS)
			throws ReportException {
		if (linkedParams != null)
			return true;
		linkedParams = new HashMap<String, Object>();
		Collection<String> masterFields = masterDS.getColumnNames();
		for (int i = 0; i < getParams().size(); i++) {
			if (masterFields.contains(getParams().getName(i))) {
				linkedParams.put(getParams().getName(i), getParams()
						.getValue(i));
			}
		}
		if (linkedParams.size() == 0) {
			masterDS.removeDataSetListener(this);
			return false;
		}
		return true;
	}

	public boolean checkParamsChange(MasterDataSet masterDS) {
		boolean result = false;
		try {
			if (!validateParams(masterDS))
				return false;
			Iterator<String> it = linkedParams.keySet().iterator();
			while (it.hasNext()) {
				String name = it.next();
				Object paramValue = linkedParams.get(name);
				Object dsValue = masterDS.getValue(name);
				if (paramValue == null && dsValue == null)
					continue;
				boolean change = paramValue == null ? !dsValue
						.equals(paramValue) : !paramValue.equals(dsValue);
				if (change) {
					result = true;
					linkedParams.put(name, dsValue);
					getParams().setValue(name, dsValue);
				}
			}
		} catch (ReportException e) {
			Utils.showError(e);
			masterDS.removeDataSetListener(this);
			return false;
		}
		return result;
	}

	public void cursorChange(CursorChangedEvent evt) {
		boolean change = checkParamsChange(evt.getMasterDataSet());
		if (change) {
			try {
				reopen();
			} catch (ReportException e) {
				Utils.showError(e);
				evt.getMasterDataSet().removeDataSetListener(this);
			}
		}
	}

	public String getMasterId() {
		return ds == null ? null : ds.getMasterId();
	}

	public void resetCursorPos() {
		cursorChange = false;
	}

	public boolean isCursorChange() {
		return cursorChange;
	}

	public Object getCurrentObject() {
		return currentObject;
	}

	public boolean hasNext() {
		return !eof;
	}

}
