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
		protected String filePath;
		protected MetricsFileNode metricsNode = null;	
	
	    public boolean setPath(String path) {
	    	filePath = path;
	    	try {
	    		metricsNode = new MetricsFileNode(new File(path));
	    		metricsNode.getFileMetrics();
	    	} catch (Exception e) {
	    		System.out.println("Could not create metricsFileNode!");
	    		e.printStackTrace();
	    	}
	    	return metricsNode.file.exists();
	    }
    
		    // returns true if current path is valid
	    public boolean isSource() {
			return false;
		};
		
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
		 public int getSourceLineCount() {
			 return 0;
		 };
		 public int getCommentLineCount() {
			 return 0;
		 };
		
		// Halstead metrics
		 public int getHalsteadn1() {
			 return 0;
		 };            // number of distinct operands
		 public int getHalsteadn2() {
			 return 0;
		 };            // number of distinct operators
		 public int getHalsteadN1() {
			 return 0;
		 };            // number of operands
		 public int getHalsteadN2(){
			 return 0;
		 };            // number of operators
		
		 public int getHalsteadVocabulary() {
			 return 0;
		 };
		 public int getHalsteadProgramLength() {
			 return 0;
		 };
		 public int getHalsteadCalculatedProgramLenght() {
			 return 0;
		 };
		 public int getHalsteadVolume() {
			 return 0;
		 };
		 public int getHalsteadDifficulty() {
			 return 0;
		 };
		 public int getHalsteadEffort() {
			 return 0;
		 };
		 public int getHalsteadTime() {
			 return 0;
		 };
		 public int getHalsteadBugs() {
			 return 0;
		 };
}
