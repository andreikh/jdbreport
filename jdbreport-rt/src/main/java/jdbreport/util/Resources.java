/*
 * Created on 04.06.2005
 *
 * Copyright (C) 2005-2014 Andrey Kholmanskih
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

import javax.swing.*;
import java.util.ResourceBundle;

/**
 * @version 1.0 06/24/06
 * @author Andrey Kholmanskih
 * 
 */
public interface Resources {

	public String getString(String name);

	public Icon getIcon(String fileName);

	public ResourceBundle getResourceBungle();

}
