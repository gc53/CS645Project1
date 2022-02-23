
package cs645project1;

import java.util.ArrayList;
import java.util.HashMap;


public class Cracker {

    static String PWD_DIR = "common-passwords.txt";
    static String SHADOW_DIR = "shadow";
    
    
    public static HashMap makePreCalcTbl( ArrayList<String> pwd_list, ArrayList<Shadow> shadow ){
        
        HashMap pre_calc_tbl = new HashMap();
        
        // Iterate through list of user data in shadow file
        for( Shadow user : shadow ){
            
            // Iterate through list of common passwords
            for( String pwd : pwd_list ){
                
                // Get hash of salt and password and use the resulting hash
                // as the key for the corresponding user and password in hash map
                pre_calc_tbl.put( 
                        MD5Shadow.crypt( pwd, user.getSalt() ), //Salt & Password Hash
                        user.getUsername() + ":" + pwd          //Username & Password
                );
                
            }
        }
        
        return pre_calc_tbl;
    }
    
    public static void main(String[] args) {
        
        // Read list of common passwords into array of strings.
        // First argument can be file path to custom password list.
        // Default password list will be used otherwise.
        ArrayList<String> pwd_list = FileToArray.toStringArray( args.length == 1 ? args[0] : PWD_DIR );
        
        // Read shadow file into array
        ArrayList<Shadow> shadow = FileToArray.toShadowArray( SHADOW_DIR );
        

        // Make pre-calculated hash table
        HashMap pre_calc_tbl = makePreCalcTbl( pwd_list, shadow );
            
        // Iterate through list of users in shadow file
        for( Shadow user : shadow ){
                
            // Check if shadow file hash exists in pre-calculated table
            if( pre_calc_tbl.containsKey( user.getHash() ) ){
                    
                // Print username and password corresponding to 
                // hash from shadow file
                System.out.println( pre_calc_tbl.get( user.getHash() ) );
                    
            }
            
            }

        
    }
    
}
