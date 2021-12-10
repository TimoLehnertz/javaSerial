package serial;

import serial.ArduinoSerialInterface.DataType;

public class FieldSetup {
	protected String name;
	protected DataType dataType;
	
	public FieldSetup(String name, DataType dataType) {
		super();
		this.name = name;
		this.dataType = dataType;
	}
}