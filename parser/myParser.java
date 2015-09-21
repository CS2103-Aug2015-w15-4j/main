package parser;

import java.util.Date;
import java.util.List;
//import java.util.Map;
//import org.slf4j.LoggerFactory;
import java.util.Map;

import com.joestelmach.natty.*;


public class myParser {

	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		//System.out.println("Hi");
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse("the day before next wednesday");
		
		for(DateGroup group:groups) {
		  List<Date> dates = group.getDates();
		  System.out.println(dates.get(0).getDate());
		  /*
		  int line = group.getLine();
		  int column = group.getPosition();
		  String matchingValue = group.getText();
		  String syntaxTree = group.getSyntaxTree().toStringTree();
		  Map<String, List<ParseLocation>> parseMap = group.getParseLocations();
		  boolean isRecurreing = group.isRecurring();
		  Date recursUntil = group.getRecursUntil();
		  */
		}
		
		
	}

}
