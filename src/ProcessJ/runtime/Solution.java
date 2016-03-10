package ProcessJ.runtime;

import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class Solution {

    public static void main(String[] args) {
    	String a = "898";
    	String[] num = String.valueOf(a).split("");
        int i1 = 0;
        int i2 = num.length - 1;
        boolean n = false;
        while (i2 > i1) {
            if (Integer.parseInt(num[i1]) != Integer.parseInt(num[i2])) {
            	n = true;
            	System.out.println("not");
            	break;
            }
            i1++;
            i2--;
        }
        if (!n)
        System.out.println("yes");
    }
}