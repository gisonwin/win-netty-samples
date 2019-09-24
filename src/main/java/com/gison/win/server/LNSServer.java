package com.gison.win.server;

import com.gison.win.constant.TBOMProtocol;
import com.gison.win.util.NettyUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:gisonwin@qq.com">GisonWin</a>
 * @date 2019/8/29 10:12
 */
@Slf4j
public class LNSServer {
    private static int port = 80;//default port 80
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    //得到当前连接的channel
    private static Map<String, Channel> activeChannelMap = new ConcurrentHashMap<String, Channel>();

    private Channel channel;

    /***
     *
     * @param port
     */
    public void setPort(int port) {
        LNSServer.port = port;
    }


    public ChannelFuture start() {
        ChannelFuture channelFuture = null;
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new LNServerInitializer(activeChannelMap));
            channelFuture = bootstrap.bind(port).syncUninterruptibly();
            channel = channelFuture.channel();
            log.info("server monitor on port " + port);
//            channelFuture.channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getMessage(), ex);
        } finally {
            if (null != channelFuture & channelFuture.isSuccess()) {
                log.info("server start success,@ {} ", LocalDateTime.now());
            } else {
                log.error("server start error @ {}", LocalDateTime.now());
            }
        }
        return channelFuture;
    }


    /***
     * 执行项目.
     */
    public void performTBOM(String tbomContent) {

        try {
            Channel tbom = activeChannelMap.get("tbom");
            log.debug("current channel ===> " + tbom);
            if (null != tbom) {
                TBOMProtocol performTBOM = NettyUtils.createPerformTBOM(tbomContent);
//                log.info("perform tbom cmd ==> {}", performTBOM.toString());
                tbom.writeAndFlush(performTBOM);
                log.info("writeandflush tbom ===> @ {}  tbomcontent = {}", LocalDateTime.now(), tbomContent);
            } else {
                log.warn("not  channel found ");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /***
     * stop current server.
     */
    public void stop() {
        if (channel == null) {
            return;
        }
        channel.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
