/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2012 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.design.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * @version 2.0 26.02.2012
 * @author Andrey Kholmanskih
 * 
 */
public class CellFunctionObject implements Cloneable {

	private static final Logger logger = Logger.getLogger(CellFunctionObject.class
			.getName());

	private String functionName;
	private String functionBody;
	private byte[] compiledClass;
	private CellFunction cellFunction;

	public CellFunctionObject(String name) {
		super();
		this.functionName = name;
	}

	public CellFunctionObject() {
		this(null);
	}

	/**
	 * @param name
	 *            The functionName to set.
	 */
	public void setFunctionName(String name) {
		this.functionName = name;
	}

	/**
	 * @return Returns the functionName.
	 */
	public String getFunctionName() {
		return functionName;
	}

	/**
	 * @param body
	 *            The functionBody to set.
	 */
	public void setFunctionBody(String body) {
		this.functionBody = body;
		cellFunction = null;
	}

	/**
	 * @return Returns the functionBody.
	 */
	public String getFunctionBody() {
		return functionBody;
	}

	/**
	 * @return Returns the compiledClass.
	 */
	public byte[] getCompiledClass() {
		return compiledClass;
	}

	public void setCompiledClass(byte[] compiledClass) {
		this.compiledClass = compiledClass;
	}

	public String getClassText() {
		StringBuffer text = new StringBuffer();
		text.append("import jdbreport.model.*;");
		text.append("import jdbreport.design.model.CellFunction;");
		text.append("import jdbreport.design.model.AbstractCellFunction;");
		text.append("import jdbreport.source.ReportDataSet;");

		text.append("import java.util.*;");
		text.append('\n');
		text.append("public class ");
		text.append(getFunctionName());
		text.append(" extends AbstractCellFunction{\n");
		text.append("public void run() throws ReportException {\n");
		text.append(getFunctionBody());
		text.append('\n');
		text.append('}');
		text.append('\n');
		text.append('}');
		return text.toString();
	}

	public void doCompile() throws Exception {
		compiledClass = null;
		String sourceFile = getFunctionName() + ".java";
		File file = new File(sourceFile);
		file.createNewFile();
		FileOutputStream fw = new FileOutputStream(file);
		try {
			fw.write(getClassText().getBytes("UTF-8"));
		} finally {
			fw.close();
		}
		File classFile = new File(getFunctionName() + ".class");
		try {
			JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
			if (javac == null) {
				logger.severe("Compiler not found");
				return;
			}
			int compileReturnCode = javac.run(null, null, null, new String[] {
					"-source", "5", "-target", "5", "-encoding", "UTF-8", sourceFile });
			if (compileReturnCode != 0) {
				throw new Exception("Compilation error");
			}
			if (classFile.exists()) {
				FileInputStream fr = new FileInputStream(classFile);
				try {
					compiledClass = new byte[(int) classFile.length()];
					fr.read(compiledClass);
				} finally {
					fr.close();
				}
			}
		} finally {
			file.delete();
			classFile.delete();
		}
	}

	public CellFunction getCellFunction() {
		if (cellFunction == null && compiledClass != null) {
			loadFunction();
		}
		return cellFunction;
	}

	private void loadFunction() {
		if (compiledClass != null) {
			try {
				ClassLoader loader = new FunctionClassLoader(this);
				Object func = loader.loadClass(getFunctionName()).newInstance();
				cellFunction = (CellFunction) func;
			} catch (ClassNotFoundException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			} catch (InstantiationException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			} catch (IllegalAccessException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return this;
	}

}
