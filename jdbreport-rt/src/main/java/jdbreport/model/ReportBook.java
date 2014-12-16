/*
 * Created on 28.02.2005
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
package jdbreport.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;

import jdbreport.model.event.ReportListEvent;
import jdbreport.model.event.ReportListListener;
import jdbreport.model.event.TableRowModelEvent;
import jdbreport.model.event.TableRowModelListener;
import jdbreport.model.io.FileType;
import jdbreport.model.io.LoadReportException;
import jdbreport.model.io.ReportReader;
import jdbreport.model.io.ReportWriter;
import jdbreport.model.math.MathValue;
import jdbreport.model.svg.SVGImage;
import jdbreport.util.Utils;
import jdbreport.view.model.JReportModel;

/**
 * @version 3.0 13.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class ReportBook implements Iterable<ReportModel>, TableRowModelListener {

	public static final String HTML = "html";
	public static final String HTML_BODY = "html_body";
	public static final String ODS = "ods";
	public static final String ODT = "odt";
	public static final String JRPT = "jrpt";
	public static final String EXCEL = "excel_xml";
	public static final String XML = "xml";
	public static final String PDF = "pdf";
	public static final String XLS = "xls";
	public static final String XLSX = "xlsx";
	public static final String DOCX = "docx";
	public static boolean enableSVG;

	protected static DateFormat dateFormatter = DateFormat.getDateInstance();

	protected static TreeMap<Object, String> WRITERS_MAP = new TreeMap<>();

	protected static TreeMap<Object, String> READERS_MAP = new TreeMap<>();

	public static final String CURRENT_VERSION = "9";

	private static final Logger logger = Logger.getLogger(ReportBook.class
			.getName());

	static {
		try {
			enableSVG = SVGImage.isEnableSVG();
		} catch (Throwable e) {
			enableSVG = false;
		}

		WRITERS_MAP.put(JRPT, "jdbreport.model.io.xml.RptFileType");
		WRITERS_MAP.put(ODS, "jdbreport.model.io.xml.odf.OdsFileType");
		WRITERS_MAP.put(ODT, "jdbreport.model.io.xml.odf.OdtFileType");
		try {
			if (Class.forName("org.apache.poi.hssf.usermodel.HSSFWorkbook") != null) {
				WRITERS_MAP.put(XLS, "jdbreport.model.io.xls.poi.XlsFileType");
				logger.info("Support MS Excel 2003 with POI is added");
				if (Class.forName("org.apache.poi.xssf.usermodel.XSSFWorkbook") != null) {
					WRITERS_MAP.put(XLSX,
							"jdbreport.model.io.xls.poi.XlsxFileType");
					logger.info("Support MS Excel 2007 with POI is added");
				}
			}
		} catch (NoClassDefFoundError | ClassNotFoundException ignored) {
		}
        try {
			Class.forName("com.lowagie.text.Document");
			WRITERS_MAP.put(PDF, "jdbreport.model.io.pdf.itext2.PdfFileType");
			logger.info("Support PDF with iText 2.1.7 is added");
		} catch (ClassNotFoundException e) {
			try {
				Class.forName("com.itextpdf.text.Document");
				WRITERS_MAP.put(PDF,
						"jdbreport.model.io.pdf.itext5.PdfFileType");
				logger.info("Support PDF with iText 5.x is added");
			} catch (ClassNotFoundException ignored) {
			}
		}

		WRITERS_MAP.put(HTML, "jdbreport.model.io.html.HTMLFileType");
		WRITERS_MAP.put(HTML_BODY, "jdbreport.model.io.html.HTMLBodyFileType");
		WRITERS_MAP.put(EXCEL, "jdbreport.model.io.xml.excel.ExcelFileType");

		READERS_MAP.put(JRPT, "jdbreport.model.io.xml.RptFileType");
		READERS_MAP.put(ODS, "jdbreport.model.io.xml.odf.OdsFileType");
		READERS_MAP.put(XML, "jdbreport.model.io.xml.XMLFileType");

		if (MathValue.isEnableMathMl()) {
			logger.info("Support MathML with JEuclid");
		}
	}

	public static boolean isEnableSVG() {
		return enableSVG;
	}

	public static boolean pdfExists() {
		return WRITERS_MAP.containsKey(PDF);
	}

	public static DateFormat getDateFormatter() {
		return dateFormatter;
	}

	private List<ReportModel> list = new ArrayList<>();

	private Map<Object, CellStyle> styleList;

	protected EventListenerList listenerList = new EventListenerList();

	private String reportCaption;

	private PropertyChangeSupport changeSupport;

	private int lockUpdate = 0;

	private String creator;

	private Date creationDate;

	private boolean showGrid = true;

	private boolean globalPageNumber = true;

	private String sourceTemplate;

	private boolean printThroughPdf = false;

	public ReportBook() {
		styleList = new HashMap<>();
		add();
	}

	protected Map<Object, String> getReaders() {
		return READERS_MAP;
	}

	protected Map<Object, String> getWriters() {
		return WRITERS_MAP;
	}

	/**
	 * 
	 * @param key file type
	 * @return true if exists FileType by key
	 * @since 1.3
	 */
	public static boolean fileTypeExists(String key) {
		return WRITERS_MAP.containsKey(key);
	}

	/**
	 * 
	 * @param key file type
	 * @return FileType by key
	 * @since 1.3
	 */
	public static FileType getFileTypeClass(String key) {
		try {
			String className = WRITERS_MAP.get(key);
			if (className != null) {
                return (FileType) Class.forName(className)
                        .newInstance();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Returns the value that represents the superclass of the ReportReader by
	 * key
	 * 
	 * @param key
	 *            key whose associated ReportReader is to be returned. It is
	 *            usually extension of Report file
	 * @return ReportReader
	 */
	public ReportReader getReaderClass(Object key) {
		try {
			String className = getReaders().get(key);
			if (className != null) {
				FileType fileType = (FileType) Class.forName(className)
						.newInstance();
				return fileType.getReader();
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Returns the value that represents the superclass of the ReportWriter by
	 * key
	 * 
	 * @param key
	 *            key whose associated ReportWriter is to be returned. It is
	 *            usually extension of Report file
	 * @return ReportWriter
	 */
	public ReportWriter getWriterClass(Object key) {
		try {
			String className = getWriters().get(key);
			if (className != null) {
				FileType fileType = (FileType) Class.forName(className)
						.newInstance();
				return fileType.getWriter();
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Puts the name of the class that represents the superclass of the
	 * ReportReader to READERS_MAP
	 * 
	 * @param key
	 *            key whose associated ReportReader is to be returned. it is
	 *            usually extension of Report file
	 * @param className
	 *            ReportReader class name
	 */
	public void putReaderClassName(Object key, String className) {
		getReaders().put(key, className);
	}

	/**
	 * Puts the name of the class that represents the superclass of the
	 * ReportWriter to WRITERS_MAP
	 * 
	 * @param key
	 *            ReportWriter key it is usually extension of Report file
	 * @param className
	 *            ReportWriter class name
	 */
	public void putWriterClassName(Object key, String className) {
		getWriters().put(key, className);
	}

	/**
	 * Returns the name of the class representing the superclass of the
	 * ReportReader by key
	 * 
	 * @param key
	 *            ReportReader key it is usually extension of Report file
	 * @return ReportReader class name
	 */
	public String getReaderClassName(Object key) {
		return getReaders().get(key);
	}

	/**
	 * Returns the name of class representing the superclass of the ReportWriter
	 * by key
	 * 
	 * @param key
	 *            ReportWriter key it is usually extension of Report file
	 * @return the name of class
	 */
	public String getWriterClassName(Object key) {
		return getWriters().get(key);
	}

	/**
	 * Returns collection of names of classes representing the superclass of the
	 * ReportReader
	 * 
	 * @return collection of names of classes
	 */
	public Collection<String> getReaderNames() {
		return getReaders().values();
	}

	/**
	 * Returns collection of the names of classes representing the superclass of
	 * the ReportWriter
	 * 
	 * @return collection of names of classes
	 */
	public Collection<String> getWriterNames() {
		return getWriters().values();
	}

	protected String getDefaultReaderKey() {
		return JRPT;
	}

	protected String getDefaultWriterKey() {
		return JRPT;
	}

	protected void lockUpdate() {
		lockUpdate++;
		for (ReportModel model : list) {
			model.startUpdate();
		}
	}

	protected void unlockUpdate() {
		if (lockUpdate > 0)
			lockUpdate--;
		for (ReportModel model : list) {
			model.endUpdate();
		}
	}

	protected boolean isLocked() {
		return lockUpdate > 0;
	}

	/**
	 * Appends the ReportModel to the end of the list of the ReportModel
	 * 
	 * @param reportModel
	 *            ReportModel instance
	 * @return the index of the last element in the list of the ReportModel
	 */
	public int add(ReportModel reportModel) {
		if (list.add(reportModel)) {
			revalidatePageNumbers();
			reportModel.getRowModel().addRowModelListener(this);
			int toIndex = list.size() - 1;
			fireReportAdded(new ReportListEvent(this, -1, toIndex));
			return toIndex;
		}
		return -1;
	}

	/**
	 * Appends the default ReportModel to the end of the list of the ReportModel
	 * 
	 * @return the index of the last element in the list of the ReportModel
	 */
	public int add() {
		return this.add(doCreateDefaultModel());
	}

	/**
	 * Inserts the element representing the ReportModel at the specified
	 * position in the list of the ReportModel
	 * 
	 * @param index
	 *            index at which the reportModel is to be inserted.
	 * @param reportModel
	 *            element instance of the ReportModel to be inserted
	 */
	public void add(int index, ReportModel reportModel) {
		list.add(index, reportModel);
		revalidatePageNumbers();
		reportModel.setStyleList(getStyleList());
		reportModel.getRowModel().addRowModelListener(this);
		fireReportAdded(new ReportListEvent(this, 0, index));
	}

	/**
	 * Appends the other reportbook
	 * 
	 * @param book ReportBook
	 * @since 2.0
	 */
	public void add(ReportBook book) {
		add(book, size());
	}

	/**
	 * Appends the other reportbook
	 * 
	 * @param book ReportBook
	 * @param index insert index
	 * @since 2.0
	 */
	public void add(ReportBook book, int index) {
		for (Object key : book.getStyleList().keySet()) {
			CellStyle style = book.getStyleList().get(key);
			if (getStyleList().containsKey(key)) {

				Object newKey = getStyleList().size();
				while (getStyleList().containsKey(newKey)
						|| book.getStyleList().containsKey(newKey)) newKey = (Integer) newKey + 1;
				style.setId(newKey);
				book.replaceStyles(key, newKey);
				getStyleList().put(newKey, style);
			} else {
				getStyleList().put(key, style);
			}
		}
		for (int i = 0; i < book.size(); i++) {
			add(index + i, book.getReportModel(i));
		}
	}

	/**
	 * Removes the ReportModel
	 * 
	 * @param index
	 *            the index of the ReportModel to removed.
	 * @return the removed ReportModel
	 */
	public ReportModel remove(int index) {
		ReportModel model = list.remove(index);
		revalidatePageNumbers();
		model.getRowModel().removeRowModelListener(this);
		fireReportRemoved(new ReportListEvent(this, index, 0));
		return model;
	}

	/**
	 * Removes the ReportModel
	 * 
	 * @param model
	 *            to be removed from list.
	 * @return true if list contained the specified model.
	 */
	public boolean remove(ReportModel model) {
		int index = list.indexOf(model);
		boolean result = list.remove(model);
		if (result) {
			revalidatePageNumbers();
			model.getRowModel().removeRowModelListener(this);
			fireReportRemoved(new ReportListEvent(this, index, 0));
		}
		return result;
	}

	/**
	 * Revalidate numbers of pages
	 */
	protected void revalidatePageNumbers() {
		int firstPage = 1;
        for (ReportModel aList : list) {
            TableRowModel rowModel = aList.getRowModel();
            rowModel.setFirstPageNumber(firstPage);
            if (isGlobalPageNumber()) {
                firstPage += rowModel.getPageCount();
            }
        }

	}

	/**
	 * Returns type of numbering of pages
	 * 
	 * @return true if pages have the common numbering of pages
	 * @since 1.4
	 */
	public boolean isGlobalPageNumber() {
		return globalPageNumber;
	}

	/**
	 * Sets type of numbering of pages If globalPageNumber - it is true, the
	 * common indexing of pages in all report is set, differently in each sheet
	 * numbering starts with 1
	 * 
	 * @param globalPageNumber  if true, then indexing of pages in all report
	 * @since 1.4
	 */
	public void setGlobalPageNumber(boolean globalPageNumber) {
		this.globalPageNumber = globalPageNumber;
		revalidatePageNumbers();
	}

	/**
	 * Returns the ReportModel at the specified position in this list.
	 * 
	 * @param index
	 *            index of ReportModel to return.
	 * @return the ReportModel at the specified position in this list.
	 */
	public JReportModel getReportModel(int index) {
		return (JReportModel) list.get(index);
	}

	/**
	 * Returns count of the ReportModel in the list
	 * 
	 * @return count of the ReportModel in the list
	 */
	public int size() {
		return list.size();
	}

	/**
	 * Moves the ReportModel from fromIndex to the position toIndex
	 * 
	 * @param fromIndex
	 *            the index of ReportModel to be moved
	 * @param toIndex
	 *            the new index of the ReportModel
	 */
	public void move(int fromIndex, int toIndex) {
		ReportModel o = list.remove(fromIndex);
		list.add(toIndex, o);
		fireReportMoved(new ReportListEvent(this, fromIndex, toIndex));
	}

	/**
	 * Returns the CellStyle by the index
	 * 
	 * @param index
	 *            the id of the CellStyle
	 * @return the CellStyle
	 */
	public CellStyle getStyles(Object index) {
		if (index != null) {
			Object o = styleList.get(index);
			if (o != null)
				return (CellStyle) o;
		}
		return CellStyle.getDefaultStyle();
	}

	/**
	 * Removes all the report models. Removes all the styles
	 * 
	 */
	public void clear() {
		while (size() > 0) {
			remove(size() - 1);
		}
		styleList.clear();
	}

	public void replaceStyles(Object oldId, Object newId) {
		for (int n = 0; n < size(); n++) {
			getReportModel(n).replaceStyleID(oldId, newId);
		}
	}

	/**
	 * 
	 * Removes styles that are not used
	 * 
	 */
	private void validateStyles() {
		Iterator<Object> keyIt = styleList.keySet().iterator();
		while (keyIt.hasNext()) {
			Object key = keyIt.next();
			boolean styleExists = false;
			if (styleList.get(key) != null)
				for (int n = 0; n < size(); n++) {
					if (getReportModel(n).findStyleID(key)) {
						styleExists = true;
						break;
					}
				}
			if (!styleExists) {
				keyIt.remove();
			}
		}
	}

	/**
	 * Returns ReportReader's or ReportWriter's key by extension of the file
	 * 
	 * @param filename
	 *            the file's name
	 * @return the ReportReader's or ReportWriter's key
	 */
	public String getKeyByFile(String filename) {
		return Utils.getFileExtension(filename).toLowerCase();
	}

	/**
	 * Saves the report in the file
	 * 
	 * @param file
	 *            the file in which the report will be saved
	 * @throws IOException 
	 */
	public void save(File file) throws IOException {
		ReportWriter writer = getWriterClass(getKeyByFile(file.getName()));
		if (writer == null)
			writer = getWriterClass(getDefaultWriterKey());
		if (writer != null) {
			save(file, writer);
		}
	}

	/**
	 * Saves the report in the file
	 * 
	 * @param file
	 *            the file in which the report will be saved
	 * @param writer
	 *            the ReportWriter that is used to save the report
	 * @throws IOException 
	 */
	public void save(File file, ReportWriter writer) throws IOException {
		try {
			File tmpFile = new File(file.getPath() + "~"); //$NON-NLS-1$
			File tmpFile2 = new File(file.getPath() + "~~"); //$NON-NLS-1$
			try {
				validateStyles();
				writer.save(tmpFile, this);
				tmpFile2.delete();
				if (file.exists() && !file.renameTo(tmpFile2)) {
					throw new IOException("Can't rename file " + file.getPath()
							+ " to " + tmpFile2.getPath());
				}
				if (tmpFile.renameTo(file)) {
					tmpFile2.delete();
				} else {
					tmpFile2.renameTo(file);
					throw new IOException("Can't rename file "
							+ tmpFile.getPath() + " to " + file.getPath());
				}
			} finally {
				tmpFile.delete();
			}
		} catch (Throwable e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw new IOException(e);
		}
	}

	/**
	 * Opens the report by the fileName
	 * 
	 * @param fileName
	 *            the file's name
	 * @throws LoadReportException
	 */
	public void open(String fileName) throws LoadReportException {
		File file = new File(fileName);
		open(file);
	}

	/**
	 * Creates a new report
	 * 
	 */
	public void newReport() {
		clear();
		add();
		setReportCaption(null);
	}

	/**
	 * Opens the report by the file The ReportReader is determined by extension
	 * of the file
	 * 
	 * @param file
	 *            the report's file
	 * @throws LoadReportException
	 */
	public void open(File file) throws LoadReportException {
		ReportReader reader = getReaderClass(getKeyByFile(file.getName()));
		if (reader == null) {
			reader = getReaderClass(getDefaultReaderKey());
		}
		if (reader != null) {
			try {
				open(file, reader);
			} catch (Exception e) {
				throw new LoadReportException(e);
			}
		} else
			throw new LoadReportException(
					Messages.getString("ReportBook.unknow_ext")); //$NON-NLS-1$
	}

	/**
	 * Opens the report from the file
	 * 
	 * @param file
	 *            the report's file
	 * @param reader
	 *            the ReportReader that is used to read the report
	 * @throws LoadReportException
	 */
	public void open(File file, ReportReader reader) throws LoadReportException {
		setSourceTemplate(file.getPath());
		try {
			open(new FileInputStream(file), reader);
		} catch (FileNotFoundException e) {
			throw new LoadReportException(e);
		}
	}

	/**
	 * Opens the report from the URL The ReportReader is determined by extension
	 * of the file
	 * 
	 * @param url
	 *            the report's URL
	 * @throws LoadReportException
	 */
	public void open(URL url) throws LoadReportException {
		ReportReader reader = getReaderClass(getKeyByFile(url.getFile()));
		if (reader != null) {
			try {
				open(url, reader);
			} catch (Exception e) {
				throw new LoadReportException(e);
			}
		} else
			throw new LoadReportException(
					Messages.getString("ReportBook.unknow_ext"));
	}

	/**
	 * Opens the report from URL
	 * 
	 * @param url
	 *            the report's URL
	 * @param reader
	 *            the ReportReader that is used to read the report
	 * @throws LoadReportException
	 */
	public void open(URL url, ReportReader reader) throws LoadReportException {
		setSourceTemplate(url.toString());
		try {
			open(url.openStream(), reader);
		} catch (IOException e) {
			throw new LoadReportException(e);
		}
	}

	/**
	 * Opens the report from the stream
	 * 
	 * @param stream
	 *            the report's stream
	 * @param reader
	 *            the ReportReader that is used to read the report
	 * @throws LoadReportException
	 */
	public void open(InputStream stream, ReportReader reader)
			throws LoadReportException {
		lockUpdate();
		try {
			try {
				reader.load(stream, this);
			} finally {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			doAfterLoad();
		} finally {
			unlockUpdate();
		}
	}

	/**
	 * @since 1.4
	 */
	protected void doAfterLoad() {
		revalidatePageNumbers();
	}

	/**
	 * Opens the report from the bytes
	 * 
	 * @param buf
	 *            the bytes' array
	 * @param readerId
	 *            the ReportReader's id
	 * @throws LoadReportException
	 */
	public void open(byte[] buf, String readerId) throws LoadReportException {
		ReportReader reader = getReaderClass(readerId);
		if (reader != null) {
			open(new ByteArrayInputStream(buf), reader);
		}
	}

	protected ReportModel doCreateDefaultModel() {
		JReportModel model = (JReportModel) createDefaultModel();
		if (isLocked())
			model.startUpdate();
		return model;
	}

	protected ReportModel createDefaultModel() {
		return new JReportModel(1, 1, styleList);
	}

	public void addReportListListener(ReportListListener x) {
		listenerList.add(ReportListListener.class, x);
	}

	public void removeReportListListener(ReportListListener x) {
		listenerList.remove(ReportListListener.class, x);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param e
	 *            the event received
	 * @see EventListenerList
	 */
	protected void fireReportAdded(ReportListEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ReportListListener.class) {
				((ReportListListener) listeners[i + 1]).reportAdded(e);
			}
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param e
	 *            the event received
	 * @see EventListenerList
	 */
	protected void fireReportRemoved(ReportListEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ReportListListener.class) {
				((ReportListListener) listeners[i + 1]).reportRemoved(e);
			}
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param e
	 *            the event received
	 */
	protected void fireReportMoved(ReportListEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 1; i >= 0; i -= 1) {
			if (listeners[i] == ReportListListener.class) {
				((ReportListListener) listeners[i + 1]).reportMoved(e);
			}
		}
	}

	/**
	 * Returns the report's title
	 * 
	 * @return the report's title
	 */
	public String getReportCaption() {
		if (reportCaption == null && getReportModel(0) != null) {
			return getReportModel(0).getReportTitle();
		}
		return reportCaption;

	}

	/**
	 * Sets the report's title
	 * 
	 * @param value
	 *            the new report's title
	 */
	public void setReportCaption(String value) {
		if (reportCaption == null || !this.reportCaption.equals(value)) {
			String oldValue = this.reportCaption;
			this.reportCaption = value;
			firePropertyChange("reportCaption", oldValue, value); //$NON-NLS-1$
		}
	}

	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		PropertyChangeSupport changeSupport = this.changeSupport;
		if (changeSupport == null || oldValue == newValue) {
			return;
		}
		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	public synchronized void addPropertyChangeListener(
			PropertyChangeListener listener) {
		if (listener == null) {
			return;
		}
		if (changeSupport == null) {
			changeSupport = new PropertyChangeSupport(this);
		}
		changeSupport.addPropertyChangeListener(listener);
	}

	public synchronized void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		if (listener == null) {
			return;
		}
		if (changeSupport == null) {
			changeSupport = new PropertyChangeSupport(this);
		}
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public synchronized void removePropertyChangeListener(
			PropertyChangeListener listener) {
		if (listener == null || changeSupport == null) {
			return;
		}
		changeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * Returns the styles' count
	 * 
	 * @return the styles' count
	 */
	public int getStylesCount() {
		return styleList.size();
	}

	/**
	 * Looks for style in the list of styles if doesn't find, adds it to the
	 * list otherwise returns the id of the founded style If style's id is null,
	 * a new id is appropriated to the style
	 * 
	 * @param style
	 *            adding style
	 * @return the style's id
	 */
	public Object addStyle(CellStyle style) {
		for (Object key : styleList.keySet()) {
			if (styleList.get(key).equals(style))
				return key;
		}
		Object id = style.getId();
		int count = styleList.size();
		if (id == null)
			id = count;
		while (styleList.containsKey(id)) {
			id = ++count;
		}
		style.setId(id);
		styleList.put(id, style);
		return id;
	}

	/**
	 * Adds the style to the list of styles. If style's id is null or style's id
	 * exists in the list, a new id is appropriated to the style
	 * 
	 * @param style
	 *            adding style
	 * @return a new style's id
	 */
	public Object appendStyle(CellStyle style) {
		Object id = style.getId();
		if (id == null || styleList.containsKey(id)) {
			id = styleList.size();
			while (styleList.containsKey(id))
                id = (Integer) id + 1;
		}
		style.setId(id);
		styleList.put(id, style);
		return id;
	}

	/**
	 * Returns the styles' map
	 * 
	 * @return the styles' map
	 */
	public Map<Object, CellStyle> getStyleList() {
		return styleList;
	}

	/**
	 * Returns an iterator over the ReportModel
	 * 
	 * @return an iterator over the ReportModel
	 */
	public Iterator<ReportModel> iterator() {
		return list.iterator();
	}

	/**
	 * Returns the name of the report's creator
	 * 
	 * @return report's creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * Sets the name of the report's creator
	 * 
	 * @param creator
	 *            report's creator
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * Returns the date of report's creating
	 * 
	 * @return the date of report's creating
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Sets the date of report's creating
	 * 
	 * @param date
	 *            the date of report's creating
	 */
	public void setCreationDate(Date date) {
		this.creationDate = date;
	}

	/**
	 * Sets whether the report draws lines between cells. If show is true it
	 * does; if it is false it doesn't.
	 * 
	 * @param b
	 *            true if report view should draw lines
	 */
	public void setShowGrid(boolean b) {
		boolean old = showGrid;
		showGrid = b;
		firePropertyChange("showGrid", old, b);
	}

	/**
	 * Returns true if the report draws lines between cells, false if it
	 * doesn't. The default is true.
	 * 
	 * @return true if the report draws lines between cells, false if it doesn't
	 */
	public boolean isShowGrid() {
		return showGrid;
	}

	public void removeDoubleBorders(ReportModel model) {
		for (int row = 0; row < model.getRowCount(); row++) {
			TableRow tableRow = model.getRowModel().getRow(row);
			for (int column = 0; column < tableRow.getColCount(); column++) {
				Cell cell = tableRow.getCellItem(column);
				if (!cell.isNull() && !cell.isChild()) {
					CellStyle style = getStyles(cell.getStyleId());
					if (style.getBorders(Border.LINE_LEFT) != null
							&& column > 0) {
						Cell otherCell = tableRow.getCellItem(column - 1);
						if (otherCell.isChild())
							otherCell = otherCell.getOwner();
						CellStyle otherStyle = getStyles(otherCell.getStyleId());
						if (otherStyle.getBorders(Border.LINE_RIGHT) != null) {

							cell.setStyleId(addStyle(style.deriveBorder(
									Border.LINE_LEFT, null)));
							style = getStyles(cell.getStyleId());
						}
					}
					if (style.getBorders(Border.LINE_TOP) != null && row > 0) {
						Cell otherCell = model.getReportCell(row - 1, column);
						if (otherCell.isChild())
							otherCell = otherCell.getOwner();
						CellStyle otherStyle = getStyles(otherCell.getStyleId());
						if (otherStyle.getBorders(Border.LINE_BOTTOM) != null) {
							cell.setStyleId(addStyle(style.deriveBorder(
									Border.LINE_TOP, null)));
						}
					}

				}
			}

		}
	}

	public void rowAdded(TableRowModelEvent e) {
		if (!e.isDraging()) {
			revalidatePageNumbers();
		}
	}

	public void rowMarginChanged(ChangeEvent e) {
		revalidatePageNumbers();
	}

	public void rowMoved(TableRowModelEvent e) {
	}

	public void rowRemoved(TableRowModelEvent e) {
		if (!e.isDraging()) {
			revalidatePageNumbers();
		}
	}

	public void rowResized(TableRowModelEvent e) {
		if (!e.isDraging()) {
			revalidatePageNumbers();
		}
	}

	public void rowSelectionChanged(ListSelectionEvent e) {
	}

	public void rowUpdated(TableRowModelEvent e) {
		if (!e.isDraging()) {
			revalidatePageNumbers();
		}
	}

	public void setShowPageNumber(boolean show) {
		for (ReportModel model : list) {
			model.getRowModel().setShowPageNumber(show);
		}
	}

	public String getMimeType() {
		return "application/jdbreport";
	}

	public void updateRowAndPageHeight(HeightCalculator hCalc) {
		for (ReportModel model : list) {
			model.updateRowAndPageHeight(hCalc);
		}
	}

	public String getSourceTemplate() {
		return sourceTemplate;
	}

	public void setSourceTemplate(String sourceTemplate) {
		this.sourceTemplate = sourceTemplate;
	}

	/**
	 * Printing through conversion to PDF
	 * @return true if the sets printing through conversion to PDF
	 * @since 2.0
	 */
	public boolean isPrintThroughPdf() {
		return printThroughPdf;
	}

	/**
	 * Printing through conversion to PDF
	 * @param printThroughPdf boolean
	 * @since 2.0
	 */
	public void setPrintThroughPdf(boolean printThroughPdf) {
		this.printThroughPdf = printThroughPdf;
	}

}
