/*
CS645 Project1

Gene Chen
Kefin Sajan
Minhazul Abedin
*/

package cs645project1;

public class Shadow {
    
    private String username;
    private String salt;
    private String hash;
    
    public Shadow( String username, String salt, String hash ){
        this.username = username;
        this.salt = salt;
        this.hash = hash;
    }
    
    public static Shadow toSimpleShadow( String string )
    {
        // Format:
        // username:salt:hash
        return new Shadow( 
                string.split( ":" )[0], //username
                string.split( ":" )[1], //salt
                string.split( ":" )[2]  //hash
        );
        
    }
    
    public static Shadow toShadow( String string )
    {
        // Format:
        // username:$1$salt$hash:17801:0:99999:7:::
        return new Shadow( 
                string.split(":")[0], //username
                ( string.split(":")[1] ).split("\\$")[2], //salt
                ( string.split(":")[1] ).split("\\$")[3]  //hash
            
            );
        
    }
    
    public String getUsername(){
        return username;
    }
    
    public String getSalt(){
        return salt;
    }
    
    public String getHash(){
        return hash;
    }
    
    public String toString(){
        return username + ":" + salt + ":" + hash;
    }
    
}
