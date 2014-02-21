/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2009 Andrey Kholmanskih. All rights reserved.
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
