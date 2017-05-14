package cn.level;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by bfliu on 17-5-14.
 */
public class JSONReadSpeedTest {

    final String sourceData = "";
    final String splitChar = ".";

    @Test
    public void testFastJson() {

        Map<String, Object> sourceMapFast = Maps.newHashMap();

        long start = System.currentTimeMillis();

        this.readDataIntoMapFast(sourceMapFast, sourceData, "");

        long end = System.currentTimeMillis();

        System.out.println("fastjson use: " + (end - start) + "ms");
        System.out.println(JSON.toJSONString(sourceMapFast));
    }

    @Test
    public void TestGson() throws IOException {

        Map<String, Object> sourceMapG = Maps.newHashMap();

        long start = System.currentTimeMillis();

        this.readDataIntoMapG(sourceMapG, sourceData, "");

        long end = System.currentTimeMillis();

        System.out.println("Gson use: " + (end - start) + "ms");
        System.out.println(JSON.toJSONString(sourceMapG));
    }

    @Test
    public void TestJackson() {

        Map<String, Object> sourceMapJack = Maps.newHashMap();

        long start = System.currentTimeMillis();

        this.readDataIntoMapFast(sourceMapJack, sourceData, "");

        long end = System.currentTimeMillis();

        System.out.println("jackson use: " + (end - start) + "ms");
        System.out.println(JSON.toJSONString(sourceMapJack));
    }

    //fastJSON
    private void readDataIntoMapFast(Map<String, Object> sourceMap, String source, String prefiex) {

        JSONObject sourceObj = JSONObject.parseObject(source);

        prefiex += splitChar;

        for (Map.Entry<String, Object> entry : sourceObj.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();

            String prefiexKey = prefiex + key;
            if (value instanceof JSONObject) {
                this.readDataIntoMapFast(sourceMap, ((JSONObject) value).toJSONString(), prefiexKey);
            } else if (value instanceof JSONArray) {

                JSONArray jsonArray = (JSONArray) value;
                int size = jsonArray.size();

                for (int i = 0; size > 0 && i < size; i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    this.readDataIntoMapFast(sourceMap, jsonObject.toJSONString(), prefiexKey + splitChar + (i + 1));

                }

            } else {
                sourceMap.put(prefiexKey, value);
            }
        }
    }

    //GSON
    private void readDataIntoMapG(Map<String, Object> sourceMap, String source, String prefiex) throws IOException {

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(source).getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if(value.isJsonObject()) {
                this.readDataIntoMapG(sourceMap, value.toString(), prefiex + splitChar + key);
            }else if(value.isJsonArray()) {
                JsonArray jsonArray = value.getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonElement jsonElement = jsonArray.get(i);
                    this.readDataIntoMapG(sourceMap, jsonElement.toString(), prefiex + splitChar + key + splitChar + (1+i));
                }
            }else {
                sourceMap.put(prefiex + splitChar + key, value.getAsString());
            }

        }
    }


    private Map<String, Object> readDataIntoMapJack(Map<String, Object> sourceMap, String source, String prefix) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(source);
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.getFields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String key = entry.getKey();
            JsonNode value = entry.getValue();
            if (value.isObject()) {
                readDataIntoMapJack(sourceMap, value.toString(), prefix + splitChar + key);
            } else if (value.isArray()) {
                for (int i = 0; i < value.size(); i++) {
                    JsonNode arrNode = value.get(i);
                    readDataIntoMapJack(sourceMap, arrNode.toString(), prefix + splitChar + key + splitChar + (1 + i));
                }
            } else {
                sourceMap.put(prefix + splitChar + key, value.toString());
            }
        }

        return sourceMap;
    }

}
