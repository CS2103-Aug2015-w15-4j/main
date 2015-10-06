package logic;

import java.util.List;

import parser.ParsedCommand;
import storage.Storage;

public class Delete implements Command {

	Task toDelete;
	int taskId;
	Storage storage = new Storage();

	public Delete() {

	}

	public Delete(ParsedCommand specifications) {
		List<Task> taskList = storage.getAllTasks();
		this.taskId = specifications.getTaskId();
		this.toDelete = taskList.get(taskId);
	}

	@Override
	public void execute() {
		deleteTask(taskId);
	}

	@Override
	public void undo() {
		if (taskId == 2) {
			DeadlineTask deleted = (DeadlineTask) toDelete;
			Add.storeTask(deleted);
		} else if (taskId == 1) {
			Task deleted = (Task) toDelete;
			Add.storeTask(deleted);
		} else if (taskId == 3) {
			Event deleted = (Event) toDelete;
			Add.storeTask(deleted);
		}
	}

	/*
	 * Deletes the specified task from the database
	 */
	public static void deleteTask(int id) {
		Storage storage = new Storage();
		storage.delete(id);
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
