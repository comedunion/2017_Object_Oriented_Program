import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * specify what request will be accepted by Home
 */
public interface HomeRemoteProvision extends Remote {
	public void makeConnectionToMe(String url, String serviceName) throws RemoteException;
	public void setGuardMode(boolean auto, String dog) throws RemoteException;
}

