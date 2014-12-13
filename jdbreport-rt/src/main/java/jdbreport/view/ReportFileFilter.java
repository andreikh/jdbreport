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
package jdbreport.view;

import java.io.File;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.swing.filechooser.*;

import jdbreport.model.io.FileType;
import jdbreport.model.io.ReportReader;
import jdbreport.model.io.ReportWriter;

/**
 * @version 1.3 16.05.2009
 * @author Andrey Kholmanskih
 * 
 */
public class ReportFileFilter extends FileFilter {

	private Hashtable<String, ReportFileFilter> filters = null;

	private String description = null;

	private String fullDescription = null;

	private boolean useExtensionsInDescription = true;

	private FileType fileType;

	public ReportFileFilter(FileType fileType) {
		this.filters = new Hashtable<String, ReportFileFilter>();
		this.fileType = fileType;
		for (int i = 0; i < fileType.getExtensions().length; i++)
			addExtension(fileType.getExtensions()[i]);
		setDescription(fileType.getDescription());
	}

	public boolean accept(File f) {
		if (f != null) {
			if (f.isDirectory()) {
				return true;
			}
			if (filters.size() == 0) {
				return true;
			}
			String extension = getExtension(f);
			if (extension != null && filters.get(getExtension(f)) != null) {
				return true;
			}
			
		}
		return false;
	}

	/**
	 * Returns the extension portion of the file's name .
	 * 
	 */
	public String getExtension(File f) {
		if (f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1) {
				return filename.substring(i + 1).toLowerCase();
			}
			
		}
		return null;
	}

	/**
	 * Adds a filetype "dot" extension to filter against.
	 * 
	 * 
	 * Note that the "." before the extension is not needed and will be ignored.
	 */
	public void addExtension(String extension) {
		if (filters == null) {
			filters = new Hashtable<String, ReportFileFilter>(5);
		}
		filters.put(extension.toLowerCase(), this);
		fullDescription = null;
	}

	/**
	 * Returns the human readable description of this filter.
	 * 
	 */
	public String getDescription() {
		if (fullDescription == null) {
			if (description == null || isExtensionListInDescription()) {
				fullDescription = description == null ? "(" : description
						+ " (";
				String exts = "";
				Enumeration<String> extensions = filters.keys();
				if (extensions != null) {
					while (extensions.hasMoreElements()) {
						exts += ", "
								+ (String) extensions.nextElement();
					}
					exts = exts.substring(1);
				}
				fullDescription += exts + " )";
			} else {
				fullDescription = description;
			}
		}
		return fullDescription;
	}

	/**
	 * Sets the human readable description of this filter.
	 * 
	 */
	public void setDescription(String description) {
		this.description = description;
		fullDescription = null;
	}

	public void setExtensionListInDescription(boolean b) {
		useExtensionsInDescription = b;
		fullDescription = null;
	}

	public boolean isExtensionListInDescription() {
		return useExtensionsInDescription;
	}

	public ReportReader getReader() {
		return fileType.getReader();
	}

	public ReportWriter getWriter() {
		return fileType.getWriter();
	}

	public FileType getFileType() {
		return fileType;
	}

}
