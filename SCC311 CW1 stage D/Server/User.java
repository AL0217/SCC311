import java.security.*;

public class User implements java.io.Serializable{
    String email;
    PublicKey pubKey;
    String challenge;
    TokenInfo token;
}
