


class Protocol {

}
class ProtocolCase { 
    String tag; 	
}
class Santa_Message extends Protocol {
    static class a extends ProtocolCase { 
	int myA;
	double myB;

	public a(int myA, double myB) {
	    this.myA = myA;
	    this.myB = myB;
	    this.tag = "a";
	}
    }

    static class b extends ProtocolCase {
        int myA;

        public b(int myA) {
            this.myA = myA;
            this.tag = "b";
        }
    }


}
class Elf_Message extends Protocol {
    static class q extends ProtocolCase {
	String myS;

	public q(String myS) {
	    this.myS = myS;
	    this.tag = "q";
	}
    }
}


class Channel<T> { 
    private T data;
    void write(T data) { this.data = data; }
    T read() { return data; }
}

public class test4 {

    public static void foo(Channel<ProtocolCase> chan) { 
	// chan is really Channel<Elf_Message>
	ProtocolCase m = chan.read();
	switch (m.tag) {
	case "a":
	    System.out.println(((Santa_Message.a)m).myA);
	    System.out.println(((Santa_Message.a)m).myB);
	    break;
	case "b":
	    System.out.println(((Santa_Message.b)m).myA);
	    break;
	case "q":
	    System.out.println(((Elf_Message.q)m).myS);
            break;
	}



    }
    


    public static void main(String args[]) {

	Channel<ProtocolCase> chan = new Channel<ProtocolCase>();
	chan.write(new Santa_Message.a(722, 564.76));
	foo(chan);
	chan.write(new Santa_Message.b(722));
	foo(chan);
	chan.write(new Elf_Message.q("Hello"));
	foo(chan);

    }
}
	