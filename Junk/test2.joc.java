import org.jcsp.lang.*;
public class bar1 implements CSProcess {
  Barrier bar;
  public bar1(Barrier bar) {
    this.bar = bar;
  }
  public void run() {
  }
}
public class fool implements CSProcess {
  ChannelInput in1;
  ChannelInput in2;
  ChannelOutput out;
  public fool(ChannelInput in1, ChannelInput in2, ChannelOutput out) {
    this.in1 = in1;
    this.in2 = in2;
    this.out = out;
  }
  public void run() {
    int y;
  }
}
mobileProcpublic class foobar implements CSProcess {
  public foobar() {
  }
  public void run() {
  }
}
public class bar implements CSProcess {
  int[] a;
  ChannelOutput santa;
  ChannelInput santa2;
  One2AnyChannel santa3;
  public bar(int[] a, ChannelOutput santa, ChannelInput santa2, One2AnyChannel santa3) {
    this.a = a;
    this.santa = santa;
    this.santa2 = santa2;
    this.santa3 = santa3;
  }
  public void run() {
    santa.write(2)int x = santa2.read()int y = (santa3).in().read();
  }
}
public class gub implements CSProcess {
  public gub() {
  }
  public One2OneChannel[] run() {
  }
}
public class baz1 implements CSProcess {
  public baz1() {
  }
  public ChannelInput[] run() {
  }
}
class Q {
  int a;
  double b;
  public Q(intn, doublen) {
    this.a = a;
    this.b = b;
  }
  public Q __copy__() {
    return new Q(a, b);
  }
}
class A {
  public enum __Type__ { one, two }
  public __Type__ __type__;
  public static class one extends A {
    int a;
    int b;
    public one(int a, int b) {
      this.a = a;
      this.b = b;
    }
    public one __copy__() {
      return new one(a, b);
    }
  }

  public static class two extends A {
    double b;
    double c;
    public two(double b, double c) {
      this.b = b;
      this.c = c;
    }
    public two __copy__() {
      return new two(b, c);
    }
  }

}
class B {
  public enum __Type__ { three }
  public __Type__ __type__;
  public static class three extends B {
    double b;
    double c;
    public three(double b, double c) {
      this.b = b;
      this.c = c;
    }
    public three __copy__() {
      return new three(b, c);
    }
  }

}
CDpublic class foo implements CSProcess {
  public foo() {
  }
  public void run() {
    One2OneChannel c1;
    One2OneChannel c11;
    One2AnyChannel c2;
    Any2OneChannel c5;
    Any2OneChannel c3;
    ChannelOutput c4;
    int[] myArray = {4, 5, 6, 7};
    int[] qwerty = {46, 45, 45};
    ChannelInput myChannel;
    int myChannel1;
    One2AnyChannel blah;
    int frqwe = 99999;
    One2AnyChannel[] c;
    class A {
      public enum __Type__ { one, two }
      public __Type__ __type__;
      public static class one extends A {
        int a;
        int b;
        public one(int a, int b) {
          this.a = a;
          this.b = b;
        }
        public one __copy__() {
          return new one(a, b);
        }
      }

      public static class two extends A {
        double b;
        double c;
        public two(double b, double c) {
          this.b = b;
          this.c = c;
        }
        public two __copy__() {
          return new two(b, c);
        }
      }

    }
     a = myC.read();
    int iv = 5567;
    if((1 == 1)) {
    } else {
    }
    ;
    ;
  }
}
