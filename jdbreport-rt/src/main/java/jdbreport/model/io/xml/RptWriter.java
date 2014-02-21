/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2010 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model.io.xml;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import and.util.Utilities;

import jdbreport.model.ReportBook;
import jdbreport.model.io.ReportWriter;
import jdbreport.model.io.SaveReportException;


/**
 * @version 2.0 17.02.2010
 * @author Andrey Kholmanskih
 *
 */
public class RptWriter implements ReportWriter  {


	private HashMap<String, Object> resMap;


	public RptWriter() {
		super();
	}
	
	protected ReportWriter createReportWriter() {
		return ReportBookParser.createReportWriter(this);
	}

	
	public void save(OutputStream out, ReportBook reportBook)
			throws SaveReportException {
		resMap = null;
		ZipOutputStream zipStream;
		try {
			zipStream = new ZipOutputStream(out);
			try {
				ZipEntry entry = new ZipEntry("mimetype"); //$NON-NLS-1$
				zipStream.putNextEntry(entry);
				zipStream.write(reportBook.getMimeType().getBytes());
				
				entry = new ZipEntry("repgrid.xml"); //$NON-NLS-1$
				zipStream.putNextEntry(entry);
				ReportWriter writer = createReportWriter();
				writer.save(zipStream, reportBook);

				if (resMap != null && resMap.size() > 0) {
					entry = new ZipEntry("resources/"); //$NON-NLS-1$
					zipStream.putNextEntry(entry);
					for (String fileName : resMap.keySet()) {
						entry = new ZipEntry("resources/" + fileName); //$NON-NLS-1$
						zipStream.putNextEntry(entry);
						Object res = resMap.get(fileName);
						if (res instanceof RenderedImage) {
							ImageIO.write((RenderedImage)res,
									Utilities.getFileExtension(fileName), zipStream);
						} else  if (res instanceof byte[]){
							zipStream.write((byte[])res);
						} else {
							zipStream.write(res.toString().getBytes("UTF-8"));
						}
					}
				}
			} finally {
				zipStream.close();
			}
		} catch (IOException e) {
			throw new SaveReportException(e);
		}
	}

	/**
	 * The method is not supported
	 */
	public void save(Writer writer, ReportBook reportBook)
			throws SaveReportException {
		throw new SaveReportException(Messages.getString("RptWriter.1")); //$NON-NLS-1$
	}

	public void save(File file, ReportBook reportBook)
			throws SaveReportException {
		try {
			save(new FileOutputStream(file), reportBook);
		} catch (FileNotFoundException e) {
			throw new SaveReportException(e);
		}
	}


	public String write(String fileName, Object resource) {
		if (resMap == null) {
			resMap = new HashMap<String, Object>();
		}
		resMap.put(fileName, resource);
		return fileName;
	}

}
