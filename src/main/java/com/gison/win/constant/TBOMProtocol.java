package com.gison.win.constant;

import lombok.*;

import java.io.Serializable;

/**
 * @author <a href="mailto:gisonwin@qq.com">GisonWin</a>
 * @date 2019/8/29 12:55
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TBOMProtocol implements Serializable {
    private short header = CONSTANT.HEADER;
    private int pkgLength;//4 bytes 包体总长度
    private short msgResp;//消息应答 2bytes 暂时不使用
    private short businessCode;//业务代码 1加载TBOM ,2 执行项目 ,3 接收进度,4 上传报告 , 5 执行结束
    //    private short respCode;//应答码 2bytes
    private byte[] content;//内容长度不定长,根据总长度计算得出.
    private byte checkBit;//检验位 1 bytes.异或校验
    private short tail = CONSTANT.TAIL;
}
