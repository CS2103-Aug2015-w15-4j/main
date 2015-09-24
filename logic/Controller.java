package logic;

public class Controller {
	
	private static View view;
	private static Logic model;
	
	public Controller() {
		model = new Logic();
	}
	
	public static View commandEntered(String command) {
		// Stub, replace get command with actual method to read string when implemented.
		if (command != null && !command.equals("")) {
			view = model.executeCommand(command);
			//updateView(view); 	// Stub, used to update the GUI output fields
			return view;
		}
		else {
			return null;
		}
		
	}	
}
