package Utilities;

import java.util.*;
import Utilities.*;

/**
 * This class wraps the idea of a Hashtable of string keys with a linked-list as the
 * value. This list is of ValueAndTags used to hold useful information to the user for the
 * compiler.
 * See AllocateStackSize.java for example.
 */
public class TableList{
    private Hashtable<String, LinkedList<ValueAndTag>> table;

  //========================================================================================
  /**
   * Initializes the class by creating it's hashtable. From here the user is free to add
   * entries and get the entries like a normal hashtable.
   */
  public TableList(){
    table = new Hashtable();
  }
  //========================================================================================
  /**
   * Adds the node to the table at key: table[key] += node. If the table has no entry for
   * key the list will be created and node will be the first entry.
   * @param key: Key to add node at in table.
   * @param node: Node to add.
   * @param return: void.
   */
  public void addNode(String key, ValueAndTag node){
    LinkedList<ValueAndTag> list;

    if(table.containsKey(key) == true){
      list = table.get(key);
      list.add(node);
    }
    else{
      list = new LinkedList();
      list.add(node);
      table.put(key, list);
    }

    return;
  }
  //========================================================================================
  /**
   * Print all the entries on the table and their perpective lists.
   */
  public String toString(){
    StringBuilder sb = new StringBuilder();

    sb.append("=================================================================\n");
    sb.append("TableList HashTable Entries:\n");
    for(String key : table.keySet() ){
      sb.append("=================================================================\n");
      sb.append( String.format("Table entry for: %s\n", key) );
      LinkedList<ValueAndTag> list = table.get(key);
      int j = 0;

      //Iterate over the nodes in our list.
      for(ValueAndTag node : list){
        String msg = String.format("Node[%d]: %d    //%s\n", j, node.getValue(),
                                   node.getTag());
        sb.append(msg);
        j++;
      }
      sb.append("TOTAL: " + sumEntries(key) + "\n");
    }

    sb.append("=================================================================\n");
    return sb.toString();
  }
  //========================================================================================
  /**
   * For a given key add all the entries of that key's list together.
   * @param key: key's value to add.
   * @param int: sum at that key.
   */
  public int sumEntries(String key){
    if(table.containsKey(key) == false)
      Error.error("Error! This TableList does not contain the key: " + key);

    LinkedList<ValueAndTag> list = table.get(key);
    int sum = 0;

    for(ValueAndTag node : list)
      sum += node.getValue();

    return sum;
  }
  //========================================================================================
  /**
   * Extra "sum entries" function that only keeps the procedure invocation with the highest
   * number. Only should be called when summing up memory size procedure declarations.
   * For a given key add all the entries of that key's list together.
   * @param key: key's value to add.
   * @param int: sum at that key.
   */
  public int sumEntriesMaxInvocationOnly(String key){
    if(table.containsKey(key) == false)
      Error.error("Error! This TableList does not contain the key: " + key);

    LinkedList<ValueAndTag> list = table.get(key);
    int sum = 0;

    //Handle non-invocation cases:
    for(ValueAndTag node : list)
      if(node.isInvocation() == false)
	sum += node.getValue();

    //Hanle invocation case:
    int max = 0;
    for(ValueAndTag node : list)
      if(node.isInvocation() == true){
	int currentValue = node.getValue();
	if(max < currentValue)
	  max = currentValue;
      }
    
    //Add biggest function.
    sum += max;
    
    return sum;
  }
  //========================================================================================
  public Set<String> getKeys(){
    return table.keySet();
  }
  //========================================================================================
  public boolean hasEntry(String key){
    return table.containsKey(key);
  }
  //========================================================================================
}
