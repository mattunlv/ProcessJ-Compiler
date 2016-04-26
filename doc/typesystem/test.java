public class test {
    public static void main(String args[]) {
	int[][] a1 = new int[10][10];
	int[][] a2 = new int[100][100];
	int[][][] b;

	a1 = a2;
	a2 = a1;
	b = a1;
   }
}
	   