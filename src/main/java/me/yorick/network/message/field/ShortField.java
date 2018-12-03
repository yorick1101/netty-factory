package me.yorick.network.message.field;

import io.netty.buffer.ByteBuf;

public final class ShortField extends Field<Short>{

	@Override
	void writeToBuffer(Short value, ByteBuf buffer) {
		buffer.writeShort(value);
	}

	@Override
	Short readFromBuffer(ByteBuf buffer) {
		return buffer.readShort();
	}

	@Override
	int getLength() {
		return Short.BYTES;
	}

}
