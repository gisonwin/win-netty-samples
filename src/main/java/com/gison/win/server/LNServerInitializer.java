package com.gison.win.server;

import com.gison.win.codec.TBOMEncoder;
import com.gison.win.codec.TBOMDecoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:gisonwin@qq.com">GisonWin</a>
 * @date 2019/8/29 11:23
 */
public class LNServerInitializer extends ChannelInitializer<SocketChannel> {
    private Map<String, Channel> activeChannelMap;

    public LNServerInitializer(Map<String, Channel> activeChannelMap) {
        this.activeChannelMap = activeChannelMap;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //编解码
        pipeline.addLast(new TBOMDecoder());
        pipeline.addLast(new TBOMEncoder());
        //超时Handler 心跳检测
        pipeline.addLast(new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS));
        pipeline.addLast(new LNServerHandler(this.activeChannelMap));
    }
}
