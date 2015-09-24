package logic;
import java.util.List;
import parser.ParsedCommand;

public class Delete implements Command{

	Task toDelete;
	ParsedCommand specifications;
	int id;
	
	public Delete() {
		
	}

	public Delete(ParsedCommand specifications,int id) {
		this.specifications = specifications;
		Storage storage = new Storage();
		List<Task> taskList = storage.getAllTasks();
		this.id = id;
		this.toDelete = taskList.get(id);
	}

	@Override
	public void execute() {
		toDelete.deleteTask(id);
	}

	@Override
	public void undo() {
		if (specifications.getTaskType() == 2) {
			DeadlineTask deleted = (DeadlineTask) toDelete; 
			deleted.createDeadlineTask(deleted);
		} else if (specifications.getTaskType() == 1) {
			Task deleted = (Task) toDelete; 
			deleted.createTask(deleted);
		} else if (specifications.getTaskType() == 3) {
			Event deleted = (Event) toDelete; 
			deleted.createEvent(deleted);
		}
	}	
	
}
