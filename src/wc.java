/*	wc.java
*	A java implementation of the unix 'wc' utility by Andrew Enright.
*
*	Usage: 'java wc <-l|-c|-w> filename
*/
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


class wcFileNode {
	int lines, chars, words;
	File file;
	wcFileNode next = null;
	
	//The constructor for this object takes a filename.
	//If there are wildcards or directories in that filename, the object will have a linked list of items attached.
	public wcFileNode(String filename) {
		this.file = new File(filename);
		if (!file.exists()) throw new IllegalArgumentException("No file found matching '" + filename + "'");
	}
}

public class wc {
	private int totalLines, totalChars, totalWords, numItems;
	private boolean countLines = false, countChars = false, countWords = false;
	private wcFileNode listHead = null;	
	
	public static void main(String[] args) {
		if (args.length == 0) printUsage();						//No arguments? Print usage
		wc wordCounterInstance = new wc();
		wordCounterInstance.run(args);
	}
	
	private void run(String[] args) {
		listHead = populateList(args, listHead);				//Populate list
		if (!countLines && !countWords && !countChars) 			
			countLines = countWords = countChars = true;		//No argument specified? Print all metrics
		printListMetrics(listHead);								//Pass in list to print item metrics.
		if (numItems > 1) formattedPrint(						//If more than 1 item, print total
							countLines ? totalLines : null, 
							countWords ? totalWords : null, 
							countChars ? totalChars : null, 
							"total");
	}
	
	private void printListMetrics(wcFileNode listHead) {
		wcFileNode lastListItem = listHead;
		
		while (lastListItem != null) {
			//System.out.println("Starting item " + lastListItem.file.getName());
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
				lastListItem.words += line.split("\\s").length; //Split on whitespace
				
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
			
			formattedPrint(	countLines ? lastListItem.lines : null, 
							countWords ? lastListItem.words : null, 
							countChars ? lastListItem.chars : null, 
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
		//	wcFileNode tmp = new wcFileNode();
		//	tmp.lineCount = tmp.wordCount = tmp.charCount = true;
			//Parse current arg. Is it a command -l, -c, -w; or is it a filename?
			//If it is neither the program should crash.
						
			if (args[i].equals("-l") || args[i].equals("-c") || args[i].equals("-w")) { 
				if (i + 1 > args.length || args[i+1] == null) printUsage(); //The actual WC utility crashes if you do this!
				countLines = args[i].equals("-l") ? true : countLines;
				countWords = args[i].equals("-w") ? true : countWords;
				countChars = args[i].equals("-c") ? true : countChars;
			}
			else {
				//Not a parsable command; assume it's a filename .
				wcFileNode tmp = new wcFileNode(args[i]);
				listHead = addToList(listHead, tmp);
			}
			
			
		}
		return listHead;
	}
	
	private wcFileNode addToList(wcFileNode listHead, wcFileNode toAdd) {
		numItems++;
		wcFileNode lastListItem = listHead;
		//System.out.println("	Added item " + toAdd.file.getName() + " with flags: \n	Linecount:" + toAdd.lineCount + "\n	Charcount: " + toAdd.charCount + "\n	Wordcount: " + toAdd.wordCount );
		//Insert tmp into linkedlist
		if (listHead == null) { //First item- list head
			listHead = toAdd;
			lastListItem = listHead;
		}
		else if (listHead.next == null){ //Second item - listhead.next
			listHead.next = toAdd;
			lastListItem = listHead.next;		
		} else { //nth item- just hook it into end of linked list and move lastListItem pointer up
			while (lastListItem.next != null) lastListItem = lastListItem.next;
			lastListItem.next = toAdd;
			lastListItem = lastListItem.next;
		}
		return listHead;
	}
	
	private static void printUsage() {
		System.out.println("Usage: 'wc <-l|-c|-w> filename1 <-l|-c|-w> filename2 ... <-l|-c|-w> filenameN'"
				+ "\n'wc -l <filename>' will print the line count of a file"
				+ "\n'wc -c <filename>' will print the character count"
				+ "\n'wc -w <filename>' will print the word count"
				+ "wc <filename> will print all of the above"
				);
			System.exit(0);
	}
	
	//Returns the linked sublist of args. 
	//This might be a linked list because there may be multiple files specified due to wildcards.
	//private static wcFileNode populateSubList(String filename, boolean countWords, boolean countLines, boolean countChars) {
	//	wcFileNode tmp = new wcFileNode(new File(filename), countLines, countChars, countWords);
	//	return tmp;
	//}
	
	private static void formattedPrint(Integer lines, Integer words, Integer chars, String filename) {
		//System.out.printf("%8d %8d %8d %s%n", lines, words, chars, filename);
		if (lines != null) System.out.printf("%8d", lines);
		if (words != null) System.out.printf("%8d", words);
		if (chars != null) System.out.printf("%8d", chars);
		System.out.printf(" %s%n", filename);
		
	}
	
}
