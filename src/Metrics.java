/*	Metrics.java

*	A java implementation of the unix 'wc' utility by Andrew Enright.
*
*	Usage: 'java wc <-l|-c|-w|-s|-C> filename
*
*  Credit to technicalkeeda.com for getFileExtension()	
*  Credit to https://www.geeksforgeeks.org/print-unique-words-string/ for unique words in string
*/
import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import picocli.CommandLine;
import picocli.CommandLine.*;

/*Wrapper class for Metrics*/
@Command(description="Prints metrics of the given file to STDOUT.",	name="Metrics")
public class Metrics implements Runnable{
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
	LinkedList<File> files;
		
	private int totalLines, totalChars, totalWords, totalCode, totalComments, totalUniqueOperands, 
		totalUniqueOperators, totalOperands, totalOperators,
		totalEffort, totalVocab, totalLength, totalCalcLength, totalVolume, totalDifficulty, 
		totalBugs, totalTime;

	private LinkedList<metricsFileNode> listHead = null;	
	
	public static void main(String[] args) {		
		Metrics wordCounterInstance = new Metrics();
		if (args.length == 0 || showHelp ) {
			CommandLine.usage(wordCounterInstance, System.err);
			System.exit(0);
		}
		CommandLine.run(wordCounterInstance, args);
	}
	
