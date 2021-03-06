package cn.level;

import org.junit.Test;

/**
 * Created by mr_level on 17-5-14.
 */
public class JSONConverterTest {

    @Test
    public void testConvert() {

        String sourceData = "";
        String settings = "";

        long start = System.currentTimeMillis();

        JSONConverter jsonConverter = new JSONConverter();
        String result = jsonConverter.convert(sourceData, settings);

        long end = System.currentTimeMillis();
        System.out.println("fastJson expend " + (end-start) + " ms");
        System.out.println("fastJson result: " + result);
    }


}
