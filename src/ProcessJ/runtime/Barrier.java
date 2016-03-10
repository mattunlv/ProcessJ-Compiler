package ProcessJ.runtime;

import java.util.ArrayList;

/**
 * ProcessJ code will have something of this sort:
 * 
 * enroll(b):                                                                                                                     
 *                                                                                                                                 
 * b.addProc(this);                                                                                                               
 *                                                                                                                                  
 * sync(b):                                                                                                                       
 *                                                                                                                                
 * b.decrement();                                                                                                                
 * yield(......., X);                                                                                                            
 *X: ...                                                                                                                           
*/
public class Barrier {
	ArrayList<Process> proc = new ArrayList<Process>();
	int count = 0;
	int procCounter = 0;

	synchronized void addProc(Process p) {
		proc.add(p);
		procCounter++;
		count++;
	}

	synchronized void decrement() {
		count--;
		if (count == 0) {
			for (int i = 0; i < procCounter; i++)
				proc.get(i).setReady();
			count = procCounter;
		}
	}
}