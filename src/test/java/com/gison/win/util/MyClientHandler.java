package com.gison.win.util;

import com.alibaba.fastjson.JSON;
import com.gison.win.constant.CONSTANT;
import com.gison.win.constant.TBOMProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


/**
 * bcc异或校验法
 * <p>
 * 实现方法：将所有数据都和一个指定的初始值（通常是0）异或一次，所得结果为校验值。接收方收到数据后自己也计算一次异或和校验值，如果和收到的校验值一致就说明收到的数据是完整的。
 * <p>
 * 特点：应用于很多基于串口的通讯方法
 *
 * @author <a href="mailto:gisonwin@qq.com">GisonWin</a>
 * @date 2019/8/29 11:18
 */
@Slf4j
public class MyClientHandler extends SimpleChannelInboundHandler<TBOMProtocol> {
    private static String sendContent = "HEARTBEAT";
    private static int reconnectCount;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    private void handleReaderIdle(ChannelHandlerContext ctx) {
        TBOMProtocol protocol = NettyUtils.createHeartBeat(sendContent.getBytes());
        log.info("handle reader idle = " + protocol.toString());
        ctx.writeAndFlush(protocol);
    }

    private void handleWriterIdle(ChannelHandlerContext ctx) {
        TBOMProtocol protocol = NettyUtils.createHeartBeat(sendContent.getBytes());
        log.info("handle writer  idle = " + protocol.toString());
        ctx.writeAndFlush(protocol);
    }

