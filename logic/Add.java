package logic;

public class Add implements Command{

	Task newTask;
	userData specifications;

	public Add(userData specifications) {
		this.specifications = specifications;
		newTask = new Task(specifications);
	}

	@Override
	public void execute() {
		if (specifications.type.equalsIgnoreCase("DeadlineTask")) {
			DeadlineTask newDeadlineTask = new DeadlineTask(specifications);
			newDeadlineTask.createDeadlineTask(newDeadlineTask);
			newTask = newDeadlineTask;
		} else if (specifications.type.equalsIgnoreCase("Task")) {
			newTask.createTask(newTask);
		} else if (specifications.type.equalsIgnoreCase("Event")) {
			Event newEvent =  new Event(specifications);
			newEvent.createEvent(newEvent);
			newTask = newEvent;
		}
	}

	@Override
	public void undo() {
		newTask.deleteTask(specifications.id);
	}

}
