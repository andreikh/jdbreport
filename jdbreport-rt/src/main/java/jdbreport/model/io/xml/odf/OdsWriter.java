/*
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2014 Andrey Kholmanskih
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

import jdbreport.model.ReportBook;
import jdbreport.model.io.ReportWriter;
import jdbreport.model.io.SaveReportException;
import jdbreport.util.Utils;

/**
 * @version 3.0 12.12.2014
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
				ZipEntry entry = new ZipEntry("mimetype");
				zipStream.putNextEntry(entry);
				mimeWrite(zipStream);

				entry = new ZipEntry("styles.xml");
				zipStream.putNextEntry(entry);
				getStylesWriter().save(zipStream, reportBook);

				entry = new ZipEntry("content.xml");
				zipStream.putNextEntry(entry);
				getContentWriter().save(zipStream, reportBook);

				entry = new ZipEntry("Pictures/");
				zipStream.putNextEntry(entry);
				if (images != null)
					for (String fileName : images.keySet()) {
						entry = new ZipEntry("Pictures/" + fileName);
						zipStream.putNextEntry(entry);
						RenderedImage image = images.get(fileName);

						ImageIO.write(image,
								Utils.getFileExtension(fileName), zipStream);
					}

				entry = new ZipEntry("settings.xml");
				zipStream.putNextEntry(entry);
				getSettingsWriter().save(zipStream, reportBook);

				entry = new ZipEntry("meta.xml");
				zipStream.putNextEntry(entry);
				getMetaWriter().save(zipStream, reportBook);

				entry = new ZipEntry("META-INF/manifest.xml");
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
				java.nio.charset.Charset.forName("UTF-8")));
		try {
			fw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			fw
					.println("<!DOCTYPE manifest:manifest PUBLIC \"-//OpenOffice.org//DTD Manifest 1.0//EN\" \"Manifest.dtd\">"); //$NON-NLS-1$
			fw
					.println("<manifest:manifest xmlns:manifest=\"urn:oasis:names:tc:opendocument:xmlns:manifest:1.0\">"); //$NON-NLS-1$
			fw
					.println("<manifest:file-entry manifest:media-type=\"application/vnd.oasis.opendocument.spreadsheet\" manifest:full-path=\"/\"/>"); //$NON-NLS-1$
			writeIconsManifest(fw);
			writeManifestFiles(fw);
			fw.println("</manifest:manifest>");
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
				fw.print("<manifest:file-entry manifest:media-type=\"image/");
				fw.print(Utils.getFileExtension(fileName));
				fw.print("\" manifest:full-path=\"Pictures/");
				fw.print(fileName);
				fw.println("\"/>");
			}
	}

	protected void mimeWrite(OutputStream stream) {
		PrintWriter fw = new PrintWriter(new OutputStreamWriter(stream,
				java.nio.charset.Charset.forName("UTF-8")));
		fw.print("application/vnd.oasis.opendocument.spreadsheet");
		fw.flush();
	}

	protected ReportWriter getContentWriter() {
		return new OdsContentWriter(this);
	}

	public void save(Writer writer, ReportBook reportBook)
			throws SaveReportException {
		throw new SaveReportException(Messages.getString("OdsWriter.24"));
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
