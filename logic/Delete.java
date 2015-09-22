package logic;
import java.util.List;

public class Delete implements Command{

	Task toDelete;
	userData specifications;
	
	public Delete() {
		
	}

	public Delete(userData specifications) {
		this.specifications = specifications;
		Storage storage = new Storage();
		List<Task> taskList = storage.getAllTasks();
		toDelete = taskList.get(specifications.id);
	}

	@Override
	public void execute() {
		toDelete.deleteTask(specifications.id);
	}

	@Override
	public void undo() {
		if (specifications.type.equalsIgnoreCase("TaskWithDeadline")) {
			DeadlineTask deleted = (DeadlineTask) toDelete; 
			deleted.createDeadlineTask(deleted);
		} else if (specifications.type.equalsIgnoreCase("TaskWithoutDeadline")) {
			Task deleted = (Task) toDelete; 
			deleted.createTask(deleted);
		} else if (specifications.type.equalsIgnoreCase("Event")) {
			Event deleted = (Event) toDelete; 
			deleted.createEvent(deleted);
		}
	}	
	
}
