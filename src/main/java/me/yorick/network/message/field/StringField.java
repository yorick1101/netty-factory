package me.yorick.network.message.field;

import io.netty.buffer.ByteBuf;

public final class StringField extends Field<String>{

	private int length;
	
	public StringField(final int length) {
		this.length = length;
	}
	
	@Override
	void writeToBuffer(String value, ByteBuf buffer) {
		if(value!=null) {
			byte[] bytes = value.getBytes();
			if(length>=bytes.length)
				buffer.writeBytes(bytes);
			else
				buffer.writeBytes(bytes, 0, length);
		}
	}

	@Override
	String readFromBuffer(ByteBuf buffer) {
		byte[] data = new byte[length];
		buffer.readBytes(data);
		return new String(data);
	}

	@Override
	int getLength() {
		return length;
	}

}
