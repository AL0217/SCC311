// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileNotFoundException;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.lang.reflect.Member;
// import javax.crypto.Cipher;
// import javax.crypto.KeyGenerator;
// import javax.crypto.SealedObject;
// import javax.crypto.SecretKey;
// import javax.crypto.spec.SecretKeySpec;
// import java.security.NoSuchAlgorithmException;
// import java.security.SecureRandom;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.util.*;


public class Replica implements Server{

    //HashMap of auctionItems
    static HashMap<Integer, AuctionNow> auctionItems = new HashMap<>();

    //HashMap of Users where their email, public key and challenge string are stored
    static HashMap<Integer, User> registration = new HashMap<>();
    //A set of email has been registered
    static Set<String> userSet = new HashSet<String>();
    static HashMap<Integer, String> challenges = new HashMap<>();

    //server elements
    static keyHandle handler = new keyHandle();
    static int itemCounter = 1;

    public static void main(String[] args)
    {
        try {
            Replica s = new Replica();
            String name = "Replica" + args[0];
            Auction auc = (Auction)UnicastRemoteObject.exportObject(s, 0);
            // handler.generateKeys();
            // // System.out.println(privKey);

            // // System.out.println(pubKey);
            // handler.storePublicKey(handler.getPublicKey(), "../keys/serverKey.pub");
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, auc);
            System.out.println("Server ready");

        } catch (Exception e) {
            // TODO: handle exception
            System.err.println("Exception:");
            e.printStackTrace();        
        }
    }

    public void broadcast(HashMap<Integer, AuctionNow> items, HashMap<Integer, User> reg, Set<String> users, int itemCount)
    {
        auctionItems = items;
        registration = reg;
        userSet = users;
        itemCounter = itemCount;
    }   

    @Override
    public Integer register(String email, PublicKey pubKey) throws RemoteException {
        if(userSet.contains(email))
        {
            System.out.println("already registered");
            return null;
        }
        userSet.add(email);
        try {
            int index = registration.size();
            User newUser = new User();
            newUser.email = email;
            newUser.pubKey = pubKey;
            registration.put(index, newUser);
            return index;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public AuctionItem getSpec(int userID,int itemID, String token) throws RemoteException {
        // TokenInfo tokenInfo = registration.get(userID).token;

        // //if the token information match the token given AND the token is not expired
        // if(token.equals(tokenInfo.token) && System.currentTimeMillis() < tokenInfo.expiryTime)
        // {
            
        // }
        // System.out.println("token is wrong or expired");
        // return null;
        try {
            return auctionItems.get(itemID).getAuctionItem();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }

    @Override
    public Integer newAuction(int userID, AuctionSaleItem item, String token) throws RemoteException {
        // TokenInfo tokenInfo = registration.get(userID).token;

        // //if the token information match the token given AND the token is not expired
        // if(token.equals(tokenInfo.token) && System.currentTimeMillis() < tokenInfo.expiryTime)
        // {
            

        // }
        // System.out.println("token is wrong or expired");
        // return null;
        try {
            System.out.println(itemCounter);
            System.out.println("name: " + item.name);
            System.out.println("description: " + item.description);
            System.out.println("highestBid: " + item.reservePrice);
            auctionItems.put(itemCounter, new AuctionNow(itemCounter, item, userID));
            System.out.println("put item" + itemCounter);
            return itemCounter++;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
            // TODO: handle exception
        }
    }

    @Override
    public AuctionItem[] listItems(int userID, String token) throws RemoteException { 
        // TokenInfo tokenInfo = registration.get(userID).token;

        // //if the token information match the token given AND the token is not expired
        // if(token.equals(tokenInfo.token) && System.currentTimeMillis() < tokenInfo.expiryTime)
        // {
            
        // }
        // System.out.println("token is wrong or expired");
        // return null;

        AuctionItem[] listItems = new AuctionItem[auctionItems.size()];
            auctionItems.forEach((index, value) -> {
                listItems[index - 1] = value.getAuctionItem();
            });
            return listItems;
    }

    @Override
    public AuctionResult closeAuction(int userID, int itemID, String token) throws RemoteException {
        //get the information of the target item
        AuctionNow item = auctionItems.get(itemID);
        if(userID != item.getUserID())
        {
            System.out.println("Not the creator");
            return null;
        }

        try {
            int bidder = item.getBidderID();
            int bidPrice = item.getAuctionItem().highestBid;
            
            //Create the return object
            AuctionResult result = new AuctionResult();
            result.winningEmail = registration.get(bidder).email;
            result.winningPrice = bidPrice;

            System.out.println(registration.get(bidder).email);

            //remove the auction item from the auction list
            auctionItems.remove(itemID);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
            // TODO: handle exception
        }
        // TokenInfo tokenInfo = registration.get(userID).token;

        // //if the token information match the token given AND the token is not expired
        // if(token.equals(tokenInfo.token) && System.currentTimeMillis() < tokenInfo.expiryTime)
        // {
            

        // }
        // System.out.println("token is wrong or expired");
        // return null;
    }

    @Override
    public boolean bid(int userID, int itemID, int price, String token) throws RemoteException {
        // TokenInfo tokenInfo = registration.get(userID).token;

        // //if the token information match the token given AND the token is not expired
        // if(token.equals(tokenInfo.token) && System.currentTimeMillis() < tokenInfo.expiryTime)
        // {
            
        // }
        // System.out.println("token is wrong or expired");
        // return false;

        try {
            AuctionNow targetItem = auctionItems.get(itemID);
            if(price > targetItem.getAuctionItem().highestBid)
            {
                targetItem.setBidder(userID);
                targetItem.setBid(price);
                return true;
            }
            System.out.println("The bid prie is too low");
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public ChallengeInfo challenge(int userID, String clientChallenge) throws RemoteException {
        return null;
        // try {

        //     byte[] response = signData(clientChallenge, handler.getPrivateKey());
        //     System.out.println("signed the data");
        //     challenges.put(userID, clientChallenge);
        //     //generate server side challenge using anything from the ascii chart
        //     Random random = new Random();
        //     StringBuilder stringBuilder = new StringBuilder();
            
        //     for (int i = 0; i < 10; i++) {
        //         int randomAscii = 33 + random.nextInt(126 - 33 + 1);
        //         char randomChar = (char) randomAscii;
        //         stringBuilder.append(randomChar);
        //     }

        //     //The challenge info returning
        //     ChallengeInfo challengeInfo = new ChallengeInfo();
        //     challengeInfo.response = response;
        //     challengeInfo.serverChallenge = stringBuilder.toString();

        //     //remembering the challenge send
        //     User user = registration.get(userID);
        //     user.challenge = challengeInfo.serverChallenge;

        //     System.out.println("sending challenge back");
        //     return challengeInfo;

        // } catch (Exception e) {
        //     // TODO: handle exception
        //     e.printStackTrace();
        //     return null;
        // }
    }
    @Override
    public TokenInfo authenticate(int userID, byte[] signature) throws RemoteException {
        return null;
        // User user = registration.get(userID);
        // String challengeSent = user.challenge;

        // System.out.println(challengeSent);

        // try {
        //     Signature sign = Signature.getInstance("SHA256withRSA");
        //     sign.initVerify(user.pubKey);
        //     sign.update(challengeSent.getBytes());

        //     // Verify the signature
        //     if(sign.verify(signature))
        //     {
        //         System.out.println("verified");
        //         Random random = new Random();
        //         StringBuilder stringBuilder = new StringBuilder();

        //         //generate server side challenge using anything from the ascii chart
        //         for (int i = 0; i < 10; i++) {
        //             int randomAscii = 33 + random.nextInt(126 - 33 + 1);
        //             char randomChar = (char) randomAscii;
        //             stringBuilder.append(randomChar);
        //         }
        //         String tokenStr = stringBuilder.toString();
        //         TokenInfo tokenInfo = new TokenInfo();
        //         tokenInfo.token = tokenStr;
        //         //set expiry time to 10s
        //         tokenInfo.expiryTime = System.currentTimeMillis() + (10 * 1000);
        //         user.token = tokenInfo;
        //         return tokenInfo;
        //     }
        //     else
        //     {   
        //         System.out.println("Failed");
        //         return null;
        //     }
        // } catch (Exception e) {
        //     e.printStackTrace();
        //     return null;
        // }
    }

    // private static byte[] signData(String challenge, PrivateKey privateKey) throws Exception {
    //     // Create a signature object
    //     Signature signature = Signature.getInstance("SHA256withRSA");

    //     //signing the challenge
    //     signature.initSign(privateKey);
    //     signature.update(challenge.getBytes());

    //     return signature.sign();
    // }
 




    @Override
    public int getPrimaryReplicaID() throws RemoteException {
        return -1;
    }

}




    // @Override
    // public SealedObject getSpec(int itemID) {
    //     AuctionItem item = auctionItems.get(itemID - 1);
    //     try {
    //         SecretKey key = getAESKey();
    //         Cipher c = Cipher.getInstance("AES");
    //         c.init(Cipher.ENCRYPT_MODE, key);
    //         SealedObject sealedItem = new SealedObject(item, c);
    //         System.out.println("Got the auction item");
    //         return sealedItem;
    //     } catch (Exception e) {
    //         // TODO: handle exception
    //         e.printStackTrace();
    //     }
    //     System.out.println("not working");
    //     return null;
    // }


    // public static SecretKey getAESKey() throws NoSuchAlgorithmException, FileNotFoundException, IOException {
    //     File temp = new File("../keys/testKey.aes");
    //     if(!temp.exists())
    //     {
    //         try {
    //             KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    //             keyGen.init(256, SecureRandom.getInstanceStrong());
    //             SecretKey key = keyGen.generateKey();
    //             try(FileOutputStream fileWrite = new FileOutputStream("../Keys/testKey.aes");)
    //             {
    //                 byte[] bArr = key.getEncoded();
    //                 fileWrite.write(bArr);
    //             }
    //             catch(Exception e)
    //             {
    //                 e.printStackTrace();  
    //             }
    //             return key;
    //         } catch (Exception e) {
    //             // TODO Auto-generated catch block
    //             e.printStackTrace();
    //         }   
    //     }
    //     try (FileInputStream f = new FileInputStream("../Keys/testKey.aes");) 
    //     {
    //         byte[] byteKey = f.readAllBytes();
    //         SecretKey key = new SecretKeySpec(byteKey, 0, byteKey.length, "AES");
    //         System.out.println("Got the Key");
    //         return key;
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     System.out.println("no key found");
    //     return null;
    // }