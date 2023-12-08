import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;

public class FrontEnd implements Auction{
    public static int id;
    static Auction priReplica;
    public static void main(String args[])
    {
        try {
            Replica s = new Replica();
            String name = "Auction";
            Auction auc = (Auction)UnicastRemoteObject.exportObject(s, 0);

            // keyHandle handler = new keyHandle();
            // handler.generateKeys();
            // pubKey = handler.getPublicKey();
            // privKey = handler.getPrivateKey();
            // System.out.println(pubKey);
            // handler.storePublicKey(pubKey, "../keys/serverKey.pub");

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, auc);
            System.out.println("FrontEnd ready");

            
            Registry priRegistry = LocateRegistry.getRegistry("localhost");
            String[] registryList =  priRegistry.list();
            String priReplicaName;
            for(int i = 0; i < registryList.length; i++)
            {
                if(startsWith("Replica"))
                {
                    priReplicaName = registryList[i];
                    System.out.println(priReplicaName);
                    break;
                }
            }
            Auction priReplica = (Auction) priRegistry.lookup(priReplicaName);
            
            // priReplica = (Auction) priRegistry.bind();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();        
        }
    }

    private void getNewReplica(){

        try {
            Registry update = LocateRegistry.getRegistry("localhost");
            String[] newList = update.list();

        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    public Integer register(String email, PublicKey pubKey) throws RemoteException {
        try {
            return priReplica.register(email, pubKey);
        } catch (Exception e) {
            //TODO: handle exception
            return null;
        }   
        
    }

    @Override
    public AuctionItem getSpec(int userID,int itemID, String token) throws RemoteException {
        return priReplica.getSpec(userID, itemID, token);
    }

    @Override
    public Integer newAuction(int userID, AuctionSaleItem item, String token) throws RemoteException {
        return priReplica.newAuction(userID, item, token);
    }

    @Override
    public AuctionItem[] listItems(int userID, String token) throws RemoteException { 
        return priReplica.listItems(userID, token);
    }

    @Override
    public AuctionResult closeAuction(int userID, int itemID, String token) throws RemoteException {
        return priReplica.closeAuction(userID, itemID, token);
    }

    @Override
    public boolean bid(int userID, int itemID, int price, String token) throws RemoteException {
        return priReplica.bid(userID, itemID, price, token);
    }

    @Override
    public ChallengeInfo challenge(int userID, String clientChallenge) throws RemoteException {
        return priReplica.challenge(userID, clientChallenge);
    }

    @Override
    public TokenInfo authenticate(int userID, byte[] signature) throws RemoteException {
        return priReplica.authenticate(userID, signature);
    }

 

    @Override
    public int getPrimaryReplicaID() throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPrimaryReplicaID'");
    }
}
