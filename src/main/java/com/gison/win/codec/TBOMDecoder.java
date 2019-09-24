package com.gison.win.codec;

import com.gison.win.constant.CONSTANT;
import com.gison.win.constant.TBOMProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author <a href="mailto:gisonwin@qq.com">GisonWin</a>
 * @date 2019/8/29 11:02
 */
@Slf4j
public class TBOMDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int BASE_LENGTH = 2 + 4;
        if (in.readableBytes() > BASE_LENGTH) {//HEADER+length 2+4 ,可读字节要大于header和包体长度才会解码
            TBOMProtocol protocol = new TBOMProtocol();
            //读取Header
            int beginReader;
//一直读数据,直到讲到的数据是header以后,再向后读取数据
            short header;
            while (true) {
                beginReader = in.readerIndex();
                in.markReaderIndex();//标记包头开始的位置
                if ((header = in.readShort()) == CONSTANT.HEADER) {
                    //读到了header, 开始读后面的数据
                    break;
                }
                in.resetReaderIndex();//重置reader index
                in.readByte();
                // 未读到包头，略过一个字节
                // 每次略过，一个字节，去读取，包头信息的开始标记
                // 当略过，一个字节之后，
                // 数据包的长度，又变得不满足
                // 此时，应该结束。等待后面的数据到达
                if (in.readableBytes() < BASE_LENGTH) {
                    return;
                }
            }

            protocol.setHeader(header);
            //判断header是否正确
            log.debug("read HEADER is " + header);
            //读包体长度
            int length = in.readInt();
            log.debug("package length =" + length);
            protocol.setPkgLength(length);
            int dataLength = length - BASE_LENGTH;//后续数据的长度
            // 判断请求数据包数据是否到齐
            if (in.readableBytes() < dataLength) {
                // 还原读指针
                in.readerIndex(beginReader);
                return;
            }
            //读取数据
            //读取消息应答
            //消息应答 2bytes ,值 0,1 方向
            short respMsg = in.readShort();
            log.debug("方向:" + respMsg);
            protocol.setMsgResp(respMsg);
            //业务代码 2bytes
            short bcode = in.readShort();
            log.debug("业务码: " + bcode);
            protocol.setBusinessCode(bcode);
            //应答码 2 bytes
//            short respCode = in.readShort();
//            protocol.setRespCode(respCode);
            //data
            byte[] data = new byte[dataLength - 2 - 2 - 2 - 1];
            in.readBytes(data);//将后续内容读入content 数组中
//            log.debug("decoder content = " + new String(data, CharsetUtil.UTF_8));
            protocol.setContent(data);
            //校验位 1 bytes
            protocol.setCheckBit(in.readByte());
            //尾部 2 bytes
            short tail = in.readShort();
            protocol.setTail(tail);
            out.add(protocol);
        }
    }
}
