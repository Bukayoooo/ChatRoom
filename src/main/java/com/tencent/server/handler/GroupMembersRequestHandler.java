package com.tencent.server.handler;

import com.tencent.message.GroupMembersRequestMessage;
import com.tencent.message.GroupMembersResponseMessage;
import com.tencent.server.session.GroupSession;
import com.tencent.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Set;

@ChannelHandler.Sharable
public class GroupMembersRequestHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage>{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception{
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Set<String> members = groupSession.getMembers(msg.getGroupName());
        ctx.writeAndFlush(new GroupMembersResponseMessage(members));
    }
}
