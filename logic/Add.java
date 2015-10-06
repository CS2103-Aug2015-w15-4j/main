package logic;

import parser.ParsedCommand;
import storage.Storage;

public class Add implements Command {

	private static final int TASK = 1;
	private static final int DEADLINETASK = 2;
	private static final int EVENT = 3;

	private Task newTask;
	private ParsedCommand specifications;
	private int id;
	private Storage storage;

	public Add(Storage storage) {
		this.storage = storage;
	}
	
	public Add(ParsedCommand specifications, int newId, Storage storage) {
		this.specifications = specifications;
		newTask = new Task(specifications);
		this.id = newId;
		this.storage = storage;
	}

	@Override
	public void execute() {
		if (specifications.getTaskType() == 2) {
			DeadlineTask newDeadlineTask = new DeadlineTask(specifications);
			newDeadlineTask.setId(id);
			storage.add((Task) newDeadlineTask);
			newTask = newDeadlineTask;
		} else if (specifications.getTaskType() == 1) {
			newTask = new Task(specifications);
			newTask.setId(id);
			storage.add(newTask);
		} else if (specifications.getTaskType() == 3) {
			Event newEvent = new Event(specifications);
			newEvent.setId(id);
			storage.add((Task) newEvent);
			newTask = newEvent;
		}
	}

	@Override
	public void undo() {
		storage.delete(id);
	}

	/*
	 * Checks whether the parsedInput is valid. Does not check task ID
	 */
	public static boolean checkValid(ParsedCommand parsedInput, View view) {
		int taskType = parsedInput.getTaskType();

		// Check if Task fields are present
		if (parsedInput.getTitle() == null) {
			view.setConsoleMessage("Error: Missing task title");
			return false;
		} /*else if (parsedInput.getDescription() == null) {
			view.setConsoleMessage("Error: Missing task description");
			return false;
		} */

		if (taskType != 0 && taskType != 1 && taskType != 2) {
			view.setConsoleMessage("Logic Error: task type missing"); 
			return false;
		}
		if (taskType == TASK) {
			if (parsedInput.getEnd() != null) { // Error: Task Should have no End Field
				view.setConsoleMessage("Error: Task Should have no end field");
				return false;
			} else if (parsedInput.getStart() != null) { // Error: Task Should have no Start Field
				view.setConsoleMessage("Error: Task Should have no start field");
				return false;
			}
		} else if (taskType == DEADLINETASK) {
			if (parsedInput.getStart() != null) { // Error: DeadlineTask Should have no Start Field
				view.setConsoleMessage("Error: DeadlineTask Should have no start field");
				return false;
			}
		} else if (taskType == EVENT) {

		}

		return true;
	}

}
