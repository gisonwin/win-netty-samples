package com.gison.win.codec;


import com.gison.win.constant.TBOMProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:gisonwin@qq.com">GisonWin</a>
 * @date 2019/8/29 11:07
 */

/****
 *
 * 报文收发格式
 *
 * 序号	内容	含义			长度
 * 1	Header	消息头(6c69)			2字节
 * 2	Size	数据包总体长度			4字节
 * 3	Direction	消息应答（0,1）			2字节
 * 4	BusinessCode	业务代码	1	加载TBOM	2字节
 * 			2	执行项目
 * 			3	接收进度
 * 			4	上传报告
 * 			5	执行结束
 * 			6	心跳
 * 5	Context	协议体	应答/结束	2字节	2+Size
 * 			其它	Size字节
 * 6	Check	校验位			1字节
 * 7	Ending	消息尾(6e6f)			2字节
 *
 */
@Slf4j
public class TBOMEncoder extends MessageToByteEncoder<TBOMProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, TBOMProtocol msg, ByteBuf out) throws Exception {
        writeEncoder(msg, out);
    }

//    private void writebytes(TBOMProtocol msg, ByteBuf out) {
//        out.writeBytes(NettyUtils.shortToBytes(msg.getHeader()));//写头
//        out.writeBytes(NettyUtils.intToBytes(msg.getPkgLength()));//包体长度
//        out.writeBytes(NettyUtils.shortToBytes(msg.getMsgResp()));//消息应答
//        out.writeBytes(NettyUtils.shortToBytes(msg.getBusinessCode()));//业务代码
//        out.writeBytes(NettyUtils.shortToBytes(msg.getRespCode()));//响应码
//        out.writeBytes(msg.getContent());//内容
//        out.writeByte(msg.getCheckBit());//检验位
//        out.writeBytes(NettyUtils.shortToBytes(msg.getTail()));//尾
//    }

    private void writeEncoder(TBOMProtocol msg, ByteBuf out) {
        out.writeShort(msg.getHeader());
        out.writeInt(msg.getPkgLength());
        out.writeShort(msg.getMsgResp());
        out.writeShort(msg.getBusinessCode());
//        out.writeShort(msg.getRespCode());
        byte[] contentBytes = msg.getContent();
//        log.debug("content:   = " + new String(contentBytes, CharsetUtil.UTF_8));
        out.writeBytes(contentBytes);//内容
        out.writeByte(msg.getCheckBit());//检验位
        out.writeShort(msg.getTail());
    }
}
