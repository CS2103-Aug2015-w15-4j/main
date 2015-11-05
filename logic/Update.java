package logic;

import java.util.List;

import storage.Storage;
import parser.ParsedCommand;
import parser.ParsedCommand.TaskType;

//@author A0124777W
public class Update implements Command {

	private static final TaskType TASK = TaskType.FLOATING_TASK;
	private static final TaskType DEADLINETASK = TaskType.DEADLINE_TASK;
	private static final TaskType EVENT = TaskType.EVENT;
	private ParsedCommand specifications;
	private Task toUpdate;
	private Task updated;
	private Storage storage;
	private Model model;

	public Update(Storage storage) {
		this.storage = storage;
	}
	
	public Update(ParsedCommand specifications,Storage storage,Model model) {
		this.specifications = specifications;
		this.storage = storage;
		this.model = model;

	}

	public void execute() {
		List<Task> taskList = storage.getAllTasks();
		toUpdate = Logic.searchList(taskList,specifications.getTaskId());
		// Clone Task
		if (toUpdate.getTaskType() == TASK) {
			updated = new Task(toUpdate);
			updated = updateTask(specifications, updated);
		} else if (toUpdate.getTaskType() == DEADLINETASK) {
			DeadlineTask newTask = new DeadlineTask((DeadlineTask)toUpdate);
			updated = newTask;
			updated = updateTask(specifications, updated);
		} else if (toUpdate.getTaskType() == EVENT) {
			Event newTask = new Event((Event)toUpdate);
			updated = newTask;
			updated = updateTask(specifications, updated);
		}
		
		storage.delete(toUpdate.getId());
		storage.add(updated);
		model.updateModel(toUpdate.getName() + " updated",updated.getId());
	}

	public void undo() {
		storage.delete(updated.getId());
		storage.add(toUpdate);
		storage.sort();
		model.updateFocus(toUpdate.getId());
	}

	/*
	 * Returns a Task Object with all the specified fields updated. Does not add
	 * anything to database.
	 */
	public Task updateTask(ParsedCommand parsedInput, Task toUpdate) {

		if (parsedInput.getTitle() != null && !parsedInput.getTitle().isEmpty()) {
			toUpdate.setName(parsedInput.getTitle());
		}
		if (parsedInput.getDescription() != null && !parsedInput.getDescription().isEmpty()) {
			toUpdate.setDetails(parsedInput.getDescription());
		}
		if (parsedInput.getTags().size() != 0) {
			toUpdate.setTags(parsedInput.getTags());
		}
		if (parsedInput.isCompleted() != null) {
			toUpdate.setIsCompleted(parsedInput.isCompleted());
		}
		if (parsedInput.getFirstDate() != null && parsedInput.getSecondDate() == null) {
			if (toUpdate.getTaskType() == TASK) {
				toUpdate = new DeadlineTask(toUpdate,parsedInput.getFirstDate());	// Convert to DeadlineTask
			} else if (toUpdate.getTaskType() == DEADLINETASK) {
				((DeadlineTask) toUpdate).setEnd(parsedInput.getFirstDate());
			} else if (toUpdate.getTaskType() == EVENT) {
				toUpdate = new DeadlineTask(toUpdate,parsedInput.getFirstDate());
			}

		} else if (parsedInput.getFirstDate() != null && parsedInput.getSecondDate() != null) {

			if (toUpdate.getTaskType() == TASK || toUpdate.getTaskType() == DEADLINETASK) {
				toUpdate = new Event(toUpdate,parsedInput.getFirstDate(),parsedInput.getSecondDate());	// Convert to Event
			} else if (toUpdate.getTaskType() == EVENT) {
				((Event) toUpdate).setStart(parsedInput.getFirstDate());
				((Event) toUpdate).setEnd(parsedInput.getSecondDate());
			}
		}

		return toUpdate;
	}

	/*
	 *	Checks if the parsedInput is valid for the execute method and updates the view with
	 * 	the corresponding error message
	 */
	public static boolean checkValid(ParsedCommand parsedInput, Model view) {

		if (parsedInput.getTaskId() >= Logic.getNewId()) {
			view.setConsoleMessage("Error: Invalid TaskID");
			return false;
		}

		return true;
	}

}
