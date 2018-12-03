package me.yorick.network.message.field;

import io.netty.buffer.ByteBuf;

public final class DoubleField extends Field<Double>{

	@Override
	void writeToBuffer(Double value, ByteBuf buffer) {
		buffer.writeDouble(value);
	}

	@Override
	Double readFromBuffer(ByteBuf buffer) {
		return buffer.readDouble();
	}

	@Override
	int getLength() {
		return Double.BYTES;
	}

}
