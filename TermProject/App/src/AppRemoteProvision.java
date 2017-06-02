import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * specify what request will be accepted by Owner App
 */
public interface AppRemoteProvision extends Remote {
	public void intrusionDetected() throws RemoteException;
	public void situationFinished() throws RemoteException;
	public void statusMessage(String strMsg) throws RemoteException;
}
