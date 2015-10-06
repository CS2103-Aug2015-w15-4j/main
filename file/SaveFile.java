package file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import logic.Task;

public class SaveFile {

	public SaveFile() {
	}

	public static void addAndSaveFile(File fileInput, Task list) {
		String taskType = "task";
		/*
		 * if ( == 1){ taskType = "task"; } else if ( == 2){ taskType =
		 * "deadlineTask"; } else if ( == 3) { taskType = "event"; }
		 */

		try {
			FileWriter fw = new FileWriter(fileInput, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(list.getId() + "|" + list.getName() + "|"
					+ list.getDetails() + "|" + list.getIsCompleted() + "|"
					+ list.getTags() + "|" + "taskType:" + taskType + "\r\n"); // getDate(),
																				// getTime()
			bw.close();
			fw.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void deleteAndSaveFile(File fileInput, int id) {
		String deletedLine = "";
		String tempLine;
		try {
			File tmp = File.createTempFile("./Storage/tmp", ""); // create a
																	// temp file
			BufferedReader br = new BufferedReader(new FileReader(fileInput));
			BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
			while (null != (tempLine = br.readLine())) {
				String[] result = tempLine.split("|");
				if (Integer.parseInt(result[0]) == (id)) {
					break;
				} else {
					bw.write(String.format("%s%n", tempLine));
				}
			}

			deletedLine = tempLine;

			String l;
			// skip the line == deletedLine
			while (null != (l = br.readLine())) {
				bw.write(String.format("%s%n", l));
			}

			br.close();
			bw.close();

			// Replace the temp to fileInput
			String fileName = fileInput.toString();
			File oldFile = new File(fileName);
			oldFile.setWritable(true);
			if (oldFile.delete()) {
				tmp.renameTo(oldFile);
			}

		} catch (IOException e) {

		}
	}

}
