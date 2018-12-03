package me.yorick.network.message.field;

import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public abstract class BinaryMessage {

	private final Field<?>[] fields;
	private final ByteBuf buffer;
	
	protected BinaryMessage() {
		this.fields = getFields();
		int length = Arrays.stream(fields).mapToInt(f -> f.getLength()).sum();
		buffer = Unpooled.buffer(length+MessageType.BYTES);
	}
	
    protected abstract Field<?>[] getFields();
    
    protected abstract MessageType getMessageType();
	
	protected <T> void writeValueToField(Field<T> field, T value) {
		field.writeToBuffer(value, buffer);
	}
	
}
