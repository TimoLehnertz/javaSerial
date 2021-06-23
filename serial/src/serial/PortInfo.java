package serial;

import com.fazecast.jSerialComm.SerialPort;

/**
 * Wrapper class for SerialPort
 * 
 * @author Timo Lehnertz
 *
 */
public class PortInfo {

	private int baudRate;
	private String descriptivePortName;
	private String portDescription;
	private String systemPortName;
	
	public PortInfo(SerialPort p) {
		super();
		baudRate = p.getBaudRate();
		descriptivePortName = p.getDescriptivePortName();
		portDescription = p.getPortDescription();
		systemPortName = p.getSystemPortName();
	}

	public int getBaudRate() {
		return baudRate;
	}

	public String getDescriptivePortName() {
		return descriptivePortName;
	}

	public String getPortDescription() {
		return portDescription;
	}

	public String getSystemPortName() {
		return systemPortName;
	}
}