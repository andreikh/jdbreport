/*
 * Test.java
 *
 * Copyright 2007-2015 Andrey Kholmanskih. All rights reserved.
 */
package jdbreport.tutorial.helper;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @version 1.0 19.04.2007
 * @author Andrey Kholmanskih
 * 
 */
public class Test {

	private static Map<String, Color> map = null;

	private static Map<String, ImageIcon> icons = null;

	private String firstName;

	private String lastName;

	private String colorName;

	private String movie;

	private Double number;

	private String food;

	public Test(String firstName, String lastName, String colorName,
			String movie, Double number, String food) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.colorName = colorName;
		this.movie = movie;
		this.number = number;
		this.food = food;
	}

	private static void addImage(String name) {
		icons.put(name, new ImageIcon(Objects.requireNonNull(Test.class
				.getResource("/tutorial/food/" + name + ".jpg"))));
	}

	private static void fillImageMap() {
		icons = new HashMap<>();
		addImage("apple");
		addImage("asparagus");
		addImage("banana");
		addImage("broccoli");
		addImage("cantaloupe");
		addImage("carrot");
		addImage("corn");
		addImage("grapes");
		addImage("grapefruit");
		addImage("kiwi");
		addImage("onion");
		addImage("pear");
		addImage("peach");
		addImage("pepper");
		addImage("pickle");
		addImage("pineapple");
		addImage("raspberry");
		addImage("asparagus");
		addImage("strawberry");
		addImage("tomato");
		addImage("watermelon");
	}

	private static void fillColorMap() {
		if (map == null) {
			map = new HashMap<>();
			map.put("blue", Color.blue);
			map.put("green", Color.green);
			map.put("black", Color.black);
			map.put("red", Color.red);
			map.put("darkgreen", Color.green.darker());
			map.put("yellow", Color.yellow);
			map.put("darkred", Color.red.darker());
			map.put("brightred", Color.red.brighter());
			map.put("orange", Color.orange);
			map.put("cyan", Color.cyan);
			map.put("magenta", Color.magenta);
			map.put("brightblue", Color.blue.brighter());
			map.put("darkblue", Color.blue.darker());
			map.put("gray", Color.gray);
			map.put("darkgray", Color.darkGray);
			map.put("darkorange", Color.orange.darker());
			map.put("pink", Color.pink);
		}
	}

	/**
	 * @return the color
	 */
	public String getColorName() {
		return colorName;
	}

	public Color getColor() {
		if (colorName != null) {
			fillColorMap();
			return map.get(colorName);
		}
		return Color.white;
	}

	public Icon getFoodImage() {
		fillImageMap();
		return icons.get(food);
	}

	/**
	 * @param colorName
	 *            the color to set
	 */
	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the food
	 */
	public String getFood() {
		return food;
	}

	/**
	 * @param food
	 *            the food to set
	 */
	public void setFood(String food) {
		this.food = food;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the movie
	 */
	public String getMovie() {
		return movie;
	}

	/**
	 * @param movie
	 *            the movie to set
	 */
	public void setMovie(String movie) {
		this.movie = movie;
	}

	/**
	 * @return the number
	 */
	public Double getNumber() {
		return number;
	}

	/**
	 * @param number
	 *            the number to set
	 */
	public void setNumber(Double number) {
		this.number = number;
	}

}
