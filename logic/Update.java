package logic;

import java.util.List;

import storage.Storage;
import parser.ParsedCommand;

public class Update implements Command {

	private static final int EVENT = 3;
	private static final int DEADLINETASK = 2;
	private static final int TASK = 1;
	private ParsedCommand specifications;
	private Task toUpdate;
	private Task updated;

	public Update(ParsedCommand specifications) {
		this.specifications = specifications;
	}

	public void execute() {
		Storage storage = new Storage();
		List<Task> taskList = storage.getAllTasks();
		toUpdate = taskList.get(specifications.getTaskId());
		

		updated = updateTask(specifications, toUpdate);
		storage.delete(toUpdate.getId());
		storage.add(updated);
	}

	public void undo() {
		Storage storage = new Storage();
		storage.delete(updated.getId());
		storage.add(toUpdate);
	}

	/*
	 * Returns a Task Object with all the specified fields updated. Does not add
	 * anything to database.
	 */
	public Task updateTask(ParsedCommand parsedInput, Task toUpdate) {

		int taskType = parsedInput.getTaskType();

		if (taskType == TASK) {
			if (parsedInput.getTitle() != null) {
				toUpdate.setName(parsedInput.getTitle());
			} else if (parsedInput.getDescription() != null) {
				toUpdate.setDetails(parsedInput.getDescription());
			} else if (parsedInput.getTags().size() != 0) {
				toUpdate.setTags(parsedInput.getTags());
			}
		} else if (taskType == DEADLINETASK) {
			if (parsedInput.getTitle() != null) {
				toUpdate.setName(parsedInput.getTitle());
			} else if (parsedInput.getDescription() != null) {
				toUpdate.setDetails(parsedInput.getDescription());
			} else if (parsedInput.getTags().size() != 0) {
				toUpdate.setTags(parsedInput.getTags());
			} else if (parsedInput.getEnd() != null) {
				((DeadlineTask) toUpdate).setEnd(parsedInput.getEnd());
			}
		} else if (taskType == EVENT) {
			if (parsedInput.getTitle() != null) {
				toUpdate.setName(parsedInput.getTitle());
			} else if (parsedInput.getDescription() != null) {
				toUpdate.setDetails(parsedInput.getDescription());
			} else if (parsedInput.getTags().size() != 0) {
				toUpdate.setTags(parsedInput.getTags());
			} else if (parsedInput.getEnd() != null) {
				((DeadlineTask) toUpdate).setEnd(parsedInput.getEnd());
			} else if (parsedInput.getStart() != null) {
				((Event) toUpdate).setStart(parsedInput.getEnd());
			}
		}

		return toUpdate;
	}

	public static boolean checkValid(ParsedCommand parsedInput, View view) {
		int taskType = parsedInput.getTaskType();

		if (parsedInput.getTaskId() >= Logic.getNewId()) {
			view.setConsoleMessage("Error: Invalid TaskID");
			return false;
		}
		if (taskType != 0 || taskType != 1 || taskType != 2) {
			view.setConsoleMessage("Logic Error: task type missing");
		}
		if (taskType == TASK) {
			if (parsedInput.getEnd() != null) { // Error: Task Should have no
												// End Field
				view.setConsoleMessage("Error: Task Should have no end field");
				return false;
			} else if (parsedInput.getStart() != null) { // Error: Task Should
															// have no Start
															// Field
				view.setConsoleMessage("Error: Task Should have no start field");
				return false;
			}
		} else if (taskType == DEADLINETASK) {
			if (parsedInput.getStart() != null) { // Error: DeadlineTask Should
													// have no Start Field
				view.setConsoleMessage("Error: DeadlineTask Should have no start field");
				return false;
			}
		} else if (taskType == EVENT) {

		}

		return true;
	}

}
