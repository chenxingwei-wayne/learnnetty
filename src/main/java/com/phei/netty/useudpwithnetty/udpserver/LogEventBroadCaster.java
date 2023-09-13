package com.phei.netty.useudpwithnetty.udpserver;

import com.phei.netty.useudpwithnetty.udpserver.encoder.LogEventEncoder;
import com.phei.netty.useudpwithnetty.model.LogEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

public class LogEventBroadCaster {
    private final EventLoopGroup eventLoopGroup;
    private final Bootstrap bootstrap;
    private final File file;


    public LogEventBroadCaster(InetSocketAddress address, File file) {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                // 初始化LogEventBroadCaster时，address传给了LogEventEncoder其实就是handler。handler里面进行了处理，
                // 作为一个DatagramPacket放到输出队列，等待消费。
                .handler(new LogEventEncoder(address));
        this.file = file;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        LogEventBroadCaster logEventBroadCaster = new LogEventBroadCaster(
                // 制定广播地址以及端口。
                new InetSocketAddress("255.255.255.255", 10001),
                new File("D:\\code\\learnnetty\\src\\main\\java\\com\\phei\\netty\\useudpwithnetty\\udpserver\\result"));

        try {
            logEventBroadCaster.run();
        } finally {
            logEventBroadCaster.stop();
        }
    }

    private void run() throws InterruptedException, IOException {
        Channel ch = bootstrap.bind(0).sync().channel();
        long pointer = 0;
        for (; ; ) {
            long len = file.length();
            if (len < pointer) {
                pointer = len;
            } else if (len > pointer) {
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.seek(pointer);
                String line;
                while ((line = raf.readLine()) != null) {
                    ch.writeAndFlush(new LogEvent(null, -1, file.getAbsolutePath(), line));
                }
                pointer = raf.getFilePointer();
                raf.close();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.interrupted();
                break;
            }
        }
    }

    private void stop() {
        eventLoopGroup.shutdownGracefully();
        
    }
}
