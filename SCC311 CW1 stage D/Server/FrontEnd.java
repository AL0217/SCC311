import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.security.*;
import java.util.HashSet;
import java.util.Set;

public class FrontEnd implements Auction{
    //static variables for connecting to replica
    public static int id;
    public static Auction priReplica;
    public static String priReplicaName = "";
    public static Registry priRegistry;
    public static Set<String> connected = new HashSet<>();
    public static void main(String args[])
    {
        try {
            FrontEnd s = new FrontEnd();
            String name = "FrontEnd";
            Auction auc = (Auction)UnicastRemoteObject.exportObject(s, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, auc);
            

            priRegistry = LocateRegistry.getRegistry("localhost");
            String[] registryList =  priRegistry.list();
            for(int i = 0; i < registryList.length; i++)
            {
                System.out.println(i + "." + registryList[i]);
            }

            NewReplica();
            System.out.println("FrontEnd ready");
            
            // keyHandle handler = new keyHandle();
            // handler.generateKeys();
            // pubKey = handler.getPublicKey();
            // privKey = handler.getPrivateKey();
            // System.out.println(pubKey);
            // handler.storePublicKey(pubKey, "../keys/serverKey.pub");
            
            // priReplica = (Auction) priRegistry.bind();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();        
        }
    }


    private static void NewReplica(){
        try {
            priReplica = null;
            priRegistry = LocateRegistry.getRegistry("localhost");
            String[] registryList =  priRegistry.list();
            for(int i = 0; i < registryList.length; i++)
            {
                if(registryList[i].equals("Auction"))
                {
                    continue;
                }

                try {   
                    priReplica = (Auction) priRegistry.lookup(registryList[i]); 
                    //call a method on it. If it fails move on to next one 
                    priReplica.challenge(0, "");
                    priReplicaName = registryList[i];
                    System.out.println("Connected to " + registryList[i]);
                    break;
                } catch (NotBoundException | RemoteException re) {
                    System.out.println(registryList[i] + " Failed");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Integer register(String email, PublicKey pubKey) throws RemoteException {
        try {
            return priReplica.register(email, pubKey);
        } catch (Exception e) {
            System.out.println("Failed to Connect");
            NewReplica();
            return priReplica.register(email, pubKey);
        }
    }

    @Override
    public AuctionItem getSpec(int userID,int itemID, String token) throws RemoteException {
        try {
            return priReplica.getSpec(userID, itemID, token);
        } catch (Exception e) {
            NewReplica();
            return priReplica.getSpec(userID, itemID, token);
        }
        
    }

    @Override
    public Integer newAuction(int userID, AuctionSaleItem item, String token) throws RemoteException {
        try {
            return priReplica.newAuction(userID, item, token);
        } catch (Exception e) {
            NewReplica();
            return priReplica.newAuction(userID, item, token);
        }
        
       
    }

    @Override
    public AuctionItem[] listItems(int userID, String token) throws RemoteException { 
        try {
            return priReplica.listItems(userID, token);
        } catch (Exception e) {
            NewReplica();
            return priReplica.listItems(userID, token);
        }
    }

    @Override
    public AuctionResult closeAuction(int userID, int itemID, String token) throws RemoteException {
        try {
            return priReplica.closeAuction(userID, itemID, token);
        } catch (Exception e) {
            NewReplica();
            return priReplica.closeAuction(userID, itemID, token);
        }
        
    }

    @Override
    public boolean bid(int userID, int itemID, int price, String token) throws RemoteException {
        try {
            return priReplica.bid(userID, itemID, price, token);
        } catch (Exception e) {
            NewReplica();
            return priReplica.bid(userID, itemID, price, token);
        }
        
    }

    @Override
    public ChallengeInfo challenge(int userID, String clientChallenge) throws RemoteException {
        return priReplica.challenge(userID, clientChallenge);
    }

    @Override
    public TokenInfo authenticate(int userID, byte[] signature) throws RemoteException {
        return null;
    }

 

    @Override
    public int getPrimaryReplicaID() throws RemoteException {
        // TODO Auto-generated method stub
        return Integer.parseInt(priReplicaName.substring(priReplicaName.lastIndexOf("a") + 1));
    }
}
