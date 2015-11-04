package logic;

import java.util.List;

import parser.ParsedCommand;
import storage.Storage;

public class Delete implements Command {

	private Task toDelete;
	private int taskId;
	private Storage storage;
	private Model model;

	public Delete(Storage storage) {
		this.storage = storage;
	}

	public Delete(ParsedCommand specifications,Storage storage,Model model) {
		this.model = model;
		List<Task> taskList = storage.getAllTasks();
		this.taskId = specifications.getTaskId();
		this.toDelete = Logic.searchList(taskList,taskId);
		this.storage = storage;
	}

	@Override
	public void execute() {
		storage.delete(taskId);

		model.updateModel("Task " + taskId + " deleted");
	}

	@Override
	public void undo() {
		Task deleted = toDelete;
		storage.add(deleted);

		model.updateFocus(deleted.getId());
	}


	public static boolean checkValid(ParsedCommand specifications, Model model) {
		int idToCheck = specifications.getTaskId();
		if (!Logic.checkID(idToCheck)) {
			model.setConsoleMessage(Logic.ERROR_INVALID_ID);
		}
		return true;
	}

}
