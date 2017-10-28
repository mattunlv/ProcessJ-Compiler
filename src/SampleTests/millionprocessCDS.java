package SampleTests;
import java.util.*;
import ProcessJ.runtime.*;

public class millionprocessCDS {

  public static class foo extends PJProcess {

      public foo() {
      }

    @Override
    public synchronized void run() {
  ;
      terminate();
    }
  }


  public static class main extends PJProcess {
    String[] _pd$args;
    int _ld0$i;

      public main(String[] _pd$args) {
        this._pd$args = _pd$args;
      }

    @Override
    public synchronized void run() {
      switch(this.runLabel) {
        case 0: break;
          case 1: resume(1); break;
      }
      final PJPar parfor1 = new PJPar(-1, this);
      int cnt = 0;  
      List<PJProcess> pp = new LinkedList<PJProcess>(); 

//      for(_ld0$i = 0; (_ld0$i < 10000000); _ld0$i++){ //(4gb)22.271secs
//      for(_ld0$i = 0; (_ld0$i < 20000000); _ld0$i++){ //(4gb)41.505secs
//      for(_ld0$i = 0; (_ld0$i < 30000000); _ld0$i++){ //(4gb)64.504secs / 78.115
//      for(_ld0$i = 0; (_ld0$i < 40000000); _ld0$i++){ //(6gb) 132.702
//      for(_ld0$i = 0; (_ld0$i < 50000000); _ld0$i++){ //(7gb) 144.681
      for(_ld0$i = 0; (_ld0$i < 60000000); _ld0$i++){ //(7gb) 144.681
        cnt++;
        pp.add(
          (new millionprocessCDS.foo(){
            public void finalize() {
              parfor1.decrement();    
            }
          })
        );
      }
      //set the process count 
      parfor1.setProcessCount(cnt);

      //schedule all the processes
      for(PJProcess p : pp) {
        p.schedule();
      }
      setNotReady();
      this.runLabel = 1;
      yield();
      label(1);
      std.io.println( "done!!" );
      terminate();
    }
  }

  public static void main(String[] args) {

    Scheduler scheduler = new Scheduler();

    PJProcess.scheduler = scheduler;
//    PJProcess.scheduler.makeItDump();
    System.out.println("Added _main process to scheduler...");
    

    (new millionprocessCDS.main(args)).schedule();

    System.out.println("Starting the scheduler...");
    PJProcess.scheduler.start();

    System.out.println("Scheduler thread.start() done.");
    
  }
  public static boolean getTrue() {
    return true;
  }
}