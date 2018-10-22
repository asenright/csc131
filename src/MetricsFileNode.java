import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class MetricsFileNode {
	
	int lines, chars, words;
	
	protected File file;
	
	public MetricsFileNode(File toCount) {
		this.file = toCount;
		if (!file.exists()) throw new IllegalArgumentException("No file found matching '" + file.getName() + "'");
		getFileMetrics();
	}
	private void getFileMetrics() {
		String line = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			while (reader.read() > -1)
				chars++;
			reader.close();
			
			reader = new BufferedReader(new FileReader(file)); //Reset reader to start reading lines
			line = reader.readLine();
			while (line != null) { 		
				getLineMetrics(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Argument could not be parsed as an argument or filename.");
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("IO Exception while reading.");
			System.exit(1);
		}
	}
	protected void getLineMetrics(String line) {
		lines++;		
		words += line.split("\\s+").length; 
	}
}