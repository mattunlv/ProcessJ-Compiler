package NameCollector;

import AST.*;
import Utilities.*;
import Utilities.Error;
import java.io.*;
import java.util.*;
//========================================================================================
/**
 * This Class implementes a visitor that will take in expressions and collect only the
 * NameExpr, this is needed by our ParBlock. The ParBlock can have arbitrary statements
 * in it. All these statementes must be wrapped in functions, hence all the NamesExpr
 * used by these statements need to be passed into our function through the parameters.
 * Notice we don't really use the <Object> type... but it's required.
 */
public class NameCollector extends Visitor<Object> {
  /**
   * Set passed by user that will be filled everytime we have a name expression
   * called by our visitor.
   */
  Set<NameExpr> myNames = null;
//========================================================================================
  /**
   * Constructor for our visitor, given a set it will fill this set with the NameExpr
   * it runs across.
   @param myNames: Java set to fill with NameExpr's.
   */
  public NameCollector(Set<NameExpr> myNames){
    Log.log("Created NameCollector for statement");

    if(myNames == null)
      Error.error("Null set passed to NameCollector Visitor!");
    this.myNames = myNames;
  }
//========================================================================================
  /**
   * We are only interested in name expressions. When we find one, add it to our
   * myNames set this way the we can extract the needed information from the
   * myDeclr.
   */
  public Object visitNameExpr(NameExpr ne) {
    String logString = "Added NameExpr for: %s statement being visited.";
    Log.log( String.format(logString, ne.toString()) );
    myNames.add(ne);

    return null;
  }
//========================================================================================
}
