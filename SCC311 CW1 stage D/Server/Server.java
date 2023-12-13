import java.rmi.RemoteException;
import java.util.*;
// import javax.crypto.SealedObject;

public interface Server extends Auction {
    public void broadcast() throws RemoteException;

    public void updateSharedVariable(HashMap<Integer, AuctionNow> items, HashMap<Integer, User> reg, Set<String> users, int itemCount) throws RemoteException;

    public HashMap<Integer, AuctionNow> getItems() throws RemoteException;

    public HashMap<Integer, User> getRegistry() throws RemoteException;

    public Set<String> getUserSet() throws RemoteException;

    public int getItemCount() throws RemoteException;
}