package CodeGeneratorJava;

public class MatrixChain {

	String[] m = {"A1", "A2", "A3", "A4"};
	
	int open = 0;
	
	void showOrder(int i, int j, int[][] factor){
		
		System.out.print("(");
		open++;
		
		if (i==j) {
			System.out.print(m[i]);
			for(int v=0; v<open; v++)
				System.out.print(")");
			return;
		}
		int k = factor[i][j];
		
		System.out.print("(");
		open++;
		
		for (int p = i; p<k; p++) {
			System.out.print(m[p]);
			System.out.print(")");
			open--;
			System.out.print("*");
		}
		
		showOrder(k, j, factor);
	}
	
	
	public static void main(String[] args) {
		
		int[][] factor = {{-1, 1, 1, 1},
						{-1, -1, 2, 3},
						{-1, -1, -1, 3},
						{-1, -1, -1, -1}};
		
		MatrixChain obj = new MatrixChain();
		
		obj.showOrder(0, 3, factor);
		
	}
	
}
