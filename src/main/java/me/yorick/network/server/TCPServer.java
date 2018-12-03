package me.yorick.network.server;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;

public class TCPServer implements Server{

	private static Logger logger = LoggerFactory.getLogger(TCPServer.class);

	private final int port;

	public TCPServer(final int port) {
		this.port = port;
	}

	@Override
	public void start() throws Exception {
		EventLoopGroup eventLoopGroup=EventLoopGroupFactory.createEventLoopGroup(EventLoopGroupFactory.TYPE.SELECTOR);
		try{
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(eventLoopGroup);
			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.childOption(ChannelOption.TCP_NODELAY , true);
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE  , true);
			bootstrap.handler(new AcceptorChannelHandler());
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new MessageChannelHandler());
				}
			});
			Channel ch = bootstrap.bind(port).sync().channel();
			ch.closeFuture().sync();
			
		}finally {
			eventLoopGroup.shutdownGracefully();
		}
	}

	private static class AcceptorChannelHandler extends ChannelInboundHandlerAdapter{

		
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) {
			logger.info("acceptor active");
		}
		
		@Override
		public void channelRegistered(ChannelHandlerContext ctx) {
			logger.info("acceptor registered");
		}
		
		
	}
	
	
	private static class MessageChannelHandler extends  ChannelDuplexHandler{

		private final static int MAX_CONNECTION= 1;
		private static AtomicInteger index = new AtomicInteger(0);

		
		@Override
		public void channelRegistered(ChannelHandlerContext ctx) {
			if(index.getAndIncrement()<MAX_CONNECTION) {
				logger.info("child registered");
				ctx.fireChannelRegistered();
			}else {
				logger.warn("over connection limit");
				ctx.close();
			}
				
		}
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) {
			logger.info("child active");
			ctx.fireChannelActive();
		}
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			ByteBuf in = (ByteBuf) msg;
		    try {
		        while (in.isReadable()) {
		            System.out.println((char) in.readByte());
		            System.out.flush();
		        }
		    } finally {
		        ReferenceCountUtil.release(msg);
		    }
		}
		
		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) {
			logger.info("ReadComplete");
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, java.lang.Throwable cause) {
			logger.error("exception",cause);
		}
		
	}





}
