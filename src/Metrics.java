import java.io.File;

/*	Metrics.java

*	A java implementation of the unix 'wc' utility by Andrew Enright.
*
*	Usage: 'java wc <-l|-c|-w|-s|-C> filename
*
*  Credit to technicalkeeda.com for getFileExtension()	
*  Credit to https://www.geeksforgeeks.org/print-unique-words-string/ for unique words in string
*/

public class Metrics implements IMetrics {
		protected MetricsFileNode item = null;	
	
	    public boolean setPath(String path) {
	    	try {
	    		this.item = new MetricsFileNode(new File(path));
	    	} catch (Exception e) {
	    		System.out.println("Could not create metricsFileNode");
	    		e.printStackTrace();
	    	}
	    	return this.item.file.exists();
	    }
    
		    // returns true if current path is valid
	    public boolean isSource() {
			return (item.ext.equals(".c")||
					item.ext.equals(".h")||
					item.ext.equals(".cpp")||
					item.ext.equals(".hpp")||
					item.ext.equals(".java")||
					item.ext.equals(".javah"));
		};             // returns true if the file is a source file
		
		// basic counts for any file
		//
		 public int getLineCount() {
			return item.lines;
		};
		 public int getWordCount() {
			 return item.words;
		 }
		 public int getCharacterCount() {
			 return item.chars;
		 };
		
		// source code line counts
		//
		 public int getSourceLineCount() {
			 return item.linesOfCode;
		 };
		 public int getCommentLineCount() {
			 return item.linesOfComment;
		 };
		
		// Halstead metrics
		//
		 public int getHalsteadn1() {
			 return item.nodeTotalOperands;
		 };            // number of distinct operands
		 public int getHalsteadn2() {
			 return item.nodeTotalOperators;
		 };            // number of distinct operators
		 public int getHalsteadN1() {
			 return item.uniqueOperands == null ? 0 : item.uniqueOperands.size();
		 };            // number of operands
		 public int getHalsteadN2(){
			 return item.uniqueOperators == null ? 0 : item.uniqueOperators.size();
		 };            // number of operators
		
		 public int getHalsteadVocabulary() {
			 return item.vocabulary;
		 };
		 public int getHalsteadProgramLength() {
			 return item.length;
		 };
		 public int getHalsteadCalculatedProgramLenght() {
			 return item.calcLength;
		 };
		 public int getHalsteadVolume() {
			 return item.volume;
		 };
		 public int getHalsteadDifficulty() {
			 return item.difficulty;
		 };
		 public int getHalsteadEffort() {
			 return item.effort;
		 };
		 public int getHalsteadTime() {
			 return item.time;
		 };
		 public int getHalsteadBugs() {
			 return item.bugs;
		 };
}
