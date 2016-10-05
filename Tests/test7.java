import java.io.*;



class A implements Serializable {
    A myA;
    int n;
    public A(int n) {
        this.n = n;
        if (n>0)
            myA = new A(n-1);
        else
            myA = null;
    }

    void print() {
        System.out.println(n);
        if (myA != null)
            myA.print();
    }
}


public class test7 {

    private static byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
                out.writeObject(object);
                return bos.toByteArray();
            }
    }

    private static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
                return in.readObject();
            }
    }

    public static void main(String args[]) {
        A a = new A(5);
        a.print();
        A b = null;
        try {
            byte[] bytes = convertToBytes(a);
            b = (A)convertFromBytes(bytes);
        } catch (Exception e) { }
        b.print();
    }
}

