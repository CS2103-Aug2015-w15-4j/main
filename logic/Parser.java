package logic;

public class Parser {
	
	public Parser() {
		
	}
	
	public UserData parse(String userInput) {
		UserData newData = new UserData();
		newData.type = "Task";
		return newData;
	}
}
