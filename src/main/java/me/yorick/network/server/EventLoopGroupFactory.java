package me.yorick.network.server;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;

public class EventLoopGroupFactory {
   
	/**
	*	EpollEventLoopGroup which uses epoll under the covers. Because of this it only works on linux.
    *   KQueueEventLoopGroup which uses kqueue on FreeBSD, not stable yet
	*	DefaultEventLoopGroup which must be used for the local transport.
	*	NioEventLoopGroup which is used for NIO Selector based Channels
	*	OioEventLoopGroup which is used to handle OIO Channel's. Each Channel will be handled by its own EventLoop to not block others.
    **/

	enum TYPE{
		BLOCKING, SELECTOR, LOCAL, EPOLL
	}

	
	public static EventLoopGroup createEventLoopGroup(TYPE type) {
		switch(type) {
		case BLOCKING:
			return new OioEventLoopGroup();
		case EPOLL:
			return new EpollEventLoopGroup();
		case LOCAL:
			return new DefaultEventLoopGroup();
		case SELECTOR:
			return new NioEventLoopGroup();
		default:
			return null;
		
		}
	}

}
