/**
 * Created	28.02.2010
 *
 * Copyright 2010 JSC Aviainstrument. All right reserved.
 */
package jdbreport.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 1.0 28.02.2010
 */
public class ExtensionFileFilter extends FileFilter {

	private String[] exts;
	private String description;

	public ExtensionFileFilter(String ext, String descr) {
		this(new String[] { ext }, descr);
	}

	public ExtensionFileFilter(String[] exts, String descr) {
		this.exts = exts;
		if (descr != null) {
			StringBuffer result = new StringBuffer(descr + " (");
			for (int i = 0; i < exts.length; i++) {
				result.append('*');
				result.append(exts[i]);
				result.append(';');
			}
			result.deleteCharAt(result.length() - 1);
			result.append(')');
			this.description = result.toString();
		}
	}

	public boolean accept(File pathname) {
		if (pathname.isDirectory())
			return true;
		for (int i = 0; i < exts.length; i++) {
			if (pathname.getName().toLowerCase().endsWith(exts[i]))
				return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public String getExtension() {
		return exts[0];
	}

}
