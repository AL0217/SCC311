import java.security.*;
import java.io.*;
import java.util.*;

public class keyHandle {
    private KeyPair keyPair;

    public PublicKey getPublicKey()
    {
        return this.keyPair.getPublic();
    }

    public PrivateKey getPrivateKey()
    {   
        // System.out.println(privKey);
        return this.keyPair.getPrivate();
    }

    public void generateKeys()
    {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.genKeyPair();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    
    // Method to write a public key to a file.
    // Example use: storePublicKey(aPublicKey, ‘../keys/serverKey.pub’)
    public void storePublicKey(PublicKey publicKey, String filePath) throws Exception 
    {
        // Convert the public key to a byte array
        byte[] publicKeyBytes = publicKey.getEncoded();

        // Encode the public key bytes as Base64
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKeyBytes);

        // Write the Base64 encoded public key to a file
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(publicKeyBase64.getBytes());
        }
    }
}
