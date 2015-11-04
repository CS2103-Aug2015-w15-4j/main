package logic;

import parser.ParsedCommand;
import parser.ParsedCommand.TaskType;
import storage.Storage;

public class Add implements Command {

	private static final TaskType TASK = TaskType.FLOATING_TASK;
	private static final TaskType DEADLINETASK = TaskType.DEADLINE_TASK;
	private static final TaskType EVENT = TaskType.EVENT;

	private Task newTask;
	private ParsedCommand specifications;
	private int id;
	private Storage storage;
	private Model model;

	public Add(ParsedCommand specifications, Storage storage,Model model) {
		this.specifications = specifications;
		newTask = new Task(specifications);
		this.id = Logic.getNewId();
		this.storage = storage;
		this.model = model;
	}

	@Override
	public void execute() {
		if (specifications.getTaskType() == ParsedCommand.TaskType.FLOATING_TASK) {
			newTask = new Task(specifications);
			newTask.setId(id);
			storage.add(newTask);
		} else if (specifications.getTaskType() == ParsedCommand.TaskType.DEADLINE_TASK) {
			DeadlineTask newDeadlineTask = new DeadlineTask(specifications);
			newDeadlineTask.setId(id);
			storage.add(newDeadlineTask);
			newTask = newDeadlineTask;
		} else if (specifications.getTaskType() == ParsedCommand.TaskType.EVENT) {
			Event newEvent = new Event(specifications);
			newEvent.setId(id);
			storage.add(newEvent);
			newTask = newEvent;
		}

		model.updateModel(newTask.getName() + " added",newTask.getId());
	}

	@Override
	public void undo() {
		storage.delete(id);

		model.updateModel();
	}

	/*
	 * Checks whether the parsedInput is valid. Does not check task ID
	 */
	public static boolean checkValid(ParsedCommand parsedInput, Model view) {
		ParsedCommand.TaskType taskType = parsedInput.getTaskType();

		// Check if Task fields are present
		if (parsedInput.getTitle() == null) {
			view.setConsoleMessage("Error: Missing task title");
			return false;
		}
		if (taskType==null) {
			view.setConsoleMessage("Error: task type missing");
			return false;
		}
		if (taskType == TASK) {
			if (parsedInput.getSecondDate() != null) { // Error: Task Should have no End Field
				view.setConsoleMessage("Error: Task Should have no end field");
				return false;
			} else if (parsedInput.getFirstDate() != null) { // Error: Task Should have no Start Field
				view.setConsoleMessage("Error: Task Should have no start field");
				return false;
			}
		} else if (taskType == DEADLINETASK) {
			if (parsedInput.getSecondDate() != null) { // Error: DeadlineTask Should have no Start Field
				view.setConsoleMessage("Error: DeadlineTask Should have no start field");
				return false;
			}
		} else if (taskType == EVENT) {

		}

		return true;
	}

}
