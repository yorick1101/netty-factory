package me.yorick.network.message.field;

import io.netty.buffer.ByteBuf;

public final class IntegerField extends Field<Integer>{

	@Override
	void writeToBuffer(Integer value, ByteBuf buffer) {
		buffer.writeInt(value);
	}

	@Override
	Integer readFromBuffer(ByteBuf buffer) {
		return buffer.readInt();
	}

	@Override
	int getLength() {
		return Integer.BYTES;
	}

}
