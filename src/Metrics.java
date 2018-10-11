/*	Metrics.java

*	A java implementation of the unix 'wc' utility by Andrew Enright.
*
*	Usage: 'java wc <-l|-c|-w|-s|-C> filename
*
*  Credit to technicalkeeda.com for getFileExtension()	
*  Credit to https://www.geeksforgeeks.org/print-unique-words-string/ for unique words in string
*/
import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import picocli.CommandLine;
import picocli.CommandLine.*;


@Command(description="Prints metrics of the given file to STDOUT.",	name="Metrics")
public class Metrics implements Runnable{
	private static final int COLUMN_SEP_WIDTH = 1; //Amount of whitespace to put between columns.
	private static final String NOT_APPLICABLE = "n/a";

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
	@Option(description = "Show usage", names = { "-h" }, paramLabel="help") 
	boolean showHelp;
	
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
	
	@Option(description = "Calculate Halstead complexity", names = {"-H"}, paramLabel="countHalstead")
	boolean calcHalstead;
	
	@Parameters
	LinkedList<File> files;
		
	private int totalLines, totalChars, totalWords, totalCode, totalComments, totalUniqueOperands, totalUniqueOperators, totalOperands, totalOperators;

	private LinkedList<metricsFileNode> listHead = null;	
	
	public static void main(String[] args) {		
		if (args.length == 0 ) printUsage();
		Metrics wordCounterInstance = new Metrics();
		CommandLine.run(wordCounterInstance, args);
	}
	
