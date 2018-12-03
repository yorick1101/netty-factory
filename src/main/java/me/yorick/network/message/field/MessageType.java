package me.yorick.network.message.field;

public enum MessageType {
	Order('o');
	
	public static final int BYTES = 1;
	
	private final byte b;
	
	private MessageType(char b) {
		this.b = Character.getDirectionality(b);
	}
	
	public byte getByte() {
		return b;
	}
}
