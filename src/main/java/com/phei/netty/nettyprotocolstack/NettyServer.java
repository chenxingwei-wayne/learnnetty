package com.phei.netty.nettyprotocolstack;

import com.phei.netty.nettyprotocolstack.auth.LoginAuthRespHandler;
import com.phei.netty.nettyprotocolstack.encoderanddecoder.NettyMessageDecoder;
import com.phei.netty.nettyprotocolstack.encoderanddecoder.NettyMessageEncoder;
import com.phei.netty.nettyprotocolstack.heartbeat.HeartBeatRespHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/*这个case暂时还没有跑通，需要debug下原因*/
public class NettyServer {

	private static final Log LOG = LogFactory.getLog(NettyServer.class);

    public void bind() throws Exception {
	// 配置服务端的NIO线程组
	EventLoopGroup bossGroup = new NioEventLoopGroup();
	EventLoopGroup workerGroup = new NioEventLoopGroup();
	ServerBootstrap b = new ServerBootstrap();
	b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
		.option(ChannelOption.SO_BACKLOG, 100)
		.handler(new LoggingHandler(LogLevel.INFO))
		.childHandler(new ChannelInitializer<SocketChannel>() {
		    @Override
		    public void initChannel(SocketChannel ch)
			    throws IOException {
			ch.pipeline().addLast(
				new NettyMessageDecoder(1024 * 1024, 4, 4));
			ch.pipeline().addLast(new NettyMessageEncoder());
			ch.pipeline().addLast("readTimeoutHandler",
				new ReadTimeoutHandler(50));
			ch.pipeline().addLast(new LoginAuthRespHandler());
			ch.pipeline().addLast("HeartBeatHandler",
				new HeartBeatRespHandler());
		    }
		});

	// 绑定端口，同步等待成功
	b.bind(NettyConstant.REMOTEIP, NettyConstant.PORT).sync();
	LOG.info("Netty server start ok : "
		+ (NettyConstant.REMOTEIP + " : " + NettyConstant.PORT));
    }

    public static void main(String[] args) throws Exception {
	new NettyServer().bind();
    }
}
