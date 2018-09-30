/*	wc.java
*	A java implementation of the unix 'wc' utility by Andrew Enright.
*
*	Usage: 'java wc <-l|-c|-w> filename
*/
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


class wcFileNode {
	int lines, chars, words, linesOfCode, linesOfComment;
	File file;
	wcFileNode next = null;
	
	//The constructor for this object takes a filename.
	//If there are wildcards or directories in that filename, the object will have a linked list of items attached.
	public wcFileNode(String filename) {
		this.file = new File(filename);
		if (!file.exists()) throw new IllegalArgumentException("No file found matching '" + filename + "'");
	}
}

public class Metrics {
	private static final int COLUMN_WIDTH = 14;
	private int totalLines, totalChars, totalWords, totalCode, totalComments, numItems;
	private boolean countLines = false, countChars = false, countWords = false, countCode = false, countComments = false;
	private wcFileNode listHead = null;	
	
	public static void main(String[] args) {
		if (args.length == 0) printUsage();						
		Metrics wordCounterInstance = new Metrics();
		wordCounterInstance.run(args);
	}
	
	void run(String[] args) {
		listHead = populateList(args, listHead);				
		if (!countLines && !countWords && !countChars && !countCode && !countComments) 			
			countLines = countWords = countChars = countCode = countComments = true;		//No argument specified? Print all metrics
		printHeader();
		printListMetrics(listHead);								
		if (numItems > 1) formattedPrint(totalLines, totalWords, totalChars, totalCode, totalComments, "total");
	}
	
	private void printListMetrics(wcFileNode listHead) {
		wcFileNode lastListItem = listHead;
		
		while (lastListItem != null) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(lastListItem.file));
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
			
			while (line != null) {
				lastListItem.lines++;			
				lastListItem.chars += line.length() + 2;  	//The Unix WC utility includes CR and LF characters in its count; String.length does not.
				lastListItem.words += line.split("\\s+").length; 
				
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
			
			formattedPrint(	lastListItem.lines, 
							lastListItem.words, 
							lastListItem.chars, 
							lastListItem.linesOfCode,
							lastListItem.linesOfComment,
							lastListItem.file.getPath());
	
			lastListItem = lastListItem.next;
			try {
				reader.close();
			} catch (IOException err) {
				System.out.println("Error closing buffered reader");
				err.printStackTrace();
			}
			
		}
	}

	private wcFileNode populateList(String[] args, wcFileNode listHead) {
		//Step 1: Populate the wcFile list with files
		for (int i = 0; i < args.length; i++) {
			//Parse current arg. Is it a command -l, -c, -w; or is it a filename?
			//If it is neither the program should crash.
						
			if (args[i].equals("-l") || args[i].equals("-c") || args[i].equals("-w")) { 
				if (i + 1 > args.length || args[i+1] == null) printUsage(); //The actual WC utility crashes if you do this!
				countLines = args[i].equals("-l") ? true : countLines;
				countWords = args[i].equals("-w") ? true : countWords;
				countChars = args[i].equals("-c") ? true : countChars;
			}
			else {
				//Not a parsable command; assume it's a filename
				wcFileNode tmp = new wcFileNode(args[i]);
				if (!tmp.file.exists()) printUsage();
				listHead = addToList(listHead, tmp);
			}	
		}
		return listHead;
	}
	
	private wcFileNode addToList(wcFileNode listHead, wcFileNode toAdd) {
		numItems++;
		wcFileNode lastListItem = listHead;
	
		//Insert tmp into linkedlist
		if (listHead == null) { 			//First item? It's now list head
			listHead = toAdd;
			lastListItem = listHead;
		}
		else if (listHead.next == null){ 	//Second item? listhead.next
			listHead.next = toAdd;
			lastListItem = listHead.next;		
		} else { 							//nth item- just hook it into end of linked list and move lastListItem reference up
			while (lastListItem.next != null) lastListItem = lastListItem.next;
			lastListItem.next = toAdd;
			lastListItem = lastListItem.next;
		}
		return listHead;
	}
	
	private static void printUsage() {
		System.out.println("Usage: 'wc <-l|-c|-w> filename1 filename2 ... filenameN'"
				+ "\n'wc -l <filename>' will print the line count of all specified files"
				+ "\n'wc -c <filename>' will print the character count"
				+ "\n'wc -w <filename>' will print the word count"
				+ "\n'wc <filename>' will print all of the above"
				+ "\n'wc -l -c filename1 filename2' will print line and char counts for filename1 and filename2" 
				);
			System.exit(0);
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
	
}
