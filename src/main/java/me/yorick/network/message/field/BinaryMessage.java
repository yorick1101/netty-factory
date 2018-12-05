package me.yorick.network.message.field;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public abstract class BinaryMessage {


	private final ByteBuf buffer;
	
	protected BinaryMessage() {
		buffer = Unpooled.buffer();
	}
	
    
    protected abstract byte getMessageType();
	

    
	
}
