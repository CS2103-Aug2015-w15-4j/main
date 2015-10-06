package storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import logic.Task;

import com.google.gson.*;

public class Storage {
	
	private static final boolean OVERWRITE = false;
	private static final boolean APPEND = true;
	private static final String DEFAULT_FILENAME = "Data.txt";
	private List<Task> taskList;
	private String fileName;
	
	public Storage() {
		taskList = new ArrayList<Task>();
		initializeStorage();
	}
	
	public void createFile() {
		fileName = DEFAULT_FILENAME;
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void add(Task task) {
		try {
			Gson gson = new Gson();
			FileWriter writer = new FileWriter(fileName,APPEND);
			writer.write(gson.toJson(task));
			writer.write('\n');
			writer.close();
			
			taskList.add(task);
			Collections.sort(taskList);
			rewriteFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void delete(int taskID) {
		for (int i=0; i<taskList.size(); i++) {
			if(taskID == taskList.get(i).getId()) {
				taskList.remove(i);
			}
		}
		Collections.sort(taskList);
		rewriteFile();
	}
	
	public void rewriteFile() {
		try {
			Gson gson = new Gson();
			FileWriter writer = new FileWriter(fileName,OVERWRITE);
			for(int i=0; i<taskList.size(); i++) {
				writer.write(gson.toJson(taskList.get(i)));
				writer.write('\n');
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Task> getAllTasks() {
		return taskList;
	}
	
	public void initializeStorage() {
		createFile();
		Gson gson = new Gson();
		Task task = new Task();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String currentLine = reader.readLine();
			while(currentLine != null) {
				task = gson.fromJson(currentLine, Task.class);
				taskList.add(task);
				currentLine = reader.readLine();
			}
			reader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
