/*
 * ManyDetailsReport.java
 *
 * Copyright 2007-2015 Andrey Kholmanskih. All rights reserved.
 */

package jdbreport.tutorial;

import java.sql.SQLException;
import java.util.*;


import jdbreport.JDBReport;
import jdbreport.model.io.LoadReportException;
import jdbreport.tutorial.helper.Test;
import jdbreport.tutorial.helper.TestDao;
import jdbreport.util.Utils;

/**
 * @version 1.0 09.04.2007
 * @author Andrey Kholmanskih
 * 
 */
public class ManyDetailsReport extends SampleItem {

	final Object[][] data = {
			{ "Zhenya", "Albers", "green", "Brazil", 44.0,
					"strawberry" },
			{ "Mark", "Verzilina", "blue", "Curse of the Demon", 3d,
					"grapes" },
			{ "Maria", "Parcker", "black", "The Blues Brothers",
					2.7182818285, "raspberry" },
			{ "Lara", "Bunny", "red", "Airplane (the whole series)",
					15d, "strawberry" },
			{ "Mike", "Black", "blue", "The Man Who Knew Too Much",
					13d, "peach" },
			{ "Andrew", "Falls", "black", "Blade Runner (Director's Cut)",
					23d, "broccoli" },
			{ "Luck", "Burov", "darkgreen", "Brazil", 27.0,
					"asparagus" },
			{ "Lucy", "Weeler", "blue", "The Lady Vanishes", 8.0,
					"kiwi" },
			{ "Ewan", "Spears", "yellow", "A Bug's Life", 2.0,
					"strawberry" },
			{ "Katerina", "Koshkina", "darkred", "Reservoir Dogs",
					3.0, "raspberry" },
			{ "Lev", "Zykin", "brightred", "Jules et Jim", 5.0,
					"raspberry" },
			{ "Daria", "White", "blue", "Pulp Fiction", 3.0,
					"watermelon" },
			{ "Kristina", "Kholmanskih", "pink",
					"Blade Runner (Director's Cut)", 21.0, "donut" },
			{ "Eric", "Hilary", "blue", "The Shawshank Redemption",
					.693, "pickle" },
			{ "Paris", "Hilton", "green", "Pulp Fiction", 2.0,
					"grapes" },
			{ "Stuart", "Little", "green", "Goodfellas", 8.0,
					"carrot" },
			{ "Robin", "Holms", "green", "The Last of the Mohicans",
					89.0, "apple" },
			{ "Jenifer", "Kim", "blue", "Lone Star", 655321.0,
					"strawberry" },
			{ "Ann", "Rues", "cyan", "The Stuntman", 7.0, "peach" },
			{ "Justin", "Kennedy", "blue", "Once Upon A Time In The West",
					17.0, "pineapple" },
			{ "Avril", "Carry", "orange", "The Music Man", 8.0,
					"broccoli" },
			{ "Peter", "Sun", "magenta", "Harold & Maude", 12.0,
					"sparegrass" },
			{ "Rick", "Lone", "black", "The Fifth Element", 1327.0,
					"raspberry" },
			{ "Jeam", "Bullon", "brightblue", "The Joy Luck Club",
					22.0, "pear" },
			{ "Victoria", "Lohan", "white", "City of Lost Children",
					9.0, "corn" },
			{ "Sasha", "Lamp", "green", "Schindler's List", 3.0,
					"strawberry" },
			{ "David", "Candle", "darkgreen", "Withnail & I", 7d,
					"peach" },
			{ "Phillip", "Hengry", "gray", "Das Boot", 3d, "banana" },
			{ "Phiona", "Makarova", "darkgray", "Eraserhead", 52d,
					"peach" },
			{ "Anton", "Muller", "darkred", "Labyrinth", 0d,
					"pineapple" },
			{ "Martin", "King", "blue", "At First Sight", 3d,
					"pineapple" },
			{ "Timur", "Jackson", "blue", "None", 69d, "pepper" },
			{ "Steve", "Field", "darkblue", "Defending Your Life",
					7d, "broccoli" },
			{
					"Garry",
					"Garner",
					"green",
					"The Adventures of Buckaroo Banzai Across the 8th Dimension",
					7d, "strawberry" },
			{
					"Jerry",
					"Plastinin",
					"black",
					"The Bicycle Thief",
					3.141592653589793238462643383279502884197169399375105820974944,
					"banana" },
			{ "Vlad", "Kokarev", "magenta", "This is Spinal Tap",
					290.0, "cantaloupe" },
			{ "Lada", "Freeze", "blue", "Repo Man", 241.0, "pepper" },
			{ "Ashanti", "Cell", "blue", "The Fifth Element", (double)0xFF,
					"pepper" },
			{ "Elena", "Schort", "green", "2001: A Space Odyssey",
					47.0, "watermelon" },
			{ "Katerina", "Bokova", "darkgreen", "Star Wars", 13.0,
					"watermelon" },
			{ "Sandy", "Tucker", "brightblue", "Aliens", 2.0,
					"broccoli" },
			{ "Pasha", "Rukov", "red", "Raiders of the Lost Ark",
					222.0, "tomato" },
			{ "Scott", "Wild", "darkorange", "The Thin Man", (double) -97,
					"banana" },
			{ "Stefany", "Carrot", "blue", "Chusingura (1962)", 8.0,
					"pear" },
			{ "Pamela", "Gwen", "black", "Raiders of the Lost Ark",
					3.0, "grapefruit" },
			{ "Julia", "Petelina", "green", "My Life as a Dog", 7.0,
					"onion" },
			{ "Jim", "Shilov", "gray", "None", 13.0, "grapes" } };

	private static final String driverName = "org.apache.derby.jdbc.EmbeddedDriver";

	private static final String connectionURL = "jdbc:derby:testdb;create=true;unicode=true";

	private List<Test> testList;

	private TestDao dao;

	private void initTestList() {
		testList = new ArrayList<>();
		for (Object[] aData : data) {
			Test test = new Test((String) aData[0], (String) aData[1],
					(String) aData[2], (String) aData[3],
					(Double) aData[4], (String) aData[5]);
			testList.add(test);
		}
	}

	private void fillDb() throws SQLException {
		dao = new TestDao(driverName, connectionURL);
		dao.deleteTest();
		for (Test test : testList) {
			dao.insertTest(test);
		}
	}

	private void closeDb() throws SQLException {
		dao.getConnection().close();
		dao = null;
	}

	public static void main(String[] args) {
		ManyDetailsReport report = new ManyDetailsReport();
		report.run();
	}

	@Override
	public String getCaption() {
		return "Many Details";
	}

	@Override
	public String getTemplate() {
		return "manydetails.jdbr";
	}

	@Override
	public void run() {
		initTestList();
		try {
			fillDb();
			closeDb();
		} catch (SQLException e) {
			Utils.showError(e);
		}
		try {
			Map<String, Object> dsList = new HashMap<>();
			dsList.put("test2", testList);
			JDBReport.showReport(getTemplateURL(), dsList);
		} catch (LoadReportException e) {
			Utils.showError(e);
		}
	}

}
