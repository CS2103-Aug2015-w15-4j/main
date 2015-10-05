package logic;

public class Invoker {

	Command theCommand;

	public Invoker(Command newCommand) {
		theCommand = newCommand;
	}

	public void execute() {
		theCommand.execute();
	}

	public void undo() {
		theCommand.undo();
	}
}
