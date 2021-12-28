package serial;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.Timer;

import serial.ArduinoField.ArduinoType;

public class ArduinoSerialInterface extends Serial implements SerialByteReceiveListener {
	
	private static final int TIMEOUT = 100;
	private static final int MAX_SIZE = 255;
	
	private static final int FRAME_TYPE_GET	 	= 'G';
	private static final int FRAME_TYPE_SET 	= 'S';
	private static final int FRAME_TYPE_EXECUTE = 'E';
	
	private static final int MODE_COMPLEX = 0;
	
	public Map<String, ArduinoField> setup = new HashMap<>();
	
	/**
	 * Parsing variables
	 */
	private long lastByteReceived = 0;
	private int framePos = 0;
	private int frameId = 0;
	private int checksum = 0;
	private int frameType;
	private int invalidCheckSums = 0;
	private byte[] valueBuff = new byte[255];
	private int valueBuffSize = 0;
	
	public ArduinoSerialInterface() {
		this(null);
		reset();
	}
	
	public ArduinoSerialInterface(String port) {
		super(port);
		addByteReceiveListener(this);
	}
	
	
	public void put(String identifier, ArduinoField field) {
		setup.put(identifier, field);
		field.serial = this;
	}
	
	public void writeEEPROM() {
		byte[] b = {'W', 'W'};
		print(b, 2);
	}
	
	public void readEEPROM() {
		byte[] b = {'R', 'R'};
		print(b, 2);
	}
	
	public void factoryReset() {
		byte[] b = {'F', 'F'};
		print(b, 2);
	}
	
	public void reboot() {
		byte[] b = {'B', 'B'};
		print(b, 2);
	}
	
	private void reset() {
		framePos = 0;
        checksum = 0;
        valueBuffSize = 0;
	}
	
	protected static int addChecksum(int checksum, char data) {
        return addChecksum(checksum, (byte) data);
    }
	
	protected static int addChecksum(int checksum, byte data) {
        return (checksum + (data & 0xff)) % 255;
    }

	protected static int addChecksum(int checksum, byte[] data, int size) {
        for (int i = 0; i < size; i++) {
            checksum = addChecksum(checksum, data[i]);
        }
        return checksum;
    }
	
	void processGet(int id) {
		
	}
	
	private ArduinoField getArduinoField(int id) {
		for (ArduinoField field : setup.values()) {
			if(field.getId() == id) return field;
		}
		return null;
	}
	
	void processSet(int id, byte[] valueBuff) {
//		System.out.print("Processing set (id=" + id + "), body: ");
//		for (int i : valueBuff) {
//			System.out.print(i);
//			System.out.print(",");
//		}
//		System.out.println();
		
		ArduinoField f = getArduinoField(id);
		if(f == null) {
			System.err.println("No id " + id + " in setup");
		}
		f.receive(valueBuff);
	}
	
	@Override
	public void byteReceived(byte c) {
//		System.out.println("got: " + c);
		if((System.currentTimeMillis() > lastByteReceived + TIMEOUT && framePos > 0) || framePos >= MAX_SIZE) { // timoutand framePos > 1 OR overflow
            reset();
        }
        lastByteReceived = System.currentTimeMillis();
        /**
         * First Byte
         */
        if(framePos == 0) {
            if(c == FRAME_TYPE_GET || c == FRAME_TYPE_SET || c == FRAME_TYPE_EXECUTE) {// Complex
//                mode = MODE_COMPLEX;
            } else {
                reset();
                return;
            }
            frameType = c;
            framePos++;
            checksum = addChecksum(checksum, c);
            return;
        }
        //Following Bytes
        //Mode Complex
        if(framePos == 1) { // ID
            frameId = c;
        } else {
            if(frameType == FRAME_TYPE_GET || frameType == FRAME_TYPE_EXECUTE) {
                if((c & 0xff) == checksum) {
                    if(frameType == FRAME_TYPE_GET) {
                    	processGet(frameId);
                    } else {
//                    	Skip execute
                    }
                } else {
                    invalidCheckSums++;
                    System.out.println("checksum: " + checksum + ", c: " + c);
                    System.out.println("Invalid checksum! (total: " + invalidCheckSums + ")");
                }
                reset();
                return;
            }
            if(frameType == FRAME_TYPE_SET) {
                if(framePos == 2) {
                	valueBuff = new byte[c];
                }
                if(framePos == valueBuff.length + 3) { // pos of checksum
                    if((c & 0xff) == checksum) {
                        processSet(frameId, valueBuff);
                    } else {
                        invalidCheckSums++;
                        System.out.println("checksum: " + checksum + ", c: " + c);
                        System.out.println("Invalid checksum! (total: " + invalidCheckSums + ")");
                    }
                    reset();
                    return;
                }
                if(framePos > 2) {
                    valueBuff[valueBuffSize] = c;
                    valueBuffSize++;
                }
            }
        }
        framePos++;
        checksum = addChecksum(checksum, c);
	}
	
	public static void main(String[] args) {
		ArduinoSerialInterface serial = new ArduinoSerialInterface();
		serial.put("i", new ArduinoField(4, ArduinoType.Int));
		serial.put("d", new ArduinoField(3, ArduinoType.Float));
		serial.put("v", new ArduinoField(2, ArduinoType.Float, 3));
		serial.put("b", new ArduinoField(5, ArduinoType.Byte));
		serial.selectPort("COM10");
		serial.addOpenListeners(() -> {
			serial.setBaudRate(9600);
			System.out.println("opened");
			serial.setup.get("d").getFloat(i -> {
				System.err.println(i);
				
				Timer t = new Timer(100, e -> {					
					serial.setup.get("d").set(300.0f);
					((Timer) e.getSource()).stop();
				});
				t.start();
				t = new Timer(200, e -> {
					serial.setup.get("d").getDouble(l -> System.err.println(l));
					
					((Timer) e.getSource()).stop();
				});
				t.start();
			});
//			serial.setup.get("i").getInt(l -> System.err.println(l));
//			serial.setup.get("d").getFloat(i -> System.err.println(i));
//			serial.setup.get("v").getFloatList(i -> System.err.println(i));
//			serial.setup.get("b").getByte(i -> System.err.println(i));
		});
		Scanner s = new Scanner(System.in);
		s.next();
		s.close();
		serial.close();
	}
}