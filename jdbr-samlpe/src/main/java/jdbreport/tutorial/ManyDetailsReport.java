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
			{ "Luck", "Burov", "darkgreen", "Brazil", new Double(27),
					"asparagus" },
			{ "Lucy", "Weeler", "blue", "The Lady Vanishes", new Double(8),
					"kiwi" },
			{ "Ewan", "Spears", "yellow", "A Bug's Life", new Double(2),
					"strawberry" },
			{ "Katerina", "Koshkina", "darkred", "Reservoir Dogs",
					new Double(3), "raspberry" },
			{ "Lev", "Zykin", "brightred", "Jules et Jim", new Double(5),
					"raspberry" },
			{ "Daria", "White", "blue", "Pulp Fiction", new Double(3),
					"watermelon" },
			{ "Kristina", "Kholmanskih", "pink",
					"Blade Runner (Director's Cut)", new Double(21), "donut" },
			{ "Eric", "Hilary", "blue", "The Shawshank Redemption",
					new Double(.693), "pickle" },
			{ "Paris", "Hilton", "green", "Pulp Fiction", new Double(2),
					"grapes" },
			{ "Stuart", "Little", "green", "Goodfellas", new Double(8),
					"carrot" },
			{ "Robin", "Holms", "green", "The Last of the Mohicans",
					new Double(89), "apple" },
			{ "Jenifer", "Kim", "blue", "Lone Star", new Double(655321),
					"strawberry" },
			{ "Ann", "Rues", "cyan", "The Stuntman", new Double(7), "peach" },
			{ "Justin", "Kennedy", "blue", "Once Upon A Time In The West",
					new Double(17), "pineapple" },
			{ "Avril", "Carry", "orange", "The Music Man", new Double(8),
					"broccoli" },
			{ "Peter", "Sun", "magenta", "Harold & Maude", new Double(12),
					"sparegrass" },
			{ "Rick", "Lone", "black", "The Fifth Element", new Double(1327),
					"raspberry" },
			{ "Jeam", "Bullon", "brightblue", "The Joy Luck Club",
					new Double(22), "pear" },
			{ "Victoria", "Lohan", "white", "City of Lost Children",
					new Double(9), "corn" },
			{ "Sasha", "Lamp", "green", "Schindler's List", new Double(3),
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
					new Double(290), "cantaloupe" },
			{ "Lada", "Freeze", "blue", "Repo Man", new Double(241), "pepper" },
			{ "Ashanti", "Cell", "blue", "The Fifth Element", (double)0xFF,
					"pepper" },
			{ "Elena", "Schort", "green", "2001: A Space Odyssey",
					new Double(47), "watermelon" },
			{ "Katerina", "Bokova", "darkgreen", "Star Wars", new Double(13),
					"watermelon" },
			{ "Sandy", "Tucker", "brightblue", "Aliens", new Double(2),
					"broccoli" },
			{ "Pasha", "Rukov", "red", "Raiders of the Lost Ark",
					new Double(222), "tomato" },
			{ "Scott", "Wild", "darkorange", "The Thin Man", new Double(-97),
					"banana" },
			{ "Stefany", "Carrot", "blue", "Chusingura (1962)", new Double(8),
					"pear" },
			{ "Pamela", "Gwen", "black", "Raiders of the Lost Ark",
					new Double(3), "grapefruit" },
			{ "Julia", "Petelina", "green", "My Life as a Dog", new Double(7),
					"onion" },
			{ "Jim", "Shilov", "gray", "None", new Double(13), "grapes" } };

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
