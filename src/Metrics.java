/*	Metrics.java

*	A java implementation of the unix 'wc' utility by Andrew Enright.
*
*	Usage: 'java wc <-l|-c|-w|-s|-C> filename
*
*  Credit to technicalkeeda.com for getFileExtension()	
*/
import java.io.*;

import java.util.LinkedList;
import java.util.List;

import picocli.CommandLine;
import picocli.CommandLine.*;


@Command(description="Prints metrics of the given file to STDOUT.",	name="Metrics")
public class Metrics implements Runnable{
	private static void printUsage() {
		System.out.println("Usage: 'java Metrics <-l|-c|-w> filename1 filename2 ... filenameN'"
				+ "\n'java Metrics  -l <filename>' will print the line count of all specified files"
				+ "\n'java Metrics  -c <filename>' will print the character count"
				+ "\n'java Metrics  -w <filename>' will print the word count"
				+ "\n'java Metrics  <filename>' will print all of the above"
				+ "\n'java Metrics  -l -c filename1 filename2' will print line and char counts for filename1 and filename2" 
				);
			System.exit(0);
	}
	
	@Option(description = "Count number of lines", names = { "-l" }, paramLabel="countLines") 
	boolean countLines;
	
	@Option(description = "Count number of words", names = { "-w" }, paramLabel="countWords") 
	boolean countWords;
	
	@Option(description = "Count number of chars", names = { "-c" }, paramLabel="countChars")
	boolean countChars;
	
	@Option(description = "Count lines of source code", names = { "-s" }, paramLabel="countCode")
	boolean countCode;

	@Option(description = "Count lines of comment", names = { "-C" }, paramLabel="countComment")
	boolean countComments;
	
	@Parameters
	LinkedList<File> files;
		
	private static final int COLUMN_WIDTH = 14;
	private int totalLines, totalChars, totalWords, totalCode, totalComments, numItems;

	private LinkedList<wcFileNode> listHead = null;	
	
	public static void main(String[] args) {
		if (args.length == 0) printUsage();		
		Metrics wordCounterInstance = new Metrics();
		CommandLine.run(wordCounterInstance, args);
	}
	
	public void run() {		
		listHead = new LinkedList<wcFileNode>();
		
		try { populateList(files, listHead); }
		catch (Exception e) { 
			System.out.println("Error encountered populating list : " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
			}
		
		//No argument specified? Print all metrics
		if (!countLines && !countWords && !countChars && !countCode && !countComments) 			
			countLines = countWords = countChars = countCode = countComments = true;		
		
		printHeader();
		gatherFileMetrics(listHead);		
		for (wcFileNode lastListItem : listHead) formattedPrint(	lastListItem.lines, 
				lastListItem.words, 
				lastListItem.chars, 
				lastListItem.linesOfCode,
				lastListItem.linesOfComment,
				lastListItem.file.getPath());
		if (numItems > 1) 
			formattedPrint(totalLines, totalWords, totalChars, totalCode, totalComments, "total");
	}
	
	private void gatherFileMetrics(LinkedList<wcFileNode> listHead) {
		for (wcFileNode lastListItem : listHead) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(lastListItem.file));
			} catch (FileNotFoundException e) {
				System.out.println("Argument could not be parsed as an argument or filename.");
				System.exit(1);
			}
			String line = null;
			String ext = getFileExtension(lastListItem.file);
			try {
				line = reader.readLine();
			} catch (IOException err) {
				System.out.println("Could not parse line 0");
			} 
			boolean currentlyBlockComment = false;
			
			while (line != null) {
				lastListItem.lines++;			
				lastListItem.chars += line.length() + 2;  	//The Unix WC utility includes CR and LF characters in its count; String.length does not.
				lastListItem.words += line.split("\\s+").length; 
				
				if (ext.equals(".c") || ext.equals(".java")) {
						
						if (line.contains("/*") && line.contains("*/")) lastListItem.linesOfComment++; 
						else if (line.contains("/*")) 	{
							currentlyBlockComment = true;
							lastListItem.linesOfComment++;
						}
						else if (line.contains("*/")) {
							currentlyBlockComment = false;
							lastListItem.linesOfComment++;
						}
						else if (line.contains("//") && !currentlyBlockComment) lastListItem.linesOfComment++;
						else if (currentlyBlockComment) lastListItem.linesOfComment++;
				
						if (line.split("//").length > 1 && !currentlyBlockComment) //there are contents on the line besides comment
							lastListItem.linesOfCode++;
						else if (!line.trim().isEmpty() && !currentlyBlockComment) lastListItem.linesOfCode++;
				} else if (ext == ".h") {
					
				}
				
				//Get next line; if null the loop breaks here
				try {
					line = reader.readLine();
				} catch (IOException err) {
					System.out.println("Could not parse line" + lastListItem.lines);
				} 
			}
	
			totalWords += lastListItem.words;
			totalChars += lastListItem.chars;
			totalLines += lastListItem.lines;
			totalCode += lastListItem.linesOfCode;
			totalComments += lastListItem.linesOfComment;
		
			
			try {
				reader.close();
			} catch (IOException err) {
				System.out.println("Error closing buffered reader");
				err.printStackTrace();
			}
			
		}
	}
	
	//Credit to technicalkeeda.com for getFileExtension()

	private String getFileExtension(File file) {
		String extension = "";
		try {
		if (file != null && file.exists()) {
			String name =file.getName();
			extension = name.substring(name.lastIndexOf("."));
		}
		return extension;
		} catch (Exception e) {
			extension = "";
		}
		return extension;
	}

	private void populateList(List<File> files, LinkedList<wcFileNode> listHead) throws FileNotFoundException {
		for (File current : files) {
				if (!current.exists()) throw new FileNotFoundException("Could not find file " + current.getName());
				wcFileNode tmp = new wcFileNode(current);
				listHead.add(tmp);
			}	
	}
	

	private void printHeader() {
		String format = "%"+COLUMN_WIDTH+"s";
		if (countLines) System.out.printf(format, "lines");
		if (countWords) System.out.printf(format, "words");
		if (countWords) System.out.printf(format, "chars");
		if (countCode) 	System.out.printf(format, "sourcelines");
		if (countComments) System.out.printf(format, "commentlines");
		
		System.out.printf(" %-"+COLUMN_WIDTH+"s%n", "filename");
	}
	
	private void formattedPrint(Integer lines, Integer words, Integer chars, Integer linesCode, Integer linesComment, String filename) {
		String format = "%"+COLUMN_WIDTH+"d";
		if (countLines) System.out.printf(format, lines);
		if (countWords) System.out.printf(format, words);
		if (countChars) System.out.printf(format, chars);
		if (countCode) System.out.printf(format, linesCode);
		if (countComments) System.out.printf(format, linesComment);
		
		System.out.printf(" %s%n", filename);
	}
	

class wcFileNode {
	int lines, chars, words, linesOfCode, linesOfComment;
	File file;
	wcFileNode next = null;
	
	public wcFileNode(File toCount) {
		this.file = toCount;
		if (!file.exists()) throw new IllegalArgumentException("No file found matching '" + file.getName() + "'");
	}
}


}
