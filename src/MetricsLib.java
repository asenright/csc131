import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class MetricsLib {
	public static HashSet<String> cIncludedOperators  = new HashSet<String>(Arrays.asList(
								"auto","extern","register","static","typedef","const","final",
								"volatile","break","case","continue",
								"default","do","if","else","enum","for","goto","if","new",
								"return","sizeof","struct","switch","union","while")), 
						cExcludedOperands  = new HashSet<String>(Arrays.asList(
								"auto","break","case","const","continue","default",
								"do","else"	,"enum"	,"extern","for"
								,"goto"	,"if","register","return",
								"signed","sizeof","static","struct"	,"switch"	,"typedef"	,"union",
								"unsigned","void"	,"volatile"	,"while")),
						cppIncludedOperators  = new HashSet<String>(Arrays.asList(
								"auto","extern","register","static","typedef","virtual",
								"mutable","inline","const","friend","volatile","final","break",
								"case","continue","default","do","if","else","enum","for","goto",
								"if","new","return","asm","operator","private","protected","public",
								"sizeof","struct","switch","union","while","this","namespace","using",
								"try","catch","throw","abstract","concrete","const_cast","static_cast",
								"dynamic_cast","reinterpret_cast","typeid","template","explicit","true","false","typename")), 
						cppExcludedOperands  = new HashSet<String>(Arrays.asList("one", "of",
								"abstract", "continue", "for", "new", "switch",
								"assert", "default", "if", "package", "synchronized",
								"do", "goto", "private", "this",
								"break", "implements", "protected", "throw",
								"else", "import", "public", "throws",
								"case", "enum", "instanceof", "return", "transient",
								"catch", "extends",  "short", "try",
								"final", "interface", "static", "void",
								"class", "finally", "strictfp", "volatile",
								"const",  "native", "super", "while")),
						javaIncludedOperators  = new HashSet<String>(Arrays.asList(
								"break","case","continue","default","do","if","else","enum","for","goto",
								"if","new","return","asm","operator","private",
								"protected","public","sizeof","struct","switch","union","while","this",
								"namespace","using","try","catch","throw","abstract","concrete",
								"const_cast","static_cast","dynamic_cast","reinterpret_cast","typeid",
								"template","explicit","true","false","typename")), 
						javaExcludedOperands = new HashSet<String>(Arrays.asList(
								"one", "of", "abstract", "continue", "for", "new", "switch",
								"assert", "default", "if", "package", "synchronized",
								"do", "goto", "private", "this",
								"break",  "implements", "protected", "throw",
								 "else", "import", "public", "throws",
								"case", "enum", "instanceof", "return", "transient",
								"catch", "extends",  "try",
								"final", "interface", "static", "void",
								"class", "finally","strictfp", "volatile",
								"const", "native", "super", "while"));
		/**Credit to technicalkeeda.com for getFileExtension()
	 * @param file File to get extension from.
	 * @return Returns the extension of the file.
	 */
	public static String getFileExtension(File file) {
		String extension = "";
		try {
		if (file != null && file.exists()) {
			String name =file.getName();
			extension = getFileExtension(name);
		}
		return extension;
		} catch (Exception e) {
			extension = "";
		}
		return extension;
	} 
	
	public static String getFileExtension(String fileName) {
		String extension = "";
		extension = fileName.substring(fileName.lastIndexOf("."));
		return extension;
	}
	
	public static int log2(double n) {
		return (int) Math.round(Math.log(n)/Math.log(2));
	}
	
    public static boolean isSource(String filePath) {
		return (getFileExtension(filePath).equals(".c")||
				getFileExtension(filePath).equals(".h")||
				getFileExtension(filePath).equals(".cpp")||
				getFileExtension(filePath).equals(".hpp")||
				getFileExtension(filePath).equals(".java")||
				getFileExtension(filePath).equals(".javah"));
	};
	
	
}
