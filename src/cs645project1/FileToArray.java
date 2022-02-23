
package cs645project1;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileToArray {
    
    public static ArrayList<String> toStringArray(String filePath)
    {
 
        ArrayList<String> builder = new ArrayList<>();
 
        // try block to check for exceptions where
        // object of BufferedReader class us created
        // to read filepath
        try (BufferedReader buffer = new BufferedReader(
                 new java.io.FileReader(filePath))) {
 
            String str;
 
            // Condition check via buffer.readLine() method
            // holding true upto that the while loop runs
            while ((str = buffer.readLine()) != null) {
 
                builder.add(str);
            }
        }
 
        // Catch block to handle the exceptions
        catch (IOException e) {
 
            // Print the line number here exception occured
            // using printStackTrace() method
            e.printStackTrace();
        }
 
        // Return ArrayList of strings
        return builder;
    }
    
    public static ArrayList<Shadow> toSimpleShadowArray( String filepath ){
        return toShadowArray( filepath, true );
    }
    
    public static ArrayList<Shadow> toShadowArray( String filepath ){
        return toShadowArray( filepath, false );
    }
    
    private static ArrayList<Shadow> toShadowArray(String filePath, boolean simple )
    {
 
        ArrayList<Shadow> builder = new ArrayList<>();
 
        // try block to check for exceptions where
        // object of BufferedReader class us created
        // to read filepath
        try (BufferedReader buffer = new BufferedReader(
                 new java.io.FileReader(filePath))) {
 
            String str;
 
            // Condition check via buffer.readLine() method
            // holding true upto that the while loop runs
            while ((str = buffer.readLine()) != null) {
 
                builder.add( simple ? Shadow.toSimpleShadow(str) : Shadow.toShadow(str) );
            }
        }
 
        // Catch block to handle the exceptions
        catch (IOException e) {
 
            // Print the line number here exception occured
            // using printStackTrace() method
            e.printStackTrace();
        }
 
        // Return ArrayList of strings
        return builder;
    }
    
}
