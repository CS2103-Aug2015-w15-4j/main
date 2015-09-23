package file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import logic.DeadlineTask;
import logic.Event;
import logic.Task;

public class ReadFile {
	
	//ArrayList<Event>;
	//ArrayList<TaskWithDeadlines>;
	//ArrayList<TaskWithoutDeadlines>;
	String fileName = "";
	
	public ReadFile(){}
	
	public static List<Task> readFile(File fileInput){
		ArrayList<Task> stubTaskList = new ArrayList<Task>();
		try {
			FileReader fr = new FileReader(fileInput);
			BufferedReader br = new BufferedReader(fr);
			String sCurrentLine = null;
			int i = 1;
			if ((sCurrentLine = br.readLine()) != null) {
				while (sCurrentLine != null) {
					//sb.append(i + ". " + sCurrentLine + "\n");
					String[] line = sCurrentLine.split("|");
					if (line[4].equalsIgnoreCase("Event")){
						//stubTaskList.add(new Event());
					} else if (line[4].equalsIgnoreCase("TaskWithDeadlines")){
						//stubTaskList.add(new DeadlineTask());
					} else if (line[4].equalsIgnoreCase("TaskWithoutDeadlines")){
						
					}
					
					i++;
					sCurrentLine = br.readLine();
				}
			}
			fr.close();
			br.close();
		} catch (IOException exception) {
			System.out.println(exception.getMessage());
		}
		return stubTaskList;
	}
	
}
