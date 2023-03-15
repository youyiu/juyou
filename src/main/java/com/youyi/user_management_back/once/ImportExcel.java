package com.youyi.user_management_back.once;

import com.alibaba.excel.EasyExcel;
import org.junit.Test;

import java.util.List;


/**
 * 导入Excel数据
 */
public class ImportExcel {
    /**
     * 监听器读数据
     */
    @Test
    public void simpleRead() {
        String fileName = "";
        EasyExcel.read(fileName, XingQiuTableUserInfo.class, new TableListener()).sheet().doRead();
    }
    /**
     * 同步读数据
     * @param fileName
     */
    public static void synchronousRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<XingQiuTableUserInfo> list = EasyExcel.read(fileName).head(XingQiuTableUserInfo.class).sheet().doReadSync();
        for (XingQiuTableUserInfo data : list) {
            System.out.println(data);
        }
    }


}
