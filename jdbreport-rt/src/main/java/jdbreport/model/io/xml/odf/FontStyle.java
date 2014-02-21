/*
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2008 Andrey Kholmanskih. All rights reserved.
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
