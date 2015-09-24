package logic;

public class Controller {
	
	private View view;
	private Logic model;
	
	public Controller() {
		model = new Logic();
	}
	
	public void commandEntered() {
		// Stub, replace get command with actual method to read string when implemented.
		String command = "undo command";
		if (command != null && !command.equals("")) {
			view = model.executeCommand(command);
			// updateView(view); 	// Stub, used to update the GUI output fields
			
		}
		
	}
	// Used to test controller, delete
	public static void main(String[] args) {
		Controller newController = new Controller(); 
		newController.commandEntered();
	}
	
}
