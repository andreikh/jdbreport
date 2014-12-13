/*
 * Consts.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2014 Andrey Kholmanskih
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

package jdbreport.grid;

import java.awt.Font;


/**
 * @version 1.2 02/08/09
 * @author Andrey Kholmanskih
 *
 */
public interface Consts {
	
	public static Font defaultFont = new Font("Tahoma", Font.PLAIN, 12);
	
	public static Font headerFont = defaultFont;

	public static Font labelFont = defaultFont;
	
	public static Font textFont = defaultFont;

	public static Font statusFont = new Font("Tahoma", Font.PLAIN, 11);

	public static Font buttonFont = defaultFont;
	
	public static Font menuFont = defaultFont;
	
	public static Font listFont = defaultFont;
	
}
