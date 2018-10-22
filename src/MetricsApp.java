/*	Metrics.java

*	A java implementation of the unix 'wc' utility by Andrew Enright.
*
*	Usage: 'java wc <-l|-c|-w|-s|-C> filename
*
*  Credit to technicalkeeda.com for getFileExtension()	
*  Credit to https://www.geeksforgeeks.org/print-unique-words-string/ for unique words in string
*/
import java.io.*;

import java.util.LinkedList;
import java.util.List;

import picocli.CommandLine;
import picocli.CommandLine.*;

/*UI Wrapper class for Metrics*/
@Command(description="Prints metrics of the given file to STDOUT.",	name="Metrics")
public class MetricsApp implements Runnable{
	@Option(description = "Show usage", names = { "-h" }, paramLabel="help")
	static boolean showHelp;
	
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
	LinkedList<String> filePaths;	
	
	private static final int COLUMN_SEP_WIDTH = 1; //Amount of whitespace to put between columns.
	private static final String COLUMN_LINES = "lines", 
								COLUMN_WORDS = "words",
								COLUMN_CHARS = "chars",
								COLUMN_SOURCE = "source",
								COLUMN_COMMENTS = "comments",
								COLUMN_OPERATORS = "operators",
								COLUMN_OPERANDS = "operands",
								COLUMN_UNIQUE_OPERATORS = "unqOperators",
								COLUMN_UNIQUE_OPERANDS = "unqOperands",
								COLUMN_VOCAB = "vocab",
								COLUMN_LENGTH = "length",
								COLUMN_CALC_LENGTH = "calcLength",
								COLUMN_VOLUME = "volume",
								COLUMN_DIFFICULTY = "difficulty",
								COLUMN_EFFORT = "effort",
								COLUMN_BUGS = "estBugs",
								COLUMN_TIME = "estTime";
	
	private int totalLines, totalChars, totalWords, totalCode, totalComments, totalUniqueOperands, 
				totalUniqueOperators, totalOperands, totalOperators,
				totalEffort, totalVocab, totalLength, totalCalcLength, totalVolume, totalDifficulty, 
				totalBugs, totalTime;
	private LinkedList<IMetrics> listHead = null;	


	public static void main(String[] args) {		
		MetricsApp metricsAppInstance = new MetricsApp();
		if (args.length == 0 || showHelp ) {
			CommandLine.usage(metricsAppInstance, System.err);
			System.exit(0);
		}
		CommandLine.run(metricsAppInstance, args);
	}	
	
