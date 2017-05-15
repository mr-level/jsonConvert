package cn.level;

import org.junit.Test;

/**
 * Created by LiuBinfeng on 2017/5/15.
 */
public class JSONConverterWithGsonTest {

    @Test
    public void testConvert() {

        String sourceData = "";
        String settings = "";

        long start = System.currentTimeMillis();

        JSONConverterWithGson jsonConverter = new JSONConverterWithGson();
        String result = jsonConverter.convert(sourceData, settings);

        long end = System.currentTimeMillis();
        System.out.println("Gson expend " + (end-start) + " ms");
        System.out.println("Gson result: " + result);
    }
}
