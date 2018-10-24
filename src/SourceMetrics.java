import java.io.File;

/*	Metrics.java

*	A java implementation of the unix 'wc' utility by Andrew Enright.
*
*	Usage: 'java wc <-l|-c|-w|-s|-C> filename
*
*  Credit to technicalkeeda.com for getFileExtension()	
*  Credit to https://www.geeksforgeeks.org/print-unique-words-string/ for unique words in string
*/

public class SourceMetrics extends Metrics {
		protected String filePath;

		public boolean setPath(String path) {
	    	filePath = path;
	    	try {
	    		this.metricsNode = new MetricsCodeNode(new File(path));
	    		((MetricsCodeNode) metricsNode).getFileMetrics();
	    	} catch (Exception e) {
	    		System.out.println("Could not create metricsCodeNode!");
	    		e.printStackTrace();
	    	}
	    	return ((MetricsCodeNode) metricsNode).file.exists();
	    }
    
	    public boolean isSource() {
			return true;
		};
		
		// source code line counts
		//
		 public int getSourceLineCount() {
			 return ((MetricsCodeNode) metricsNode).linesOfCode;
		 };
		 public int getCommentLineCount() {
			 return ((MetricsCodeNode) metricsNode).linesOfComment;
		 };
		
		// Halstead metrics
		//
		 public int getHalsteadn1() {
			 return ((MetricsCodeNode) metricsNode).nodeTotalOperands;
		 };            // number of distinct operands
		 public int getHalsteadn2() {
			 return ((MetricsCodeNode) metricsNode).nodeTotalOperators;
		 };            // number of distinct operators
		 public int getHalsteadN1() {
			 return ((MetricsCodeNode) metricsNode).uniqueOperands == null ? 0 : ((MetricsCodeNode) metricsNode).uniqueOperands.size();
		 };            // number of operands
		 public int getHalsteadN2(){
			 return ((MetricsCodeNode) metricsNode).uniqueOperators == null ? 0 : ((MetricsCodeNode) metricsNode).uniqueOperators.size();
		 };            // number of operators
		
		 public int getHalsteadVocabulary() {
			 return ((MetricsCodeNode) metricsNode).vocabulary;
		 };
		 public int getHalsteadProgramLength() {
			 return ((MetricsCodeNode) metricsNode).length;
		 };
		 public int getHalsteadCalculatedProgramLenght() {
			 return ((MetricsCodeNode) metricsNode).calcLength;
		 };
		 public int getHalsteadVolume() {
			 return ((MetricsCodeNode) metricsNode).volume;
		 };
		 public int getHalsteadDifficulty() {
			 return ((MetricsCodeNode) metricsNode).difficulty;
		 };
		 public int getHalsteadEffort() {
			 return ((MetricsCodeNode) metricsNode).effort;
		 };
		 public int getHalsteadTime() {
			 return ((MetricsCodeNode) metricsNode).time;
		 };
		 public int getHalsteadBugs() {
			 return ((MetricsCodeNode) metricsNode).bugs;
		 };
 }
