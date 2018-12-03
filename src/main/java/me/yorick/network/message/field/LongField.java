package me.yorick.network.message.field;

import io.netty.buffer.ByteBuf;

public class LongField extends Field<Long>{

	@Override
	void writeToBuffer(Long value, ByteBuf buffer) {
		buffer.writeLong(value);
	}

	@Override
	Long readFromBuffer(ByteBuf buffer) {
		return buffer.readLong();
	}

	@Override
	int getLength() {
		return Long.BYTES;
	}

}
