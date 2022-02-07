package com.tencent.server.handler;

import com.tencent.message.GroupJoinRequestMessage;
import com.tencent.message.GroupJoinResponseMessage;
import com.tencent.server.session.Group;
import com.tencent.server.session.GroupSession;
import com.tencent.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage>{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception{

        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.joinMember(msg.getGroupName(), msg.getUsername());
        if(group != null){
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, "您已成功加入" + msg.getGroupName()));
        }else{
            ctx.writeAndFlush(new GroupJoinResponseMessage(false, "群聊不存在！"));
        }
    }
}
