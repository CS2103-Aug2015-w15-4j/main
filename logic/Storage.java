package logic;

import java.util.ArrayList;
import java.util.List;

public class Storage {

	public Storage() {

	}

	public void add(Task userInput) {
		System.out.println("TaskWithoutDeadlines Add successful!");
	}

	public void add(DeadlineTask userInput) {
		System.out.println("TaskWithDeadlines add successful!");
	}

	public void add(Event userInput) {
		System.out.println("Event add successful!");
	}

	public void delete(int id) {
		System.out.println("Delete successful!");
	}

	public List<Task> getAllTasks() {
		ArrayList<Task> stubTaskList = new ArrayList<Task>();
		stubTaskList.add(new Event());
		stubTaskList.add(new DeadlineTask());
		stubTaskList.add(new DeadlineTask());
		stubTaskList.add(new Event());
		return stubTaskList;
	}

}
