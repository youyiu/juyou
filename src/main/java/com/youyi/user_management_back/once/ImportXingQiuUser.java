package com.youyi.user_management_back.once;

import com.alibaba.excel.EasyExcel;
import org.apache.commons.lang3.StringUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 导入星球用户到数据库
 */
public class ImportXingQiuUser {
    String fileName = "";
    public static void synchronousRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<XingQiuTableUserInfo> list = EasyExcel.read(fileName).head(XingQiuTableUserInfo.class).sheet().doReadSync();
        Map<String, List<XingQiuTableUserInfo>> listMap =
                list.stream()
                        .filter(item -> StringUtils.isNoneEmpty(item.getUserName()))
                        .collect(Collectors.groupingBy(XingQiuTableUserInfo::getUserName));

    }
}
