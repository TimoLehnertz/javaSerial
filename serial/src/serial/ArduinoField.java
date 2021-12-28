package serial;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ArduinoField {
	
	public enum ArduinoType {
		Byte,Int,Float
	}
	
	protected ArduinoSerialInterface serial;
	/**
	 * ID in arduino
	 */
	private int id;
	private List<Consumer<Object>> callbacks = new ArrayList<>();
	ArduinoType type;
	protected int size;
	protected int qty;
	
	public ArduinoField(int id, ArduinoType type) {
		this(id, type, 1);
	}
	
	public ArduinoField(int id, ArduinoType type, int qty) {
		super();
		if(qty < 1) {
			throw new RuntimeException("Qty cant be < 1");
		}
		this.id = id;
		this.type = type;
		this.qty = qty;
		this.size = sizeFromType(type);
	}

	public void getByte(Consumer<Byte> callback) {
		if(type != ArduinoType.Byte) throw new RuntimeException("Can't get byte from a " + type.name() + " field!");
		sendGet();
		callbacks.add(obj -> {
			callback.accept((byte) obj);
		});
	}
	
	@SuppressWarnings("unchecked")
	public void getByteList(Consumer<List<Byte>> callback) {
		if(type != ArduinoType.Byte || qty <= 1) throw new RuntimeException("Can't get byte[] from a " + type.name() + " field of qty " + qty + "!");
		sendGet();
		callbacks.add(obj -> {
			callback.accept((List<Byte>) obj);
		});
	}
	
	public void getBoolean(Consumer<Boolean> callback) {
		if(type != ArduinoType.Byte) throw new RuntimeException("Can't get boolean from a " + type.name() + " field!");
		sendGet();
		callbacks.add(obj -> {
			callback.accept((byte) obj != 0);
		});
	}
	
	@SuppressWarnings("unchecked")
	public void getBooleanList(Consumer<List<Boolean>> callback) {
		if(type != ArduinoType.Byte || qty <= 1) throw new RuntimeException("Can't get boolean[] from a " + type.name() + " field of qty " + qty + "!");
		sendGet();
		callbacks.add(obj -> {
			callback.accept((List<Boolean>) obj);
		});
	}
	
	public void getChar(Consumer<Character> callback) {
		if(type != ArduinoType.Byte) throw new RuntimeException("Can't get char from a " + type.name() + " field!");
		sendGet();
		callbacks.add(obj -> {
			callback.accept((char) obj);
		});
	}
	
	@SuppressWarnings("unchecked")
	public void getCharList(Consumer<List<Character>> callback) {
		if(type != ArduinoType.Byte || qty <= 1) throw new RuntimeException("Can't get char[] from a " + type.name() + " field of qty " + qty + "!");
		sendGet();
		callbacks.add(obj -> {
			callback.accept((List<Character>) obj);
		});
	}
	
	public void getInt(Consumer<Integer> callback) {
		if(type != ArduinoType.Int) throw new RuntimeException("Can't get int from a " + type.name() + " field!");
		sendGet();
		callbacks.add(obj -> {
			callback.accept((int) obj);
		});
	}
	
	@SuppressWarnings("unchecked")
	public void getIntList(Consumer<List<Integer>> callback) {
		if(type != ArduinoType.Int || qty <= 1) throw new RuntimeException("Can't get int[] from a " + type.name() + " field of qty " + qty + "!");
		sendGet();
		callbacks.add(obj -> {
			callback.accept((List<Integer>) obj);
		});
	}
	
	public void getFloat(Consumer<Float> callback) {
		if(type != ArduinoType.Float) throw new RuntimeException("Can't get float from a " + type.name() + " field!");
		sendGet();
		callbacks.add(obj -> {
			callback.accept((float) obj);
		});
	}
	
	@SuppressWarnings("unchecked")
	public void getFloatList(Consumer<List<Float>> callback) {
		if(type != ArduinoType.Float || qty <= 1) throw new RuntimeException("Can't get float[] from a " + type.name() + " field of qty " + qty + "!");
		sendGet();
		callbacks.add(obj -> {
			callback.accept((List<Float>) obj);
		});
	}
	
	public void getDouble(Consumer<Double> callback) {
		if(type != ArduinoType.Float) throw new RuntimeException("Can't get double from a " + type.name() + " field!");
		sendGet();
		callbacks.add(obj -> {
			callback.accept((double) ((float) obj)); // Java trash
		});
	}
	
	@SuppressWarnings("unchecked")
	public void getDoubleList(Consumer<List<Double>> callback) {
		if(type != ArduinoType.Float || qty <= 1) throw new RuntimeException("Can't get double[] from a " + type.name() + " field of qty " + qty + "!");
		sendGet();
		callbacks.add(obj -> {
			callback.accept((List<Double>) obj);
		});
	}
	
	public void set(byte bVal) {
		if(type != ArduinoType.Byte) throw new RuntimeException("Can't set byte of a " + type.name() + " field!");
		sendSet(ByteBuffer.allocate(1).put(bVal).array());
	}
	
	public void set(byte[] bList) {
		if(type != ArduinoType.Byte || qty <= 1) throw new RuntimeException("Can't set byte[] of a " + type.name() + " field with qty=" + qty);
		ByteBuffer buffer = ByteBuffer.allocate(qty);
		for (Byte b : bList) {
			buffer.put(b);
		}
		sendSet(buffer.array());
	}
	
	public void set(boolean b) {
		if(type != ArduinoType.Byte) throw new RuntimeException("Can't set boolean of a " + type.name() + " field!");
		sendSet(ByteBuffer.allocate(1).put((byte) (b ? 1 : 0)).array());
	}
	
	public void set(boolean[] bList) {
		if(type != ArduinoType.Byte || qty <= 1) throw new RuntimeException("Can't set boolean[] of a " + type.name() + " field with qty=" + qty);
		ByteBuffer buffer = ByteBuffer.allocate(qty);
		for (Boolean b : bList) {
			buffer.put((byte) (b ? 1 : 0));
		}
		sendSet(buffer.array());
	}
	
	public void set(char c) {
		if(type != ArduinoType.Byte) throw new RuntimeException("Can't set char of a " + type.name() + " field!");
		sendSet(ByteBuffer.allocate(1).putChar(c).array());
	}
	
	public void set(char[] cList) {
		if(type != ArduinoType.Byte || qty <= 1) throw new RuntimeException("Can't set char[] of a " + type.name() + " field with qty=" + qty);
		ByteBuffer buffer = ByteBuffer.allocate(qty);
		for (char c : cList) {
			buffer.putChar(c);
		}
		sendSet(buffer.array());
	}
	
	public void set(int iVal) {
		if(type != ArduinoType.Int) throw new RuntimeException("Can't set int of a " + type.name() + " field!");
		sendSet(ByteBuffer.allocate(4).putInt(iVal).array());
	}
	
	public void set(int[] iList) {
		if(type != ArduinoType.Int || qty <= 1) throw new RuntimeException("Can't set int[] of a " + type.name() + " field with qty=" + qty);
		ByteBuffer buffer = ByteBuffer.allocate(qty);
		for (int i : iList) {
			buffer.putInt(i);
		}
		sendSet(buffer.array());
	}
	
	public void set(float f) {
		if(type != ArduinoType.Float) throw new RuntimeException("Can't set float of a " + type.name() + " field!");
		sendSet(ByteBuffer.allocate(4).putFloat(f).array());
	}
	
	public void set(float[] fList) {
		if(type != ArduinoType.Float || qty <= 1) throw new RuntimeException("Can't set float[] of a " + type.name() + " field with qty=" + qty);
		ByteBuffer buffer = ByteBuffer.allocate(qty);
		for (float f : fList) {
			buffer.putFloat(f);
		}
		sendSet(buffer.array());
	}
	
	public void set(double d) {
		if(type != ArduinoType.Float) throw new RuntimeException("Can't set double of a " + type.name() + " field!");
		sendSet(ByteBuffer.allocate(4).putFloat((float) d).array());
	}
	
	public void set(double[] dList) {
		if(type != ArduinoType.Float || qty <= 1) throw new RuntimeException("Can't set double[] of a " + type.name() + " field with qty=" + qty);
		ByteBuffer buffer = ByteBuffer.allocate(qty);
		for (double f : dList) {
			buffer.putFloat((float) f);
		}
		sendSet(buffer.array());
	}
	
	/**
	 * <SET>       ::= S <ID> <Size> <Value> <Checksum>
	 */
	private void sendSet(byte[] value) {
		byte[] b = new byte[size * qty + 4];
		int checksum = initSet(b);
		
		for (int i = 0; i < value.length; i++) {
			b[i + 3] = value[i];
		}
		
		checksum = ArduinoSerialInterface.addChecksum(checksum, value, value.length);
		b[b.length - 1] = (byte) checksum;
		serial.print(b);
	}
	
	private int initSet(byte[] b) {
		int checksum = 0;
		b[0] = 'S'; checksum = ArduinoSerialInterface.addChecksum(checksum, 'S');
		b[1] = (byte) id; checksum = ArduinoSerialInterface.addChecksum(checksum, (byte) id); // @todo check
		b[2] = (byte) (size * qty); checksum = ArduinoSerialInterface.addChecksum(checksum, (byte) (size * qty));
		return checksum;
	}
	
	private void sendGet() {
		byte[] b = {'G', (byte) getId(), (byte) (('G' + getId()) % 255)};
		serial.print(b, 3);
	}
	
	protected void receive(byte[] bytes) {
		if(bytes.length != size * qty) return;
		Object newVal;
		if(qty > 1) {
			List<Object> newListVal = new ArrayList<Object>();
			for (int i = 0; i < qty; i++) {
				newListVal.add(parse(bytes, i * size));
			}
			newVal = newListVal;
		} else {
			newVal = parse(bytes, 0);
		}
		
		for (Consumer<Object> c : callbacks) {
			c.accept(newVal);
		}
		callbacks.clear();
	}
	
	protected Object parse(byte[] arr, int start) {
		switch(type) {
		case Byte: return arr[start];
		case Int: return ByteBuffer.wrap(arr, start, size).getInt();
		case Float: return ByteBuffer.wrap(arr, start, size).getFloat();
		default: return null;
		}
	}

	public int getId() {
		return id;
	}
	
	private static int sizeFromType(ArduinoType type) {
		switch(type) {
		case Byte: return 1;
		case Float: return 4;
		case Int: return 4;
		default: return 0;
		}
	}
}