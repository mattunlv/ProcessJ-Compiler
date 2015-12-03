package Utilities;


/**
 * Simple immutable struct containing a tag and a value.
 */
public class ValueAndTag{
  private int value = -1;
  private String tag = null;

  public ValueAndTag(int value, String tag){
    this.value = value;
    this.tag = tag;

    return;
  }

  public int getValue(){  return value; }

  public String getTag(){ return tag; }
}
