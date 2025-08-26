package com.gov.common.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtil {
    public static void main(String[] args) {
        String ss="102sq";
        System.out.println(extractNumbers(ss));
    }

    public static Integer extractNumbers(String input) {
        // 定义匹配数字的正则表达式
        String regex = "\\d+";

        // 创建 Pattern 对象
        Pattern pattern = Pattern.compile(regex);

        // 创建 Matcher 对象
        Matcher matcher = pattern.matcher(input);

        // 查找第一个匹配项
        if (matcher.find()) {
            String numberStr = matcher.group();
            return Integer.parseInt(numberStr);
        } else {
            // 没有匹配到数字，返回默认值或抛出异常，根据需求进行处理
            return 0; // 返回默认值 0
            // 或者抛出异常
            // throw new IllegalArgumentException("No number found in the input string");
        }
    }
}
