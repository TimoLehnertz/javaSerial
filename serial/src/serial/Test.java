package serial;

public class Test {

	public static void main(String[] args) {
		Serial serial = new Serial("COM5");
		serial.addReceiveListener(new SerialReceiveListener() {
			@Override
			public void receive(String msg) {
				System.out.println("received: " + msg);
			}
		});
	}
}