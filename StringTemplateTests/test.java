//import java.util.StringTokenizer;
import org.stringtemplate.v4.*;
public class test {
    public static void main(String[] args) {
	/*int x;
	double x;

	System.out.println("Hello");
	String[] result = "this is a test.pj.hello ".split("\\.");
	for (int x=0;x<result.length; x++)
	System.out.println(x + ": " + result[x]);*/

	
	ST hello = new ST("Hello, <name>");
	hello.add("name", "World");
	System.out.println(hello.render());

	STGroup group = new STGroupFile("test.stg");

	
		ST st = group.getInstanceOf("decl");
	st.add("type", "int");
	st.add("name", "x");
	//st.add("value", 0);
	String result = st.render(); // yields "int x = 0;"
	System.out.println(result);
	

		st = group.getInstanceOf("test");
	
	st.add("a", "proc");
	st.add("b", "Fna");
	st.add("c", "end");
	//st.add("value", 0);
	result = st.render(); // yields "int x = 0;"
	//System.out.println(result);

	ST st2 = group.getInstanceOf("test");
	st2.add("a","proc");
	st2.add("b",st.render());
	st2.add("c","end");
	System.out.println(st2.render());


    }
}