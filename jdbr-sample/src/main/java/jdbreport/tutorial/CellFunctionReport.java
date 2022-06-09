/*
 * CellFunctionReport.java
 *
 * Copyright 2007-2015 Andrey Kholmanskih. All rights reserved.
 */

package jdbreport.tutorial;

import java.util.*;

import jdbreport.JDBReport;
import jdbreport.model.io.LoadReportException;
import jdbreport.tutorial.helper.Test;
import jdbreport.util.Utils;

/**
 * @version 1.0 09.04.2007
 * @author Andrey Kholmanskih
 * 
 */
public class CellFunctionReport extends SampleItem {

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
			{ "Luck", "Burov", "darkgreen", "Brazil", 27d,
					"asparagus" },
			{ "Lucy", "Weeler", "blue", "The Lady Vanishes", 8d,
					"kiwi" },
			{ "Ewan", "Spears", "yellow", "A Bug's Life", 2d,
					"strawberry" },
			{ "Katerina", "Koshkina", "darkred", "Reservoir Dogs",
					3d, "raspberry" },
			{ "Lev", "Zykin", "brightred", "Jules et Jim", 5d,
					"raspberry" },
			{ "Daria", "White", "blue", "Pulp Fiction", 3d,
					"watermelon" },
			{ "Kristina", "Kholmanskih", "pink",
					"Blade Runner (Director's Cut)", 21d, "donut" },
			{ "Eric", "Hilary", "blue", "The Shawshank Redemption",
					.693, "pickle" },
			{ "Paris", "Hilton", "green", "Pulp Fiction", 2d,
					"grapes" },
			{ "Stuart", "Little", "green", "Goodfellas", 8d,
					"carrot" },
			{ "Robin", "Holms", "green", "The Last of the Mohicans",
					89d, "apple" },
			{ "Jenifer", "Kim", "blue", "Lone Star", 655321d,
					"strawberry" },
			{ "Ann", "Rues", "cyan", "The Stuntman", 7d, "peach" },
			{ "Justin", "Kennedy", "blue", "Once Upon A Time In The West",
					17d, "pineapple" },
			{ "Avril", "Carry", "orange", "The Music Man", 8d,
					"broccoli" },
			{ "Peter", "Sun", "magenta", "Harold & Maude", 12d,
					"sparegrass" },
			{ "Rick", "Lone", "black", "The Fifth Element", 1327d,
					"raspberry" },
			{ "Jeam", "Bullon", "brightblue", "The Joy Luck Club",
					22d, "pear" },
			{ "Victoria", "Lohan", "white", "City of Lost Children",
					9d, "corn" },
			{ "Sasha", "Lamp", "green", "Schindler's List", 3d,
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
					290d, "cantaloupe" },
			{ "Lada", "Freeze", "blue", "Repo Man", 241d, "pepper" },
			{ "Ashanti", "Cell", "blue", "The Fifth Element", (double)0xFF,
					"pepper" },
			{ "Elena", "Schort", "green", "2001: A Space Odyssey",
					47d, "watermelon" },
			{ "Katerina", "Bokova", "darkgreen", "Star Wars", 13d,
					"watermelon" },
			{ "Sandy", "Tucker", "brightblue", "Aliens", 2d,
					"broccoli" },
			{ "Pasha", "Rukov", "red", "Raiders of the Lost Ark",
					222d, "tomato" },
			{ "Scott", "Wild", "darkorange", "The Thin Man", (double) -97,
					"banana" },
			{ "Stefany", "Carrot", "blue", "Chusingura (1962)", 8d,
					"pear" },
			{ "Pamela", "Gwen", "black", "Raiders of the Lost Ark",
					3d, "grapefruit" },
			{ "Julia", "Petelina", "green", "My Life as a Dog", 7d,
					"onion" },
			{ "Jim", "Shilov", "gray", "None", 13d, "grapes" } };

	private List<Test> testList;

	private void initTestList() {
		testList = new ArrayList<>();
		for (Object[] aData : data) {
			Test test = new Test((String) aData[0], (String) aData[1],
					(String) aData[2], (String) aData[3],
					(Double) aData[4], (String) aData[5]);
			testList.add(test);
		}
	}

	public static void main(String[] args) {
		CellFunctionReport report = new CellFunctionReport();
		report.run();
	}

	@Override
	public String getCaption() {
		return "Cell Function";
	}

	@Override
	public String getTemplate() {
		return "cellfunc.jdbr";
	}

	@Override
	public void run() {
		initTestList();
		try {
			Map<String, Object> dsList = new HashMap<>();
			dsList.put("test", testList);
			JDBReport.showReport(getTemplateURL(), dsList);
		} catch (LoadReportException e) {
			Utils.showError(e);
		}
	}

}
