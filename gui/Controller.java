package gui;

import logic.Logic;
import logic.View;

public class Controller {

	private View view;
	private Logic model;

	public Controller() {
		model = new Logic();
	}

	public View commandEntered(String command) {
		// Stub, replace get command with actual method to read string when
		// implemented.
		if (command != null && !command.equals("")) {
			view = model.executeCommand(command);
<<<<<<< HEAD:logic/Controller.java
			// updateView(view); // Stub, used to update the GUI output fields
=======
>>>>>>> 85d174f727779744f8dc0a86be30367e63c61326:gui/Controller.java
			return view;
		} else {
			return null;
		}

	}

}
