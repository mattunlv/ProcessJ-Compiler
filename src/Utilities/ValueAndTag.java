package Utilities;


/**
 * Simple immutable struct containing a tag and a value.
 */
public class ValueAndTag{
  private int value = -1;
  private String tag = null;

  /*Compiler optimization: if this was a invocation we keep track of that so later we only
    create memory for the max invocation called in the top level of the procedure. */
  private boolean isInvocation = false;

  //Main constructor for all purposes.
  public ValueAndTag(int value, String tag){
    this.value = value;
    this.tag = tag;

    return;
  }

  /**
   * Extra constructor to be used if use wants to specify if this is an invocation call.
   * This is false by default.
   */
  public ValueAndTag(int value, String tag, boolean isInvocation){
    this.value = value;
    this.tag = tag;
    this.isInvocation = isInvocation;

    return;
  }

  public int getValue(){  return value; }

  public String getTag(){ return tag; }

  public boolean isInvocation(){ return isInvocation; }
}
