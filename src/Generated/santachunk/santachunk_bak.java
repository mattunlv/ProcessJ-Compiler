package Generated.santachunk;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class santachunk_bak { 
  public static class Protocol_Reindeer_msg {
    public static class Protocol_Reindeer_msg_holiday extends PJProtocolCase {
      public int id;
      public Protocol_Reindeer_msg_holiday(int id) {
          this.id = id;
          this.tag = "holiday";
      }
    }
    public static class Protocol_Reindeer_msg_deer_ready extends PJProtocolCase {
      public int id;
      public Protocol_Reindeer_msg_deer_ready(int id) {
          this.id = id;
          this.tag = "deer_ready";
      }
    }
    public static class Protocol_Reindeer_msg_deliver extends PJProtocolCase {
      public int id;
      public Protocol_Reindeer_msg_deliver(int id) {
          this.id = id;
          this.tag = "deliver";
      }
    }
    public static class Protocol_Reindeer_msg_deer_done extends PJProtocolCase {
      public int id;
      public Protocol_Reindeer_msg_deer_done(int id) {
          this.id = id;
          this.tag = "deer_done";
      }
    }
  }
  public static class Protocol_Elf_msg{
    public static class Protocol_Elf_msg_working extends PJProtocolCase {
      public int id;
      public Protocol_Elf_msg_working(int id) {
          this.id = id;
          this.tag = "working";
      }
    }
    public static class Protocol_Elf_msg_elf_ready extends PJProtocolCase {
      public int id;
      public Protocol_Elf_msg_elf_ready(int id) {
          this.id = id;
          this.tag = "elf_ready";
      }
    }
    public static class Protocol_Elf_msg_waiting extends PJProtocolCase {
      public int id;
      public Protocol_Elf_msg_waiting(int id) {
          this.id = id;
          this.tag = "waiting";
      }
    }
    public static class Protocol_Elf_msg_consult extends PJProtocolCase {
      public int id;
      public Protocol_Elf_msg_consult(int id) {
          this.id = id;
          this.tag = "consult";
      }
    }
    public static class Protocol_Elf_msg_elf_done extends PJProtocolCase {
      public int id;
      public Protocol_Elf_msg_elf_done(int id) {
          this.id = id;
          this.tag = "elf_done";
      }
    }
  }
  public static class Protocol_Santa_msg {
    public static class Protocol_Santa_msg_reindeer_ready extends PJProtocolCase {
      ;
      public Protocol_Santa_msg_reindeer_ready() {
          this.tag = "reindeer_ready";
      }
    }
    public static class Protocol_Santa_msg_harness extends PJProtocolCase {
      public int id;
      public Protocol_Santa_msg_harness(int id) {
          this.id = id;
          this.tag = "harness";
      }
    }
    public static class Protocol_Santa_msg_mush_mush extends PJProtocolCase {
      ;
      public Protocol_Santa_msg_mush_mush() {
          this.tag = "mush_mush";
      }
    }
    public static class Protocol_Santa_msg_woah extends PJProtocolCase {
      ;
      public Protocol_Santa_msg_woah() {
          this.tag = "woah";
      }
    }
    public static class Protocol_Santa_msg_unharness extends PJProtocolCase {
      public int id;
      public Protocol_Santa_msg_unharness(int id) {
          this.id = id;
          this.tag = "unharness";
      }
    }
    public static class Protocol_Santa_msg_elves_ready extends PJProtocolCase {
      ;
      public Protocol_Santa_msg_elves_ready() {
          this.tag = "elves_ready";
      }
    }
    public static class Protocol_Santa_msg_greet extends PJProtocolCase {
      public int id;
      public Protocol_Santa_msg_greet(int id) {
          this.id = id;
          this.tag = "greet";
      }
    }
    public static class Protocol_Santa_msg_consulting extends PJProtocolCase {
      ;
      public Protocol_Santa_msg_consulting() {
          this.tag = "consulting";
      }
    }
    public static class Protocol_Santa_msg_santa_done extends PJProtocolCase {
      ;
      public Protocol_Santa_msg_santa_done() {
          this.tag = "santa_done";
      }
    }
    public static class Protocol_Santa_msg_goodbye extends PJProtocolCase {
      public int id;
      public Protocol_Santa_msg_goodbye(int id) {
          this.id = id;
          this.tag = "goodbye";
      }
    }
  }
  public static class Protocol_Message {
  }
  public static final int N_REINDEER = 9;
  public static final int G_REINDEER = N_REINDEER;
  public static final int N_ELVES = 10;
  public static final int G_ELVES = 3;
  public static final int HOLIDAY_TIME = 10000;
  public static final int WORKING_TIME = 15000;
  public static final int DELIVERY_TIME = 10000;
  public static final int CONSULTATION_TIME = 15000;
  
  public static boolean getTrue() {
    return true;
  }
  
  public static class display extends PJProcess {
    PJChannel<PJProtocolCase> _pd$in;
    PJProtocolCase _ld0$msg;

      public display(PJChannel<PJProtocolCase> _pd$in) {
        this._pd$in = _pd$in;
      }

    @Override
    public synchronized void run() {
      switch(this.runLabel) {
        case 0: break;
          case 1: resume(1); break;
          case 2: resume(2); break;
      }
      _ld0$msg = null;;
      while( getTrue() ) {
        label(1);
        if(_pd$in.isReadyToRead(this)) {
          _ld0$msg = _pd$in.read(this);
          this.runLabel = 2;
          yield();
        } else {
          setNotReady();
          _pd$in.addReader(this);
          this.runLabel = 1;
          yield();
        }
        label(2);;
        switch( _ld0$msg.tag ){
            case "holiday":
              std.io.println( (("                                                        Reindeer-" + ((Protocol_Reindeer_msg.Protocol_Reindeer_msg_holiday) _ld0$msg).id) + ": on holiday ... wish you were here") );
              break;
            case "deer_ready":
              std.io.println( (("                                                        Reindeer-" + ((Protocol_Reindeer_msg.Protocol_Reindeer_msg_deer_ready) _ld0$msg).id) + ": back from holiday ... ready for work") );
              break;
            case "deliver":
              std.io.println( (("                                                        Reindeer-" + ((Protocol_Reindeer_msg.Protocol_Reindeer_msg_deliver) _ld0$msg).id) + ": delivering toys...la-di-da-di-da-di-da") );
              break;
            case "deer_done":
              std.io.println( (("                                                        Reindeer-" + ((Protocol_Reindeer_msg.Protocol_Reindeer_msg_deer_done) _ld0$msg).id) + ": all toys delivered...want a holiday") );
              break;
            case "working":
              std.io.println( (("                            Elf-" + ((Protocol_Elf_msg.Protocol_Elf_msg_working) _ld0$msg).id) + ": working") );
              break;
            case "elf_ready":
              std.io.println( (("                            Elf-" + ((Protocol_Elf_msg.Protocol_Elf_msg_elf_ready) _ld0$msg).id) + ": need to consult Santa, ;(") );
              break;
            case "waiting":
              std.io.println( (("                            Elf-" + ((Protocol_Elf_msg.Protocol_Elf_msg_waiting) _ld0$msg).id) + ": in the waiting room...") );
              break;
            case "consult":
              std.io.println( (("                            Elf-" + ((Protocol_Elf_msg.Protocol_Elf_msg_consult) _ld0$msg).id) + ": about these toys...??") );
              break;
            case "elf_done":
              std.io.println( (("                            Elf-" + ((Protocol_Elf_msg.Protocol_Elf_msg_elf_done) _ld0$msg).id) + ": OK...we'll build it, bye...") );
              break;
            case "reindeer_ready":
              std.io.println( "Santa: Ho-ho-ho...the reindeer are back!" );
              break;
            case "harness":
              std.io.println( ("Santa: harnessing reindeer:" + ((Protocol_Santa_msg.Protocol_Santa_msg_harness) _ld0$msg).id) );
              break;
            case "mush_mush":
              std.io.println( "Santa: mush mush ..." );
              break;
            case "woah":
              std.io.println( "Santa: woah...we're back home!" );
              break;
            case "unharness":
              std.io.println( ("Santa: un-harnessing reindeer:" + ((Protocol_Santa_msg.Protocol_Santa_msg_unharness) _ld0$msg).id) );
              break;
            case "elves_ready":
              std.io.println( "Santa: Ho-ho-ho...some elves are here!" );
              break;
            case "greet":
              std.io.println( ("Santa: hello elf:" + ((Protocol_Santa_msg.Protocol_Santa_msg_greet) _ld0$msg).id) );
              break;
            case "consulting":
              std.io.println( "Santa: consulting with elves..." );
              break;
            case "santa_done":
              std.io.println( "Santa: OK, all done - thanks!" );
              break;
            case "goodbye":
              std.io.println( ("Santa: goodbye elf:" + ((Protocol_Santa_msg.Protocol_Santa_msg_goodbye) _ld0$msg).id) );
              break;
        };
      }
//      terminate();
    }
  }


  public static class p_barrier_knock extends PJProcess {
    int _pd$n;
    PJChannel<Boolean> _pd$a;
    PJChannel<Boolean> _pd$b;
    PJChannel<Boolean> _pd$knock;
    int _ld0$i;
    boolean _ld1$any;
    int _ld2$i;
    boolean _ld3$any;

      public p_barrier_knock(int _pd$n, PJChannel<Boolean> _pd$a, PJChannel<Boolean> _pd$b, PJChannel<Boolean> _pd$knock) {
        this._pd$a = _pd$a;
        this._pd$b = _pd$b;
        this._pd$knock = _pd$knock;
        this._pd$n = _pd$n;
      }

    @Override
    public synchronized void run() {
      switch(this.runLabel) {
        case 0: break;
          case 1: resume(1); break;
          case 2: resume(2); break;
          case 3: resume(3); break;
          case 4: resume(4); break;
          case 5: resume(5); break;
          case 6: resume(6); break;
      }
      while( getTrue() ) {
        for(_ld0$i = 0; (_ld0$i < _pd$n); _ld0$i++){
          label(1);
          if(_pd$a.isReadyToRead(this)) {
            _ld1$any = _pd$a.read(this);
            this.runLabel = 2;
            yield();
          } else {
            setNotReady();
            _pd$a.addReader(this);
            this.runLabel = 1;
            yield();
          }
          label(2);
        };

        label(3);
        if (_pd$knock.isReadyToWrite()) {
          _pd$knock.write(this, true);
          this.runLabel = 4;
          yield();
        } else {
          setNotReady();
          this.runLabel = 3;
          yield();
        }

        label(4);;
        for(_ld2$i = 0; (_ld2$i < _pd$n); _ld2$i++){
          label(5);
          if(_pd$b.isReadyToRead(this)) {
            _ld3$any = _pd$b.read(this);
            this.runLabel = 6;
            yield();
          } else {
            setNotReady();
            _pd$b.addReader(this);
            this.runLabel = 5;
            yield();
          }
          label(6);
        };
      }
    }
  }


  public static class p_barrier extends PJProcess {
    int _pd$n;
    PJChannel<Boolean> _pd$a;
    PJChannel<Boolean> _pd$b;
    int _ld0$i;
    boolean _ld1$any;
    int _ld2$i;
    boolean _ld3$any;

      public p_barrier(int _pd$n, PJChannel<Boolean> _pd$a, PJChannel<Boolean> _pd$b) {
        this._pd$a = _pd$a;
        this._pd$b = _pd$b;
        this._pd$n = _pd$n;
      }

    @Override
    public synchronized void run() {
      switch(this.runLabel) {
        case 0: break;
          case 1: resume(1); break;
          case 2: resume(2); break;
          case 3: resume(3); break;
          case 4: resume(4); break;
      }
      while( getTrue() ) {
        for(_ld0$i = 0; (_ld0$i < _pd$n); _ld0$i++){
          label(1);
          if(_pd$a.isReadyToRead(this)) {
            _ld1$any = _pd$a.read(this);
            this.runLabel = 2;
            yield();
          } else {
            setNotReady();
            _pd$a.addReader(this);
            this.runLabel = 1;
            yield();
          }
          label(2);
        };
        for(_ld2$i = 0; (_ld2$i < _pd$n); _ld2$i++){
          label(3);
          if(_pd$b.isReadyToRead(this)) {
            _ld3$any = _pd$b.read(this);
            this.runLabel = 4;
            yield();
          } else {
            setNotReady();
            _pd$b.addReader(this);
            this.runLabel = 3;
            yield();
          }
          label(4);
        };
      }
    }
  }


  public static class syncronize extends PJProcess {
    PJChannel<Boolean> _pd$a;
    PJChannel<Boolean> _pd$b;

      public syncronize(PJChannel<Boolean> _pd$a, PJChannel<Boolean> _pd$b) {
        this._pd$a = _pd$a;
        this._pd$b = _pd$b;
      }

    @Override
    public synchronized void run() {
      switch(this.runLabel) {
        case 0: break;
          case 1: resume(1); break;
          case 2: resume(2); break;
          case 3: resume(3); break;
          case 4: resume(4); break;
          case 5: resume(5); break;
          case 6: resume(6); break;
      }
      label(3);
      if(!_pd$a.claim() ) {
        this.runLabel = 3;
        yield();
      }

      label(1);
      if (_pd$a.isReadyToWrite()) {
        _pd$a.write(this, true);
        this.runLabel = 2;
        yield();
      } else {
        _pd$a.addWriter(this);
        setNotReady();
        this.runLabel = 1;
        yield();
      }

      label(2);
      _pd$a.unclaim();;
      label(6);
      if(!_pd$b.claim() ) {
        this.runLabel = 6;
        yield();
      }

      label(4);
      if (_pd$b.isReadyToWrite()) {
        _pd$b.write(this, true);
        this.runLabel = 5;
        yield();
      } else {
        _pd$b.addWriter(this);
        setNotReady();
        this.runLabel = 4;
        yield();
      }

      label(5);
      _pd$b.unclaim();
      terminate();
    }
  }


  public static class reindeer extends PJProcess {
    int _pd$id;
    long _pd$seed;
    PJBarrier _pd$just_reindeer;
    PJBarrier _pd$santa_reindeer;
    PJChannel<Integer> _pd$to_santa;
    PJChannel<PJProtocolCase> _pd$report;
    long _ld0$my_seed;
    PJTimer _ld1$tim;
    long _ld2$t;
    long _ld3$wait;

      public reindeer(int _pd$id, long _pd$seed, PJBarrier _pd$just_reindeer, PJBarrier _pd$santa_reindeer, PJChannel<Integer> _pd$to_santa, PJChannel<PJProtocolCase> _pd$report) {
        this._pd$santa_reindeer = _pd$santa_reindeer;
        this._pd$to_santa = _pd$to_santa;
        this._pd$seed = _pd$seed;
        this._pd$just_reindeer = _pd$just_reindeer;
        this._pd$report = _pd$report;
        this._pd$id = _pd$id;
      }

    @Override
    public synchronized void run() {
      switch(this.runLabel) {
        case 0: break;
          case 1: resume(1); break;
          case 2: resume(2); break;
          case 3: resume(3); break;
          case 4: resume(4); break;
          case 5: resume(5); break;
          case 6: resume(6); break;
          case 7: resume(7); break;
          case 8: resume(8); break;
          case 9: resume(9); break;
          case 10: resume(10); break;
          case 11: resume(11); break;
          case 12: resume(12); break;
          case 13: resume(13); break;
          case 14: resume(14); break;
          case 15: resume(15); break;
          case 16: resume(16); break;
          case 17: resume(17); break;
          case 18: resume(18); break;
          case 19: resume(19); break;
          case 20: resume(20); break;
          case 21: resume(21); break;
          case 22: resume(22); break;

          case 23: resume(23); break;
      }
      _ld0$my_seed = _pd$seed;;
      _ld1$tim = new PJTimer();;
      _ld3$wait = HOLIDAY_TIME;;
      while( getTrue() ) {
        label(3);
        if(!_pd$report.claim() ) {
          this.runLabel = 3;
          yield();
        }

        label(1);
        if (_pd$report.isReadyToWrite()) {
          _pd$report.write(this, new Protocol_Reindeer_msg.Protocol_Reindeer_msg_holiday(_pd$id));
          this.runLabel = 2;
          yield();
        } else {
          _pd$report.addWriter(this);
          setNotReady();
          this.runLabel = 1;
          yield();
        }

        label(2);
        _pd$report.unclaim();;
        
        
        
//        _ld2$t = _ld1$tim.read();;
//        _ld1$tim = new Timer(this, 1000);
//        try {
//          _ld1$tim.start();
//          setNotReady();
//          this.runLabel = 4;
//          yield();
//        } catch (InterruptedException e) {
//          System.out.println("Timer Interrupted Exception!");
//        }
//        label(4);;
        
        
        label(4); 
        final PJPar par20 = new PJPar(1, this);

        (new santachunk.random_wait( _ld3$wait, _ld0$my_seed ){
          public void finalize() {
            par20.decrement();    
          }
        }).schedule();

        setNotReady();
        this.runLabel = 23;
        yield();
        label(23);;
        
        
        
        
        
        label(7);
        if(!_pd$report.claim() ) {
          this.runLabel = 7;
          yield();
        }

        label(5);
        if (_pd$report.isReadyToWrite()) {
          _pd$report.write(this, new Protocol_Reindeer_msg.Protocol_Reindeer_msg_deer_ready(_pd$id));
          this.runLabel = 6;
          yield();
        } else {
          _pd$report.addWriter(this);
          setNotReady();
          this.runLabel = 5;
          yield();
        }

        label(6);
        _pd$report.unclaim();;
        _pd$just_reindeer.sync(this);
        this.runLabel = 8;
        yield();
        label(8);;
        label(11);
        if(!_pd$to_santa.claim() ) {
          this.runLabel = 11;
          yield();
        }

        label(9);
        if (_pd$to_santa.isReadyToWrite()) {
          _pd$to_santa.write(this, _pd$id);
          this.runLabel = 10;
          yield();
        } else {
          _pd$to_santa.addWriter(this);
          setNotReady();
          this.runLabel = 9;
          yield();
        }

        label(10);
        _pd$to_santa.unclaim();;
        _pd$santa_reindeer.sync(this);
        this.runLabel = 12;
        yield();
        label(12);;
        label(15);
        if(!_pd$report.claim() ) {
          this.runLabel = 15;
          yield();
        }

        label(13);
        if (_pd$report.isReadyToWrite()) {
          _pd$report.write(this, new Protocol_Reindeer_msg.Protocol_Reindeer_msg_deliver(_pd$id));
          this.runLabel = 14;
          yield();
        } else {
          _pd$report.addWriter(this);
          setNotReady();
          this.runLabel = 13;
          yield();
        }

        label(14);
        _pd$report.unclaim();;
        _pd$santa_reindeer.sync(this);
        this.runLabel = 16;
        yield();
        label(16);;
        label(19);
        if(!_pd$report.claim() ) {
          this.runLabel = 19;
          yield();
        }

        label(17);
        if (_pd$report.isReadyToWrite()) {
          _pd$report.write(this, new Protocol_Reindeer_msg.Protocol_Reindeer_msg_deer_done(_pd$id));
          this.runLabel = 18;
          yield();
        } else {
          _pd$report.addWriter(this);
          setNotReady();
          this.runLabel = 17;
          yield();
        }

        label(18);
        _pd$report.unclaim();;
        label(22);
        if(!_pd$to_santa.claim() ) {
          this.runLabel = 22;
          yield();
        }

        label(20);
        if (_pd$to_santa.isReadyToWrite()) {
          _pd$to_santa.write(this, _pd$id);
          this.runLabel = 21;
          yield();
        } else {
          _pd$to_santa.addWriter(this);
          setNotReady();
          this.runLabel = 20;
          yield();
        }

        label(21);
        _pd$to_santa.unclaim();;
      }
    }
  }


  public static class elf extends PJProcess {
    int _pd$id;
    long _pd$seed;
    PJChannel<Boolean> _pd$elves_a;
    PJChannel<Boolean> _pd$elves_b;
    PJChannel<Boolean> _pd$santa_elves_a;
    PJChannel<Boolean> _pd$santa_elves_b;
    PJChannel<Integer> _pd$to_santa;
    PJChannel<PJProtocolCase> _pd$report;
    long _ld0$my_seed;
    PJTimer _ld1$tim;
    long _ld2$t;
    long _ld3$wait;

      public elf(int _pd$id, long _pd$seed, PJChannel<Boolean> _pd$elves_a, PJChannel<Boolean> _pd$elves_b, PJChannel<Boolean> _pd$santa_elves_a, PJChannel<Boolean> _pd$santa_elves_b, PJChannel<Integer> _pd$to_santa, PJChannel<PJProtocolCase> _pd$report) {
        this._pd$elves_a = _pd$elves_a;
        this._pd$to_santa = _pd$to_santa;
        this._pd$seed = _pd$seed;
        this._pd$report = _pd$report;
        this._pd$santa_elves_a = _pd$santa_elves_a;
        this._pd$santa_elves_b = _pd$santa_elves_b;
        this._pd$id = _pd$id;
        this._pd$elves_b = _pd$elves_b;
      }

    @Override
    public synchronized void run() {
      switch(this.runLabel) {
        case 0: break;
          case 1: resume(1); break;
          case 2: resume(2); break;
          case 3: resume(3); break;
          case 4: resume(4); break;
          case 5: resume(5); break;
          case 6: resume(6); break;
          case 7: resume(7); break;
          case 8: resume(8); break;
          case 9: resume(9); break;
          case 10: resume(10); break;
          case 11: resume(11); break;
          case 12: resume(12); break;
          case 13: resume(13); break;
          case 14: resume(14); break;
          case 15: resume(15); break;
          case 16: resume(16); break;
          case 17: resume(17); break;
          case 18: resume(18); break;
          case 19: resume(19); break;
          case 20: resume(20); break;
          case 21: resume(21); break;
          case 22: resume(22); break;
          case 23: resume(23); break;
          case 24: resume(24); break;
          case 25: resume(25); break;

          case 26: resume(26); break;
      }
      _ld0$my_seed = _pd$seed;;
      _ld1$tim = new PJTimer();;
      _ld3$wait = WORKING_TIME;;
      while( getTrue() ) {
        label(3);
        if(!_pd$report.claim() ) {
          this.runLabel = 3;
          yield();
        }

        label(1);
        if (_pd$report.isReadyToWrite()) {
          _pd$report.write(this, new Protocol_Elf_msg.Protocol_Elf_msg_working(_pd$id));
          this.runLabel = 2;
          yield();
        } else {
          _pd$report.addWriter(this);
          setNotReady();
          this.runLabel = 1;
          yield();
        }

        label(2);
        _pd$report.unclaim();;
        
        
        
        
//        _ld2$t = _ld1$tim.read();;
//        _ld1$tim = new Timer(this, 1000);
//        try {
//          _ld1$tim.start();
//          setNotReady();
//          this.runLabel = 4;
//          yield();
//        } catch (InterruptedException e) {
//          System.out.println("Timer Interrupted Exception!");
//        }
//        label(4);;
        
        label(4); 
        final PJPar par20 = new PJPar(1, this);

        (new santachunk.random_wait( _ld3$wait, _ld0$my_seed ){
          public void finalize() {
            par20.decrement();    
          }
        }).schedule();

        setNotReady();
        this.runLabel = 26;
        yield();
        label(26);;
        
        
        
        label(7);
        if(!_pd$report.claim() ) {
          this.runLabel = 7;
          yield();
        }

        label(5);
        if (_pd$report.isReadyToWrite()) {
          _pd$report.write(this, new Protocol_Elf_msg.Protocol_Elf_msg_elf_ready(_pd$id));
          this.runLabel = 6;
          yield();
        } else {
          _pd$report.addWriter(this);
          setNotReady();
          this.runLabel = 5;
          yield();
        }

        label(6);
        _pd$report.unclaim();;
        label(8); 
        final PJPar par1 = new PJPar(1, this);

        (new santachunk.syncronize( _pd$elves_a, _pd$elves_b ){
          public void finalize() {
            par1.decrement();    
          }
        }).schedule();

        setNotReady();
        this.runLabel = 9;
        yield();
        label(9);;
        label(12);
        if(!_pd$to_santa.claim() ) {
          this.runLabel = 12;
          yield();
        }

        label(10);
        if (_pd$to_santa.isReadyToWrite()) {
          _pd$to_santa.write(this, _pd$id);
          this.runLabel = 11;
          yield();
        } else {
          _pd$to_santa.addWriter(this);
          setNotReady();
          this.runLabel = 10;
          yield();
        }

        label(11);
        _pd$to_santa.unclaim();;
        label(13);  
        final PJPar par2 = new PJPar(1, this);

        (new santachunk.syncronize( _pd$santa_elves_a, _pd$santa_elves_b ){
          public void finalize() {
            par2.decrement();    
          }
        }).schedule();

        setNotReady();
        this.runLabel = 14;
        yield();
        label(14);;
        label(17);
        if(!_pd$report.claim() ) {
          this.runLabel = 17;
          yield();
        }

        label(15);
        if (_pd$report.isReadyToWrite()) {
          _pd$report.write(this, new Protocol_Elf_msg.Protocol_Elf_msg_consult(_pd$id));
          this.runLabel = 16;
          yield();
        } else {
          _pd$report.addWriter(this);
          setNotReady();
          this.runLabel = 15;
          yield();
        }

        label(16);
        _pd$report.unclaim();;
        label(18);  
        final PJPar par3 = new PJPar(1, this);

        (new santachunk.syncronize( _pd$santa_elves_a, _pd$santa_elves_b ){
          public void finalize() {
            par3.decrement();    
          }
        }).schedule();

        setNotReady();
        this.runLabel = 19;
        yield();
        label(19);;
        label(22);
        if(!_pd$report.claim() ) {
          this.runLabel = 22;
          yield();
        }

        label(20);
        if (_pd$report.isReadyToWrite()) {
          _pd$report.write(this, new Protocol_Elf_msg.Protocol_Elf_msg_elf_done(_pd$id));
          this.runLabel = 21;
          yield();
        } else {
          _pd$report.addWriter(this);
          setNotReady();
          this.runLabel = 20;
          yield();
        }

        label(21);
        _pd$report.unclaim();;
        label(25);
        if(!_pd$to_santa.claim() ) {
          this.runLabel = 25;
          yield();
        }

        label(23);
        if (_pd$to_santa.isReadyToWrite()) {
          _pd$to_santa.write(this, _pd$id);
          this.runLabel = 24;
          yield();
        } else {
          _pd$to_santa.addWriter(this);
          setNotReady();
          this.runLabel = 23;
          yield();
        }

        label(24);
        _pd$to_santa.unclaim();;
      }
    }
  }


  public static class santa extends PJProcess {
    long _pd$seed;
    PJChannel<Boolean> _pd$knock;
    PJChannel<Integer> _pd$from_reindeer;
    PJChannel<Integer> _pd$from_elf;
    PJBarrier _pd$santa_reindeer;
    PJChannel<Boolean> _pd$santa_elves_a;
    PJChannel<Boolean> _pd$santa_elves_b;
    PJChannel<PJProtocolCase> _pd$report;
    long _ld0$my_seed;
    PJTimer _ld1$tim;
    long _ld2$t;
    long _ld3$wait;
    int _ld4$id;
    boolean _ld5$any;
    int _ld6$i;
    int _ld7$i;
    int _ld8$i;
    int _ld9$i;
    PJAlt alt;
    int chosen;

      public santa(long _pd$seed, PJChannel<Boolean> _pd$knock, PJChannel<Integer> _pd$from_reindeer, PJChannel<Integer> _pd$from_elf, PJBarrier _pd$santa_reindeer, PJChannel<Boolean> _pd$santa_elves_a, PJChannel<Boolean> _pd$santa_elves_b, PJChannel<PJProtocolCase> _pd$report) {
        this._pd$from_reindeer = _pd$from_reindeer;
        this._pd$knock = _pd$knock;
        this._pd$santa_reindeer = _pd$santa_reindeer;
        this._pd$seed = _pd$seed;
        this._pd$report = _pd$report;
        this._pd$santa_elves_a = _pd$santa_elves_a;
        this._pd$santa_elves_b = _pd$santa_elves_b;
        this._pd$from_elf = _pd$from_elf;
      }

    @Override
    public synchronized void run() {
      switch(this.runLabel) {
        case 0: break;
          case 1: resume(1); break;
          case 2: resume(2); break;
          case 3: resume(3); break;
          case 4: resume(4); break;
          case 5: resume(5); break;
          case 6: resume(6); break;
          case 7: resume(7); break;
          case 8: resume(8); break;
          case 9: resume(9); break;
          case 10: resume(10); break;
          case 11: resume(11); break;
          case 12: resume(12); break;
          case 13: resume(13); break;
          case 14: resume(14); break;
          case 15: resume(15); break;
          case 16: resume(16); break;
          case 17: resume(17); break;
          case 18: resume(18); break;
          case 19: resume(19); break;
          case 20: resume(20); break;
          case 21: resume(21); break;
          case 22: resume(22); break;
          case 23: resume(23); break;
          case 24: resume(24); break;
          case 25: resume(25); break;
          case 26: resume(26); break;
          case 27: resume(27); break;
          case 28: resume(28); break;
          case 29: resume(29); break;
          case 30: resume(30); break;
          case 31: resume(31); break;
          case 32: resume(32); break;
          case 33: resume(33); break;
          case 34: resume(34); break;
          case 35: resume(35); break;
          case 36: resume(36); break;
          case 37: resume(37); break;
          case 38: resume(38); break;
          case 39: resume(39); break;
          case 40: resume(40); break;
          case 41: resume(41); break;
          case 42: resume(42); break;

          case 43: resume(43); break;
          case 44: resume(44); break;
          case 45: resume(45); break;
          case 46: resume(46); break;

          case 47: resume(47); break;
          case 48: resume(48); break;
          case 49: resume(49); break;
          case 50: resume(50); break;
      }
      _ld0$my_seed = _pd$seed;;
      _ld1$tim = new PJTimer();;
      while( getTrue() ) {
        alt = new PJAlt(2, this); 
        Object[] guards = {_pd$from_reindeer, _pd$knock};
        boolean[] boolGuards = {true, true};  

        boolean bRet = alt.setGuards(boolGuards, guards);

        if (!bRet) {
          System.out.println("RuntimeException: One of the boolean guards needs to be true!!");
          System.exit(1);
        }

        label(41);  

        chosen = alt.getReadyGuardIndex();
        switch(chosen) {
          case 0: 
              _ld4$id = _pd$from_reindeer.read(this);
              ;
              label(5);
              if(!_pd$report.claim() ) {
                this.runLabel = 5;
                yield();
              }

              label(1);
              if (_pd$report.isReadyToWrite()) {
                _pd$report.write(this, new Protocol_Santa_msg.Protocol_Santa_msg_reindeer_ready());
                this.runLabel = 2;
                yield();
              } else {
                _pd$report.addWriter(this);
                setNotReady();
                this.runLabel = 1;
                yield();
              }

              label(2);;

              label(3);
              if (_pd$report.isReadyToWrite()) {
                _pd$report.write(this, new Protocol_Santa_msg.Protocol_Santa_msg_harness(_ld4$id));
                this.runLabel = 4;
                yield();
              } else {
                _pd$report.addWriter(this);
                setNotReady();
                this.runLabel = 3;
                yield();
              }

              label(4);
              _pd$report.unclaim();;
              for(_ld6$i = 0; (_ld6$i < (G_REINDEER - 1)); _ld6$i++){
//                _ld4$id = _pd$from_reindeer.read(this);
               
              label(47);
            if(_pd$from_reindeer.isReadyToRead(this)) {
             _ld4$id = _pd$from_reindeer.read(this);
              this.runLabel = 48;
              yield();
            } else {
              setNotReady();
              _pd$from_reindeer.addReader(this);
              this.runLabel = 47;
              yield();
            }
            label(48);;
                
                
                
                
                label(8);
                if(!_pd$report.claim() ) {
                  this.runLabel = 8;
                  yield();
                }

                label(6);
                if (_pd$report.isReadyToWrite()) {
                  _pd$report.write(this, new Protocol_Santa_msg.Protocol_Santa_msg_harness(_ld4$id));
                  this.runLabel = 7;
                  yield();
                } else {
                  _pd$report.addWriter(this);
                  setNotReady();
                  this.runLabel = 6;
                  yield();
                }

                label(7);
                _pd$report.unclaim();
              };
              label(11);
              if(!_pd$report.claim() ) {
                this.runLabel = 11;
                yield();
              }

              label(9);
              if (_pd$report.isReadyToWrite()) {
                _pd$report.write(this, new Protocol_Santa_msg.Protocol_Santa_msg_mush_mush());
                this.runLabel = 10;
                yield();
              } else {
                _pd$report.addWriter(this);
                setNotReady();
                this.runLabel = 9;
                yield();
              }

              label(10);
              _pd$report.unclaim();;
              _pd$santa_reindeer.sync(this);
              this.runLabel = 12;
              yield();
              label(12);;
              _ld2$t = _ld1$tim.read();;
              _ld1$tim = new PJTimer(this, 1000);
              ;
              label(16);
              if(!_pd$report.claim() ) {
                this.runLabel = 16;
                yield();
              }

              label(14);
              if (_pd$report.isReadyToWrite()) {
                _pd$report.write(this, new Protocol_Santa_msg.Protocol_Santa_msg_woah());
                this.runLabel = 15;
                yield();
              } else {
                _pd$report.addWriter(this);
                setNotReady();
                this.runLabel = 14;
                yield();
              }

              label(15);
              _pd$report.unclaim();;
              _pd$santa_reindeer.sync(this);
              this.runLabel = 17;
              yield();
              label(17);;
              for(_ld7$i = 0; (_ld7$i < G_REINDEER); _ld7$i++){
//                _ld4$id = _pd$from_reindeer.readPreRendezvous(this);
               
                label(49);
            if(_pd$from_reindeer.isReadyToRead(this)) {
             _ld4$id = _pd$from_reindeer.read(this);
              this.runLabel = 50;
              yield();
            } else {
              setNotReady();
              _pd$from_reindeer.addReader(this);
              this.runLabel = 49;
              yield();
            }
            label(50);;
                
                
                label(20);
                if(!_pd$report.claim() ) {
                  this.runLabel = 20;
                  yield();
                }

                label(18);
                if (_pd$report.isReadyToWrite()) {
                  _pd$report.write(this, new Protocol_Santa_msg.Protocol_Santa_msg_unharness(_ld4$id));
                  this.runLabel = 19;
                  yield();
                } else {
                  _pd$report.addWriter(this);
                  setNotReady();
                  this.runLabel = 18;
                  yield();
                }

                label(19);
                _pd$report.unclaim();
//                _pd$from_reindeer.readPostRendezvous(this);

              };
            break;
          case 1: 
              _ld5$any = _pd$knock.read(this);
              ;
              label(23);
              if(!_pd$report.claim() ) {
                this.runLabel = 23;
                yield();
              }

              label(21);
              if (_pd$report.isReadyToWrite()) {
                _pd$report.write(this, new Protocol_Santa_msg.Protocol_Santa_msg_elves_ready());
                this.runLabel = 22;
                yield();
              } else {
                _pd$report.addWriter(this);
                setNotReady();
                this.runLabel = 21;
                yield();
              }

              label(22);
              _pd$report.unclaim();;
              for(_ld8$i = 0; (_ld8$i < G_ELVES); _ld8$i++){
//                _ld4$id = _pd$from_elf.read(this);
                
               label(43);
            if(_pd$from_elf.isReadyToRead(this)) {
              _ld4$id = _pd$from_elf.read(this);
              this.runLabel = 44;
              yield();
            } else {
              setNotReady();
              _pd$from_elf.addReader(this);
              this.runLabel = 43;
              yield();
            }
            label(44);
                
                
                label(26);
                if(!_pd$report.claim() ) {
                  this.runLabel = 26;
                  yield();
                }

                label(24);
                if (_pd$report.isReadyToWrite()) {
                  _pd$report.write(this, new Protocol_Santa_msg.Protocol_Santa_msg_greet(_ld4$id));
                  this.runLabel = 25;
                  yield();
                } else {
                  _pd$report.addWriter(this);
                  setNotReady();
                  this.runLabel = 24;
                  yield();
                }

                label(25);
                _pd$report.unclaim();
              };
              label(29);
              if(!_pd$report.claim() ) {
                this.runLabel = 29;
                yield();
              }

              label(27);
              if (_pd$report.isReadyToWrite()) {
                _pd$report.write(this, new Protocol_Santa_msg.Protocol_Santa_msg_consulting());
                this.runLabel = 28;
                yield();
              } else {
                _pd$report.addWriter(this);
                setNotReady();
                this.runLabel = 27;
                yield();
              }

              label(28);
              _pd$report.unclaim();;
              label(30);  
              final PJPar par1 = new PJPar(1, this);

              (new santachunk.syncronize( _pd$santa_elves_a, _pd$santa_elves_b ){
                public void finalize() {
                  par1.decrement();    
                }
              }).schedule();

              setNotReady();
              this.runLabel = 31;
              yield();
              label(31);;
              _ld2$t = _ld1$tim.read();;
              _ld1$tim = new PJTimer(this, 1000);
              ;
              label(35);
              if(!_pd$report.claim() ) {
                this.runLabel = 35;
                yield();
              }

              label(33);
              if (_pd$report.isReadyToWrite()) {
                _pd$report.write(this, new Protocol_Santa_msg.Protocol_Santa_msg_santa_done());
                this.runLabel = 34;
                yield();
              } else {
                _pd$report.addWriter(this);
                setNotReady();
                this.runLabel = 33;
                yield();
              }

              label(34);
              _pd$report.unclaim();;
              label(36);  
              final PJPar par2 = new PJPar(1, this);

              (new santachunk.syncronize( _pd$santa_elves_a, _pd$santa_elves_b ){
                public void finalize() {
                  par2.decrement();    
                }
              }).schedule();

              setNotReady();
              this.runLabel = 37;
              yield();
              label(37);;
              for(_ld9$i = 0; (_ld9$i < G_ELVES); _ld9$i++){
//                _ld4$id = _pd$from_elf.readPreRendezvous(this);
                
              label(45);
            if(_pd$from_elf.isReadyToRead(this)) {
              _ld4$id = _pd$from_elf.read(this);
              this.runLabel = 46;
              yield();
            } else {
              setNotReady();
              _pd$from_elf.addReader(this);
              this.runLabel = 45;
              yield();
            }
            label(46);
            
                label(40);
                if(!_pd$report.claim() ) {
                  this.runLabel = 40;
                  yield();
                }

                label(38);
                if (_pd$report.isReadyToWrite()) {
                  _pd$report.write(this, new Protocol_Santa_msg.Protocol_Santa_msg_goodbye(_ld4$id));
                  this.runLabel = 39;
                  yield();
                } else {
                  _pd$report.addWriter(this);
                  setNotReady();
                  this.runLabel = 38;
                  yield();
                }

                label(39);
                _pd$report.unclaim();
//                _pd$from_elf.readPostRendezvous(this);

              };
            break;
        }


        if (chosen == -1) {
          this.runLabel = 41;
          yield();  
        } else {
          this.runLabel = 42;
          yield();
        }

        label(42);;
      }
    }
  }


  public static class main extends PJProcess {
    String[] _pd$args;
    PJTimer _ld0$tim;
    long _ld1$seed;
    PJBarrier _ld2$just_reindeer;
    PJBarrier _ld3$santa_reindeer;
    PJMany2OneChannel<Boolean> _ld4$elves_a;
    PJMany2OneChannel<Boolean> _ld5$elves_b;
    PJOne2OneChannel<Boolean> _ld6$knock;
    PJMany2OneChannel<Boolean> _ld7$santa_elves_a;
    PJMany2OneChannel<Boolean> _ld8$santa_elves_b;
    PJMany2OneChannel<Integer> _ld9$reindeer_santa;
    PJMany2OneChannel<Integer> _ld10$elf_santa;
    PJMany2OneChannel<PJProtocolCase> _ld11$report;
    int _ld12$i;
    int _ld13$i;

      public main(String[] _pd$args) {
        this._pd$args = _pd$args;
      }

    @Override
    public synchronized void run() {
      switch(this.runLabel) {
        case 0: break;
          case 1: resume(1); break;
          case 2: resume(2); break;
      }
      _ld0$tim = new PJTimer();;
      _ld1$seed = _ld0$tim.read();;
      _ld1$seed = ((_ld1$seed >> 2) + 42);
      _ld2$just_reindeer = new PJBarrier();;
      _ld3$santa_reindeer = new PJBarrier();;
      _ld4$elves_a = new PJMany2OneChannel<Boolean>();;
      _ld5$elves_b = new PJMany2OneChannel<Boolean>();;
      _ld6$knock = new PJOne2OneChannel<Boolean>();;
      _ld7$santa_elves_a = new PJMany2OneChannel<Boolean>();;
      _ld8$santa_elves_b = new PJMany2OneChannel<Boolean>();;
      _ld9$reindeer_santa = new PJMany2OneChannel<Integer>();;
      _ld10$elf_santa = new PJMany2OneChannel<Integer>();;
      _ld11$report = new PJMany2OneChannel<PJProtocolCase>();;
      label(1); 
      final PJPar par1 = new PJPar(5, this);

      new PJProcess(){
        @Override
        public synchronized void run() {
          switch(this.runLabel) {
            case 0: break;
              case 3: resume(3); break;
              case 4: resume(4); break;
          }
          label(3); 
          final PJPar par2 = new PJPar(2, this);
          _ld3$santa_reindeer.enroll(2);

          (new santachunk.santa( (_ld1$seed + (N_REINDEER + N_ELVES)), _ld6$knock, _ld9$reindeer_santa, _ld10$elf_santa, _ld3$santa_reindeer, _ld7$santa_elves_a, _ld8$santa_elves_b, _ld11$report ){
            public void finalize() {
              par2.decrement();    
            _ld3$santa_reindeer.resign();
            }
          }).schedule();
          new PJProcess(){
            @Override
            public synchronized void run() {
              switch(this.runLabel) {
                case 0: break;
                  case 5: resume(5); break;
                  case 6: resume(6); break;
              }
              label(5); 
              final PJPar parfor3 = new PJPar(-1, this);
              int cnt = 0;  
              List<PJProcess> pp = new LinkedList<PJProcess>(); 

//              _ld2$just_reindeer.enroll(9);
//              _ld3$santa_reindeer.enroll(9);

              for(_ld12$i = 0; (_ld12$i < N_REINDEER); _ld12$i++){
                cnt++;
                pp.add(
//                  new Process(){
//                    @Override
//                    public synchronized void run() {
                      (new santachunk.reindeer( _ld12$i, (_ld1$seed + _ld12$i), _ld2$just_reindeer, _ld3$santa_reindeer, _ld9$reindeer_santa, _ld11$report ){
                        public void finalize() {
                          parfor3.decrement();    
                          _ld2$just_reindeer.resign();
                          _ld3$santa_reindeer.resign();
                        }
                      })
//                          terminate();
//                    }

//                    @Override
//                    public void finalize() {
//                      parfor3.decrement();  
//                      _ld2$just_reindeer.resign();
//                      _ld3$santa_reindeer.resign();
//                    }
//                  }
                );
              }
              //set the process count 
              parfor3.setProcessCount(cnt);
              _ld2$just_reindeer.enroll(cnt);
              _ld3$santa_reindeer.enroll(cnt);

              //schedule all the processes
              for(PJProcess p : pp) {
                p.schedule();
              }
              setNotReady();
              this.runLabel = 6;
              yield();
              label(6);;
              terminate();
            }

            @Override
            public void finalize() {
              par2.decrement(); 
              _ld3$santa_reindeer.resign();
            }
          }.schedule();

          setNotReady();
          this.runLabel = 4;
          yield();
          label(4);;
          terminate();
        }

        @Override
        public void finalize() {
          par1.decrement(); 
        }
      }.schedule();
      new PJProcess(){
        @Override
        public synchronized void run() {
          switch(this.runLabel) {
            case 0: break;
              case 7: resume(7); break;
              case 8: resume(8); break;
          }
          label(7); 
          final PJPar parfor4 = new PJPar(-1, this);
          int cnt = 0;  
          List<PJProcess> pp = new LinkedList<PJProcess>(); 

          for(_ld13$i = 0; (_ld13$i < N_ELVES); _ld13$i++){
            cnt++;
            pp.add(
//              new Process(){
//                @Override
//                public synchronized void run() {
                  (new santachunk.elf( _ld13$i, (N_REINDEER + (_ld1$seed + _ld13$i)), _ld4$elves_a, _ld5$elves_b, _ld7$santa_elves_a, _ld8$santa_elves_b, _ld10$elf_santa, _ld11$report ){
                    public void finalize() {
                      parfor4.decrement();    
                    }
                  })
//                      terminate();
//                }

//                @Override
//                public void finalize() {
//                  parfor4.decrement();  
//                }
//              }
            );
          }
          //set the process count 
          parfor4.setProcessCount(cnt);

          //schedule all the processes
          for(PJProcess p : pp) {
            p.schedule();
          }
          setNotReady();
          this.runLabel = 8;
          yield();
          label(8);;
          terminate();
        }

        @Override
        public void finalize() {
          par1.decrement(); 
        }
      }.schedule();
      (new santachunk.display( _ld11$report ){
        public void finalize() {
          par1.decrement();    
        }
      }).schedule();
      (new santachunk.p_barrier_knock( G_ELVES, _ld4$elves_a, _ld5$elves_b, _ld6$knock ){
        public void finalize() {
          par1.decrement();    
        }
      }).schedule();
      (new santachunk.p_barrier( (G_ELVES + 1), _ld7$santa_elves_a, _ld8$santa_elves_b ){
        public void finalize() {
          par1.decrement();    
        }
      }).schedule();

      setNotReady();
      this.runLabel = 2;
      yield();
      label(2);
      terminate();
    }
  }
  
  public static class random_wait extends PJProcess {
	    long _pd$max_wait;
	    long _pd$seed;
	    PJTimer _ld0$t;
	    long _ld1$wait;

	      public random_wait(long _pd$max_wait, long _pd$seed) {
	        this._pd$max_wait = _pd$max_wait;
//	        System.out.println("max wait=" + _pd$max_wait);
	        this._pd$seed = _pd$seed;
	      }

	    @Override
	    public synchronized void run() {
	      switch(this.runLabel) {
	        case 0: break;
	          case 1: resume(1); break;
	      }
	      _ld0$t = new PJTimer();;
//	      System.out.println("_pd$_pd$max_wait=" + _pd$max_wait);
	      std.Random.initRandom( _pd$max_wait );
	      _ld1$wait=std.Random.longRandom();
	      _ld1$wait %= 10000;
//	      System.out.println("_ld1$wait=" + _ld1$wait);
	      _ld0$t = new PJTimer(this, _ld1$wait);
	      try {
	        _ld0$t.start();
	        setNotReady();
	        this.runLabel = 1;
	        yield();
	      } catch (InterruptedException e) {
	        System.out.println("Timer Interrupted Exception!");
	      }
	      label(1);
	      terminate();
	    }
	  }

  public static void main(String[] args) {
    Scheduler scheduler = new Scheduler();

    PJProcess.scheduler = scheduler;

    System.out.println("Added _main process to scheduler...");

    (new santachunk.main(args)).schedule();

    System.out.println("Starting the scheduler...");
    PJProcess.scheduler.start();

    System.out.println("Scheduler thread.start() done.");
  }
}