package com.gov.ae.netty;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.regex.Pattern;

public class TdskDataDecoder extends ByteToMessageDecoder {
    // 匹配以tdsk_raem1_开头，后面跟着9个逗号分隔字段的正则
    private static final Pattern DATA_PATTERN =
            Pattern.compile("tdsk_raem1_\\d{3},[^,]+(,[^,]+){8}");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 1. 将缓冲区内容转为字符串（不移动读指针）
        String bufferContent = in.toString(CharsetUtil.UTF_8);

        // 2. 查找第一条完整数据
        int dataEndIndex = findCompleteData(bufferContent);
        if (dataEndIndex == -1) {
            return; // 没有找到完整数据包，等待更多数据
        }

        // 3. 提取完整数据
        String completeData = bufferContent.substring(0, dataEndIndex);
        in.readerIndex(in.readerIndex() + dataEndIndex); // 移动读指针

        // 4. 添加到输出列表
        out.add(completeData.trim());
    }

    private int findCompleteData(String bufferContent) {
        // 查找第一个tdsk_raem1_开头的位置
        int startIdx = bufferContent.indexOf("tdsk_raem1_");
        if (startIdx == -1) {
            return -1;
        }

        // 从开始位置查找第10个逗号的位置
        int commaCount = 0;
        int currentPos = startIdx;
        while (currentPos < bufferContent.length()) {
            if (bufferContent.charAt(currentPos) == ',') {
                commaCount++;
                if (commaCount == 9) {
                    // 找到第10个字段的结尾（可能是数字或换行）
                    int endPos = currentPos + 1;
                    while (endPos < bufferContent.length() &&
                            !Character.isWhitespace(bufferContent.charAt(endPos))) {
                        endPos++;
                    }
                    return endPos;
                }
            }
            currentPos++;
        }
        return -1;
    }
}