package com.tencent.server.handler;

import com.tencent.message.ChatRequestMessage;
import com.tencent.message.ChatResponseMessage;
import com.tencent.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage>{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception{
        // 获取要发送的对象
        String to_username = msg.getTo();
        // 获取该对象对应的链接通道channel
        Channel channel = SessionFactory.getSession().getChannel(to_username);
        if (channel != null) {
            // 用户在线
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
//                                        ctx.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
        } else {
            ctx.writeAndFlush(new ChatResponseMessage(false, "用户不存在或者不在线！"));
        }
    }
}
