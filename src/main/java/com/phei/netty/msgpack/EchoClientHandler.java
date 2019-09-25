package com.phei.netty.msgpack;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoClientHandler extends ChannelHandlerAdapter {

    private final int sendNumber;

    private int counter;
    static final String ECHO_REQ = "Hi wayne, welcome to Netty.$_";

    public EchoClientHandler(int sendNumber) {
        this.sendNumber = sendNumber;
    }

    public void channelActive(ChannelHandlerContext ctx) {
        UserInfo [] userInfos = userInfo();

        for (UserInfo userInfo : userInfos) {
            ctx.write(userInfo);
        }
        ctx.flush();
    }

    private UserInfo[] userInfo() {
        UserInfo [] userInfos = new UserInfo[sendNumber];
        UserInfo userInfo = null;
        for (int i = 0; i < sendNumber; i++) {
            userInfo = new UserInfo();
            userInfo.setAge(i);
            userInfo.setName("ABCDEFG------>" + i);
            userInfos[i] = userInfo;
        }
        return userInfos;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Client receive the msgpack message : " + msg);
        ctx.write(msg);
    }

    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
