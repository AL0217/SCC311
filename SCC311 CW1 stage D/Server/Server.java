import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.*;
import java.util.*;
// import javax.crypto.SealedObject;

public interface Server extends Auction {
    public void broadcast() throws RemoteException;

    public void updateSharedVariable(HashMap<Integer, AuctionNow> items, HashMap<Integer, User> reg, Set<String> users, int itemCount) throws RemoteException;
}