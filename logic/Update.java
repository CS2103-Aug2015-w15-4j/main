package logic;

public class Update implements Command {
	
	UserData specifications;
	
	public Update(UserData specifications) {
		this.specifications = specifications;
	}
	
	public void execute() {
		if (specifications.type.compareToIgnoreCase("Task") == 0) {
			
		} else if (specifications.type.compareToIgnoreCase("DeadlineTask") == 0) {
			
		} else if (specifications.type.compareToIgnoreCase("Event") == 0) {
			
		}
	}
	
	public void undo() {
		
	}
	
	
}
