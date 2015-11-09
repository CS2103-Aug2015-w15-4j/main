package logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import storage.Storage;
import parser.ParsedCommand;
import parser.ParsedCommand.TaskType;

//@@author A0124777W
public class Update implements Command {

	private static final TaskType TASK = TaskType.FLOATING_TASK;
	private static final TaskType DEADLINETASK = TaskType.DEADLINE_TASK;
	private static final TaskType EVENT = TaskType.EVENT;

	public static final String MESSAGE_UPDATED = "%1$s updated";

	private ParsedCommand specifications;
	private Task toUpdate;
	private Task updated;
	private Storage storage;
	private Model model;

	public Update() {

	}

	public Update(ParsedCommand specifications,Storage storage,Model model) {
		this.specifications = specifications;
		this.storage = storage;
		this.model = model;
	}

	public void execute() {
		List<Task> taskList = storage.getAllTasks();
		toUpdate = Logic.searchList(taskList, specifications.getTaskId());
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
		model.updateModel(String.format(MESSAGE_UPDATED, toUpdate.getName()),updated.getId());
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
		String title = parsedInput.getTitle();
		String description = parsedInput.getDescription();
		ArrayList<String> tags = parsedInput.getTags();
		Boolean isCompleted = parsedInput.isCompleted();
		Calendar firstDate = parsedInput.getFirstDate();
		Calendar secondDate = parsedInput.getSecondDate();

		return updateTask(title,description,tags,isCompleted,firstDate,secondDate, toUpdate);
	}

	/*
	 * Returns a Task Object with all the specified fields updated. Does not add
	 * anything to database.
	 */
	public Task updateTask(String title,String description,ArrayList<String> tags,Boolean isCompleted,Calendar firstDate,Calendar secondDate, Task toUpdate) {

		if (title != null && !title.isEmpty()) {
			toUpdate.setName(title);
		}
		if (description != null && !description.isEmpty()) {
			toUpdate.setDescription(description);
		}
		if (tags != null && tags.size() != 0) {
			toUpdate.setTags(tags);
		}
		if (isCompleted != null) {
			toUpdate.setIsCompleted(isCompleted);
		}
		if (firstDate != null && secondDate == null) {
			if (toUpdate.getTaskType() == TASK) {
				toUpdate = new DeadlineTask(toUpdate,firstDate);	// Convert from task to DeadlineTask
			} else if (toUpdate.getTaskType() == DEADLINETASK) {
				((DeadlineTask) toUpdate).setEnd(firstDate);
			} else if (toUpdate.getTaskType() == EVENT) {
				toUpdate = new DeadlineTask(toUpdate,firstDate);	// Convert from Event to DealineTask
			}

		} else if (firstDate != null && secondDate != null) {

			if (toUpdate.getTaskType() == TASK || toUpdate.getTaskType() == DEADLINETASK) {
				toUpdate = new Event(toUpdate,firstDate,secondDate);	// Convert to Event
			} else if (toUpdate.getTaskType() == EVENT) {
				((Event) toUpdate).setStart(firstDate);
				((Event) toUpdate).setEnd(secondDate);
			}
		}

		return toUpdate;
	}

	/*
	 *	Checks if the parsedInput is valid for the execute method and updates the view with
	 * 	the corresponding error message
	 */
	public static boolean checkValid(ParsedCommand parsedInput, Model view) {

		if (!Logic.checkID(parsedInput.getTaskId())) {
			view.setConsoleMessage(Logic.ERROR_INVALID_ID);
			return false;
		}

		return true;
	}

}
