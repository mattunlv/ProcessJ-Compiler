package solution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;

public class Solution {

	static String[] Braces(String[] values) {
		Stack<String> st = new Stack<String>();
		
		for (int i = 0; i < values.length; i++) {
			String[] tokens = values[i].split("");

			for (int j = 0; j < tokens.length; j++) {
				String t = tokens[j];
				if ("(".equals(t) || "{".equals(t) || "[".equals(t)) {
					st.push(t);
				} else {
					if (("(".equals(st.peek()) && t.equals(")")) ||
							("{".equals(st.peek()) && t.equals("}")) ||
							("[".equals(st.peek()) && t.equals("]"))
							) {
						st.pop();
					} else {
						break;
					}
				}
			}
			
			if (st.isEmpty()) {
				values[i] = "YES";
			} else {
				values[i] = "NO";
			}
			
			st.clear();
		}

		return values;
	}

	public static void main(String[] args) {
		
		String[] result = Braces(null);
		
		
		System.out.println("resul1=" + result[0]);
		System.out.println("resul1=" + result[1]);
	}

//	public static void main(String[] args) throws IOException {
//		Scanner in = new Scanner(System.in);
//		final String fileName = System.getenv("OUTPUT_PATH");
//		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
//		String[] res;
//
//		int _values_size = 0;
//		_values_size = Integer.parseInt(in.nextLine());
//		String[] _values = new String[_values_size];
//		String _values_item;
//		for (int _values_i = 0; _values_i < _values_size; _values_i++) {
//			try {
//				_values_item = in.nextLine();
//			} catch (Exception e) {
//				_values_item = null;
//			}
//			_values[_values_i] = _values_item;
//		}
//
//		res = Braces(_values);
//		for (int res_i = 0; res_i < res.length; res_i++) {
//			bw.write(String.valueOf(res[res_i]));
//			bw.newLine();
//		}
//
//		bw.close();
//	}

}