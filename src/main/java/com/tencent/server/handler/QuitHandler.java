package com.tencent.server.handler;

import com.tencent.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "h.QuitHandler")
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter{

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception{
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("{} 已经断开...", ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
        log.debug("{}异常断开， 异常为{}", ctx.channel(), cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }
}