    private void handleAllIdle(ChannelHandlerContext ctx) {
        TBOMProtocol protocol = NettyUtils.createHeartBeat(sendContent.getBytes());
        log.info("handle all idle = " + protocol.toString());
        ctx.writeAndFlush(protocol);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = ((InetSocketAddress) ctx.channel().remoteAddress());
        log.warn("客户端[ " + ctx.channel().localAddress() + " ]被服务端[ " + address + " ]断开 @ " + LocalDateTime.now());

        ctx.fireChannelInactive();
        new Thread(() -> {
            try {
                reconnect(address, ctx);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                try {
                    reconnect(address, ctx);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void reconnect(InetSocketAddress address, ChannelHandlerContext ctx) throws Exception {
        new MyClient().connect(address);
        TimeUnit.MICROSECONDS.sleep(500L);
//        if (10 == reconnectCount) {
//            log.error(reconnectCount + " count ,close it");
//            ctx.close();
//            System.exit(0);
//        }
        reconnectCount++;
        log.warn("reccont NO. " + reconnectCount + " count(s)");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().localAddress() + "客户端连接到了服务器:" + ctx.channel().remoteAddress() + " @ " + LocalDateTime.now());
        client_request_tbomlist(ctx);//
//        fileTransferTest(ctx);

    }

    /**
     * @param ctx
     * @throws IOException
     */
    private void fileTransferTest(ChannelHandlerContext ctx) throws IOException {
        String suffix = "docx";
        byte[] bytes = Files.readAllBytes(Paths.get("D:", "2." + suffix));
        String fileJson = NettyUtils.bytes2hex(bytes);//.byteArrToBinStr(bytes);
//        System.out.println("2进制===>" + fileJson);
        String json = "{\"ID\":\"10000\",\"Suffix\":\"" + suffix + "\",\"Report\":\"" + fileJson + "\"}";
//        System.out.println("json==> " + json);
        int length = json.getBytes().length;
        TBOMProtocol protocol = new TBOMProtocol();
        protocol.setHeader(CONSTANT.HEADER);
        protocol.setPkgLength(length + 2 + 4 + 2 + 2 + 1 + 2);
        protocol.setTail(CONSTANT.TAIL);
        protocol.setMsgResp((short) 0x00);
        protocol.setBusinessCode((short) 0x04);//type
//        protocol.setRespCode((short) 0x00);
        protocol.setCheckBit((byte) 0x01);
        protocol.setContent(json.getBytes());
        ctx.writeAndFlush(protocol);
    }

    private void client_request_tbomlist(ChannelHandlerContext ctx) {
//        for (int i = 0; i < 10; i++) {
        TBOMProtocol protocol = NettyUtils.createLoadTBOM("request_tbom_list".getBytes());
        ctx.writeAndFlush(protocol);
//        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TBOMProtocol msg) throws Exception {
        //从服务端接收到的消息
        //接收server返回tbom列表.并返回接收成功的信息
//        log.info("Server return data = " + msg.toString() + ", " + LocalDateTime.now());
//        log.info("businesscode =" + msg.getBusinessCode());
        short businessCode = msg.getBusinessCode();
        switch (businessCode) {
            case 1://TBOM List
                log.info("client recv data from server" + ctx.channel().remoteAddress() + " ,msg = " + msg.toString());
                log.info("客户端接收到Server发送的TBOM列表.成功");
//            应答OK
                String respJson = "{\"ID\":\"\",\"BID\":\"1\",\"Code\":\"0\",\"Message\":\"sucess\"}";
                TBOMProtocol protocol = NettyUtils.genRespProtocol(respJson);
                log.info("客户端接收TBOM列表正确,并返回给服务端应答" + protocol.toString());
                ctx.writeAndFlush(protocol);
                break;
            case 2://客户端接收 服务端下发的要执行的tbom 项目 id和state
                String s = new String(msg.getContent());
                log.info("客户端接收到服务端下发的执行TBOM命令==>" + s);
                String id = JSON.parseObject(s).get("ID").toString();
                //客户端向服务端写回执行成功的命令
                respJson = "{\"ID\":\"" + id + "\",\"BID\":\"2\",\"Code\":\"0\",\"Message\":\"sucess\"}";
                TBOMProtocol protocol1 = NettyUtils.genRespProtocol(respJson);
                log.info("客户端 启动 TBOM 项目成功,并向服务端返回成功的消息应答");
                ctx.writeAndFlush(protocol1);
                //发送进度
                for (int i = 1; i <= 100; i++) {
                    String progressJson = "{\"ID\":\"" + id  + "\",\"Progress\":\"" + i + "%\",}";
                    TBOMProtocol protocol2 = NettyUtils.genProgressProtocol(progressJson);
                    ctx.writeAndFlush(protocol2);
//                    TimeUnit.SECONDS.sleep(1);
//                    progressJson = "{\"ID\":\"" + id  + "\",\"Progress\":\"" + i + "%\",}";
//                    protocol2 = NettyUtils.genProgressProtocol(progressJson);
//                    ctx.writeAndFlush(protocol2);
//                    progressJson = "{\"ID\":\"" + id + "\",\"Progress\":\"" + i + "%\",}";
//                    protocol2 = NettyUtils.genProgressProtocol(progressJson);
//                    ctx.writeAndFlush(protocol2);
                    log.info("客户端 主动向服务端发送"+id+"进度 " + i + "%");
                    TimeUnit.SECONDS.sleep(1);
                }
                //进度完成后发送项目执行结束的应答
                String completeJson = "{\"ID\":\""+id +"\",\n" +
                        "\"State\":\"5\",\n" +
                        "}";
                TBOMProtocol completeProtocol = NettyUtils.createPerformTBOM(completeJson);
                ctx.writeAndFlush(completeProtocol);
//                completeJson = "{\"ID\":\"316\",\n" +
//                        "\"State\":\"5\",\n" +
//                        "}";
//                completeProtocol = NettyUtils.createPerformTBOM(completeJson);
//                ctx.writeAndFlush(completeProtocol);
//                completeJson = "{\"ID\":\"280\",\n" +
//                        "\"State\":\"5\",\n" +
//                        "}";
//                completeProtocol = NettyUtils.createPerformTBOM(completeJson);
//                ctx.writeAndFlush(completeProtocol);
                log.info("客户端向服务端发送该项目执行完毕的命令消息");
                log.info("==================================================");
                log.info("项目执行完成,发送该项目的测试报告");
                fileTransferTest(ctx);
                break;

            case 3:

                break;

            case 4:
                break;

            case 5:

                break;

            case 6:
                break;

            default:
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
