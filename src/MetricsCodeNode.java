import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetricsCodeNode extends MetricsFileNode {
	private boolean currentlyBlockComment;
	
	int linesOfCode, linesOfComment, nodeTotalOperators, nodeTotalOperands,
		vocabulary, length, calcLength, volume, difficulty, effort, time, bugs;
	Set<String> uniqueOperators = new HashSet<String>(), uniqueOperands = new HashSet<String>();
	
	public MetricsCodeNode(File toCount) {
		super(toCount);	
	}
	protected void getFileMetrics() {
		super.getFileMetrics();
		deriveHalsteads();
	}
	
	protected void getLineMetrics(String line) {
		super.getLineMetrics(line);
		getCodeLineMetrics(line);
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

		if (!currentlyBlockComment && codeLine.length() > 1) {
			Set<String> excludedOperands = new HashSet<String>(), includedOperators = new HashSet<String>();
			String operatorsRegex;
			String operandsRegex;
			switch (ext) {
				case ".java" :
				case ".javah" : 
					excludedOperands = MetricsLib.javaExcludedOperands;
					includedOperators = MetricsLib.javaIncludedOperators;
					break;
				case ".c":
				case ".h":
					excludedOperands = MetricsLib.cExcludedOperands;
					includedOperators = MetricsLib.cIncludedOperators;
					break;
				case ".cpp":
				case ".hpp":
					excludedOperands = MetricsLib.cppExcludedOperands;
					includedOperators = MetricsLib.cppIncludedOperators;
					break;
			}
			operatorsRegex = "(=)|(>)|(<)|(!)|(~)|(/?)|(:)|(==)|(<=)|(>=)|(!=)|(&&)|(/|/|)|(/+/+)|(--)|(/+)|(-)|(/*)|(/)|(&)|(/|)|(^)|(%)|(<<)|(>>)|(>>>)|(/+=)|(-=)|(/*=)|(/=)|(&=)|(/|=)|(^=)|(%=)|(<<=)|(>>=)|(>>>=)";
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
				   if (group.length() == 0) continue;
				   else if (includedOperators.contains(group)) {
					   uniqueOperators.add(group);
					   nodeTotalOperators++;
				   }
				   else if (!excludedOperands.contains(group)) { // per doc referenced in Design, reserved words are not operands for H-metrics
				       uniqueOperands.add(group);
				       nodeTotalOperands++;
				  	}
			   }
	
				System.out.println(	"Code: " + codeLine + 
						"\n	Operators: " + uniqueOperators.toString() + 
				     	"\n	Operands: " + uniqueOperands.toString());
			}
		}
		private void deriveHalsteads() {
			vocabulary = uniqueOperators.size() + uniqueOperands.size();
			length = nodeTotalOperators + nodeTotalOperands;
			calcLength = uniqueOperators.size() * MetricsLib.log2(uniqueOperators.size()) + uniqueOperands.size()*MetricsLib.log2(uniqueOperands.size());
			volume = nodeTotalOperators * MetricsLib.log2(vocabulary);
			difficulty = uniqueOperands.size() > 0 ? ((uniqueOperators.size() / 2) * (nodeTotalOperands / uniqueOperands.size()) ): 0;
			effort = difficulty*volume;
			time = effort / 18;
			bugs = volume > 0 ? (volume / 3000) :  0 ;		
		}

}
