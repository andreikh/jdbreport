/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2014 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.design.model.xml;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

import jdbreport.design.model.CellFunctionObject;
import jdbreport.design.model.GroupKey;
import jdbreport.design.model.ReplaceItem;
import jdbreport.design.model.TemplateBook;
import jdbreport.model.Cell;
import jdbreport.model.DetailGroup;
import jdbreport.model.Group;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportModel;
import jdbreport.model.io.ReportWriter;
import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.ResourceWriter;
import jdbreport.model.io.SaveReportException;
import jdbreport.model.io.xml.CellParser;
import jdbreport.model.io.xml.JReportHandler;
import jdbreport.model.io.xml.ReportBookParser;
import jdbreport.source.JdbcDataSet;
import jdbreport.source.JdbcReportSource;

import org.xml.sax.Attributes;

import and.util.xml.XMLCoder;
import and.util.xml.XMLParser;

/**
 * @version 3.0 22.02.2014
 * @author Andrey Kholmanskih
 * 
 */
public class TemplateBookParser extends ReportBookParser {

	private boolean inFunctions;

	private CellFunctionObject cellFunctionObject;

	private boolean inDataSets;

	private boolean inDataSet;

	private JdbcDataSet ds;

	private boolean inDsItem;

	private JdbcReportSource source;

	private boolean inVars;

	private boolean inReplacements;

	public TemplateBookParser(ResourceWriter rw) {
		super(rw);
	}

	public TemplateBookParser(JReportHandler reportHandler, ResourceReader rr) {
		super(reportHandler, rr);
	}

	public static ReportWriter createTemplateReportWriter(ResourceWriter rw) {
		return new TemplateBookParser(rw);
	}

	protected XMLParser createSheetHandler() {
    	return new TemplateReportParser(getDefaultReportHandler(), resourceReader);
	}

	private TemplateBook getTemplateReportBook() {
		return (TemplateBook) getReportBook();
	}

