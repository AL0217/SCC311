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
            String name = "FrontEnd";
            Registry registry = LocateRegistry.getRegistry("localhost"); 
            Auction server = (Auction) registry.lookup(name);
//            keyHandle handler = new keyHandle();
//            handler.generateKeys();
//            PublicKey pubKey1 = handler.getPublicKey();
//            PrivateKey privKey1 = handler.getPrivateKey();


            // try {
            //     int user1 = server.register("yunirrrsama@gmail.com", pubKey1);

            //     //generate challenge
            //     Random random = new Random();
            //     StringBuilder stringBuilder = new StringBuilder();

            //     //generate client side challenge using anything from the ascii chart
            //     for (int i = 0; i < 10; i++) {
            //         int randomAscii = 33 + random.nextInt(126 - 33 + 1);
            //         char randomChar = (char) randomAscii;
            //         stringBuilder.append(randomChar);
            //     }
            //     challenges.put(user1, stringBuilder.toString());

            //     //send challenge
            //     // ChallengeInfo challengeInfo= server.challenge(user1, challenges.get(user1));
            //     try {
            //         Path path = Paths.get("../keys/serverKey.pub");
            //         byte[] publicKeyBytes = Files.readAllBytes(path);
            //         String publicKeyBase64 = new String(publicKeyBytes);
            //         // Decode the Base64 string to obtain the raw bytes
            //         byte[] decodedBytes = Base64.getDecoder().decode(publicKeyBase64);

            //         X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedBytes);
            //         KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            //         PublicKey serverKey = keyFactory.generatePublic(keySpec);
            //         // System.out.println(serverKey);
            //         Signature sign = Signature.getInstance("SHA256withRSA");
            //         sign.initVerify(serverKey);
            //         sign.update(challenges.get(user1).getBytes());

            //         if(sign.verify(challengeInfo.response))
            //         {
            //             byte[] signature = signData(challengeInfo.serverChallenge, privKey1);
            //             TokenInfo tokenInfo = server.authenticate(user1, signature);
            //             if(tokenInfo != null)
            //             {
            //                 System.out.println(tokenInfo.token);
            //             }
            //             Tokens.put(user1, tokenInfo.token);
            //         }
            //     } catch (Exception e) {
            //         e.printStackTrace();
            //         // TODO: handle exception
            //     }
            // } catch (Exception e) {
            //     // TODO: handle exception
            //     System.out.println("registered");
            // }
            int auction1 = -1, auction2 = -1;
            int user1 = -2;

            if(args[0].equals("1"))
            {
                user1 = server.register("yunirrrsama@gmail.com", null);
                System.out.println(user1);
                AuctionSaleItem item = new AuctionSaleItem();
    
                item.name = "test1";
                item.description = "first auction";
                item.reservePrice = 100;
                System.out.println("created Item 1");
                auction1 = server.newAuction(user1, item, Tokens.get(0));
                System.out.println(auction1);
    
                AuctionSaleItem item1 = new AuctionSaleItem();
                item1.name = "test2";
                item1.description = "2 auction";
                item1.reservePrice = 100;
                System.out.println("created Item 2");
                auction2 = server.newAuction(user1, item1, Tokens.get(0));
                System.out.println(auction2);
    
                AuctionItem[] itemList = server.listItems(user1, "..........");
                for(int i = 0; i < itemList.length; i++)
                {
                    System.out.println("itemID: " + itemList[i].itemID);
                    System.out.println("name: " + itemList[i].name);
                    System.out.println("description: " + itemList[i].description);
                    System.out.println("highestBid: " + itemList[i].highestBid);
                }
    

                
                // AuctionResult result = server.closeAuction(user1, auction1, "..........");
                // System.out.println(result.winningEmail);
                // System.out.println(result.winningPrice);
            }
            else
            {
                // int user2 = server.register("123321@gmail.com", null);
    
                AuctionItem[] itemList = server.listItems(2, "..........");
                System.out.println(itemList.length);

                for(int i = 0; i < itemList.length; i++)
                {
                    System.out.println("itemID: " + itemList[i].itemID);
                    System.out.println("name: " + itemList[i].name);
                    System.out.println("description: " + itemList[i].description);
                    System.out.println("highestBid: " + itemList[i].highestBid);
                }
    
                // //user2, item1, price
                // boolean b = server.bid(1, 1, 120, "..........");
                // System.out.println(b);

                // //user1, item1, price
                // boolean b2 = server.bid(2, 1, 140, "..........");
                // System.out.println(b2);
                
                // AuctionResult result = server.closeAuction(1, 1, "..........");
                // System.out.println(result.winningEmail);
                // System.out.println(result.winningPrice);
            }
        }
        catch(Exception e)
        {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    }
}
