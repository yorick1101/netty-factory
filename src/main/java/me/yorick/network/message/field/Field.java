package me.yorick.network.message.field;

import io.netty.buffer.ByteBuf;

/**
 * 
 * Design a field class which can be able to both restrict supported type and also verify the value is an instance of the type
 * 
 * @author Yorick
 *
 */
public abstract class Field<T>{

	abstract void writeToBuffer(T value, ByteBuf buffer, int start);
	abstract T readFromBuffer(ByteBuf buffer, int start);
	
}
