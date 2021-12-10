package serial;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import maths.Vec3;

public class ArduinoSerialInterface extends Serial implements SerialByteReceiveListener {
	
	public enum DataType {
		INT, DOUBLE, VEC3, FUNCTION
	}
	
	public static void main(String[] args) {
		ArduinoSerialInterface serial = new ArduinoSerialInterface();
		serial.setup.put((byte) 0, new FieldSetup("i", DataType.INT));
		serial.selectPort("COM6");
		serial.addOpenListeners(() -> {
			System.out.println("opened");
			serial.getInt(0);
		});
		Scanner s = new Scanner(System.in);
		s.next();
		s.close();
		serial.close();
	}
	
	private static final byte GET_COMMAND = 0;
	private static final byte SET_COMMAND = 1;
	private static final byte WRITE_COMMAND = 2;
	private static final byte READ_COMMAND = 3;
	private static final byte FACTORY_RESET_COMMAND = 4;
	
	private static final double DOUBLE_PRECISION = 100.0;
	
	public Map<Byte, FieldSetup> setup = new HashMap<>();
	
	private byte[] bytes = new byte[100];
	private int byteSize = 0;
	
	public ArduinoSerialInterface() {
		this(null);
	}
	
	public ArduinoSerialInterface(String port) {
		super(port);
		setup.put((byte) 0, new FieldSetup("i", DataType.INT));
		addByteReceiveListener(this);
	}
	
	public void writeEEPROM() {
		write(WRITE_COMMAND);
	}
	
	public void readEEPROM() {
		write(READ_COMMAND);
	}
	
	public void factoryReset() {
		write(FACTORY_RESET_COMMAND);
	}
	
	private int parseInt(byte[] bytes) {
		return (int) (((long) bytes[0] << 8) + bytes[1]);
	}
	
	public void getInt(int id) {
		write(GET_COMMAND);
		write(id);
	}
	
	public void set(byte id, int value) {
		write(SET_COMMAND);
		write(id);
		write(value);
	}
	
	public void set(byte id, long value) {
		write(SET_COMMAND);
		write(id);
		writeVal(value);
	}

	public void set(byte id, Vec3 value) {
		write(SET_COMMAND);
		write(id);
		writeVal(value);
	}
	
	private void writeVal(int val) {
		write((byte) ((long) val >> 8));
		write((byte) val);
	}
	
	private void writeVal(double val) {
		int iVal = (int) (val * DOUBLE_PRECISION);
		write((byte) ((long) iVal >> 16));
		write((byte) ((long) iVal >> 8));
		write((byte) iVal);
	}
	
	private void writeVal(Vec3 vec) {
		writeVal(vec.x);
		writeVal(vec.y);
		writeVal(vec.z);
    }

	@Override
	public void byteReceived(int b) {
		System.out.println(b);
//		bytes[byteSize++] = b;
//		if(byteSize >= 2) {
//			System.out.println(parseInt(bytes));
//			byteSize = 0;
//		}
	}
}