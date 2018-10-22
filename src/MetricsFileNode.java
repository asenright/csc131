import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetricsFileNode {
	
	int lines, chars, words, linesOfCode, linesOfComment, nodeTotalOperators, nodeTotalOperands,
		vocabulary, length, calcLength, volume, difficulty, effort, time, bugs;
	Set<String> uniqueOperators = new HashSet<String>(), uniqueOperands = new HashSet<String>();
	protected File file;
	String ext = "";
	private boolean currentlyBlockComment = false;
	
	public MetricsFileNode(File toCount) {
		this.file = toCount;
		if (!file.exists()) throw new IllegalArgumentException("No file found matching '" + file.getName() + "'");
		
		ext = getFileExtension(file);
		getFileMetrics();
	}
	private void getFileMetrics() {
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
		if (	!line.isEmpty() && 
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
	if (!currentlyBlockComment && codeLine.length() > 1) {
		Set<String> excludedOperands = new HashSet<String>(), includedOperators = new HashSet<String>();
		
		String operatorsRegex;
		String operandsRegex;
		switch (ext) {
			case ".java" :
			case ".javah" : 
				excludedOperands = new HashSet<String>(Arrays.asList("one", "of",
						"abstract", "continue", "for", "new", "switch",
						"assert", "default", "if", "package", "synchronized",
						"do", "goto", "private", "this",
						"break",  "implements", "protected", "throw",
						 "else", "import", "public", "throws",
						"case", "enum", "instanceof", "return", "transient",
						"catch", "extends",  "try",
						"final", "interface", "static", "void",
						"class", "finally","strictfp", "volatile",
						"const", "native", "super", "while"));
				includedOperators = new HashSet<String>(
						Arrays.asList("break","case","continue","default","do","if","else","enum","for","goto","if","new","return","asm","operator","private",
							"protected","public","sizeof","struct","switch","union","while","this","namespace","using","try","catch","throw","abstract","concrete",
							"const_cast","static_cast","dynamic_cast","reinterpret_cast","typeid","template","explicit","true","false","typename"));
				break;
			case ".c":
			case ".h":
				excludedOperands = new HashSet<String>(Arrays.asList("auto","break","case","const","continue","default",
						"do","else"	,"enum"	,"extern","for"
						,"goto"	,"if","register","return",
						"signed","sizeof","static","struct"	,"switch"	,"typedef"	,"union",
						"unsigned","void"	,"volatile"	,"while"));
				includedOperators = new HashSet<String>(
						Arrays.asList("auto","extern","register","static","typedef","const","final",
								 "volatile","break","case","continue",
								 "default","do","if","else","enum","for","goto","if","new",
								 "return","sizeof","struct","switch","union","while"));
				break;
			
			case ".cpp":
			case ".hpp":
				excludedOperands = new HashSet<String>(Arrays.asList("one", "of",
						"abstract", "continue", "for", "new", "switch",
						"assert", "default", "if", "package", "synchronized",
						"do", "goto", "private", "this",
						"break", "implements", "protected", "throw",
						"else", "import", "public", "throws",
						"case", "enum", "instanceof", "return", "transient",
						"catch", "extends",  "short", "try",
						"final", "interface", "static", "void",
						"class", "finally", "strictfp", "volatile",
						"const",  "native", "super", "while"));
				includedOperators = new HashSet<String>(
						Arrays.asList("auto","extern","register","static","typedef","virtual",
								"mutable","inline","const","friend","volatile","final","break",
								"case","continue","default","do","if","else","enum","for","goto",
								"if","new","return","asm","operator","private","protected","public",
								"sizeof","struct","switch","union","while","this","namespace","using",
								"try","catch","throw","abstract","concrete","const_cast","static_cast",
								"dynamic_cast","reinterpret_cast","typeid","template","explicit","true","false","typename"));
				break;
		}
		operatorsRegex = "(=)|(>)|(<)|(!)|(~)|(/?)|(:)|(==)|(<=)|(>=)|(!=)|(&&)|(/|/|)|(/+/+)|(--)|(/+)|(-)|(/*)|(/)|(&)|(/|)|(^)|(%)|(<<)|(>>)|(>>>)|(/+=)|(-=)|(/*=)|(/=)|(&=)|(/|=)|(^=)|(%=)|(<<=)|(>>=)|(>>>=)";
	    //Keywords and ops regex shamelessly borrowed from https://www.daniweb.com/programming/software-development/threads/307653/halstead-metrics
	    operandsRegex = "(\\w+)|(\\d+)";
	    for (String word : includedOperators) operandsRegex += "|(" + word + ")";

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
			   if (includedOperators.contains(group)) {
				   uniqueOperators.add(group);
				   nodeTotalOperators++;
			   }
		       if (group.length() > 0  && !excludedOperands.contains(group)) { // per doc referenced in Design, reserved words are not operands for H-metrics
			       uniqueOperands.add(group);
			       nodeTotalOperands++;
			  	}
		   }
	
			vocabulary = uniqueOperators.size() + uniqueOperands.size();
			length = nodeTotalOperators + nodeTotalOperands;
			calcLength = uniqueOperators.size() * log2(uniqueOperators.size()) + uniqueOperands.size()*log2(uniqueOperands.size());
			volume = nodeTotalOperators * log2(vocabulary);
			difficulty = uniqueOperands.size() > 0 ? ((uniqueOperators.size() / 2) * (nodeTotalOperands / uniqueOperands.size()) ): 0;
			effort = difficulty*volume;
			time = effort / 18;
			bugs = volume > 0 ? (volume / 3000) :  0 ;
			
			//System.out.println(	"Code: " + codeLine + 
			//		"\n	Operators: " + uniqueOperators.toString() + 
			//     	"\n	Operands: " + uniqueOperands.toString());
	}
	

	}
	
	private int log2(double n) {
		return (int) Math.round(Math.log(n)/Math.log(2));
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

}