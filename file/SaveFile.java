package file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveFile {

	public SaveFile() {
	}

	public void saveFile(String fileInput, String newPath) {
		File file = new File(fileInput);
		try {
			FileWriter fw = new FileWriter(file);
			fw.flush();

			fw.write(newPath);
			fw.write('\n');

			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
