package cn.level;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by mr-level on 17-5-14.
 */
public class JSONParseSpeedTest {

    final String settings = "{\"totalOrderLines\":\"entryOrder.totalOrderLines\",\"entryOrderCode\":\"entryOrder.entryOrderCode\",\"orderLines\":[{\"g2MappingName\":\"cargoList\",\"batchs\":[{\"g2MappingName\":\"batchs\",\"batchName\":\"batchName\",\"batchCode\":\"batchCode\",\"entrybatchCode\":\"=>entryOrder.entryOrderCode\",\"items\":[{\"g2MappingName\":\"items\",\"itemCode\":\"itemCode\",\"entrybatchCode\":\"=>entryOrder.entryOrderCode\"}]}],\"orderLineNo\":\"orderLineNo\",\"outBizCode\":\"outBizCode\",\"ownerCode\":\"ownerCode\"}],\"getInfo\":{\"getCompany\":\"getInfo.getCompany\"}}";

    @Test
    public void testFastJson() {


        JSONObject jsonObject = JSONObject.parseObject(settings);

    }

    @Test
    public void testGson() {


        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(settings).getAsJsonObject();


    }

    @Test
    public void testJackson() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(settings);

    }
}
