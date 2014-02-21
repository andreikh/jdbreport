/*
 * DocxWriter.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2010 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model.io.xml.ooxml;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
 * @author Andrey Kholmanskih
 * 
 * @version 1.0 02.06.2010
 */
public class DocxWriter implements ReportWriter {

	private Map<String, RenderedImage> images;
	private String countPage;
	private String countWords;
	private String countCharacters;
	private String countLines;
	private String countParagraphs;
	private String charWithSpaces;
	private ReportBook reportBook;

	public void save(OutputStream out, ReportBook reportBook)
			throws SaveReportException {
		this.reportBook = reportBook;
		ZipOutputStream zipStream;
		try {
			zipStream = new ZipOutputStream(out);
			try {
				ZipEntry entry = new ZipEntry("[Content_Types].xml"); //$NON-NLS-1$
				zipStream.putNextEntry(entry);
				writeContentTypes(zipStream);

				
				entry = new ZipEntry("content.xml"); //$NON-NLS-1$
				zipStream.putNextEntry(entry);
				getContentWriter().save(zipStream, reportBook);

				if (images != null)
					entry = new ZipEntry("word/media/"); //$NON-NLS-1$
					zipStream.putNextEntry(entry);
					for (String fileName : images.keySet()) {
						entry = new ZipEntry("word/media/" + fileName); //$NON-NLS-1$
						zipStream.putNextEntry(entry);
						RenderedImage image = images.get(fileName);

						ImageIO.write(image, Utilities
								.getFileExtension(fileName), zipStream);
					}

				entry = new ZipEntry("settings.xml"); //$NON-NLS-1$
				zipStream.putNextEntry(entry);
				getSettingsWriter().save(zipStream, reportBook);

				entry = new ZipEntry("meta.xml"); //$NON-NLS-1$
				zipStream.putNextEntry(entry);
				getMetaWriter().save(zipStream, reportBook);

				
				entry = new ZipEntry("docProps/app.xml"); //$NON-NLS-1$
				zipStream.putNextEntry(entry);
				writeDocPropApp(zipStream);

				entry = new ZipEntry("docProps/core.xml"); //$NON-NLS-1$
				zipStream.putNextEntry(entry);
				writeDocPropCore(zipStream);

				entry = new ZipEntry("_rels/rels"); //$NON-NLS-1$
				zipStream.putNextEntry(entry);
				writeRels(zipStream);
				
			} finally {
				zipStream.close();
			}
		} catch (IOException e) {
			throw new SaveReportException(e);
		}
	}

	protected ReportWriter getSettingsWriter() {
		return null;//TODO
	}

	protected ReportWriter getMetaWriter() {
		return null;//TODO
	}

	protected ReportWriter getContentWriter() {
		return null;//TODO
	}
	
	private void writeXmlHead(PrintWriter fw) {
		fw
				.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
	}
	
