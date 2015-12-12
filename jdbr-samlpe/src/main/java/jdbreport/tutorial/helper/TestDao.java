/*
 * TestDao.java
 *
 * Copyright 2007-2015 Andrey Kholmanskih. All rights reserved.
 */

package jdbreport.tutorial.helper;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;



/**
 * @version 1.0 13.04.2007
 * @author Andrey Kholmanskih
 *
 */
public class TestDao {


	private static final String createSql = "create table test(firstname varchar(50), lastname varchar(50), color varchar(20), movie varchar(100), number double, food varchar(20))";
	
	private static final String selectSql = "select firstname, lastname, color, movie, number, food from test";

	private static final String insertSql = "insert into test(firstname, lastname, color, movie, number, food) values (?, ?, ?, ?, ?, ?)";
	
	private static final String deleteSql = "delete from test";
	
	private PreparedStatement selectStatement;
	
	private PreparedStatement insertStatement;

	private String driverName;

	private String connectionUrl;

	private Connection connection;

	public TestDao(String driverName, String connectionUrl) {
		this.driverName = driverName;
		this.connectionUrl = connectionUrl;
		createIfNotExists();
	}
	
	public Connection getConnection() throws SQLException {
		if (connection == null) {
			try {
				Class.forName(driverName);
				connection = DriverManager.getConnection(connectionUrl);
			} catch (ClassNotFoundException e) {
				throw new SQLException(e.getMessage());
			} catch (SQLException e) {
				throw new SQLException(e.getMessage());
			}
		}
		return connection;
	}
	
	private boolean createIfNotExists() {			

		boolean result = false;
		try {
			DatabaseMetaData dmd = getConnection().getMetaData();
			ResultSet rs = dmd.getTables(null, null, "TEST", null);
			try {
			if (!rs.next()) {
					System.out.println(" . . . . creating table TEST");
					Statement s = getConnection().createStatement();
					s.execute(createSql );
					getConnection().commit();
					result = true;
			}
			} finally {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public void insertTest(Test test) throws SQLException {
			if (insertStatement == null)
				insertStatement = getConnection().prepareStatement(insertSql);
			insertStatement.setString(1, test.getFirstName());
			insertStatement.setString(2, test.getLastName());			
			insertStatement.setString(3, test.getColorName());
			insertStatement.setString(4, test.getMovie());
			insertStatement.setDouble(5, test.getNumber());
			insertStatement.setString(6, test.getFood());
			insertStatement.execute();
			getConnection().commit();
	}

	public void deleteTest() throws SQLException {
			Statement deleteStatement = getConnection().createStatement();
			deleteStatement.executeUpdate(deleteSql);
			getConnection().commit();
	}

	public List<Test> getTestList() throws SQLException {
		List<Test> result = new ArrayList<Test>();
			if (selectStatement == null)
				selectStatement = getConnection().prepareStatement(selectSql);
			ResultSet rs = selectStatement.executeQuery();
			try {
				while (rs.next()) {
					Test test = new Test(rs.getString("firstname"), rs.getString("lastname"),
							rs.getString("color"),rs.getString("movie"),rs.getDouble("number"),
							rs.getString("food"));
					result.add(test);
				}
			} finally {
				rs.close();
			}
		return result;
	}
	
}
