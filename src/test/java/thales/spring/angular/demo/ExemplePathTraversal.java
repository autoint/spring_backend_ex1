package thales.spring.angular.demo;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExemplePathTraversal {

	public static boolean containsIllegals(String toExamine) {
	    Pattern pattern = Pattern.compile("[.=~#@*+%{}<>\\[\\]|\"\\_^]");
	    Matcher matcher = pattern.matcher(toExamine);
	    return matcher.find();
	}
	public static void main(String[] args) throws IOException {
		String toExamine = args[0];
		System.out.println(ExemplePathTraversal.containsIllegals(toExamine));			
	}
}
