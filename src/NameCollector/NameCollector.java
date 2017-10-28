package NameCollector;

import AST.*;
import Utilities.*;
import Utilities.Error;
import java.io.*;
import java.util.*;
//========================================================================================
/**
 * This Class implementes a visitor that will take in expressions and collect only the
 * unique NameExpr, this is needed by our ParBlock and ParFor. Both of these can have
 arbitrary statements in it. All these statementes must be wrapped in functions, hence all
 * the NamesExpr used by these statements need to be passed into our function through the
 * parameters. Notice we don't really use the <Object> type... but it's required.
 */
public class NameCollector extends Visitor<Object>{
  /**
   * List passed by user that will be filled everytime we have a unique NameExpr
   * inside our Statement.
   */
  private LinkedList<NameExpr> myNames = null;
  private Set<String> namesSeen = null;
//========================================================================================
  /**
   * Constructor for our visitor, given a LinkedList  it will fill this with the NameExpr
   * it runs across without repeats.
   @param myNames: Java LinkedList to fill with NameExpr's.
   */
  public NameCollector(LinkedList<NameExpr> myNames){
    Log.log("   Created NameCollector for statement");
    if(myNames == null)
      Error.error("Null list passed to NameCollector Visitor!");

    this.myNames = myNames;
    this.namesSeen = new HashSet();
  }
//========================================================================================
  /**
   * We are only interested in name expressions. When we find one, add it to our
   * myNames set this way the we can extract the needed information from the
   * myDeclr.
   */
  public Object visitNameExpr(NameExpr ne){
    String name = ne.toString();
    String logString = "   Added NameExpr for: %s statement being visited.";
    Log.log( String.format(logString, name) );

    //Add this
    if(namesSeen.contains(name) == false){
      namesSeen.add(name);
      myNames.add(ne);
    }

    return null;
  }
//========================================================================================
}
