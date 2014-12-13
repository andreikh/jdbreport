/*
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
package jdbreport.source;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @version 1.1 22.10.2010
 * @author Andrey Kholmanskih
 * 
 */
public interface JdbcSource {

	/**
	 * @return Returns the alias.
	 */
	public String getAlias();

	/**
	 * @param alias
	 *            The alias to set.
	 */
	public void setAlias(String alias);

	/**
	 * @return Returns the driverName.
	 */
	public String getDriverName();

	/**
	 * @param driverName
	 *            The driverName to set.
	 */
	public void setDriverName(String driverName);

	/**
	 * @return Returns the url.
	 */
	public String getUrl();

	/**
	 * @param url
	 *            The url to set.
	 */
	public void setUrl(String url);

	/**
	 * @return Returns the user.
	 */
	public String getUser();

	/**
	 * @param user
	 *            The user to set.
	 */
	public void setUser(String user);

	/**
	 * @param password
	 *            The password to set.
	 */
	public void setPassword(String password);

	/**
	 * @return Returns the properties.
	 */
	public Properties getProperties();

	/**
	 * @param properties
	 *            The properties to set.
	 */
	public void setProperties(Properties properties);


	public String getJndiName();

	public void setJndiName(String jndiName);
	
	/**
	 * 
	 * @return dataSource
	 */
	public DataSource getDataSource();
}
