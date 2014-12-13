/*
 * JDBReport Generator
 * 
 * Copyright (C) 2012-2014 Andrey Kholmanskih
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
package jdbreport.design.model;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * @version	1.0 30.01.2012
 *
 * @author Andrey Kholmanskih
 */
public class ReplaceItem implements Serializable, Cloneable {

	private String regex;
	
	private String replacement;
	
	private Pattern pattern;

	public ReplaceItem(String regexp, String replacement) {
		setRegex(regexp);
		this.replacement = replacement;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
		if (regex != null) {
			pattern = Pattern.compile(regex);
		} else {
			pattern = null;
		}
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public ReplaceItem clone(){
		try {
			return (ReplaceItem)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
