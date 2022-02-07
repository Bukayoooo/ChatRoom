package com.tencent.server.handler;

import com.tencent.message.GroupChatRequestMessage;
import com.tencent.server.session.GroupSession;
import com.tencent.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * 群聊请求信息处理器
 */
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage>{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception{
        // 获取所有群成员socketChannel连接通道
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        List<Channel> channels = groupSession.getMembersChannel(msg.getGroupName());
        for(Channel channel : channels){
            channel.writeAndFlush(new GroupChatRequestMessage(msg.getFrom(), msg.getGroupName(), msg.getContent()));
        }
    }
}
