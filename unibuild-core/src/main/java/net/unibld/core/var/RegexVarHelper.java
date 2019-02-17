package net.unibld.core.var;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for variable substitution-related regular expressions
 * @author andor
 *
 */
public class RegexVarHelper {
	private static Pattern pattern;

	/**
	 * Returns a regex matcher for variables, seeking for the pattern ${...}
	 * @param str String to check/match
	 * @return Regex matcher for variables
	 */
	public static Matcher getMatcher(String str) {
		if (str==null) {
			throw new IllegalArgumentException("Input string was null");
		}
		if (pattern==null) {
			pattern = Pattern.compile("\\$\\{(.*?)\\}");
		}
		return pattern.matcher(str);
	}
}
