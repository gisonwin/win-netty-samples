package com.gison.win.util;

import com.gison.win.constant.CONSTANT;
import com.gison.win.constant.TBOMProtocol;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;

/**
 * @author <a href="mailto:gisonwin@qq.com">GisonWin</a>
 * @date 2019/8/29 13:41
 */
@Slf4j
public class NettyUtils {
    private static final int FIXED_LENGTH = 13;//2 + 2 + 1 + 2 + 4 + 2
    public static final String TBOMLIST = "tbomlist";
    public static final String REPORTPATH = "reportpath";
    public static final String TBOMMAP = "tbommap";

    /***
     * int value to byte array.
     * @param intVal
     * @return
     */
    public static byte[] intToBytes(int intVal) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (intVal >>> (24 - i * 8));
        }
        return b;
    }

    /***
     * short value to byte array
     * @param shortVal
     * @return
     */
    public static byte[] shortToBytes(short shortVal) {
        byte[] b = new byte[2];
        for (int i = 0; i < 2; i++) {
            b[i] = (byte) (shortVal >>> (i * 8));
        }
        return b;
    }


    /***
     * HEADER
     * @return
     */
    private static byte[] header() {
        byte[] header = new byte[2];//HEADER 6c69
        header[1] = (byte) 0x69 & 0xff;
        header[0] = (byte) ((0x6c00 & 0xff00) >> 8);
        return header;
    }

    /***
     *TAIL
     * @return
     */
    private static byte[] tail() {
        byte[] tail = new byte[2];//TAIL 0x6e6f
        tail[0] = (byte) ((0x6e00 & 0xff00) >> 8);
        tail[1] = 0x6f & 0xff;
        return tail;
    }

    /**
     * 组装 加载TBOM 数据.
     *
     * @param sendContent
     * @return
     */
    public static TBOMProtocol createLoadTBOM(byte[] sendContent) {
        try {
            return genProtocol(sendContent, (short) 0x01);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /***
     * 组装心跳包
     */
    public static TBOMProtocol createHeartBeat(byte[] sendMsg) {
        return genProtocol(sendMsg, (short) 0x06);
    }

    private static TBOMProtocol genProtocol(byte[] sendMsg, short businessCode) {
        TBOMProtocol protocol = new TBOMProtocol();
        genHeader(protocol);
        protocol.setBusinessCode(businessCode);//发送type
        genData(sendMsg, protocol, businessCode);
        genTail(protocol);
        return protocol;
    }

    /***
     * 返回应答码
     * @return
     */
    public static TBOMProtocol genRespProtocol(String respJson) {
        return genProtocol(respJson.getBytes(CharsetUtil.UTF_8), (short) 0x05);
    }

    /***
     * 客户端 主动向服务端 发送项目进度.
     * @param progressJson
     * @return
     */
    public static TBOMProtocol genProgressProtocol(String progressJson) {
        return genProtocol(progressJson.getBytes(CharsetUtil.UTF_8), (short) 0x03);
    }

    /***
     * generate data method.
     * @param sendMsg
     * @param protocol
     */
    private static void genData(byte[] sendMsg, TBOMProtocol protocol, short businessCode) {
        int length = sendMsg.length + FIXED_LENGTH;
        protocol.setPkgLength(length);
        protocol.setMsgResp((short) 0x00);//发送方填0 返回时填1方向
//        protocol.setRespCode((short) 0x00);
        protocol.setContent(sendMsg);
        byte[] checkbit = new byte[8 + sendMsg.length];
        //copy length to check bit
        System.arraycopy(intToBytes(length), 0, checkbit, 0, 4);
        //copy msg resp to check bit
        System.arraycopy(shortToBytes((short) 0), 0, checkbit, 4, 2);
        //copy business code to check bit
        System.arraycopy(shortToBytes(businessCode), 0, checkbit, 6, 2);
        //copy resp code to check bit
//        System.arraycopy(shortToBytes((short) 0), 0, checkbit, 8, 2);
        //copy content to check bit
        System.arraycopy(sendMsg, 0, checkbit, 8, sendMsg.length);
        protocol.setCheckBit(getXor(checkbit));
    }


    /***
     *
     * @param b
     * @return
     */
    public static short bytesToShort(byte[] b) {
        return (short) (((b[1] << 8) | b[0] & 0xff));
    }

    /***
     *
     * @param bytes
     * @return
     */
    public static String bytes2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            sb.append(tmp);
        }
        return sb.toString();
    }

    /*
     * 将16进制的字符串装换为对应的byte数组，例如"A5000C5A81000000000000000000010E90AA" 转换为对应的数组形式
     *
     * @param hexString
     * @return 转换后的数组
     */
    public static byte[] hexStringToBytes(String hexString) {
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /***
     *  对数组数据进行异或处理.
     * @param data
     * @return
     */
    public static byte getXor(byte[] data) {
        byte temp = data[0];
        for (int i = 1; i < data.length; i++) {
            temp ^= data[i];
        }
        return temp;
    }

    /***执行TBOM启动指令实现.
     *
     * @param tbomContent
     * @return
     */
    public static TBOMProtocol createPerformTBOM(String tbomContent) {
        return genProtocol(tbomContent.getBytes(CharsetUtil.UTF_8), (short) 0x02);
    }

    /**
     * byte数组转换为二进制字符串,每个字节以","隔开
     **/
    public static String byteArrToBinStr(byte[] b) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            result.append(Long.toString(b[i] & 0xff, 2) + ",");
        }
        return result.toString().substring(0, result.length() - 1);
    }

    /**
     * 二进制字符串转换为byte数组,每个字节以","隔开
     **/
    public static byte[] binStrToByteArr(String binStr) {
        String[] temp = binStr.split(",");
        byte[] b = new byte[temp.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = Long.valueOf(temp[i], 2).byteValue();
        }
        return b;
    }

    /***
     * 将int value转换为32位的16进制字符串(模拟生成32位的UUID).
     * @param value
     * @return
     */
    public static String longValueTo32HexString(long value) {
        String hex = Long.toHexString(value);
        String format = String.format("%32s", hex);
        format = format.replaceAll(" ", "0");
        return format;
    }

    /***
     * 将int value转换为32位的16进制字符串(模拟生成32位的UUID).
     * @param value
     * @return
     */
    public static String intValueTo32HexString2(int value) {
        String hex = Integer.toHexString(value);
        int length = hex.length();
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < 32 - length; i++) {

            sb.append("0");
        }
        sb.append(hex);

        return sb.toString().trim();
    }

    /***
     * 将int value转换为32位的16进制字符串(模拟生成32位的UUID).
     * @param value
     * @return
     */
    public static String intValueTo32HexString(int value) {
        String hex = Integer.toHexString(value);
        String format = String.format("%32s", hex);
        format = format.replaceAll(" ", "0");
        return format;
    }

    /***
     * 32位16进制数据转为int值.
     * @param value
     * @return
     */
    public static int hexStringToInt(String value) {
        BigInteger bigInteger = new BigInteger(value, 16);
        return bigInteger.intValue();
    }

    /***
     * 32位16进制数据转为Long型.
     * @param value
     * @return
     */
    public static long hexStringToLong(String value) {
        BigInteger bigInteger = new BigInteger(value, 16);
        return bigInteger.longValue();
    }

    private static void genTail(TBOMProtocol protocol) {
        protocol.setTail(CONSTANT.TAIL);
    }

    private static void genHeader(TBOMProtocol protocol) {
        protocol.setHeader(CONSTANT.HEADER);
    }
}
