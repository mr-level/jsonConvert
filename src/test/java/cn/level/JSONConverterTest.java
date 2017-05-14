package cn.level;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

/**
 * Created by mr_level on 17-5-14.
 */
public class JSONConverterTest {

    @Test
    public void testConvert() {

        String sourceData = "{\"getInfo\":{\"getCompany\":\"安徽\"},\"entryOrder\":{\"totalOrderLines\":10,\"entryOrderCode\":3000},\"cargoList\":[{\"batchs\":[{\"batchName\":\"test1111\",\"batchCode\":\"1111\",\"items\":[{\"itemCode\":\"111111\"}]},{\"batchName\":\"test2323\",\"batchCode\":\"2323\"}],\"orderLineNo\":\"333\",\"outBizCode\":\"33\",\"ownerCode\":\"3333\"},{\"batchs\":[{\"batchName\":\"test1\",\"batchCode\":\"1\",\"items\":[{\"itemCode\":\"222222\"}]},{\"batchName\":\"test5656\",\"batchCode\":\"5656\",\"items\":[{\"itemCode\":\"777777\"}]},{\"batchName\":\"test676767\",\"batchCode\":\"6767\",\"items\":[{\"itemCode\":\"676767\"}]},{\"batchName\":\"test2\",\"batchCode\":\"2\"},{\"batchName\":\"test3\",\"batchCode\":\"3\"}],\"orderLineNo\":\"444\",\"outBizCode\":\"44\",\"ownerCode\":\"4444\"},{\"batchs\":[{\"batchName\":\"test3333\",\"batchCode\":\"3333\",\"items\":[{\"itemCode\":\"333333\"}]},{\"batchName\":\"test4545\",\"batchCode\":\"4545\",\"items\":[{\"itemCode\":\"444444\"}]},{\"batchName\":\"test5656\",\"batchCode\":\"5656\"}],\"orderLineNo\":\"555\",\"outBizCode\":\"55\",\"ownerCode\":\"5555\"},{\"batchs\":[{\"batchName\":\"test8888\",\"batchCode\":\"888\",\"items\":[{\"itemCode\":\"10000\"}]},{\"batchName\":\"test4545\",\"batchCode\":\"4545\",\"items\":[{\"itemCode\":\"444444\"}]},{\"batchName\":\"test4545\",\"batchCode\":\"4545\",\"items\":[{\"itemCode\":\"444444\"}]},{\"batchName\":\"test5656\",\"batchCode\":\"5656\"}],\"orderLineNo\":\"666\",\"outBizCode\":\"66\",\"ownerCode\":\"6666\"}]}";
        String settings = "{\"totalOrderLines\":\"entryOrder.totalOrderLines\",\"entryOrderCode\":\"entryOrder.entryOrderCode\",\"orderLines\":[{\"parentNode\":\"cargoList\",\"batchs\":[{\"parentNode\":\"batchs\",\"batchName\":\"batchName\",\"batchCode\":\"batchCode\",\"entrybatchCode\":\"=>entryOrder.entryOrderCode\",\"items\":[{\"parentNode\":\"items\",\"itemCode\":\"itemCode\",\"entrybatchCode\":\"=>entryOrder.entryOrderCode\",}]}],\"orderLineNo\":\"orderLineNo\",\"outBizCode\":\"outBizCode\",\"ownerCode\":\"ownerCode\"}],\"getInfo\":{\"getCompany\":\"getInfo.getCompany\"}}";

        JSONConverter jsonConverter = new JSONConverter();
        long start = System.currentTimeMillis();
        JSONObject result = jsonConverter.convert(sourceData, settings);
        long end = System.currentTimeMillis();
        System.out.println("expend " + (end-start) + " ms");
        System.out.println("result: " + result);
    }
}
