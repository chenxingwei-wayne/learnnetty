package com.phei.netty.useudpwithnetty.udpclient.handler;

import com.phei.netty.useudpwithnetty.model.LogEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    protected void messageReceived(ChannelHandlerContext ctx, LogEvent msg) throws Exception {
        String builder = msg.getReceived() +
                " [" +
                msg.getSource().toString() +
                "] [" +
                msg.getLogfile() +
                "] : [" +
                msg.getMsg();
        System.out.println(builder);
    }
}
