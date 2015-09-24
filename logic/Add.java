package logic;

import parser.ParsedCommand;

public class Add implements Command{

	Task newTask;
	ParsedCommand specifications;
	int id;

	public Add(ParsedCommand specifications,int newId) {
		this.specifications = specifications;
		newTask = new Task(specifications);
		this.id = newId;
	}

	@Override
	public void execute() {
		if (specifications.getTaskType() == 2) {
			DeadlineTask newDeadlineTask = new DeadlineTask(specifications);
			newDeadlineTask.createDeadlineTask(newDeadlineTask);
			newTask = newDeadlineTask;
		} else if (specifications.getTaskType() == 1) {
			newTask.createTask(newTask);
		} else if (specifications.getTaskType() == 3) {
			Event newEvent =  new Event(specifications);
			newEvent.createEvent(newEvent);
			newTask = newEvent;
		}
	}

	@Override
	public void undo() {
		newTask.deleteTask(id);
	}

}
