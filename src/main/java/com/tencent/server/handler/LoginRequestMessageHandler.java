package com.tencent.server.handler;

import com.tencent.message.LoginRequestMessage;
import com.tencent.message.LoginResponseMessage;
import com.tencent.server.service.UserServiceFactory;
import com.tencent.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage>{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception{
        String username = msg.getUsername();
        String password = msg.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, password);
        // 向客户端返回登录响应信息
        LoginResponseMessage message;
        if (login) {
            // 登录成功, 将username, channel存入session中，以便后面发送消息根据用户获取channel
            SessionFactory.getSession().bind(ctx.channel(), username);
            message = new LoginResponseMessage(true, "登录成功!");
        } else {
            message = new LoginResponseMessage(false, "登录失败, 用户名或密码错误!");
        }
        ctx.writeAndFlush(message);
    }
}
