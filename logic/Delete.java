package logic;

import java.util.List;

import parser.ParsedCommand;
import storage.Storage;

public class Delete implements Command {

	private Task toDelete;
	private int taskId;
	private Storage storage;

	public Delete(Storage storage) {
		this.storage = storage;
	}

	public Delete(ParsedCommand specifications,Storage storage) {
		List<Task> taskList = storage.getAllTasks();
		this.taskId = specifications.getTaskId();
		this.toDelete = Logic.searchList(taskList,taskId);
		this.storage = storage;
	}

	@Override
	public void execute() {
		storage.delete(taskId);
	}

	@Override
	public void undo() {
		Task deleted = toDelete;
		storage.add(deleted);
	}


	public static boolean checkValid(ParsedCommand specifications) {
		int idToCheck = specifications.getTaskId();
		if (idToCheck < Logic.getNewId() && idToCheck > 0) {
			return true;
		} else {
			return false;
		}
	}

}
