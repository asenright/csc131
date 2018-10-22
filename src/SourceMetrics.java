import java.io.File;

/*	Metrics.java

*	A java implementation of the unix 'wc' utility by Andrew Enright.
*
*	Usage: 'java wc <-l|-c|-w|-s|-C> filename
*
*  Credit to technicalkeeda.com for getFileExtension()	
*  Credit to https://www.geeksforgeeks.org/print-unique-words-string/ for unique words in string
*/

public class SourceMetrics implements IMetrics {
		protected String filePath;
		protected MetricsCodeNode metricsNode = null;	
	
	    public boolean setPath(String path) {
	    	filePath = path;
	    	try {
	    		this.metricsNode = new MetricsCodeNode(new File(path));
	    		metricsNode.getFileMetrics();
	    	} catch (Exception e) {
	    		System.out.println("Could not create metricsCodeNode!");
	    		e.printStackTrace();
	    	}
	    	return metricsNode.file.exists();
	    }
    
	    public boolean isSource() {
			return true;
		};             // returns true if the file is a source file
		// basic counts for any file
		//
		 public int getLineCount() {
			return metricsNode.lines;
		};
		 public int getWordCount() {
			 return metricsNode.words;
		 }
		 public int getCharacterCount() {
			 return metricsNode.chars;
		 };
		
		// source code line counts
		//
		 public int getSourceLineCount() {
			 return metricsNode.linesOfCode;
		 };
		 public int getCommentLineCount() {
			 return metricsNode.linesOfComment;
		 };
		
		// Halstead metrics
		//
		 public int getHalsteadn1() {
			 return metricsNode.nodeTotalOperands;
		 };            // number of distinct operands
		 public int getHalsteadn2() {
			 return metricsNode.nodeTotalOperators;
		 };            // number of distinct operators
		 public int getHalsteadN1() {
			 return metricsNode.uniqueOperands == null ? 0 : metricsNode.uniqueOperands.size();
		 };            // number of operands
		 public int getHalsteadN2(){
			 return metricsNode.uniqueOperators == null ? 0 : metricsNode.uniqueOperators.size();
		 };            // number of operators
		
		 public int getHalsteadVocabulary() {
			 return metricsNode.vocabulary;
		 };
		 public int getHalsteadProgramLength() {
			 return metricsNode.length;
		 };
		 public int getHalsteadCalculatedProgramLenght() {
			 return metricsNode.calcLength;
		 };
		 public int getHalsteadVolume() {
			 return metricsNode.volume;
		 };
		 public int getHalsteadDifficulty() {
			 return metricsNode.difficulty;
		 };
		 public int getHalsteadEffort() {
			 return metricsNode.effort;
		 };
		 public int getHalsteadTime() {
			 return metricsNode.time;
		 };
		 public int getHalsteadBugs() {
			 return metricsNode.bugs;
		 };
 }
