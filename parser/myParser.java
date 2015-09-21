package parser;

import java.util.Date;
import java.util.List;
//import java.util.Map;

import com.joestelmach.natty.*;


public class myParser {

	
	public static void main(String[] args) {
		System.out.println("Hi");
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse("the day before next thursday");
		/*
		for(DateGroup group:groups) {
			System.out.println("Hi");
			/*
		  List<Date> dates = group.getDates();
		  int line = group.getLine();
		  int column = group.getPosition();
		  String matchingValue = group.getText();
		  String syntaxTree = group.getSyntaxTree().toStringTree();
		  Map parseMap = group.getParseLocations();
		  boolean isRecurreing = group.isRecurring();
		  Date recursUntil = group.getRecursUntil();
		  //*/
		//}
		
		
	}

}
