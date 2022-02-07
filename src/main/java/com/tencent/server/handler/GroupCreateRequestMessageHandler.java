package com.tencent.server.handler;

import com.tencent.message.GroupCreateRequestMessage;
import com.tencent.message.GroupCreateResponseMessage;
import com.tencent.server.session.Group;
import com.tencent.server.session.GroupSession;
import com.tencent.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

// 在服务器中创建群聊
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage>{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception{
        // 获取创建的群聊名称
        String groupName = msg.getGroupName();
        // 获取群成员
        Set<String> members = msg.getMembers();
        // 使用群管理器创建群聊
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);

        // group返回null，群聊创建成功
        if(group == null){
            ctx.channel().writeAndFlush(new GroupCreateResponseMessage(true, groupName + "群聊创建成功！"));
            // 获取所有在线群友的socketChannel, 向群友发送群创建成功消息
            List<Channel> channels = groupSession.getMembersChannel(groupName);

            for(Channel channel : channels){
                channel.writeAndFlush(new GroupCreateResponseMessage(true, "您已经被拉入" + groupName));
            }
        }else{
            ctx.writeAndFlush(new GroupCreateResponseMessage(false, "群聊创建失败，" + groupName + "已经存在！"));
        }
    }
}
