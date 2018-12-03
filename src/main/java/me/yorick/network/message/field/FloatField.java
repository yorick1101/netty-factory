package me.yorick.network.message.field;

import io.netty.buffer.ByteBuf;

public final class FloatField extends Field<Float>{

	@Override
	void writeToBuffer(Float value, ByteBuf buffer) {
		buffer.writeFloat(value);
	}

	@Override
	Float readFromBuffer(ByteBuf buffer) {
		return buffer.readFloat();
	}

	@Override
	int getLength() {
		return Float.BYTES;
	}

}
