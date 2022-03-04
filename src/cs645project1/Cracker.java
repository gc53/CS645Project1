/*
CS645 Project1

Gene Chen
Kefin Sajan
Minhazul Abedin
*/
package cs645project1;

import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Cracker extends Thread{

    static String PWD_DIR = "common-passwords.txt";
    static String SHADOW_DIR = "shadow";
    static int TASK_SIZE = 1000;
    private static final ExecutorService workers = 
            Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );
            //Executors.newCachedThreadPool();  //Aggressive threading scheme. Not recommended or optimal.
    
    // Deprecated original single threaded function that calculates hash for all users
    private static HashMap makePreCalcTbl( ArrayList<String> pwd_list, ArrayList<Shadow> shadow ){
        
        // Catch null inputs
        if( pwd_list == null || shadow == null ){
            throw new NullPointerException( 
                    pwd_list == null ? "pwd_list" : "" + 
                    pwd_list == null && shadow == null? " & " : "" + 
                    shadow == null ? "shadow" : "" + 
                    " arg is null.");
        }
        
        HashMap pre_calc_tbl = new HashMap();
        
        // Iterate through list of user data in shadow file
        for( Shadow user : shadow ){
            
            // Iterate through list of common passwords
            for( String pwd : pwd_list ){
                
                if( pwd.length() > 16){
                    continue;
                }
                //System.out.println( MD5Shadow.crypt( pwd, user.getSalt() ) + " : " + user.getUsername() + ":" + pwd );
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
    
    // Deprecated revised single threaded function that calcuates hashes for a single user
    // to find their password
    private static String getPwd( ArrayList<String> pwd_list, Shadow user ){
        
        // Catch null inputs
        if( pwd_list == null || user == null ){
            throw new NullPointerException( 
                    pwd_list == null ? "pwd_list" : "" + 
                    pwd_list == null && user == null? " & " : "" + 
                    user == null ? "user" : "" + 
                    " arg is null.");
        }
        
        HashMap pre_calc_tbl = new HashMap();
        
        // Iterate through list of common passwords
        for( String pwd : pwd_list ){
                
            // MD5Shadow.crypt() does not appear to work with passwords longer than 16 characters.
            if( pwd.length() > 16){
                continue;
            }
            //System.out.println( MD5Shadow.crypt( pwd, user.getSalt() ) + " : " + user.getUsername() + ":" + pwd );
            // Get hash of salt and password and use the resulting hash
            // as the key for the corresponding user and password in hash map
            pre_calc_tbl.put( 
                    MD5Shadow.crypt( pwd, user.getSalt() ), //Salt & Password Hash
                    user.getUsername() + ":" + pwd          //Username & Password
            );
                
            }

        if( pre_calc_tbl.containsKey( user.getHash() ) ){
                    
                // Print username and password corresponding to 
                // hash from shadow file
                return (String) pre_calc_tbl.get( user.getHash() );
                    
            }
        else{
            return null;
        }
    }
    
    // Multithreaded function that calcuates hashes for a single user
    // to find their password
    private static String getPwdMT( ArrayList<String> pwd_list, Shadow user ) 
            throws InterruptedException, ExecutionException{
        
        // Catch null inputs
        if( pwd_list == null || user == null ){
            throw new NullPointerException( 
                    pwd_list == null ? "pwd_list" : "" + 
                    pwd_list == null && user == null ? " & " : "" + 
                    user == null ? "user" : "" + 
                    " arg is null.");
        }
        
        // Initialize list of tasks
        List<Callable<String[]>> tasks = new ArrayList<>();

        // Iterate through list of common passwords
        for( String pwd : pwd_list ){

            // Add task to calculate and return hash of password & salt along
            // with corresponding password to list of tasks to be run
            tasks.add(
                new Callable<String[]>()
                {
                    public String[] call() throws Exception
                    {
                        String[] out = new String[2];
                        
                        // Store password that will be calculated to hash.
                        // Truncate passwords longer than 16 characters since
                        // MD5Shadow.crypt() does not appear to work with passwords longer than that.
                        out[0] = pwd.length() > 16 ? pwd.substring( 0, 16 ) : pwd;
                        
                        // Calculate hash of password & salt
                        out[1] = MD5Shadow.crypt( out[0], user.getSalt() );

                        return out;
                    }
                }
            );
            
        }
        
        // Iterate through list of tasks to execute "small" chunks of tasks
        for( int fromIndex=0; fromIndex < tasks.size(); fromIndex += TASK_SIZE ){
            
            int toIndex, temp = 0;
            toIndex = (temp = fromIndex + TASK_SIZE - 1) > tasks.size() ? tasks.size() : temp;

            // Run "small" chunks of list of tasks
            List<Future<String[]>> results = workers.invokeAll(
                    tasks.subList(fromIndex, toIndex ), 
                    10, TimeUnit.SECONDS
            );
            
            // Iterate through finished tasks
            for (Future<String[]> f : results) {
                
                // Get results from task
                String[] result = f.get();
                
                // Return username & password if password matches the user's hash
                if( result[1].equals( user.getHash() ) ){
                    return user.getUsername() + ":" + result[0];
                }
                
            }
        }
        
        // Return null to signal no matching password was found
        return null;

    }
    
    public static void main(String[] args) {
        
        
        // Initialize list of file paths to password lists with default password list.
        String pwdFileList[] = { PWD_DIR };
        
        // Read arguments as file paths to custom password lists that will be 
        // merged with default password list.
        if( args.length > 0 ){
            ArrayList<String> pwdFileArrayList = new ArrayList<>( Arrays.asList(pwdFileList) );
            pwdFileArrayList.addAll( (new ArrayList<>( Arrays.asList(args)) ) );
            pwdFileList = pwdFileArrayList.toArray( pwdFileList );
        }
        
        // Read list of common passwords into array of strings.
        ArrayList<String> pwd_list = FileToArray.toStringArray( pwdFileList ); 

        // Read shadow file into array
        ArrayList<Shadow> shadow = FileToArray.toShadowArray( SHADOW_DIR );
        

        // Deprecated call to make pre-calculated hash table
        //HashMap pre_calc_tbl = makePreCalcTbl( pwd_list, shadow );
            
        // Iterate through list of users in shadow file
        for( Shadow user : shadow ){
            
            // Deprecated single threaded call to get password for the given user
            //String pwd = getPwd( pwd_list, user );
            
            // Initialize result variable
            String pwd = null;

            // Try to get password for the given user from the list of passwords
            try {
                pwd = getPwdMT( pwd_list, user );

            } catch (CancellationException | InterruptedException | ExecutionException ex) {
                Logger.getLogger(Cracker.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Print password. Null password means no password was found.
            System.out.print( pwd == null ? 
                                //user.getUsername() + ": Password not found" : 
                                "" : 
                                pwd + "\n" );

            
        }
        
        // Cleanup after all users have been analyzed
        workers.shutdown();
        
    }
    
}
