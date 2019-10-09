package com.phei.netty.nettyprotocolstack.heartbeat;

import com.phei.netty.nettyprotocolstack.MessageType;
import com.phei.netty.nettyprotocolstack.NettyMessage;
import com.phei.netty.nettyprotocolstack.pojo.Header;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import javax.swing.*;

public class HeartBeatRespHandler extends ChannelHandlerAdapter {
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        NettyMessage nettyMessage = (NettyMessage) msg;
        // 返回心跳应答消息
        if (nettyMessage.getHeader() != null && nettyMessage.getHeader().getType() == MessageType.HEARTBEAT_REQ.value()) {
            System.out.println("Receive cliet heartbeat message: ---->" + nettyMessage);
            NettyMessage heartBeat = buildHeartBeat();
            System.out.println("sent heart beat response message to client : " + heartBeat);
            ctx.writeAndFlush(heartBeat);
        } else {
            ctx.fireChannelRead(msg);
        }


    }

    private NettyMessage buildHeartBeat() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_RESP.value());
        message.setHeader(header);
        return message;
    }

}
