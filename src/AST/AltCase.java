package AST;
import Utilities.Visitor;
import Utilities.Error;

public class AltCase extends AST {
  /*AltCases known which number they are in the list as they will
    be assigned one in the case statement they will eventually go into.*/
  private int caseNumber = -1;
  //TODO I actually don't know if we need this...
  /*If this is a Timeout Stat this string will hold the name of what the
    the temp Time should be called.*/
  private String tempTimerName = null;

    /* expr can be null */
    public AltCase(Expression expr, Guard guard, Statement stat) {
      super(guard);
      nchildren = 3;
      children = new AST[] { expr, guard, stat };
    }


  public Expression precondition() { return (Expression)children[0]; }
  public Guard guard()     { return (Guard)children[1]; }
  public Statement  stat() { return (Statement)children[2];  }

  public <S extends Object> S visit(Visitor<S> v) {
    return v.visitAltCase(this);
  }

  public int getCaseNumber(){
    if(caseNumber == -1)
      Error.error("AltCase Error: The caseNumber for this AltCase was never set!");

    return caseNumber;
  }

  public void setCaseNumber(int n){
    caseNumber = n;

    return;
  }

  public String getTempTimerName(){
    if(tempTimerName == null)
      Error.error("AltCase Error: The tempTimerName for this AltCase was never set!");

    return tempTimerName;
  }

  public void setTempTimerName(String str){
    tempTimerName = str;

    return;
  }

}
