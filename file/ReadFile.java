package file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import logic.Task;

public class ReadFile {

	String fileName = "";
	static String name;
	static String details;
	static int id;
	static boolean isCompleted;
	static ArrayList<String> tags;

	public ReadFile() {
	}

	public static List<Task> readFile(File fileInput) {
		List<Task> taskList = new ArrayList<Task>();
		try {
			FileReader fr = new FileReader(fileInput);
			BufferedReader br = new BufferedReader(fr);
			String sCurrentLine = null;
			if ((sCurrentLine = br.readLine()) != null) {
				while (sCurrentLine != null) {
					String[] line = sCurrentLine.split("|");
					name = line[0];
					details = line[1];
					id = Integer.parseInt(line[2]);
					isCompleted = Boolean.parseBoolean(line[3]);
					String[] tagFromFile = line[4].split(",");
					List<String> temp = Arrays.asList(tagFromFile);
					ArrayList<String> tagList = new ArrayList<String>(temp);
					/*
					 * for (String temp : tagFromFile){ tags.add(temp); }
					 */
					Task task = new Task();
					task.setName(name);
					task.setDetails(details);
					task.setId(id);
					task.setIsCompleted(isCompleted);
					task.setTags(tagList);
					taskList.add(task); // add task to list
					sCurrentLine = br.readLine();
				}
			}
			fr.close();
			br.close();
		} catch (IOException exception) {
			System.out.println(exception.getMessage());
		}
		return taskList;
	}

}
