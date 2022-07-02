package me.straikerinacz.theapi.demo.stringutils;

import java.util.Arrays;

import me.devtec.shared.API;
import me.devtec.shared.utility.StringUtils;
import me.devtec.shared.utility.StringUtils.FormatType;

public class StringUtilsExample {

	public static void init() {
		//Simple colorizing of text
		String colorized = StringUtils.colorize("&cThis is red &fand this is white text!");
		System.out.println(colorized);

		/** Requires load Basics from API class **/
		API.basics().load();

		//Gradient (gradient tags and gradient settings of prefix/suffix is editable in the plugins/TheAPI/Tags.yml file)
		colorized = StringUtils.colorize("!#123456Example of gradient and tag!!fire");
		System.out.println(colorized);

		/** Requires load Basics from API class **/

		//Our speciality, for 1.7.10 - 1.15, default mc colors to rainbow
		//For 1.16+, random gradients
		colorized = StringUtils.colorize("&uRRRRAAAINBOOW &fand white!");
		System.out.println(colorized);

		//Gathering integers or any other numbers from String
		double number = StringUtils.getInt("15");
		number = StringUtils.getInt("19x"); //Returns 19
		number = StringUtils.getDouble("19x.9"); //Returns 19.9
		System.out.println(number);

		//Calculating exam from String
		number = StringUtils.calculate("97.6 + 18.02 / (3.2 * 4 - 9)"); //Returns double
		System.out.println(number);

		//Formating double

		/**
		 * Formatters:
		 * BASIC: (19799946.9797 -> 19799946.98)
		 * NORMAL: (19799946.9797 -> 19,799,946.98)
		 * COMPLEX: (19799946.9797 -> 19.8M)
		 */
		String formatted = StringUtils.formatDouble(FormatType.BASIC, number);
		System.out.println(formatted);

		/**
		 * Building String[] arguments (usable in the commands)
		 */
		String[] args = {"subcmd", "add", "164"};
		String build = StringUtils.buildString(args); //Returns "subcmd add 164"
		System.out.println(build);

		//Starting from specified position
		build = StringUtils.buildString(1, args); //Returns "add 164"
		System.out.println(build);

		//Starting & ending on specified position - do you want to "cut" args?
		build = StringUtils.buildString(1, 2, args); //Returns "add"
		System.out.println(build);

		/**
		 * Joins all values of Iterable/Collection/Array into one String and adds split between these values
		 */
		String joined = StringUtils.join(Arrays.asList("random", "Strings", "inside", "list"), " - ");
		System.out.println(joined); //Returns "random - Strings - inside - list"

		//Generates random integer between specified range
		int randomInt = StringUtils.generateRandomInt(-61, 61);
		System.out.println(randomInt);

		//Generates random double between specified range
		double randonDouble = StringUtils.generateRandomDouble(-61.5, 61.5);
		System.out.println(randonDouble);

		//Select random value from list
		//Works same for any other Collection, use StringUtils#getRandomFromCollection(Collection<T>) method
		String randomValue = StringUtils.getRandomFromList(Arrays.asList("random", "Strings", "inside", "list"));
		System.out.println(randomValue);

		/** Requires load Basics from API class **/

		//Convert time to readable String
		String timeInString = StringUtils.timeToString(3601);
		System.out.println(timeInString);

		//Get time from String to long
		long time = StringUtils.timeFromString(timeInString); //1hour 1second
		System.out.println(time);

		//Convert time to digits String
		timeInString = StringUtils.timeToString(time, ":"); //1:00:01
		System.out.println(timeInString);

		//Checks if String contains special symbols (which Minecraft doens't support in player's name)
		System.out.println(StringUtils.containsSpecial("testing_name")); //return false
		System.out.println(StringUtils.containsSpecial("testing.name")); //return true
	}
}
