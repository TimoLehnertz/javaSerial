package serial;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 * Libary class for comunication over Serial
 * simplest example:
 * 	Serial serial = new Serial("COM5");
		serial.addReceiveListener(new SerialReceiveListener() {
			@Override
			public void receive(String msg) {
				System.out.println("received: " + msg);
			}
		});
		serial.println("moin");
		while(System.currentTimeMillis() % 1000 > 10); // delay
		serial.close();
		//expected output: received: moin
		 * 
 * @author Timo Lehnertz
 *
 */
public class Serial implements AutoCloseable{
	
	private List<SerialReceiveListener> receiveListeners = new ArrayList<>();
	private SerialPort selectedPort = null;
	private byte[] buffer;
	private int bufferSize;
	private Charset charset = StandardCharsets.UTF_8;
	private byte[] termination = {0, 10, 11, 12, 13};
	
	public Serial() {
		this(null);
	}
	
	public Serial(String port) {
		super();
		selectPort(port);
	}
	
	public static List<PortInfo> getSerialPorts() {
		SerialPort[] ports = getPorts();
		List<PortInfo> out = new ArrayList<>();
		for (SerialPort port : ports) {
			out.add(new PortInfo(port));
		}
		return out;
	}
	
	private static SerialPort[] getPorts() {
		return SerialPort.getCommPorts();
	}
	
	public byte[] getTermination() {
		return termination;
	}

	public void setTermination(byte[] termination) {
		this.termination = termination;
	}

	public boolean addReceiveListener(SerialReceiveListener l) {
		return receiveListeners.add(l);
	}
	
	public boolean removeReceiveListener(SerialReceiveListener l) {
		return receiveListeners.remove(l);
	}
	
	private SerialPort getPort(String name) {
		if(name == null) return null;
		SerialPort[] ports = getPorts();
		for (SerialPort port : ports) {
			if(port.getSystemPortName().contentEquals(name) || port.getDescriptivePortName().contentEquals(name) || port.getPortDescription().contentEquals(name)) {
				return port;
			}
		}
		return null;
	}
	public boolean selectPort(String name) {
		SerialPort port = getPort(name);
		if(port == null) return false;
		openPort(port);
		return true;
	}
	
	private void closePort(SerialPort port) {
		port.removeDataListener();
		port.closePort();
	}
	
	private void openPort(SerialPort port) {
		if(selectedPort != null) {
//			if(selectedPort.getSystemPortName().equals(port.getSystemPortName())) {
//				System.out.println("same");
//				return;
//			}
			closePort(selectedPort);
		}
		selectedPort = port;
		port.openPort();
		System.out.println("initiating " + port);
		buffer = new byte[port.getDeviceReadBufferSize()];
		bufferSize = 0;
		System.out.println("buffer size: " + port.getDeviceReadBufferSize());
		System.out.println(port.bytesAvailable() + "Bytes available");
		port.addDataListener(new SerialPortDataListener() {
			@Override
			public void serialEvent(SerialPortEvent arg0) {
				for (byte b : arg0.getReceivedData()) {
					if(isTermination(b)) {
						processBuffer();
					} else {
						buffer[bufferSize] = b;
						bufferSize++;
					}
				}
			}
			
			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
			}
		});
	}
	
	private boolean isTermination(byte b) {
		for (byte t : termination) {
			if(t == b) {
				return true;
			}
		}
		return false;
	}
	
	private void processBuffer() {
		if(bufferSize == 0)
			return;
		String msg = new String(buffer, 0, bufferSize, charset);
		bufferSize = 0;
		for (SerialReceiveListener listener : receiveListeners) {
			listener.receive(msg);
		}
	}
	
	public void close() {
		if(selectedPort == null) {
			return;
		}
		closePort(selectedPort);
	}
	
	public synchronized boolean println(String msg) {
		msg += '\n';
		return print(msg.getBytes(charset), msg.length());
	}
	
	public boolean print(String msg) {
		return print(msg.getBytes(charset), msg.length() + 1);
	}
	
	public boolean print(byte[] msg, int size) {
		if(selectedPort == null) {
			return false;
		}
		selectedPort.writeBytes(msg, size);
		return true;
	}
}