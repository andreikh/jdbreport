/**
 * Created 16.06.2007
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
package jdbreport.design.grid.dialogs;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 3.0 12.12.2014
 */
public class JdbcDrivers {

	private static Map<String, JdbcDrivers> drivers;

	private static void fillDrivers() {
		if (drivers != null)
			return;
		drivers = new TreeMap<>();
		JdbcDrivers d;
		drivers.put("", null);
		d = new JdbcDrivers("Firebird", "org.firebirdsql.jdbc.FBDriver",
				"jdbc:firebirdsql://");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("MySQL", "com.mysql.jdbc.Driver", "jdbc:mysql://");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("ODBC", "sun.jdbc.odbc.JdbcOdbcDriver",
				"jdbc:odbc:", null);
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("Hypersonic SQL", "org.hsqldb.jdbcDriver",
				"jdbc:hsqldb:hsql://", "HSQL");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("Oracle OCI", "oracle.jdbc.driver.OracleDriver",
				"jdbc:oracle:oci", "Oracle");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("Oracle Thin", "oracle.jdbc.driver.OracleDriver",
				"jdbc:oracle:thin:@", "Oracle");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("PostgreSQL", "org.postgresql.Driver",
				"jdbc:postgresql://");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("StelsDBF", "jstels.jdbc.dbf.DBFDriver",
				"jdbc:jstels:dbf:");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("Derby", "org.apache.derby.jdbc.ClientDriver",
				"jdbc:derby://");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("Derby Embedded",
				"org.apache.derby.jdbc.EmbeddedDriver", "jdbc:derby:", "Derby");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("InterBase", "interbase.interclient.Driver",
				"jdbc:interbase://", "Interbase");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("MSSQL",
				"com.microsoft.sqlserver.jdbc.SQLServerDriver",
				"jdbc:sqlserver://", "SQLServer");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("Pervasive", "com.pervasive.jdbc.v2.Driver",
				"jdbc:pervasive://", null);
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("DB2 Application",
				"com.ibm.db2.jdbc.app.DB2Driver", "jdbc:db2:", "DB2");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("DB2 Net", "com.ibm.db2.jdbc.net.DB2Driver",
				"jdbc:db2://", "DB2");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("DB2 AS400",
				"com.ibm.as400.access.AS400JDBCDriver", "jdbc:as400://",
				"DB2400");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("PointBase",
				"com.pointbase.jdbc.jdbcUniversalDriver",
				"jdbc:pointbase:server://", "Pointbase");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("FrontBase", "jdbc.FrontBase.FBJDriver",
				"jdbc:FrontBase://");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("Informix", "com.informix.jdbc.IfxDriver",
				"jdbc:informix-sqli://");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("SQLBase", "centura.java.sqlbase.SqlbaseDriver",
				"jdbc:sqlbase://", null);
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("Sybase 4.x", "com.sybase.jdbc.SybDriver",
				"jdbc:sybase:Tds:", "Sybase");
		drivers.put(d.getName(), d);
		d = new JdbcDrivers("Sybase 5.x", "com.sybase.jdbc2.jdbc.SybDriver",
				"jdbc:sybase:Tds:", "Sybase");
		drivers.put(d.getName(), d);
	}

	public static Collection<JdbcDrivers> getDrivers() {
		fillDrivers();
		return drivers.values();
	}

	public static JdbcDrivers findByName(String name) {
		fillDrivers();
		return drivers.get(name);
	}

	public static JdbcDrivers findByDriver(String driverName) {
		fillDrivers();
		for (JdbcDrivers driver : drivers.values()) {
			if (driver != null && driver.getDriver().equals(driverName)) {
				return driver;
			}
		}
		return null;
	}

	private String name;

	private String driver;

	private String url;

	private String dialect;

	public JdbcDrivers(String name, String driver, String url) {
		this(name, driver, url, name);
	}

	public JdbcDrivers(String name, String driver, String url, String dialect) {
		super();
		this.name = name;
		this.driver = driver;
		this.url = url;
		this.dialect = dialect;
	}

	/**
	 * @return the driver
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Dialect name prefix for Hibernate
	 * 
	 * @return dialect
	 */
	public String getDialect() {
		return dialect;
	}

	public String toString() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((driver == null) ? 0 : driver.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final JdbcDrivers other = (JdbcDrivers) obj;
		if (driver == null) {
			if (other.driver != null)
				return false;
		} else if (!driver.equals(other.driver))
			return false;
		return true;
	}

}
