import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.util.*;


public class Replica implements Server{

    /* Shared Variables among backup servers*/
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
    static Registry registry;
    static String name;

    public static void main(String[] args)
    {
        //start this replica as a server
        try {
            Replica s = new Replica();
            name = "Replica" + args[0];
            Auction auc = (Auction)UnicastRemoteObject.exportObject(s, 0);

            registry = LocateRegistry.getRegistry("localhost");
            registry.rebind(name, auc);
            System.out.println(name + " ready");

        } catch (Exception e) {
            // TODO: handle exception
            System.err.println("Exception:");
            e.printStackTrace();        
        }
    }


    //Method to send the shared variables to other backup server
    @Override
    public void broadcast()
    {
        try{
            //
            Registry backupsRegistry = LocateRegistry.getRegistry("localhost");
            String[] backupReplicas = backupsRegistry.list();

            for(int i = 0; i < backupReplicas.length; i++)
            {
                //connect to all replica found, if connected update their variables
                if(backupReplicas[i].startsWith("Replica") && !backupReplicas[i].equals(name))
                {
                    try {
                        Server backUps = (Server) backupsRegistry.lookup(backupReplicas[i]);
                        backUps.updateSharedVariable(auctionItems, registration, userSet, itemCounter);
                    } catch (Exception e) {
                        // e.printStackTrace();
                        System.out.println("failed to connect: " + backupReplicas[i]);
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }   


    //Method to update the variable of this replica when this replica is not the primary one
    @Override
    public void updateSharedVariable(HashMap<Integer, AuctionNow> items, HashMap<Integer, User> reg, Set<String> users, int itemCount) {
        auctionItems = items;
        registration = reg;
        userSet = users;
        itemCounter = itemCount;
    }


    //method to register a user
    @Override
    public Integer register(String email, PublicKey pubKey) throws RemoteException {
        if(userSet.contains(email))
        {
            System.out.println("already registered");
            return null;
        }
        userSet.add(email);
        try {
            int index = registration.size() + 1;
            User newUser = new User();
            newUser.email = email;
            newUser.pubKey = pubKey;
            registration.put(index, newUser);
            broadcast();
            System.out.println(index);
            System.out.println(newUser.email);
            
            return index;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }


    //Method to get information of a specific item
    @Override
    public AuctionItem getSpec(int userID,int itemID, String token) throws RemoteException {

        try {
            broadcast();
            return auctionItems.get(itemID).getAuctionItem();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }

    //Method to start a new auction
    @Override
    public Integer newAuction(int userID, AuctionSaleItem item, String token) throws RemoteException {

        try {
            System.out.println(itemCounter);
            System.out.println("name: " + item.name);
            System.out.println("description: " + item.description);
            System.out.println("highestBid: " + item.reservePrice);

            //put the item in the hashMap
            auctionItems.put(itemCounter, new AuctionNow(itemCounter, item, userID));
            System.out.println("put item" + itemCounter);
            //tell backup servers the auctionItems has been updated
            broadcast();
            //return the id of the auction
            return itemCounter++;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
            // TODO: handle exception
        }
    }


    //Method to get the list of auction items
    @Override
    public AuctionItem[] listItems(int userID, String token) throws RemoteException { 

        AuctionItem[] listItems = new AuctionItem[auctionItems.size()];
        int itemCount = 0;

        //turn the hashmap to an array
        for (Map.Entry<Integer, AuctionNow> entry : auctionItems.entrySet()) {
            AuctionItem auctionItem = entry.getValue().getAuctionItem();
            listItems[itemCount++] = auctionItem;
        }
        broadcast();
        return listItems;
    }


    //Method to close an auction
    @Override
    public AuctionResult closeAuction(int userID, int itemID, String token) throws RemoteException {
        //get the information of the target item
        AuctionNow item = auctionItems.get(itemID);
        //check if the user sent this request is the creator of the auction
        if(userID != item.getUserID())
        {
            System.out.println("Not the creator");
            return null;
        }

        //if yes, get the information of the winner and returns it
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
            //update the servers
            broadcast();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
            }
        }


        //Method to bid
    @Override
    public boolean bid(int userID, int itemID, int price, String token) throws RemoteException {
        try {
            AuctionNow targetItem = auctionItems.get(itemID);
            if(price > targetItem.getAuctionItem().highestBid)
            {
                targetItem.setBidder(userID);
                targetItem.setBid(price);
                broadcast();
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
    }

    @Override
    public TokenInfo authenticate(int userID, byte[] signature) throws RemoteException {
        return null;
    }
 
    @Override
    public int getPrimaryReplicaID() throws RemoteException {
        return -1;
    }
}