	public void run() {		
		//No argument specified? Print all metrics
		if (!countLines && !countWords && !countChars && !countCode && !countComments) 			
			countLines = countWords = countChars = countCode = countComments = calcHalstead = true ;	
		
		listHead = new LinkedList<IMetrics>();
		
		try { populateMetricsList(filePaths, listHead); }
		catch (Exception e) { 
			System.out.println("Error encountered populating list : " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		
		gatherFileMetrics(listHead);	
		printHeader();
		for (IMetrics lastListItem : listHead) formattedPrint(lastListItem);
		if (listHead.size() > 1) 
			formattedPrint(this);
		}
	
	/** Gathers total metrics from specified list.
	 * @param fileList List of items to iterate over.
	 */
	private void gatherFileMetrics(LinkedList<IMetrics> fileList) {
		for (IMetrics lastListItem : fileList) {
			totalWords += lastListItem.getWordCount();
			totalChars += lastListItem.getCharacterCount();
			totalLines += lastListItem.getLineCount();
			totalCode += lastListItem.getSourceLineCount();
			totalComments += lastListItem.getCommentLineCount();
			totalOperators += lastListItem.getHalsteadn2();
			totalOperands += lastListItem.getHalsteadn1();
			totalUniqueOperators += lastListItem.getHalsteadN1();
			totalUniqueOperands += lastListItem.getHalsteadN2();
			totalEffort  += lastListItem.getHalsteadEffort();
			totalVocab += lastListItem.getHalsteadVocabulary();
			totalLength += lastListItem.getHalsteadProgramLength();
			totalCalcLength  += lastListItem.getHalsteadCalculatedProgramLenght();
			totalVolume += lastListItem.getHalsteadVolume();
			totalDifficulty  += lastListItem.getHalsteadDifficulty();
			totalBugs += lastListItem.getHalsteadBugs();
			totalTime += lastListItem.getHalsteadTime();
		}
	}

	/** Populates a linkedList of MetricsFileNodes with the given List of filePaths.
	 * @param filePaths List of filePaths.
	 * @param listHead List of MetricsFileNodes.
	 * @throws FileNotFoundException Throws if file not found.
	 */
	private void populateMetricsList(List<String> filePaths, LinkedList<IMetrics> listHead) throws FileNotFoundException {
		for (String current : filePaths) {
				IMetrics tmp;
				if (MetricsLib.isSource(current)) tmp = new SourceMetrics();
				else tmp = new Metrics();
				tmp.setPath(current);
				listHead.add(tmp);
			}	
	}

	/**
	 * Prints output header.
	 */
	private void printHeader() {
		if (countLines) printHeader(COLUMN_LINES, totalLines);
		if (countWords) printHeader(COLUMN_WORDS, totalWords);
		if (countChars) printHeader(COLUMN_CHARS, totalChars);
		if (countCode)  printHeader(COLUMN_SOURCE, totalCode);
		if (countComments) printHeader(COLUMN_COMMENTS, totalComments);
		if (calcHalstead) {
			printHeader(COLUMN_OPERATORS, totalOperators);	
			printHeader(COLUMN_OPERANDS, totalOperands);	
			printHeader(COLUMN_UNIQUE_OPERATORS, totalUniqueOperators);	
			printHeader(COLUMN_UNIQUE_OPERANDS, totalUniqueOperands);	
		
			printHeader(COLUMN_VOCAB, totalVocab);	
			printHeader(COLUMN_LENGTH, totalLength);	
			printHeader(COLUMN_CALC_LENGTH, totalCalcLength);	
			printHeader(COLUMN_VOLUME, totalVolume);	
			printHeader(COLUMN_DIFFICULTY, totalDifficulty);	
			printHeader(COLUMN_EFFORT, totalEffort);	
			printHeader(COLUMN_TIME, totalTime);	
			printHeader(COLUMN_BUGS, totalBugs);	
		}
		System.out.printf(" %-20s%n", "filename");
	}

	
	/**
	 * Master print method, used for printing individual node metrics as well as the total.
	 */
	private void formattedPrint(Integer lines, Integer words, Integer chars, Integer linesCode, Integer linesComment, 
						Integer operators, Integer operands, Integer uniqueOperators, Integer uniqueOperands,
						Integer vocab, Integer length, Integer totalCalcLength, Integer volume, Integer difficulty, Integer effort,
						Integer bugs, Integer time,
						String filename) {
		if (countLines) printMetric(COLUMN_LINES, lines);
		if (countWords) printMetric(COLUMN_WORDS, words);
		if (countChars) printMetric(COLUMN_CHARS, chars);
		
		if (countCode) printCodeMetric(COLUMN_SOURCE, linesCode);
		if (countComments) printCodeMetric(COLUMN_COMMENTS, linesComment);
		if (calcHalstead) {
			printCodeMetric(COLUMN_OPERATORS, operators);
			printCodeMetric(COLUMN_OPERANDS, operands);
			printCodeMetric(COLUMN_UNIQUE_OPERATORS, uniqueOperators);
			printCodeMetric(COLUMN_UNIQUE_OPERANDS, uniqueOperands);

			printCodeMetric(COLUMN_VOCAB, vocab);
			printCodeMetric(COLUMN_LENGTH, length);
			printCodeMetric(COLUMN_CALC_LENGTH, totalCalcLength);
			printCodeMetric(COLUMN_VOLUME, volume);
			printCodeMetric(COLUMN_DIFFICULTY, difficulty);
			printCodeMetric(COLUMN_EFFORT, effort);
			
			printCodeMetric(COLUMN_TIME, time);
			printCodeMetric(COLUMN_BUGS, bugs);
			
		}
		System.out.printf(" %s%n", filename);
	}

	/** Abstraction method for neatness.
	 * @param toPrint Node whose metrics we want to print.
	 */
	private void formattedPrint(IMetrics toPrint) {
		String path = ""; //TODO: I'd really like to add a getPath() call to IMetrics to get rid of this messy try/catch!
		try {
			path = ((Metrics) toPrint).metricsNode.file.getPath();
		} catch (Exception e){
			path = ((SourceMetrics) toPrint).metricsNode.file.getPath();
		}
		formattedPrint(toPrint.getLineCount(), 
					toPrint. getWordCount(), 
					toPrint.getCharacterCount(), 
					toPrint.getSourceLineCount(), 
					toPrint.getCommentLineCount(), 
					toPrint.getHalsteadn2(), 
					toPrint.getHalsteadn1(), 
					toPrint.getHalsteadN2(),
					toPrint.getHalsteadN1(),
					toPrint.getHalsteadVocabulary(),
					toPrint.getHalsteadProgramLength(), 
					toPrint.getHalsteadCalculatedProgramLenght(), 
					toPrint.getHalsteadVolume(), 
					toPrint.getHalsteadDifficulty(), 
					toPrint.getHalsteadEffort(),
					toPrint.getHalsteadBugs(), 
					toPrint.getHalsteadTime(), 
					path);
	}
	
	
	/** Abstraction method for neatness. Used for printing totals.
	 * @param toPrint Metrics object whose totals are to be printed.
	 */
	private void formattedPrint(MetricsApp toPrint) {
		formattedPrint(toPrint.totalLines, toPrint.totalWords, toPrint.totalChars, toPrint.totalCode, toPrint.totalComments, 
				toPrint.totalOperators, toPrint.totalOperands, toPrint.totalUniqueOperators, toPrint.totalUniqueOperands,
				toPrint.totalVocab, toPrint.totalLength, toPrint.totalCalcLength, toPrint.totalVolume, toPrint.totalDifficulty, 
				toPrint.totalEffort, toPrint.totalBugs, toPrint.totalTime,
				"total" );
}
	
	/** Printing helper method for managing column width.
	 * @param minimumWidth Narrowest column width returnable, usually the label width
	 * @param totalToMeasure Integer in column
	 * @return
	 */
	private int getColumnWidth(int minimumWidth, int totalToMeasure) {		
		return Math.max(new Integer(totalToMeasure).toString().length(), minimumWidth) + COLUMN_SEP_WIDTH;
	}
	
	private void printHeader(String columnName, int metricTotal) {
		System.out.printf("%"+ getColumnWidth(columnName.length() , metricTotal) +"s", columnName);
	}
	private void printMetric(String columnName, int metric) {
		System.out.printf("%"+ getColumnWidth(columnName.length(), metric) + "s", new Integer(metric).toString());
	}

	private void printCodeMetric(String columnName, int metric) {
		System.out.printf("%"+ getColumnWidth(columnName.length(), metric) + "s", metric > 0 ? new Integer(metric).toString() : "");
	}
	

		
	/** Metrics File Node class. Give it a file and it'll automatically accumulate appropriate metrics.
	 */
}
