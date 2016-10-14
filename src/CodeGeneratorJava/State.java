package CodeGeneratorJava;

import java.util.HashMap;
import java.util.Map;

/*
 * FIXME put java doc stating why this class is necessary.
 * 
 * This handles the state of the codegen visitor. And is useful for
 * nested visits to nodes such as par/parfor etc where old state needs to
 * be maintained.
 */
public class State {

    public static final String PARAMS = "params";
    public static final String PAR_BLOCK = "parblock";
    public static final String PROC_YIELDS = "procyields";
    public static final String FOREVER_LOOP = "foreverloop";
    public static final String PARFOR = "parfor";
    public static final String ALT = "alt";
    public static final String ALT_GUARD = "altguard";
    public static final String PROTOCOL_EXPR = "protocolexpr";
    public static final String CLAIMSTAT = "claimstat"; //nested claims are not allowed. so maybe not necessary to put this here and instead just use a class var in code gen.

    private static Map<String, Boolean> state_table = new HashMap<String, Boolean>();

    public static void init() {
        state_table.put(PARAMS, false);
        state_table.put(PAR_BLOCK, false);
        state_table.put(PROC_YIELDS, false);
        state_table.put(FOREVER_LOOP, false);
        state_table.put(PARFOR, false);
        state_table.put(ALT, false);
        state_table.put(ALT_GUARD, false);
        state_table.put(PROTOCOL_EXPR, false);
        state_table.put(CLAIMSTAT, false);

        //		currentState();
    }

    public static boolean is(String field) {
        return state_table.get(field);
    }

    public static boolean set(String field, boolean value) {
        boolean old = state_table.get(field);
        state_table.put(field, value);
        //printStateChange(field);
        return old;
    }

    private static void printStateChange(String field) {
        System.out.println("---------------------------------");
        System.out.println("State '" + field + "' changed to "
                + (state_table.get(field) ? "ON." : "OFF."));
        System.out.println("---------------------------------");
    }

    //debug method
    public static void currentState() {
        System.out.println("*******************");
        System.out.println("   Current State   ");
        System.out.println("*******************");
        for (String key : state_table.keySet()) {
            System.out.println(key + " : " + state_table.get(key));
        }
    }
}