	private void writeContentTypes(OutputStream stream) {
		PrintWriter fw = new PrintWriter(new OutputStreamWriter(stream,
				java.nio.charset.Charset.forName("UTF-8"))); //$NON-NLS-1$
		writeXmlHead(fw);
		fw
				.print("<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">");
		fw
				.print("<Override PartName=\"/word/footnotes.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.footnotes+xml\"/>");
		fw.print("<Default Extension=\"png\" ContentType=\"image/png\"/>");
		fw
				.print("<Override PartName=\"/word/comments.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.comments+xml\"/>");
		fw.print("<Default Extension=\"emf\" ContentType=\"image/x-emf\"/>");
		fw
				.print("<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>");
		fw
				.print("<Default Extension=\"xml\" ContentType=\"application/xml\"/>");
		fw
				.print("<Override PartName=\"/word/document.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/>");
		fw
				.print("<Override PartName=\"/word/numbering.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.numbering+xml\"/>");
		fw
				.print("<Override PartName=\"/word/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml\"/>");
		fw
				.print("<Override PartName=\"/word/endnotes.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.endnotes+xml\"/>");
		fw
				.print("<Override PartName=\"/docProps/app.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.extended-properties+xml\"/>");
		fw
				.print("<Override PartName=\"/word/settings.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.settings+xml\"/>");
		fw
				.print("<Override PartName=\"/word/footer2.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.footer+xml\"/>");
		fw
				.print("<Override PartName=\"/word/footer1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.footer+xml\"/>");
		fw
				.print("<Override PartName=\"/word/theme/theme1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.theme+xml\"/>");
		fw
				.print("<Override PartName=\"/word/header2.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.header+xml\"/>");
		fw
				.print("<Override PartName=\"/word/fontTable.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.fontTable+xml\"/>");
		fw
				.print("<Override PartName=\"/word/webSettings.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.webSettings+xml\"/>");
		fw
				.print("<Override PartName=\"/word/header1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.header+xml\"/>");
		fw
				.print("<Override PartName=\"/docProps/core.xml\" ContentType=\"application/vnd.openxmlformats-package.core-properties+xml\"/>");
		fw.print("</Types>");
		fw.flush();
	}

	private void writeDocPropApp(OutputStream stream) {
		PrintWriter fw = new PrintWriter(new OutputStreamWriter(stream,
				java.nio.charset.Charset.forName("UTF-8"))); //$NON-NLS-1$
		writeXmlHead(fw);
		fw.print("<Properties xmlns=\"http://schemas.openxmlformats.org/officeDocument/2006/extended-properties\" xmlns:vt=\"http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes\">");
		fw.print("<Template>Normal.dotm</Template>");
		fw.print("<TotalTime>5</TotalTime>");
		fw.print("<Pages>" + countPage + "</Pages>");
		fw.print("<Words>" + countWords + "</Words>");
		fw.print("<Characters>" + countCharacters + "</Characters>");
		fw.print("<Application>Microsoft Office Word</Application>");
		fw.print("<DocSecurity>0</DocSecurity>");
		fw.print("<Lines>" + countLines + "</Lines>");
		fw.print("<Paragraphs>" + countParagraphs + "</Paragraphs>");
		fw.print("<ScaleCrop>false</ScaleCrop>");
		fw.print("<Company></Company>");
		fw.print("<LinksUpToDate>false</LinksUpToDate>");
		fw.print("<CharactersWithSpaces>" + charWithSpaces + "</CharactersWithSpaces>");
		fw.print("<SharedDoc>false</SharedDoc>");
		fw.print("<HyperlinksChanged>false</HyperlinksChanged>");
		fw.print("<AppVersion>12.0000</AppVersion>");
		fw.print("</Properties>");
	}
	

	private void writeDocPropCore(OutputStream stream) {
		PrintWriter fw = new PrintWriter(new OutputStreamWriter(stream,
				java.nio.charset.Charset.forName("UTF-8"))); //$NON-NLS-1$
		writeXmlHead(fw);
		fw.print("<cp:coreProperties xmlns:cp=\"http://schemas.openxmlformats.org/package/2006/metadata/core-properties\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:dcmitype=\"http://purl.org/dc/dcmitype/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		fw.print("<dc:title></dc:title>");
		fw.print("<dc:subject></dc:subject>");
		fw.print("<dc:creator>" + reportBook.getCreator() + "</dc:creator>");
		fw.print("<cp:keywords></cp:keywords>");
		fw.print("<dc:description></dc:description>");
		fw.print("<cp:lastModifiedBy>" +reportBook.getCreator() + "</cp:lastModifiedBy>");
		fw.print("<cp:revision>1</cp:revision>");
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
		if (reportBook.getCreationDate() == null) {
			reportBook.setCreationDate(Calendar.getInstance().getTime());
		}
		String date = format.format(reportBook.getCreationDate());
		fw.print("<dcterms:created xsi:type=\"dcterms:W3CDTF\">" + date + "</dcterms:created>");
		fw.print("<dcterms:modified xsi:type=\"dcterms:W3CDTF\">" + date + "</dcterms:modified>");
		fw.print("</cp:coreProperties>");		
	}	

	private void writeRels(OutputStream stream) {
		PrintWriter fw = new PrintWriter(new OutputStreamWriter(stream,
				java.nio.charset.Charset.forName("UTF-8"))); //$NON-NLS-1$
		writeXmlHead(fw);
		fw.print("<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">");
		fw.print("<Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties\" Target=\"docProps/app.xml\"/>");
		fw.print("<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties\" Target=\"docProps/core.xml\"/>");
		fw.print("<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"word/document.xml\"/>");
		fw.print("</Relationships>");
	}	
	
	public void save(Writer writer, ReportBook reportBook)
			throws SaveReportException {
		throw new SaveReportException("Method not supported"); //$NON-NLS-1$
	}

	public void save(File file, ReportBook reportBook)
			throws SaveReportException {
		try {
			save(new FileOutputStream(file), reportBook);
		} catch (FileNotFoundException e) {
			throw new SaveReportException(e);
		}
	}

	public String write(String fileName, Object resource)
			throws SaveReportException {
		if (resource instanceof RenderedImage) {
			writeIcon(fileName, (RenderedImage) resource);
			return fileName;
		}
		return "";
	}

	public void writeIcon(String fileName, RenderedImage image) {
		if (images == null) {
			images = new HashMap<String, RenderedImage>();
		}
		images.put(fileName, image);
	}

}
