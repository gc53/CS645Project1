
package cs645project1;

import java.util.ArrayList;
import java.util.Collection;
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
    private static final ExecutorService workers = Executors.newCachedThreadPool();
    
    private static HashMap makePreCalcTbl( ArrayList<String> pwd_list, ArrayList<Shadow> shadow ){
        
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
    
    private static String getPwd( ArrayList<String> pwd_list, Shadow user ){
        
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
    
    private static String getPwdMT( ArrayList<String> pwd_list, Shadow user ) throws InterruptedException, ExecutionException{
        
        HashMap pre_calc_tbl = new HashMap();
        
        Collection<Callable<String[]>> tasks = new ArrayList<>();

        // Iterate through list of common passwords
        for( String pwd : pwd_list ){
                
            // MD5Shadow.crypt() does not appear to work with passwords longer than 16 characters.
            if( pwd.length() > 16){
                continue;
            }
            

            tasks.add(new Callable<String[]>()
            {

            public String[] call()
                  throws Exception
            {
                String[] out = new String[2];
                out[0] = MD5Shadow.crypt( pwd, user.getSalt() );
                out[1] = user.getUsername() + ":" + pwd;
                return out;
            }

            });
        }
            
                
        List<Future<String[]>> results = workers.invokeAll(tasks/*, 120, TimeUnit.SECONDS*/);
        for (Future<String[]> f : results) {
            String[] str = f.get();
            pre_calc_tbl.put( 
                    str[0], //Salt & Password Hash
                    str[1]  //Username & Password
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
    
    public static void main(String[] args) {
        
        // Read list of common passwords into array of strings.
        // First argument can be file path to custom password list.
        // Default password list will be used otherwise.
        ArrayList<String> pwd_list = FileToArray.toStringArray( args.length == 1 ? args[0] : PWD_DIR );
        
        // Read shadow file into array
        ArrayList<Shadow> shadow = FileToArray.toShadowArray( SHADOW_DIR );
        

        // Make pre-calculated hash table
        //HashMap pre_calc_tbl = makePreCalcTbl( pwd_list, shadow );
            
        // Iterate through list of users in shadow file
        for( Shadow user : shadow ){
            
            
            //String out = getPwd( pwd_list, user );
            String out = null;

            try {
                out = getPwdMT( pwd_list, user );

            } catch (CancellationException | InterruptedException | ExecutionException ex) {
                Logger.getLogger(Cracker.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println( out == null ? 
                                user.getUsername() + ": Password not found" : 
                                out );

            
        }
        
        workers.shutdown();
        
    }
    
}
