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

    final String settings = "";

    @Test
    public void testFastJson() {


        JSONObject jsonObject = JSONObject.parseObject(settings);

        System.out.println("fastjson: " + jsonObject.toJSONString());
    }

    @Test
    public void testGson() {


        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(settings).getAsJsonObject();

        System.out.println("gson : " + jsonObject.toString());


    }

    @Test
    public void testJackson() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(settings);


    }
}
