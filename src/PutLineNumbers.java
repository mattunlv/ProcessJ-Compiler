import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;


public class PutLineNumbers {
	
	static int len = 0;
	static int start = 0;
	static boolean redoing = false;
	
	public static String getLineNumber() {
		
		int len1 = String.valueOf(start).length();
		
		String s = start + ": ";
		if (!redoing) {
			for(int i = 0; i<(len-len1); i++) {
				s += " ";
			}
		}
		start++;
		return s;
	}
	
	public static void main(String[] args) {
		
		try {
			File in= new File("/Users/cabel/Dropbox/github/ProcessJ-Compiler/linenumber/input.txt");
			BufferedReader reader = new BufferedReader(new FileReader(in));
			
			PrintWriter writer = new PrintWriter("/Users/cabel/Dropbox/github/ProcessJ-Compiler/linenumber/output.txt", "UTF-8");
			
			String line = null;

			int lines = 0;
			while (reader.readLine() != null)
				lines++;
			reader.close();
			
			len = String.valueOf(lines).length();
			
			System.out.println("cnt=" + lines);

			reader = new BufferedReader(new FileReader(in));
			start= 1;
			boolean retrieved = false;
			while((line = reader.readLine()) != null) {
				int ind = line.indexOf(": ");
				if(ind != -1) {
//				if(false) {
					redoing = true;
					if(!retrieved) {
						start = Integer.valueOf(line.substring(0, ind));
						retrieved = true;
					}
					line = line.substring(ind+2);
				}
				line = line.replace("\t", "  ");
//				line = line.replace("    ", "  ");
				writer.write(getLineNumber() + line + "\n");
//				writer.write(line + "\n");
			}
			writer.close();

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