	public void run() {		
		if (showHelp) printUsage();
		listHead = new LinkedList<metricsFileNode>();
		
		try { populateList(files, listHead); }
		catch (Exception e) { 
			System.out.println("Error encountered populating list : " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
			}
		
		//No argument specified? Print all metrics
		if (!countLines && !countWords && !countChars && !countCode && !countComments) 			
			countLines = countWords = countChars = countCode = countComments = true;		
		
		gatherFileMetrics(listHead);	
		printHeader();
		for (metricsFileNode lastListItem : listHead) formattedPrint(	lastListItem.lines, 
				lastListItem.words, 
				lastListItem.chars, 
				lastListItem.linesOfCode,
				lastListItem.linesOfComment,
				lastListItem.file.getPath());
		if (listHead.size() > 1) 
			formattedPrint(totalLines, totalWords, totalChars, totalCode, totalComments, "total");
	}
	
	private void gatherFileMetrics(LinkedList<metricsFileNode> listHead) {
		for (metricsFileNode lastListItem : listHead) {
			totalWords += lastListItem.words;
			totalChars += lastListItem.chars;
			totalLines += lastListItem.lines;
			totalCode += lastListItem.linesOfCode;
			totalComments += lastListItem.linesOfComment;
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

	private void populateList(List<File> files, LinkedList<metricsFileNode> listHead) throws FileNotFoundException {
		for (File current : files) {
				if (!current.exists()) throw new FileNotFoundException("Could not find file " + current.getName());
				metricsFileNode tmp = new metricsFileNode(current);
				listHead.add(tmp);
			}	
	}

	private void printHeader() {
		if (countLines) System.out.printf("%"+ getColumnWidth(totalLines, 7) +"s", "lines");
		if (countWords) System.out.printf("%"+ getColumnWidth(totalWords, 7) +"s", "words");
		if (countChars) System.out.printf("%"+ getColumnWidth(totalChars, 7 )+"s", "chars");
		if (countCode) System.out.printf("%"+ getColumnWidth(totalCode, 8) + "s", "source");
		if (countComments) System.out.printf("%"+ getColumnWidth(totalComments, 10) + "s", "comments");
				
		System.out.printf(" %-10s%n", "filename");
	}
	
	private void formattedPrint(Integer lines, Integer words, Integer chars, Integer linesCode, Integer linesComment, String filename) {
		if (countLines) System.out.printf("%"+ getColumnWidth(totalLines, 7) +"d", lines);
		if (countWords) System.out.printf("%"+ getColumnWidth(totalWords, 7) +"d", words);
		if (countChars) System.out.printf("%"+ getColumnWidth(totalChars, 7 )+"d", chars);
		if (countCode) System.out.printf("%"+ getColumnWidth(totalCode, 8) + "s", linesCode > 0 ? new Integer(linesCode).toString() : NOT_APPLICABLE);
		if (countComments) System.out.printf("%"+ getColumnWidth(totalComments, 10) + "s", linesComment > 0 ? new Integer(linesComment).toString() : NOT_APPLICABLE);
		
		System.out.printf(" %s%n", filename);
	}
	
	private int getColumnWidth(int totalToMeasure, int minimumWidth) {		
		return Math.max(new Integer(totalLines).toString().length()  + COLUMN_SEP_WIDTH, minimumWidth);
	}
	
class metricsFileNode {
	int lines, chars, words, linesOfCode, linesOfComment, totalOperators, totalOperands;
	Set<String> operators = new HashSet<String>(), operands = new HashSet<String>();
	File file;
	metricsFileNode next = null;
	String ext = "";
	
	public metricsFileNode(File toCount) {
		this.file = toCount;
		if (!file.exists()) throw new IllegalArgumentException("No file found matching '" + file.getName() + "'");
		
		ext = getFileExtension(file);
		System.out.println("File " + file.getName() + " is a dirty rotten " + ext);
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.out.println("Argument could not be parsed as an argument or filename.");
			System.exit(1);
		}
		String line = null;
		
		try {
			line = reader.readLine();
		} catch (IOException err) {
			System.out.println("Could not parse line 0");
		} 
	
		boolean currentlyBlockComment = false;
		while (line != null) {
			line = line.replace("\t", "").replace("\n", "");
			lines++;		
			chars += line.length() ;  	//The Unix WC utility includes CR and LF characters in its count; String.length does not.
			words += line.split("\\s+").length; 
					
			if ((countCode || countComments) && 
					!line.isEmpty() && 
					(ext.equals(".c") || 
					ext.equals(".java") || 
					ext.equals(".cpp") || 
					ext.equals(".h") || 
					ext.equals(".hpp"))) {
				
					//Halstead Metrics
					
					//Count code and Comments
					if (currentlyBlockComment && !line.contains("*/")) linesOfComment++; 
						else if (line.contains("/*")) { //Does this line have a self-contained comment /*like this*/
							linesOfComment++; 		
							String before = line.substring(0, line.indexOf('*')).trim(),
									after = "";
							if (line.contains("*/")) {
								after = line.substring(line.lastIndexOf("*/"));
							} else currentlyBlockComment = true;
							if (before.length() > 1) linesOfCode++; //Are there contents before the comment?
							else if (after.length() > 2) linesOfCode++; //Are there contents after the comment?
					} else if (line.contains("*/")) {
							linesOfComment++;
							currentlyBlockComment = false;
							String after = line.substring(line.lastIndexOf("*/"));
							if (after.length() > 2) linesOfCode++; //Are there contents after the comment?
					}
					else if (line.contains("//")) {
						linesOfComment++;
						String before = line.substring(0, line.indexOf("//")).trim();
								
						if (before.length() > 1) linesOfCode++;
					} else linesOfCode++;				
			} 
			
			try { //Get next line; if null the loop breaks here
				line = reader.readLine();
			} catch (IOException err) {
				System.out.println("Could not parse line" + lines);
			} 
	
		}
		
		try {
			reader.close();
		} catch (IOException err) {
			System.out.println("Error closing buffered reader");
			err.printStackTrace();
		}
	}
}
}

class metricsLib {
		public static int countCode(String toCount){
			return 0;
		}
		public static int countComments(String toCount) {
			return 0;
		}
		public static int countOperators(String toCount) {
			return 0;
		}
		public static int countOperands(String toCount) {
			return 0;
		}
}