	public void run() {		
		//No argument specified? Print all metrics
		if (!countLines && !countWords && !countChars && !countCode && !countComments) 			
			countLines = countWords = countChars = countCode = countComments = calcHalstead = true ;	
		
		listHead = new LinkedList<metricsFileNode>();
		
		try { populateMetricsFileNodeList(files, listHead); }
		catch (Exception e) { 
			System.out.println("Error encountered populating list : " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		
		gatherFileMetrics(listHead);	
		printHeader();
		for (metricsFileNode lastListItem : listHead) formattedPrint(lastListItem);
		if (listHead.size() > 1) 
			formattedPrint(this);
	}
	
	/** Gathers total metrics from specified list.
	 * @param fileList List of items to iterate over.
	 */
	private void gatherFileMetrics(LinkedList<metricsFileNode> fileList) {
		for (metricsFileNode lastListItem : fileList) {
			totalWords += lastListItem.words;
			totalChars += lastListItem.chars;
			totalLines += lastListItem.lines;
			totalCode += lastListItem.linesOfCode;
			totalComments += lastListItem.linesOfComment;
			totalOperators += lastListItem.nodeTotalOperators;
			totalOperands += lastListItem.nodeTotalOperands;
			totalUniqueOperators += lastListItem.uniqueOperators.size();
			totalUniqueOperands += lastListItem.uniqueOperands.size();
			totalEffort  += lastListItem.effort;
			totalVocab += lastListItem.vocabulary;
			totalLength += lastListItem.length;
			totalCalcLength  += lastListItem.calcLength;
			totalVolume += lastListItem.volume;
			totalDifficulty  += lastListItem.difficulty;
			totalBugs += lastListItem.bugs;
			totalTime += lastListItem.time;
		}
	}
	
	
	/**Credit to technicalkeeda.com for getFileExtension()
	 * @param file File to get extension from.
	 * @return Returns the extension of the file.
	 */
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

	/** Populates a linkedList of MetricsFileNodes with the given List of Files.
	 * @param files List of files.
	 * @param listHead List of MetricsFileNodes.
	 * @throws FileNotFoundException Throws if file not found.
	 */
	private void populateMetricsFileNodeList(List<File> files, LinkedList<metricsFileNode> listHead) throws FileNotFoundException {
		for (File current : files) {
				if (!current.exists()) throw new FileNotFoundException("Could not find file " + current.getName());
				metricsFileNode tmp = new metricsFileNode(current);
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
	private void formattedPrint(metricsFileNode toPrint) {
		formattedPrint(toPrint.lines, toPrint.words, toPrint.chars, toPrint.linesOfCode, toPrint.linesOfComment, 
					toPrint.nodeTotalOperators, toPrint.nodeTotalOperands, toPrint.uniqueOperators.size(), toPrint.uniqueOperands.size(),
					toPrint.vocabulary, toPrint.length, toPrint.calcLength, toPrint.volume, toPrint.difficulty, toPrint.effort,
					toPrint.time, toPrint.bugs,
					toPrint.file.getName());
	}
	
	
	/** Abstraction method for neatness. Used for printing totals.
	 * @param toPrint Metrics object whose totals are to be printed.
	 */
	private void formattedPrint(Metrics toPrint) {
		formattedPrint(toPrint.totalLines, toPrint.totalWords, toPrint.totalChars, toPrint.totalCode, toPrint.totalComments, 
				toPrint.totalOperators, toPrint.totalOperands, toPrint.totalUniqueOperators, toPrint.totalUniqueOperands,
				toPrint.totalVocab, toPrint.totalLength, toPrint.totalCalcLength, toPrint.totalVolume, toPrint.totalDifficulty, 
				toPrint.totalEffort, toPrint.totalTime, toPrint.totalBugs,
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
	class metricsFileNode {
	
		int lines, chars, words, linesOfCode, linesOfComment, nodeTotalOperators, nodeTotalOperands,
			vocabulary, length, calcLength, volume, difficulty, effort, time, bugs;
		Set<String> uniqueOperators = new HashSet<String>(), uniqueOperands = new HashSet<String>();
		File file;
		metricsFileNode next = null;
		String ext = "";
		private boolean currentlyBlockComment = false;
		
		public metricsFileNode(File toCount) {
			this.file = toCount;
			if (!file.exists()) throw new IllegalArgumentException("No file found matching '" + file.getName() + "'");
			
			ext = getFileExtension(file);
			String line = null;
			
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				while (reader.read() > -1)
					chars++;
				reader.close();
				
				reader = new BufferedReader(new FileReader(file)); //Reset reader to start reading lines
				line = reader.readLine();
				while (line != null) { 		
					getLineMetrics(line);
					line = reader.readLine();
				}
				reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("Argument could not be parsed as an argument or filename.");
				System.exit(1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("IO Exception while reading.");
				System.exit(1);
			}
		}
		private void getLineMetrics(String line) {
			lines++;		
			words += line.split("\\s+").length; 
			if (	(countCode || countComments || calcHalstead) &&
					!line.isEmpty() && 
					(ext.equals(".c") || 
					ext.equals(".java") || 
					ext.equals(".cpp") || 
					ext.equals(".h") || 
					ext.equals(".hpp"))) {		
					getCodeLineMetrics(line);
			}
		}
		private void getCodeLineMetrics(String line) {
				String codeLine = "";
				//Count code and Comments
				if (currentlyBlockComment && !line.contains("*/")) linesOfComment++; 
					else if (line.contains("/*")) { //Does this line have a self-contained comment /*like this*/
						linesOfComment++; 		
						String before = line.substring(0, line.indexOf('*') - 1).trim(),
								after = "";
						if (line.contains("*/")) {
							after = line.substring(line.lastIndexOf("*/") + 2).trim();
						} else currentlyBlockComment = true;
						if (before.length() > 1) { 
							linesOfCode++; //Are there contents before the comment?
							codeLine = before;
						}
						else if (after.length() > 2) {
							codeLine = after;
							linesOfCode++; //Are there contents after the comment?
						}
				} else if (line.contains("*/")) {
						linesOfComment++;
						currentlyBlockComment = false;
						String after = line.substring(line.lastIndexOf("*/") + 2).trim();
						if (after.length() > 2) {
							codeLine = after;
							linesOfCode++; //Are there contents after the comment?
						}
				}
				else if (line.contains("//")) {
					linesOfComment++;
					String before = line.substring(0, line.indexOf("//")).trim();
							
					if (before.length() > 1) {
						codeLine = before;
						linesOfCode++;
					}
				} else  {
					linesOfCode++;	
					codeLine = line;
				}
		
		
		//Halstead Metrics
		if (calcHalstead && !currentlyBlockComment && codeLine.length() > 1) {
			Set<String> keywords = new HashSet<String>();
			String operatorsRegex;
			String operandsRegex;
			switch (ext) {
				case ".java" :
				case ".javah" : 
					keywords = new HashSet<String>(Arrays.asList("one", "of",
							"abstract", "continue", "for", "new", "switch",
							"assert", "default", "if", "package", "synchronized",
							"boolean", "do", "goto", "private", "this",
							"break", "double", "implements", "protected", "throw",
							"byte", "else", "import", "public", "throws",
							"case", "enum", "instanceof", "return", "transient",
							"catch", "extends", "int", "short", "try",
							"char", "final", "interface", "static", "void",
							"class", "finally", "long", "strictfp", "volatile",
							"const", "float", "native", "super", "while"));
					break;
				case ".c":
				case ".h":
					keywords = new HashSet<String>(Arrays.asList("auto","break","case","char","const","continue","default","do","double","else"	,"enum"	,"extern","float"	,"for"
							,"goto"	,"if","int"	,"long"	,"register"	,"return","short",
							"signed","sizeof","static","struct"	,"switch"	,"typedef"	,"union",
							"unsigned","void"	,"volatile"	,"while"));
					break;
				
				case ".cpp":
				case ".hpp":
					keywords = new HashSet<String>(Arrays.asList("one", "of",
							"abstract", "continue", "for", "new", "switch",
							"assert", "default", "if", "package", "synchronized",
							"boolean", "do", "goto", "private", "this",
							"break", "double", "implements", "protected", "throw",
							"byte", "else", "import", "public", "throws",
							"case", "enum", "instanceof", "return", "transient",
							"catch", "extends", "int", "short", "try",
							"char", "final", "interface", "static", "void",
							"class", "finally", "long", "strictfp", "volatile",
							"const", "float", "native", "super", "while"));
					break;
			}
			
		    operatorsRegex = "/?/?";
		    		//"(=)|(>)|(<)|(!)|(~)|(/?)|(:)|(==)|(<=)|(>=)|(!=)|(&&)|(/|/|)|(/+/+)|(--)|(/+)|(-)|(/*)|(/)|(&)|(/|)|(^)|(%)|(<<)|(>>)|(>>>)|(/+=)|(-=)|(/*=)|(/=)|(&=)|(/|=)|(^=)|(%=)|(<<=)|(>>=)|(>>>=)"; 
		    //Keywords and ops regex shamelessly borrowed from https://www.daniweb.com/programming/software-development/threads/307653/halstead-metrics
		    operandsRegex = "(\\w+)|(\\d+)";

		    Matcher operatorsMatcher = Pattern.compile(operatorsRegex).matcher(codeLine);
		    Matcher operandsMatcher = Pattern.compile(operandsRegex).matcher(codeLine);
		
			  while (operatorsMatcher.find()) {
				  	String group = operatorsMatcher.group();
				  	if (group.length() > 0) {
				       uniqueOperators.add(group);
				       nodeTotalOperators++;
				  	}
			   }
			  while (operandsMatcher.find()) {
				   String group = operandsMatcher.group();
			       if (group.length() > 0  && !keywords.contains(group)) {
				       uniqueOperands.add(group);
				       nodeTotalOperands++;
				  	}
			   }
		
				vocabulary = uniqueOperators.size() + uniqueOperands.size();
				length = nodeTotalOperators + nodeTotalOperands;
				calcLength = uniqueOperators.size() * log2(uniqueOperators.size()) + uniqueOperands.size()*log2(uniqueOperands.size());
				volume = nodeTotalOperators * log2(vocabulary);
				difficulty = uniqueOperands.size() > 0 ? (uniqueOperators.size() / 2) * (nodeTotalOperands / uniqueOperands.size()) : 0;
				effort = volume > 0 ? difficulty/volume : 0;
				time = effort / 18;
				bugs = volume > 0 ? (volume / 3000) :  0 ;
				//System.out.println(	"Code: " + codeLine + 
				//		"\n	Operators: " + uniqueOperators.toString() + 
				//		"\n	Operands: " + uniqueOperands.toString());
		}
		
	
		}
		
		private int log2(double n) {
			return (int) Math.round(Math.log(n)/Math.log(2));
		}
	}
}
