/*
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2009 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model.io.xml.odf;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import and.util.Utilities;

import jdbreport.model.ReportBook;
import jdbreport.model.io.ReportWriter;
import jdbreport.model.io.SaveReportException;

/**
 * @version 1.3 15.08.2009
 * @author Andrey Kholmanskih
 * 
 */
public class OdsWriter implements ReportWriter, ImageWriter {

	private Map<String, RenderedImage> images;

	public OdsWriter() {
		super();
	}

	public void save(OutputStream out, ReportBook reportBook)
			throws SaveReportException {
		ZipOutputStream zipStream;
		try {
			zipStream = new ZipOutputStream(out);
			try {
				ZipEntry entry = new ZipEntry("mimetype"); //$NON-NLS-1$
				zipStream.putNextEntry(entry);
				mimeWrite(zipStream);

				entry = new ZipEntry("styles.xml"); //$NON-NLS-1$
				zipStream.putNextEntry(entry);
				getStylesWriter().save(zipStream, reportBook);

				entry = new ZipEntry("content.xml"); //$NON-NLS-1$
				zipStream.putNextEntry(entry);
				getContentWriter().save(zipStream, reportBook);

				entry = new ZipEntry("Pictures/"); //$NON-NLS-1$
				zipStream.putNextEntry(entry);
				if (images != null)
					for (String fileName : images.keySet()) {
						entry = new ZipEntry("Pictures/" + fileName); //$NON-NLS-1$
						zipStream.putNextEntry(entry);
						RenderedImage image = images.get(fileName);

						ImageIO.write(image,
								Utilities.getFileExtension(fileName), zipStream);
					}

				entry = new ZipEntry("settings.xml"); //$NON-NLS-1$
				zipStream.putNextEntry(entry);
				getSettingsWriter().save(zipStream, reportBook);

				entry = new ZipEntry("meta.xml"); //$NON-NLS-1$
				zipStream.putNextEntry(entry);
				getMetaWriter().save(zipStream, reportBook);

				entry = new ZipEntry("META-INF/manifest.xml"); //$NON-NLS-1$
				zipStream.putNextEntry(entry);
				writeManifest(zipStream);
			} finally {
				zipStream.close();
			}
		} catch (IOException e) {
			throw new SaveReportException(e);
		}
	}

	
	protected ReportWriter getMetaWriter() {
		return new OdfMetaWriter();
	}

	protected ReportWriter getSettingsWriter() {
		return new OdfSettingsWriter();
	}

	protected ReportWriter getStylesWriter() {
		return new OdsStylesWriter();
	}

	protected void writeManifest(OutputStream stream) {
		PrintWriter fw = new PrintWriter(new OutputStreamWriter(stream,
				java.nio.charset.Charset.forName("UTF-8"))); //$NON-NLS-1$
		try {
			fw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); //$NON-NLS-1$
			fw
					.println("<!DOCTYPE manifest:manifest PUBLIC \"-//OpenOffice.org//DTD Manifest 1.0//EN\" \"Manifest.dtd\">"); //$NON-NLS-1$
			fw
					.println("<manifest:manifest xmlns:manifest=\"urn:oasis:names:tc:opendocument:xmlns:manifest:1.0\">"); //$NON-NLS-1$
			fw
					.println("<manifest:file-entry manifest:media-type=\"application/vnd.oasis.opendocument.spreadsheet\" manifest:full-path=\"/\"/>"); //$NON-NLS-1$
			writeIconsManifest(fw);
			writeManifestFiles(fw);
			fw.println("</manifest:manifest>"); //$NON-NLS-1$
		} finally {
			fw.flush();
		}
	}

	protected void writeManifestFiles(PrintWriter fw) {
		fw
				.println("<manifest:file-entry manifest:media-type=\"\" manifest:full-path=\"Pictures/\"/>"); //$NON-NLS-1$
		fw
				.println("<manifest:file-entry manifest:media-type=\"text/xml\" manifest:full-path=\"content.xml\"/>"); //$NON-NLS-1$
		fw
				.println("<manifest:file-entry manifest:media-type=\"text/xml\" manifest:full-path=\"styles.xml\"/>"); //$NON-NLS-1$
		fw
				.println("<manifest:file-entry manifest:media-type=\"text/xml\" manifest:full-path=\"meta.xml\"/>"); //$NON-NLS-1$
		fw
				.println("<manifest:file-entry manifest:media-type=\"text/xml\" manifest:full-path=\"settings.xml\"/>"); //$NON-NLS-1$
	}

	protected void writeIconsManifest(PrintWriter fw) {
		if (images != null)
			for (String fileName : images.keySet()) {
				fw.print("<manifest:file-entry manifest:media-type=\"image/"); //$NON-NLS-1$
				fw.print(Utilities.getFileExtension(fileName));
				fw.print("\" manifest:full-path=\"Pictures/"); //$NON-NLS-1$
				fw.print(fileName);
				fw.println("\"/>"); //$NON-NLS-1$
			}
	}

	protected void mimeWrite(OutputStream stream) {
		PrintWriter fw = new PrintWriter(new OutputStreamWriter(stream,
				java.nio.charset.Charset.forName("UTF-8"))); //$NON-NLS-1$
		fw.print("application/vnd.oasis.opendocument.spreadsheet"); //$NON-NLS-1$
		fw.flush();
	}

	protected ReportWriter getContentWriter() {
		return new OdsContentWriter(this);
	}

	public void save(Writer writer, ReportBook reportBook)
			throws SaveReportException {
		throw new SaveReportException(Messages.getString("OdsWriter.24")); //$NON-NLS-1$
	}

	public void save(File file, ReportBook reportBook)
			throws SaveReportException {
		try {
			save(new FileOutputStream(file), reportBook);
		} catch (FileNotFoundException e) {
			throw new SaveReportException(e);
		}
	}

	public void writeIcon(String fileName, RenderedImage image) {
		if (images == null) {
			images = new HashMap<>();
		}
		images.put(fileName, image);
	}

	public String write(String fileName, Object resource) {
		if (resource instanceof RenderedImage) {
			writeIcon(fileName, (RenderedImage) resource);
			return fileName;
		}
		return "";
	}

}
