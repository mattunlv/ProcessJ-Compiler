public class Sample {

  public void label(int i){};
  public void resume(int i){};

  public void foo(int runLabel){
    switch(runLabel) {
      case 0: resume(0);
      case 1: resume(1);
    }
    label(0);
    System.out.println("Started at label(0)");
    label(1);
    System.out.println("Started at label(1)");
  }


}
