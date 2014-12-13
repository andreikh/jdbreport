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

import jdbreport.model.ReportBook;
import jdbreport.model.io.ReportWriter;
import jdbreport.model.io.SaveReportException;
import jdbreport.util.Utils;


/**
 * @version 3.0 12.12.2014
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
									Utils.getFileExtension(fileName), zipStream);
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
			resMap = new HashMap<>();
		}
		resMap.put(fileName, resource);
		return fileName;
	}

}
