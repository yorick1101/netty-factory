package me.yorick.network.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class BinaryMessage {

	private final byte title;
	private ByteBuf buffer;
	
	protected BinaryMessage(byte title, int length) {
		buffer = Unpooled.buffer(length);
		buffer.writeByte(title);
		this.title = title;
	}
	
	public void setBuffer(byte[] data) {
		buffer = Unpooled.wrappedBuffer(data);
	}
	
	protected ByteBuf getBuffer() {
		return buffer;
	}
	
	public byte[] getData() {
		return buffer.array();
	}
	
	public byte getTitle() {
		return title;
	}
	
}
