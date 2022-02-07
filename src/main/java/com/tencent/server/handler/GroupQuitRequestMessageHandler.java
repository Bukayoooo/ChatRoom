package com.tencent.server.handler;

import com.tencent.message.GroupQuitRequestMessage;
import com.tencent.message.GroupQuitResponseMessage;
import com.tencent.server.session.Group;
import com.tencent.server.session.GroupSession;
import com.tencent.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class GroupQuitRequestMessageHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception{
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.removeMember(msg.getGroupName(), msg.getUsername());
        if(group != null){
            ctx.writeAndFlush(new GroupQuitResponseMessage(true, "您已退出" + msg.getGroupName()));
        }else{
            ctx.writeAndFlush(new GroupQuitResponseMessage(false, "群聊不存在！"));
        }
    }
}
