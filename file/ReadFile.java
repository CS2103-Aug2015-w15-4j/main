package file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReadFile {

	public ReadFile() {
	}

	public String readFile(String fileInput) {
		String sCurrentLine = "", result = null;
		File file = new File(fileInput);
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			while ((sCurrentLine = br.readLine()) != null) {
				result = sCurrentLine;
			}
			fr.close();
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

}
