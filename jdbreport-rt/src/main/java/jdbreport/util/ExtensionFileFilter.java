/**
 * Created	28.02.2010
 *
 * Copyright 2010-2014 Andrey Kholmanskih
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
package jdbreport.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 3.0 13.12.2014
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
			StringBuilder result = new StringBuilder(descr + " (");
			for (String ext : exts) {
				result.append('*');
				result.append(ext);
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
		for (String ext : exts) {
			if (pathname.getName().toLowerCase().endsWith(ext))
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
