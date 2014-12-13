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

import jdbreport.util.Compress;
import jdbreport.util.xml.XMLCoder;
import jdbreport.util.xml.XMLStored;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.swing.event.EventListenerList;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.sql.*;
import java.util.Properties;
import java.util.zip.DataFormatException;

/**
 * @version 3.0 13.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class DbSource implements JdbcSource, XMLStored, Serializable {

	private static final long serialVersionUID = 1L;

	private static final String PREFIX = "-4398070554958534145L";
	private static final String POSTFIX = "8005098017894178616L";

	private String alias;

	private String driverName;

	private String url;

	private String user;

	private String password;

	private Properties properties;

	private Connection connection;

	private String jndiName;

	private boolean autoCommit = false;
	private boolean inTransaction;

	protected transient EventListenerList listenerList = new EventListenerList();

	private DataSource dataSource;

	private boolean keepConnection;

	public DbSource() {
		super();
	}

	public DbSource(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}

	public DbSource(Connection connection) {
		super();
		setConnection(connection);
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void startTransaction() {
		inTransaction = true;
	}

	public void commitTransaction() throws SQLException {
		try {
			inTransaction = false;
			getConnection().commit();
		} catch (InstantiationException | NamingException | ClassNotFoundException | IllegalAccessException e) {
			throw new SQLException(e.getMessage());
		}
	}

	public void rollbackTransaction() {
		try {
			inTransaction = false;
			getConnection().rollback();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isTransaction() {
		return inTransaction;
	}

	/**
	 * @param alias
	 *            The alias to set.
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @return Returns the alias.
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param driverName
	 *            The driverName to set.
	 */
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	/**
	 * @return Returns the driverName.
	 */
	public String getDriverName() {
		return driverName;
	}

	/**
	 * @param url
	 *            The url to set.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return Returns the url.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param user
	 *            The user to set.
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return Returns the user.
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param password
	 *            The password to set.
	 */
	public void setPassword(String password) {
		this.password = encodePassword(password);
	}

	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return decodePassword(password);
	}

	/**
	 * @param properties
	 *            The properties to set.
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * @return Returns the properties.
	 */
	public Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
		}
		return properties;
	}

	public void store(Element parent) {
		Element node = parent.getOwnerDocument().createElement("DBURL");
		storeAttributes(node);
		parent.appendChild(node);
	}

	/**
	 * Save to XML
	 * 
	 * @param node xml element
	 */
	protected void storeAttributes(Element node) {
		node.setAttribute("alias", getAlias());
		node.setAttribute("jndiName", getJndiName());
		node.setAttribute("driver", getDriverName());
		node.setAttribute("url", getUrl());
		node.setAttribute("user", getUser());
		if (password != null)
			node.setAttribute("passwd", password);
		if (properties != null && properties.size() > 0) {
			for (Object key : properties.keySet()) {
				Element prop = node.getOwnerDocument()
						.createElement("property");
				String value = (String) properties.get(key);
				prop.setAttribute("name", key.toString());
				prop.setAttribute("value", value);
				node.appendChild(prop);
			}
		}
		node.setAttribute("autocommit", "" + isAutoCommit());
	}

	/**
	 * 
	 * @see XMLStored#load(org.w3c.dom.Element)
	 */
	public void load(Element parent) {
		clear();
		NodeList nodes = parent.getElementsByTagName("DBURL");
		if (nodes == null)
			return;
		Element node = (Element) nodes.item(0);
		loadAttributes(node);
	}

	/**
	 * Load from XML
	 * 
	 * @param node xml element
	 */
	protected void loadAttributes(Element node) {
		alias = node.getAttribute("alias");
		jndiName = node.getAttribute("jndiName");
		driverName = node.getAttribute("driver");
		url = node.getAttribute("url");
		user = node.getAttribute("user");
		password = node.getAttribute("passwd");
		if (password == null || password.length() == 0) {
			setPassword(node.getAttribute("password"));
		}
		autoCommit = Boolean.parseBoolean(node.getAttribute("autocommit"));
		NodeList props = node.getElementsByTagName("property");
		for (int i = 0; i < props.getLength(); i++) {
			Element el = (Element) props.item(i);
			properties.put(el.getAttribute("name"), el.getAttribute("value"));
		}
	}

	/**
	 * 
	 */
	protected void clear() {
		getProperties().clear();
		alias = "";
		jndiName = "";
		driverName = "";
		url = "";
		user = "";
		password = "";
		autoCommit = false;
		keepConnection = false;
	}

	public String toString() {
		return alias;
	}

	public boolean isConnected() {
		try {
			return (connection != null && !connection.isClosed());
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Connection createConnection() throws SQLException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, NamingException {

		if (dataSource == null && getJndiName() != null
				&& !"".equals(getJndiName())) {
			try {
				InitialContext ctx = new InitialContext();
				dataSource = (DataSource) ctx.lookup(getJndiName());
			} catch (NamingException e) {
				if (driverName == null) {
					throw e;
				}
				System.out.println(e.getMessage());
			}
		}

		if (dataSource != null) {
			Connection conn;
			if (user != null && user.length() > 0) 
				conn = dataSource.getConnection(user, getPassword());
			else
				conn = dataSource.getConnection();
			conn.setAutoCommit(isAutoCommit());
			return conn;
		}

		java.sql.DriverManager.registerDriver((Driver) Class.forName(
				driverName).newInstance());
		Properties connectionProperties = new Properties();
		if (user != null && user.length() > 0) {
			connectionProperties.put("user", user);
		}
		String p = getPassword();
		if (p != null && p.length() > 0) {
			connectionProperties.put("password", p);
		}
		if (properties != null) {
			for (Object key : properties.keySet()) {
				String value = properties.get(key).toString();
				if (value.length() > 0)
					connectionProperties.put(key, value);
			}
		}
		Driver d = java.sql.DriverManager.getDriver(url);
		Connection conn = d.connect(url, connectionProperties);
		conn.setAutoCommit(isAutoCommit());
		setAutoCommit(conn.getAutoCommit());
		if (conn.getMetaData().supportsTransactionIsolationLevel(
				Connection.TRANSACTION_READ_COMMITTED)) {
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		}
		return conn;
	}

	public Connection getConnection() throws SQLException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, NamingException {
		if (connection == null || connection.isClosed()) {
			connection = createConnection();
			keepConnection = false;
		}
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
		keepConnection = connection != null;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setConnection(null);
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return connection.getMetaData();
	}

	/**
	 * @param autoCommit
	 *            The autoCommit to set.
	 */
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	/**
	 * @return Returns the autoCommit.
	 */
	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void connect() throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, NamingException {

		try {
			if (connection == null || connection.isClosed()) {
				connection = createConnection();
				keepConnection = false;
			}
		} catch (SQLException | ClassNotFoundException | NamingException | InstantiationException | IllegalAccessException e) {
			disconnect();
			throw e;
		}
	}

	public void disconnect() {
		fireConnectionClose();
		Connection tmpConnection = connection;
		connection = null;
		if (keepConnection)
			return;
		try {
			if (tmpConnection != null && !tmpConnection.isClosed()) {
				tmpConnection.rollback();
				tmpConnection.close();
			}
		} catch (Exception ignored) {
		}
	}

	/**
	 * @param jndiName
	 *            the jndiName to set
	 */
	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	/**
	 * @return the jndiName
	 */
	public String getJndiName() {
		return jndiName;
	}

	private static String encodePassword(String pass) {
		if (pass == null) {
			return null;
		}
		StringBuilder tmp = new StringBuilder();
		tmp.append(PREFIX);
		tmp.append(pass);
		tmp.append(POSTFIX);
		tmp.append(tmp);
		byte[] b;
		b = Compress.compress(tmp.toString().getBytes());
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) ~b[i];
		}
		b = XMLCoder.base64Encode(b);
		return new String(b);
	}

	private static String decodePassword(String pass) {
		if (pass == null) {
			return null;
		}
		byte[] b = pass.getBytes();
		try {
			b = XMLCoder.base64Decode(b);
		} catch (Exception e) {
			e.printStackTrace();
			return pass;
		}
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) ~b[i];
		}
		try {
			b = Compress.decompress(b);
		} catch (DataFormatException e1) {
			e1.printStackTrace();
		}
		String result = new String(b);
		result = result.substring(0, result.length() / 2);
		result = result.substring(PREFIX.length(),
				result.length() - POSTFIX.length());
		return result;
	}

	public void addConnectionListener(ConnectionListener x) {
		listenerList.add(ConnectionListener.class, x);
	}

	public void removePeriodListener(ConnectionListener x) {
		listenerList.remove(ConnectionListener.class, x);
	}

	protected void fireConnectionClose() {
		ConnectionEvent e = new ConnectionEvent(this);
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ConnectionListener.class) {
				((ConnectionListener) listeners[i + 1]).connectionClose(e);
			}
		}
	}

	public static void execScript(Connection connection, URL url)
			throws DaoException {
		execScript(connection, url, "UTF-8", "^");
	}

	public static void execScript(Connection connection, URL url, String charSet)
			throws DaoException {
		execScript(connection, url, charSet, "^");
	}

	public static void execScript(Connection connection, URL url,
			String characterSet, String delimiter) throws DaoException {
		if (url == null)
			return;
		try {
			String charSet = characterSet;

			if (charSet == null) {
				charSet = "UTF-8";
			}
			String delim = delimiter;
			if (delim == null) {
				delim = "^";
			}
			int bufSize = 16384;
			try (InputStreamReader reader = new InputStreamReader(url.openStream(),
					charSet)) {
				try (Statement stat = connection.createStatement()) {
					StringBuffer script = new StringBuffer();
					int l;
					char[] buf = new char[bufSize];
					do {
						l = reader.read(buf);
						if (l > 0) {
							script.append(new String(buf, 0, l));
							findAndExecSql(connection, delim, stat, script);
						}
					} while (l == bufSize);
					findAndExecSql(connection, delim, stat, script);
					if (script.length() > 0) {
						String sql = script.toString().trim();
						if (sql.length() > 0) {
							if (sql.equalsIgnoreCase("COMMIT")
									|| sql.equalsIgnoreCase("COMMIT WORK")) {
								connection.commit();
							} else {
								stat.execute(sql);
							}
						}
					}
				}
			}
		} catch (DaoException e) {
			throw e;
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	protected static void findAndExecSql(Connection connection,
			String delimiter, Statement stat, StringBuffer script)
			throws DaoException {
		String sql = null;
		try {
			int i;
			do {
				i = script.indexOf(delimiter);
				if (i > 0) {
					sql = script.substring(0, i).trim();
					script.delete(0, i + 1);
					if (sql.length() > 0) {
						if (sql.equalsIgnoreCase("COMMIT")
								|| sql.equalsIgnoreCase("COMMIT WORK")) {
							connection.commit();
						} else {
							stat.execute(sql);
						}

					}
				}
			} while (i > 0);
		} catch (Exception e) {
			System.out.println("SQL : " + sql);
			throw new DaoException(e);
		}
	}

}
