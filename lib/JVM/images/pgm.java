package images;

public class pgm {
    
    public static boolean write_P2_PGM(int pic[][], String filename, int max) {
        // assume that pic is rectangular.
        boolean ok = true;
        try {
            int width = pic[0].length;
            int height = pic.length;
            java.io.PrintWriter writer = new java.io.PrintWriter(filename, "UTF-8");
            writer.println("P2");
            writer.println(width + " " + height);
            writer.println(max);
            for (int i =0; i<height; i++) {
                for (int j=0; j<width; j++) {
//                	System.out.println(pic[i][j]);
                    writer.print(pic[i][j] + " ");
                }
                writer.println("");
            }
            writer.close();
        } catch (Exception e) { ok = false; }       
        return ok;
    }
}
