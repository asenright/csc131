/*	wc.java
*	A java implementation of the unix 'wc' utility by Andrew Enright.
*
*	Usage: 'java wc <-l|-c|-w> filename
*			Multiple files can be
*
*	countWords() method provided by Michael Yaworski on StackOverflow.
*	https://stackoverflow.com/questions/5864159/count-words-in-a-string-method
*/
import java.io.*;
class wcFileNode {
	boolean lineCount = true, charCount = true, wordCount = true;
	int lines, chars, words;
	File file;
	wcFileNode next = null;
	
	//The constructor for this object takes a filename.
	//If there are wildcards or directories in that filename, the object will have a linked list of items attached.
	public wcFileNode(
			String filename, 
			boolean lineCount, 
			boolean charCount, 
			boolean wordCount) {
		
			this.file = new File(filename);
			this.charCount = charCount;
			this.lineCount = lineCount;
			this.wordCount = wordCount;
			
			if (!file.exists()) throw new IllegalArgumentException("Filename invalid: " + filename);
	}
}

public class wc {
	public static void main(String[] args) {
		if (args.length == 0) printUsage();		//No arguments? Print usage
		wcFileNode listHead = null;				
		listHead = populateList(args, listHead);//Populate list
//		if (listHead == null) System.exit(0);	//Shouldn't need this but I don't want to get rid of it. I guess I'm sentimental.
		printListMetrics(listHead);				//Pass in list to print item metrics.
	}
	
	private static void printListMetrics(wcFileNode listHead) {
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
			

			int lineIndex = 0;
			while (line != null) {
				lastListItem.lines++;
				lastListItem.chars += line.length();
				lastListItem.words += countWords(line);
				
				//Get next line; if null the loop breaks here
				try {
					line = reader.readLine();
				} catch (IOException err) {
					System.out.println("Could not parse line" + lineIndex);
				} 
				
				lineIndex++;
			}
			System.out.println("Metrics for " + lastListItem.file.getName() + ":");
			if (lastListItem.lineCount) System.out.println(lastListItem.lines + " lines.");
			if (lastListItem.charCount) System.out.println(lastListItem.chars + " characters.");
			if (lastListItem.wordCount) System.out.println(lastListItem.words + " words.");
			
			lastListItem = lastListItem.next;
			try {
				reader.close();
			} catch (IOException err) {
				System.out.println("Error closing buffered reader");
				err.printStackTrace();
			}
		}
	}

	private static wcFileNode populateList(String[] args, wcFileNode listHead) {
		//Step 1: Populate the wcFile list with files
		for (int i = 0; i < args.length; i++) {
		//	wcFileNode tmp = new wcFileNode();
		//	tmp.lineCount = tmp.wordCount = tmp.charCount = true;
			//Parse current arg. Is it a command -l, -c, -w; or is it a filename?
			//If it is neither the program should crash.
						
			if (args[i].equals("-l") || args[i].equals("-c") || args[i].equals("-w")) { 
				if (i + 1 > args.length || args[i+1] == null) printUsage();
				//The filename might contain wildcards, which means it's actually many filenames.
				wcFileNode tmp = new wcFileNode(args[i + 1], args[i].equals("-l"), args[i].equals("-c"), args[i].equals("-w") );
			
				listHead = addToList(listHead, tmp);
				i++; //skip an index ahead, since we already have the filename
			}
			else {
				//Not a parsable command; assume it's a filename .
				wcFileNode tmp = new wcFileNode(args[i], true, true, true);
				listHead = addToList(listHead, tmp);
			}
			
			
		}
		return listHead;
	}
	
	private static wcFileNode addToList(wcFileNode listHead, wcFileNode toAdd) {
		wcFileNode lastListItem = listHead;
		System.out.println("	Added item " + toAdd.file.getName() + " with flags: \n	Linecount:" + toAdd.lineCount + "\n	Charcount: " + toAdd.charCount + "\n	Wordcount: " + toAdd.wordCount );
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
		
	//countWords() provided by Michael Yaworski on StackOverflow.
	//https://stackoverflow.com/questions/5864159/count-words-in-a-string-method
	private static int countWords(String line) {
		int wordCount = 0;
	    boolean word = false;
	    int endOfLine = line.length() - 1;
	    for (int i = 0; i < line.length(); i++) {
	        // if the char is a letter, word = true.
	        if (Character.isLetter(line.charAt(i)) && i != endOfLine) {
	            word = true;
	            // if char isn't a letter and there have been letters before,
	            // counter goes up.
	        } else if (!Character.isLetter(line.charAt(i)) && word) {
	            wordCount++;
	            word = false;
	            // last word of String; if it doesn't end with a non letter, it
	            // wouldn't count without this.
	        } else if (Character.isLetter(line.charAt(i)) && i == endOfLine) {
	            wordCount++;
	        }
	    }
	    return wordCount;
	}
	
	private static void printUsage() {
		System.out.println("Usage: 'wc <-l|-c|-w> filename1 <-l|-c|-w> filename2 ... <-l|-c|-w> filenameN'"
				+ "\n'wc -l <filename>' will print the line count of a file"
				+ "\n'wc -c <filename>' will print the character count"
				+ "\n'wc -w <filename>' will print the word count"
				+ "wc <filename> will print all of the above"
				+ "\nMultiple files each get their own arguments; for example, 'wc -l Hamlet.txt Romeo.txt' will give you all a line count for Hamlet.txt and all metrics for Romeo.txt.");
			System.exit(0);
	}
	
	//Returns the linked sublist of args. 
	//This might be a linked list because there may be multiple files specified due to wildcards.
	//private static wcFileNode populateSubList(String filename, boolean countWords, boolean countLines, boolean countChars) {
	//	wcFileNode tmp = new wcFileNode(new File(filename), countLines, countChars, countWords);
	//	return tmp;
	//}
	
}
