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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.EventListenerList;

import jdbreport.model.ReportException;
import jdbreport.util.Utils;

/**
 * @version 3.1 15.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class BufferedDataSet implements MasterDataSet, DataSetListener {

	private ReportDataSet ds;

	private boolean inCache = false;

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
		eof = !readToCache();
		setOpened(true);
		fireDataSetCursorChanged();
	}

	/**
	 * @throws ReportException
	 */
	private void initValues() throws ReportException {
		currentObject = null;
	}

	public boolean reopen() throws ReportException {
		inCache = false;
		dsEof = !ds.reopen();
		initValues();
		eof = !readToCache();
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

	private boolean readToCache() {
		if (inCache)
			return !eof;
		if (dsEof) {
			return false;
		}
		inCache = true;
		try {
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
					.getString("BufferedDataSet.not_opened"), getId()));
		cursorChange = true;
		inCache = false;
		eof = !readToCache();
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
		return ds.getValue(currentObject, name);
	}

	@Override
	public Object getValue(Object current, String name) throws ReportException {
		return ds.getValue(current, name);
	}

	@Override
	public boolean containsKey(String name) {
		return ds.containsKey(name);
	}

	public boolean findColumn(String name) {
		return ds.containsKey(name);
	}

	public String getId() {
		return ds.getId();
	}

	public Object clone() {
		try {
			BufferedDataSet newDataSet = (BufferedDataSet) super.clone();
			newDataSet.ds = (ReportDataSet) this.ds.clone();
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
		linkedParams = new HashMap<>();
		Collection<String> masterFields = masterDS.getColumnNames();
		for (int i = 0; i < getParams().size(); i++) {
			String paramName = getParams().getName(i);
			if (masterFields.contains(paramName)) {
				linkedParams.put(paramName, getParams()
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
			for (String name : linkedParams.keySet()) {
				Object paramValue = linkedParams.get(name);
				Object dsValue = masterDS.getValue(name);
				if (paramValue == null && dsValue == null)
					continue;
				boolean change = (paramValue != null && !paramValue.equals(dsValue))
						|| (dsValue != null && !dsValue.equals(paramValue));
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
