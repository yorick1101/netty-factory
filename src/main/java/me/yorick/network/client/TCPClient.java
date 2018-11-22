package me.yorick.network.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TCPClient {

	public void connect() throws InterruptedException {
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			Bootstrap b = new Bootstrap(); // (1)
			b.group(workerGroup); // (2)
			b.channel(NioSocketChannel.class); // (3)
			b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ClientChannelHandler());
				}
			});

			// Start the client.
			ChannelFuture f = b.connect("localhost", 1234).sync(); // (5)

			// Wait until the connection is closed.
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
		}
	}

	private static class ClientChannelHandler extends ChannelDuplexHandler{

		@Override
		public void connect(ChannelHandlerContext ctx, java.net.SocketAddress remoteAddress, java.net.SocketAddress localAddress, ChannelPromise promise)  throws java.lang.Exception{
			System.out.println("connect");
			ctx.connect(remoteAddress, localAddress, promise);
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) {
			System.out.println("channelActive");
			final ByteBuf msg = ctx.alloc().buffer(1024);
			msg.writeBytes("Hello".getBytes());
			final ChannelFuture f = ctx.writeAndFlush(msg);
			f.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) {
					System.out.println("write complete");
				}
			});
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			cause.printStackTrace();
			ctx.close();
		}
	}


}
