import java.io.FileInputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Client {
    private static byte[] signData(String challenge, PrivateKey privateKey) throws Exception {
        // Create a signature object
        Signature signature = Signature.getInstance("SHA256withRSA");

        //signing the challenge
        signature.initSign(privateKey);
        signature.update(challenge.getBytes());

        return signature.sign();
    }


    public static void main(String[] args)
    {
        int n = Integer.parseInt(args[0]);
        HashMap<Integer, String> challenges = new HashMap<>();
        HashMap<Integer, String> Tokens = new HashMap<>();

        try{
            String name = "Auction";
            Registry registry = LocateRegistry.getRegistry("localhost"); 
            Auction server = (Auction) registry.lookup(name);
            keyHandle handler = new keyHandle();
            handler.generateKeys();
            PublicKey pubKey1 = handler.getPublicKey();
            PrivateKey privKey1 = handler.getPrivateKey();


            try {
                int user1 = server.register("yunirrrsama@gmail.com", pubKey1);

                //generate challenge
                Random random = new Random();
                StringBuilder stringBuilder = new StringBuilder();

                //generate client side challenge using anything from the ascii chart
                for (int i = 0; i < 10; i++) {
                    int randomAscii = 33 + random.nextInt(126 - 33 + 1);
                    char randomChar = (char) randomAscii;
                    stringBuilder.append(randomChar);
                }
                challenges.put(user1, stringBuilder.toString());

                //send challenge
                ChallengeInfo challengeInfo= server.challenge(user1, challenges.get(user1));
                try {
                    Path path = Paths.get("../keys/serverKey.pub");
                    byte[] publicKeyBytes = Files.readAllBytes(path);
                    String publicKeyBase64 = new String(publicKeyBytes);
                    // Decode the Base64 string to obtain the raw bytes
                    byte[] decodedBytes = Base64.getDecoder().decode(publicKeyBase64);

                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedBytes);
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    PublicKey serverKey = keyFactory.generatePublic(keySpec);
                    // System.out.println(serverKey);
                    Signature sign = Signature.getInstance("SHA256withRSA");
                    sign.initVerify(serverKey);
                    sign.update(challenges.get(user1).getBytes());

                    if(sign.verify(challengeInfo.response))
                    {
                        byte[] signature = signData(challengeInfo.serverChallenge, privKey1);
                        TokenInfo tokenInfo = server.authenticate(user1, signature);
                        if(tokenInfo != null)
                        {
                            System.out.println(tokenInfo.token);
                        }
                        Tokens.put(user1, tokenInfo.token);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }
            } catch (Exception e) {
                // TODO: handle exception
                System.out.println("registered");
            }

            AuctionSaleItem item = new AuctionSaleItem();
            item.name = "test1";
            item.description = "first auction";
            item.reservePrice = 100;
            System.out.println("created Item 1");
            int auction1 = server.newAuction(0, item, Tokens.get(0));
            System.out.println(auction1);

            AuctionSaleItem item1 = new AuctionSaleItem();
            item1.name = "test2";
            item1.description = "2 auction";
            item1.reservePrice = 100;
            System.out.println("created Item 2");
            int auction2 = server.newAuction(0, item1, Tokens.get(0));
            System.out.println(auction2);

            AuctionItem[] itemList = server.listItems(0, "..........");
            for(int i = 0; i < itemList.length; i++)
            {
                System.out.println("itemID: " + itemList[i].itemID);
                System.out.println("name: " + itemList[i].name);
                System.out.println("description: " + itemList[i].description);
                System.out.println("highestBid: " + itemList[i].highestBid);
            }

            boolean b = server.bid(0, 1, 120, "..........");
            System.out.println(b);
            
            AuctionResult result = server.closeAuction(0, 1, "..........");
            System.out.println(result.winningEmail);
            System.out.println(result.winningPrice);


            // AuctionItem auctionItem = server.getSpec(auction1);
            // System.out.println("itemID: " + auctionItem.itemID);
            // System.out.println("name: " + auctionItem.name);
            // System.out.println("description: " + auctionItem.description);
            // System.out.println("highestBid: " + auctionItem.highestBid);

            // AuctionItem item = server.getSpec();
            // SealedObject item = server.getSpec(n);
            // try (FileInputStream f = new FileInputStream("../Keys/testKey.aes");) 
            // {
            //     byte[] byteKey = f.readAllBytes();
            //     SecretKey key = new SecretKeySpec(byteKey, 0, byteKey.length, "AES");
            //     // System.out.println("Got the key");
            //     Cipher c = Cipher.getInstance("AES");
            //     c.init(Cipher.DECRYPT_MODE, key);

            //     Serializable unsealObject = (Serializable) item.getObject(c);
            //     AuctionItem auctionItem = (AuctionItem) unsealObject;
            //     System.out.println("itemID: " + auctionItem.itemID);
            //     System.out.println("name: " + auctionItem.name);
            //     System.out.println("description: " + auctionItem.description);
            //     System.out.println("highestBid: " + auctionItem.highestBid);
            // } catch (ClassNotFoundException e) {
            //     e.printStackTrace();
            // }

        }
        catch(Exception e)
        {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    }
}
