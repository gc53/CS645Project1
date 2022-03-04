/*
CS645 Project1

Gene Chen
Kefin Sajan
Minhazul Abedin
*/

package cs645project1;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class FileToArray {

    public static ArrayList<String> toStringArray(String ... filePaths)
    {
 
        // LinkedHashSet is used to prevent duplicate strings from being added
        Set<String> builder = new LinkedHashSet<>();
 
        // Iterate through file paths
        for( String filePath : filePaths ){
        
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
                e.printStackTrace();
            }
        }
 
        // Return ArrayList of common passwords with no duplicates
        return new ArrayList<>( builder );
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
