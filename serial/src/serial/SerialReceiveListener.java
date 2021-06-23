package serial;

/**
 * 
 * @author Timo Lehnertz
 *
 */
public interface SerialReceiveListener {

	/**
	 * Called when a line has been received
	 */
	public void receive(String msg);
}