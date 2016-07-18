package CodeGeneratorJava;

import java.util.HashMap;
import java.util.Map;

public class State {
	
	public static final String PARAMS = "params";
	public static final String PAR_BLOCK = "parblock";
	public static final String PROC_YIELDS = "procyields";
	public static final String FOREVER_LOOP = "foreverloop";
	public static final String PARFOR ="parfor";
	public static final String ALT = "alt";
	public static final String ALT_GUARD = "altguard";
	public static final String PROTOCOL_EXPR = "protocolexpr";

	private static Map<String, Boolean> state_table= new HashMap<String, Boolean>();

	public static void init() {
		state_table.put(PARAMS, false);
		state_table.put(PAR_BLOCK, false);
		state_table.put(PROC_YIELDS, false);
		state_table.put(FOREVER_LOOP, false);
		state_table.put(PARFOR, false);
		state_table.put(ALT, false);
		state_table.put(ALT_GUARD, false);
		state_table.put(PROTOCOL_EXPR, false);
		
//		currentState();
	}
	
	public static boolean is(String field) {
		return state_table.get(field);
	}

	public static boolean set(String field, boolean value) {
		boolean old = state_table.get(field);
		state_table.put(field, value);
		printStateChange(field);
		return old;
	}
	
	private static void printStateChange(String field) {
		System.out.println("---------------------------------");
		System.out.println("State '" + field + "' changed to " + (state_table.get(field)?"ON.":"OFF."));
		System.out.println("---------------------------------");
	}
	
	//debug method
	public static void currentState() {
		System.out.println("*******************");
		System.out.println("   Current State   ");
		System.out.println("*******************");
		for(String key : state_table.keySet()) {
			System.out.println(key + " : " + state_table.get(key));
		}
	}
}