	public void endElement(String name, StringBuffer value) {
		super.endElement(name, value);
		if (inFunctions) {
			if (name.equals("class")) {
				getTemplateReportBook().getFunctionsList().put(
						cellFunctionObject.getFunctionName(),
						cellFunctionObject);
				return;
			}
			if (name.equals("function-text")) {
				cellFunctionObject.setFunctionBody(value.toString());
				return;
			}
			if (name.equals("function-code")) {
				try {
					cellFunctionObject.setCompiledClass(XMLCoder
							.base64Decode(value.toString().getBytes()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
			if (name.equals("cell-functions")) {
				inFunctions = false;
				return;
			}
		}
		if (inDataSet) {
			if (name.equals("ID")) {
				ds.setId(value.toString());
				return;
			}
			if (name.equals("query")) {
				ds.setQuery(value.toString());
				return;
			}
			if (name.equals("LinkID")) {
				ds.setMasterId(value.toString());
				return;
			}
			if (inDsItem) {
				if (name.equals("item")) {
					inDsItem = false;
				}
				return;
			}
			if (name.equals("DataSet")) {
				inDataSet = false;
				source.add(ds);
				return;
			}
			return;
		}
		if (inDataSets && name.equals("DataSetList")) {
			inDataSets = false;
			getTemplateReportBook().addSource(source);
			return;
		}
		if (name.equals("vars") && inVars) {
			inVars = false;
			return;
		}
		if (inReplacements && name.equals("replacements")) {
			inReplacements = false;
        }
	}

	public boolean startElement(String name, Attributes attributes) {
		if (inDataSet) {
			if (name.equals("ID")) {
				return true;
			}
			if (name.equals("query")) {
				return true;
			}
			if (name.equals("LinkID")) {
				return true;
			}
			if (name.equals("item")) {
				inDsItem = true;
				return true;
			}
		}
		if (inDataSets) {
			if (name.equals("DataSet")) {
				inDataSet = true;
				ds = new JdbcDataSet();
				return true;
			}
			if (name.equals("Properties")) {
				Properties p = new Properties();
				for (int i = 0; i < attributes.getLength(); i++) {
					p.put(attributes.getQName(i), attributes.getValue(i));
				}
				source.setProperties(p);
				return true;
			}
		}
		if (name.equals("DataSetList") && !inDataSets) {
			inDataSets = true;
			source = new JdbcReportSource();
			source.setAlias(attributes.getValue("alias"));
			source.setJndiName(attributes.getValue("jndiName"));
			source.setDriverName(attributes.getValue("driver"));
			source.setUrl(attributes.getValue("url"));
			source.setDriverName(attributes.getValue("driver"));
			source.setUser(attributes.getValue("user"));
			source.setPassword(attributes.getValue("pass"));
			return true;
		}
		if (inVars && name.equals("var")) {
			String varName = attributes.getValue("name");
			String varValue = attributes.getValue("value");
			getTemplateReportBook().setVarValue(varName, varValue);
			return true;
		}
		if (name.equals("vars")) {
			inVars = true;
			return true;
		}
		if (name.equals("cell-functions")) {
			inFunctions = true;
			return true;
		}
		if (inFunctions) {
			if (name.equals("class")) {
				cellFunctionObject = new CellFunctionObject(attributes
						.getValue("name"));
				return true;
			}
			if (name.equals("function-text")) {
				return true;
			}
			if (name.equals("function-code")) {
				return true;
			}
		}
		if (inReplacements) {
			if (name.equals("replacement")) {
				String pattern = attributes.getValue("pattern");
				String value = attributes.getValue("value");
				getTemplateReportBook().addReplacePattern(pattern, value);
				return true;
			}
		} else if (name.equals("replacements")) {
			inReplacements = true;
			getTemplateReportBook().clearReplacePatterns();
			return true;
		}

		return super.startElement(name, attributes);
	}

	@Override
	protected String getRootName() {
		return "DesignReport";
	}

	@Override
	protected void writeCell(PrintWriter writer, ReportModel model, Cell cell,
			int r, int c) throws SaveReportException {
		super.writeCell(writer, model, cell, r, c);
	}

	@Override
	protected void writeSheets(ReportBook reportBook, PrintWriter fw) throws SaveReportException {
		writeFunctions((TemplateBook) reportBook, fw);
		writeDataSources((TemplateBook) reportBook, fw);
		writeVars((TemplateBook) reportBook, fw);
		writeReplacements((TemplateBook) reportBook, fw);
		super.writeSheets(reportBook, fw);
	}

	private void writeReplacements(TemplateBook book, PrintWriter fw) {
		if (book.getReplacePatterns().size() > 0) {
			fw.println("<replacements>");
			for (ReplaceItem ri : book.getReplacePatterns()) {
				fw.print("<replacement pattern=\"" + XMLCoder.replaceSpecChar(ri.getRegex())
							+ "\" ");
				if (ri.getReplacement() != null && ri.getReplacement().length() > 0) {
						fw.print("value=\""
							+ XMLCoder.replaceSpecChar(ri.getReplacement()) + "\"");
				}			
				fw.println(" />");
			}
			fw.println("</replacements>");
		}
	}

	private void writeVars(TemplateBook book, PrintWriter fw) {
		if (book.getVars().size() > 0) {
			fw.println("<vars>");
            for (Object o : book.getVars().keySet()) {
                String key = (String) o;
                Object value = book.getVars().get(key);
                if (value != null && value.toString().length() > 0)
                    fw.println("<var name=\"" + XMLCoder.replaceSpecChar(key)
                            + "\" value=\""
                            + XMLCoder.replaceSpecChar(value.toString())
                            + "\" />");
                else
                    fw.println("<var name=\"" + XMLCoder.replaceSpecChar(key)
                            + "\"/>");
            }
			fw.println("</vars>");
		}

	}

	protected void writeDataSources(TemplateBook book, PrintWriter writer) {
		for (int i = 0; i < book.getSourcesList().size(); i++) {
			JdbcReportSource source = book.getSourcesList()
					.get(i);
			StringBuilder params = new StringBuilder("alias=\""
					+ XMLCoder.replaceSpecChar(source.getAlias()) + "\"");
			if (source.getJndiName() != null)
				params.append(" jndiName=\"").append(XMLCoder
                        .replaceSpecChar(source.getJndiName())).append("\"");
			if (source.getDriverName() != null)
				params.append(" driver=\"").append(XMLCoder.replaceSpecChar(source.getDriverName())).append("\"");
			if (source.getUrl() != null)
				params.append(" url=\"").append(XMLCoder.replaceSpecChar(source.getUrl())).append("\"");
			if (source.getUser() != null)
				params.append(" user=\"").append(XMLCoder.replaceSpecChar(source.getUser())).append("\"");
			if (source.getPassword() != null)
				params.append(" pass=\"").append(XMLCoder
                        .replaceSpecChar(source.getPassword())).append("\"");
			writer.println("<DataSetList " + params + ">");
			if (source.getProperties().size() > 0) {
				StringBuffer properties = new StringBuffer("<Properties ");
				Enumeration<Object> e = source.getProperties().keys();
				while (e.hasMoreElements()) {
					String key = (String) e.nextElement();
					properties.append(key);
					properties.append('=');
					properties.append('"');
					properties.append(XMLCoder.replaceSpecChar((String) source
							.getProperties().get(key)));
					properties.append('"');
				}
				properties.append("/>");
				writer.println(properties);
			}
			for (int l = 0; l < source.getDataSetCount(); l++) {
				JdbcDataSet ds = source.getDataSet(l);
				writer.println("<DataSet>");
				writer.println("<ID>" + XMLCoder.replaceSpecChar(ds.getId())
						+ "</ID>");
				writer.println("<query>"
						+ XMLCoder.replaceSpecChar(ds.getQuery()) + "</query>");
				if (ds.getMasterId() != null) {
					writer.println("<LinkID>"
							+ XMLCoder.replaceSpecChar(ds.getMasterId())
							+ "</LinkID>");
				}
				writer.println("</DataSet>");
			}
			writer.println("</DataSetList>");
		}
	}

	protected void writeFunctions(TemplateBook book, PrintWriter writer) {
		if (book.getFunctionsList().isEmpty())
			return;
		writer.println("<cell-functions>");
        for (CellFunctionObject cellObject : book.getFunctionsList().values()) {
            if (cellObject.getFunctionName() == null)
                continue;

            writer.println("<class name=\"" + cellObject.getFunctionName()
                    + "\">");
            if (cellObject.getFunctionBody() != null) {
                writer.print("<function-text><![CDATA[");
                writer.print(cellObject.getFunctionBody());
                writer.println("]]></function-text>");
            }
            if (cellObject.getCompiledClass() != null) {
                writer.println("<function-code>"
                        + new String(XMLCoder.base64Encode(cellObject
                        .getCompiledClass())) + "</function-code>");
            }
            writer.println("</class>");
        }
		writer.println("</cell-functions>");

	}

	@Override
	protected void writeGroupChild(PrintWriter writer, Group group,
			ReportModel model) throws SaveReportException {
		if (group instanceof DetailGroup) {
			DetailGroup dGroup = (DetailGroup) group;
			
			for (int i = 0; i < dGroup.getKeyCount(); i++) {
				GroupKey key = dGroup.getKey(i);
				if (key.getName() != null) {
					String dsId;
					if (key.getDatasetID() != null)
						dsId = " dataset=\"" + key.getDatasetID() + "\"";
					else
						dsId = "";
					writer.println("<GroupKey name=\"" + key.getName() + "\""
							+ dsId + " />");
				}
			}
			
			if (dGroup.getMinRowCount() > 0 || dGroup.getMaxRowCount() > 0) {
				writer.println("<Limits min=\"" + dGroup.getMinRowCount() + "\" max=\""
						+ dGroup.getMaxRowCount() + "\" />");   
			}
		}
		super.writeGroupChild(writer, group, model);
	}

	protected CellParser createCellHandler() {
		return new TemplCellHandler(getDefaultReportHandler(), this);
	}

	protected String getSheetName() {
		return "DesReportGrid";
	}

	public String getDescription() {
		return "TemplateReport Files";
	}

}
