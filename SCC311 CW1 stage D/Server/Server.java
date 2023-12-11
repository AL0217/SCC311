import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.*;
// import javax.crypto.SealedObject;

public interface Server extends Auction {
    public void broadcast() throws RemoteException;
}