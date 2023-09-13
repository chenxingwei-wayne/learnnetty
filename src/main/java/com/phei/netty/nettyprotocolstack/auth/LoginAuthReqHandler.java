package com.phei.netty.nettyprotocolstack.auth;

import com.phei.netty.nettyprotocolstack.MessageType;
import com.phei.netty.nettyprotocolstack.NettyMessage;
import com.phei.netty.nettyprotocolstack.pojo.Header;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.awt.*;

public class LoginAuthReqHandler extends ChannelHandlerAdapter {

    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(buildLogInReq());
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        NettyMessage message = new NettyMessage();

        // 如果市握手应答消息，需要判断是否认证成功
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
            byte loginResult = (byte) message.getBody();
            if (loginResult != (byte) 0) {
                // 握手失败关闭链接
                ctx.close();
            } else {
                System.out.println("Login is ok: " + message);
                ctx.fireChannelRead(msg);
            }

        } else {
            ctx.fireChannelRead(msg);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.fireExceptionCaught(cause);
    }

    private NettyMessage buildLogInReq() {
        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.value());
        nettyMessage.setHeader(header);
        return nettyMessage;
    }


}
