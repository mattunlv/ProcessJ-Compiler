package SampleTests;

import java.io.PrintWriter;

public class mandelbrotseq {
	public static int cal_pixel(double real, double imag) {
		int count, max;
		double z_real, z_imag;
		double temp, lengthsq;

		max = 256;
		z_real = 0;
		z_imag = 0;
		count = 0;
		do {
			temp = z_real * z_real - z_imag * z_imag + real;
			z_imag = 2 * z_real * z_imag + imag;
			z_real = temp;
			lengthsq = z_real * z_real + z_imag * z_imag;
			count++;
		} while ((lengthsq < 4.0) && (count < max));
		return count;
	}

	public static boolean write_P2_PGM(int pic[][], String filename, int max) {
		// assume that pic is rectangular.
		boolean ok = true;
		try {
			int width = pic[0].length;
			int height = pic.length;
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			writer.println("P2");
			writer.println(width + " " + height);
			writer.println(max);
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++)
					writer.print(pic[i][j] + " ");
				writer.println("");
			}
			writer.close();
		} catch (Exception e) {
			ok = false;
		}
		return ok;
	}

	public static void main(String args[]) {

		int disp_width = 4000;
		int disp_height = 3000;

		int mandelbrot[][] = new int[disp_height][disp_width];

		double real_min = -0.7801785714285;
		double real_max = -0.7676785714285;
		double imag_min = -0.1279296875000;
		double imag_max = -0.1181640625000;

		double scale_real = (real_max - real_min) / disp_width;
		double scale_imag = (imag_max - imag_min) / disp_height;

		for (int y = 0; y < disp_height; y++) {
			for (int x = 0; x < disp_width; x++) {
				double c_real = real_min + ((double) x * scale_real);
				double c_imag = imag_min + ((double) y * scale_imag);
				mandelbrot[y][x] = 256 - cal_pixel(c_real, c_imag);
			}
		}

		write_P2_PGM(mandelbrot, "mm.pgm", 256);
	}
}
