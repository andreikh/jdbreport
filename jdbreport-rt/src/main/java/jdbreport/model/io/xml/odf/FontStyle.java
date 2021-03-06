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

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 */
class FontStyle {

	private String name;
	private String family;
	private String familyGeneric;
	private String pitch;

	public FontStyle(String name, String family, String generic, String pitch) {
		super();
		this.name = name;
		this.family = family;
		this.familyGeneric = generic;
		this.pitch = pitch;
	}

	public String getName() {
		return name;
	}

	public String getFamily() {
		return family;
	}

	public String getFamilyGeneric() {
		return familyGeneric;
	}

	public String getPitch() {
		return pitch;
	}

}
