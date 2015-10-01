package file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import logic.Task;

public class SaveFile {

	public SaveFile() {
	}

	public static void addAndSaveFile(File fileInput, List<Task> list) {
		try {
			FileWriter fw = new FileWriter(fileInput, true);
			BufferedWriter bw = new BufferedWriter(fw);
			Task temp = list.get(list.size() - 1);
			bw.write(temp.getName() + "|" + temp.getDetails() + "|"
					+ temp.getId() + "|" + temp.getIsCompleted() + "|"
					+ temp.getTags() + "\r\n"); // getDate(), getTime()
			bw.close();
			fw.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void deleteAndSaveFile(File fileInput, Task list) {
		String deletedLine = "";
		String tempLine;
		try {
			File tmp = File.createTempFile("tmp", ""); // create a temp file
			BufferedReader br = new BufferedReader(new FileReader(
					fileInput.getName()));
			BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
			String input = list.getName() + "|" + list.getDetails() + "|"
					+ list.getId() + "|" + list.getIsCompleted() + "|"
					+ list.getTags() + "\r\n";
			while (!(tempLine = br.readLine()).equals(input)) {
				bw.write(String.format("%s%n", tempLine));
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
			File oldFile = new File(fileInput.getName());
			oldFile.setWritable(true);
			if (oldFile.delete()) {
				tmp.renameTo(oldFile);
			}

		} catch (IOException e) {

		}
	}

}
