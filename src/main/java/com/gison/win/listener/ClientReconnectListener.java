//package com.linose.cloudplatform.listener;
//
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelFutureListener;
//import io.netty.channel.EventLoop;
//import lombok.extern.slf4j.Slf4j;
//
//import java.net.InetSocketAddress;
//import java.time.LocalDateTime;
//import java.util.concurrent.TimeUnit;
//
///***
// *   @author <a href="mailto:gisonwin@qq.com>GisonWin</a>
// *   @create 2019-08-31 9:06
// */
//@Slf4j
//public class ClientReconnectListener implements ChannelFutureListener {
//    private InetSocketAddress address;
//
//    public ClientReconnectListener(InetSocketAddress address) {
//        this.address = address;
//    }
//
//    @Override
//    public void operationComplete(ChannelFuture future) throws Exception {
//        if (future.isSuccess()) {
//            log.info("client start success, " + LocalDateTime.now());
//            return;
//        }
//        EventLoop loop = future.channel().eventLoop();
//        loop.schedule(() -> {
//            new MyClient().connect(address);
//            log.info(" client start done. @ " + LocalDateTime.now());
//            try {
//                TimeUnit.MICROSECONDS.sleep(500);
//            } catch (Exception ex) {
//                log.error(ex.getMessage(), ex);
//            }
//        }, 1L, TimeUnit.SECONDS);
//    }
//}
