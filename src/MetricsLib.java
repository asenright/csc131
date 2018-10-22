import java.io.File;

public class MetricsLib {
	
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
