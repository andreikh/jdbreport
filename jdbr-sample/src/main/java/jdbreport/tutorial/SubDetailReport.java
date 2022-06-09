/*
 *
 * Copyright 2008-2015 Andrey Kholmanskih. All rights reserved.
 */

package jdbreport.tutorial;

import java.util.*;

import jdbreport.JDBReport;
import jdbreport.model.io.LoadReportException;
import jdbreport.tutorial.helper.Test;
import jdbreport.util.Utils;

/**
 * @version 1.0 12.05.2008
 * @author Andrey Kholmanskih
 * 
 */
public class SubDetailReport extends SampleItem {

	public static class ColorFavorits {
		
		private final String colorName;
		private final List<Test> tests = new ArrayList<>();
	
		public ColorFavorits(String colorName) {
			super();
			this.colorName = colorName;			
		}
		
		public String getColorName() {
			return colorName;
		}
		
		public  List<Test> getTests() {
			return tests;
		}
		
		public int getCount() {
			return tests.size();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((colorName == null) ? 0 : colorName.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final ColorFavorits other = (ColorFavorits) obj;
			if (colorName == null) {
				return other.colorName == null;
			} else return colorName.equals(other.colorName);
		}
		
		
	}
	
	final Object[][] data = {
			{ "Zhenya", "Albers", "green", "Brazil", 44.0,
					"strawberry" },
			{ "Mark", "Verzilina", "blue", "Curse of the Demon", 3.0,
					"grapes" },
			{ "Maria", "Parcker", "black", "The Blues Brothers",
					2.7182818285, "raspberry" },
			{ "Lara", "Bunny", "red", "Airplane (the whole series)",
					15.0, "strawberry" },
			{ "Mike", "Black", "blue", "The Man Who Knew Too Much",
					13.0, "peach" },
			{ "Andrew", "Falls", "black", "Blade Runner (Director's Cut)",
					23.0, "broccoli" },
			{ "Luck", "Burov", "darkgreen", "Brazil", 27.0,
					"asparagus" },
			{ "Lucy", "Weeler", "blue", "The Lady Vanishes", 8.0,
					"kiwi" },
			{ "Ewan", "Spears", "yellow", "A Bug's Life", 2.0,
					"strawberry" },
			{ "Katerina", "Koshkina", "violet", "Reservoir Dogs",
					3.0, "raspberry" },
			{ "Lev", "Zykin", "purple", "Jules et Jim", 5.0,
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
			{ "Ann", "Rues", "turquoise", "The Stuntman", 7.0,
					"peach" },
			{ "Justin", "Kennedy", "blue", "Once Upon A Time In The West",
					17.0, "pineapple" },
			{ "Avril", "Carry", "orange", "The Music Man", 8.0,
					"broccoli" },
			{ "Peter", "Sun", "sunpurple", "Harold & Maude", 12.0,
					"sparegrass" },
			{ "Rick", "Lone", "black", "The Fifth Element", 1327.0,
					"raspberry" },
			{ "Jeam", "Bullon", "jfcblue", "The Joy Luck Club", 22.0,
					"pear" },
			{ "Victoria", "Lohan", "beige", "City of Lost Children",
					9.0, "corn" },
			{ "Sasha", "Lamp", "green", "Schindler's List", 3.0,
					"strawberry" },
			{ "David", "Candle", "forestgreen", "Withnail & I", 7.0,
					"peach" },
			{ "Phillip", "Hengry", "suspectpink", "Das Boot", 3.0,
					"banana" },
			{ "Phiona", "Makarova", "cybergreen", "Eraserhead", 52.0,
					"peach" },
			{ "Anton", "Muller", "rustred", "Labyrinth", (double) 0,
					"pineapple" },
			{ "Martin", "King", "blue", "At First Sight", 3.0,
					"pineapple" },
			{ "Timur", "Jackson", "blue", "None", 69.0, "pepper" },
			{ "Steve", "Field", "jfcblue2", "Defending Your Life",
					7.0, "broccoli" },
			{
					"Garry",
					"Garner",
					"green",
					"The Adventures of Buckaroo Banzai Across the 8th Dimension",
					7.0, "strawberry" },
			{
					"Jerry",
					"Plastinin",
					"black",
					"The Bicycle Thief",
					3.141592653589793238462643383279502884197169399375105820974944,
					"banana" },
			{ "Vlad", "Kokarev", "aqua", "This is Spinal Tap", 290.0,
					"cantaloupe" },
			{ "Lada", "Freeze", "blue", "Repo Man", 241.0, "pepper" },
			{ "Ashanti", "Cell", "blue", "The Fifth Element", (double) 0xFF,
					"pepper" },
			{ "Elena", "Schort", "green", "2001: A Space Odyssey",
					47.0, "watermelon" },
			{ "Katerina", "Bokova", "darkgreen", "Star Wars", 13.0,
					"watermelon" },
			{ "Sandy", "Tucker", "eblue", "Aliens", 2.0, "broccoli" },
			{ "Pasha", "Rukov", "red", "Raiders of the Lost Ark",
					222.0, "tomato" },
			{ "Scott", "Wild", "violet", "The Thin Man", (double) -97,
					"banana" },
			{ "Stefany", "Carrot", "blue", "Chusingura (1962)", 8.0,
					"pear" },
			{ "Pamela", "Gwen", "black", "Raiders of the Lost Ark",
					3.0, "grapefruit" },
			{ "Julia", "Petelina", "green", "My Life as a Dog", 7.0,
					"onion" },
			{ "Jim", "Shilov", "gray", "None", 13.0, "grapes" } };

	private Map<String, ColorFavorits> colorMap;

	private void initTestList() {
		colorMap = new TreeMap<>();
		for (Object[] datum : data) {
			Test test = new Test((String) datum[0], (String) datum[1],
					(String) datum[2], (String) datum[3],
					(Double) datum[4], (String) datum[5]);

			ColorFavorits cf = colorMap.get(test.getColorName());
			if (cf == null) {
				cf = new ColorFavorits(test.getColorName());
				colorMap.put(cf.getColorName(), cf);
			}
			cf.getTests().add(test);
		}
	}

	public static void main(String[] args) {
		SubDetailReport report = new SubDetailReport();
		report.run();
	}

	@Override
	public String getCaption() {
		return "Dynamic insert dataset's in functions of cells";
	}

	@Override
	public String getTemplate() {
		return "subdetail.jdbr";
	}

	@Override
	public void run() {
		initTestList();
		try {
			Map<String, Object> dsList = new HashMap<>();
			Collection<ColorFavorits> colors = colorMap.values();
			
			dsList.put("colors", colors);
			dsList.put("test", colorMap.get("black").getTests());
			JDBReport.showReport(getTemplateURL(), dsList);
		} catch (LoadReportException e) {
			Utils.showError(e);
		}
	}

}
