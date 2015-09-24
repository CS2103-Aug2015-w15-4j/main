package logic;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class CommandUnitTest {

	public static void main(String[] args) {
		LinkedList<Invoker> commandHistory = new LinkedList<Invoker>();
		
		String userInput = "duck";
		Parser parser = new Parser();
		UserData data = parser.parse(userInput);
		
		data.id = 1;
		data.type = "Event";
		Command newCommand1 = new Add(data);
		Invoker invoke = new Invoker(newCommand1);
		
		invoke.execute();
		commandHistory.addFirst(invoke);
		
		UserData data1 = parser.parse(userInput);
		data1.type = "Taskwithoutdeadline";
		Command newCommand2 = new Add(data1);
		invoke = new Invoker(newCommand2);
		
		invoke.execute();
		commandHistory.addFirst(invoke);
		commandHistory.poll().undo();;
		invoke.execute();
		commandHistory.addFirst(invoke);
		invoke.execute();
		commandHistory.addFirst(invoke);
		
		invoke = new Invoker(newCommand1);
		
		invoke.execute();
		commandHistory.addFirst(invoke);
		
		UserData data2 = parser.parse(userInput);
		data2.type = "Taskwithoutdeadline";
		Command newCommand3 = new Delete(data2);
		invoke = new Invoker(newCommand3);
		invoke.execute();
		commandHistory.addFirst(invoke);
		
		Command newCommand4 = new Delete(data2);
		invoke = new Invoker(newCommand4);
		invoke.execute();
		commandHistory.addFirst(invoke);
		
		UserData data3 = parser.parse(userInput);
		data3.type = "Event";
		Command newCommand5 = new Delete(data3);
		invoke = new Invoker(newCommand5);
		invoke.execute();
		commandHistory.addFirst(invoke);
		
		// Undo all
		while (commandHistory.size() != 0) {
			commandHistory.pollFirst().undo();
		}

	}
	
	public List<Task> getCompletedTasks() {
		Storage storage = new Storage();
		List<Task> taskList = storage.getAllTasks();
		List<Task> completed = new ArrayList<Task>();
		for (int i=0; i<taskList.size(); i++) {
			if (taskList.get(i).getIsCompleted() == true) {
				completed.add(taskList.get(i));
			}
		}
		return taskList;
	}
	
	public List<Task> getUncompletedTasks() {
		Storage storage = new Storage();
		List<Task> taskList = storage.getAllTasks();
		List<Task> completed = new ArrayList<Task>();
		for (int i=0; i<taskList.size(); i++) {
			if (taskList.get(i).getIsCompleted() == false) {
				completed.add(taskList.get(i));
			}
		}
		return taskList;
	}
}
