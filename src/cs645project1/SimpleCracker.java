/*
CS645 Project1

Gene Chen
Kefin Sajan
Minhazul Abedin
*/

package cs645project1;

import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SimpleCracker {
    
    static String PWD_DIR = "common-passwords.txt";
    static String SHADOW_DIR = "shadow-simple";
    
    
    private static String toHex(byte[] bytes)
    {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    
    private static HashMap makePreCalcTbl( ArrayList<String> pwd_list, ArrayList<Shadow> shadow ) throws NoSuchAlgorithmException{
        
        HashMap pre_calc_tbl = new HashMap();
        MessageDigest md = MessageDigest.getInstance("MD5");
        
        // Iterate through list of user data in shadow file
        for( Shadow user : shadow ){
            
            // Iterate through list of common passwords
            for( String pwd : pwd_list ){
                
                // Pass salt concatenated with password into message digest
                md.update( (user.getSalt() + pwd).getBytes() );
                
                // Get hash of salt and password and use the resulting hash
                // as the key for the corresponding user and password in hash map
                pre_calc_tbl.put( 
                        toHex( md.digest() ),           //Salt & Password Hash
                        user.getUsername() + ":" + pwd  //Username & Password
                );
                
            }
        }
        
        return pre_calc_tbl;
    }
    
    public static void main(String[] args) {
        
        // Read list of common passwords into array of strings
        ArrayList<String> pwd_list = FileToArray.toStringArray( PWD_DIR );
        
        // Read shadow file into array
        ArrayList<Shadow> shadow = FileToArray.toSimpleShadowArray( SHADOW_DIR );
        
        try{
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
        catch( Exception e ){
            e.printStackTrace();
        }
        
        
        
    }
    
}
