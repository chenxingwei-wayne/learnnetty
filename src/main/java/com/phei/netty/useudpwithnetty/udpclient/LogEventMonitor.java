package com.phei.netty.useudpwithnetty.udpclient;

import com.phei.netty.useudpwithnetty.udpclient.decoder.LogEventDecoder;
import com.phei.netty.useudpwithnetty.udpclient.handler.LogEventHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

public class LogEventMonitor {

    private final EventLoopGroup eventLoopGroup;
    private final Bootstrap bootstrap;

    public LogEventMonitor(InetSocketAddress address) {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LogEventDecoder());
                        pipeline.addLast(new LogEventHandler());
                    }
                }).localAddress(address);
    }

    public static void main(String[] args) {
        LogEventMonitor monitor = new LogEventMonitor(new InetSocketAddress(10001));
        try {
            Channel channel = monitor.bind();
            System.out.println("LogEventMonitor running");
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            monitor.stop();
        }
    }

    private void stop() {
        eventLoopGroup.shutdownGracefully();
    }


    private Channel bind() {
        return bootstrap.bind().syncUninterruptibly().channel();
    }
}
