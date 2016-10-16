import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import AST.Compilation;
import Printers.PrettyPrinter;

public class ReadInclude {

    public static void main(String args[]) {
        Compilation c = null;
        if (args.length != 1) {
            System.out.println("usage: java ReadInclude filename");
            System.exit(1);
        }

        try {
            FileInputStream fileIn = new FileInputStream(args[0]);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            c = (Compilation) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            System.out.println("File " + args[0] + " not found.");
            e.printStackTrace();
            return;
        }
        c.visit(new PrettyPrinter());
    }

}
