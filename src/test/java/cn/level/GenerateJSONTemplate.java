package cn.level;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.Map;

/**
 * Created by LiuBinfeng on 2017/5/15.
 */
public class GenerateJSONTemplate {

    @Test
    public void testGenerateTemplate() {
        String sourceData = "";

        JSONObject jsonObject = generateTemplate(JSONObject.parseObject(sourceData));

        System.out.println(jsonObject);
    }

    public JSONObject generateTemplate(JSONObject sourceObject) {

        JSONObject template = new JSONObject();

        for (Map.Entry<String, Object> entry : sourceObject.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();
            String templateKey = key + "_template";

            if (value instanceof JSONObject) {
                JSONObject jsonObject = this.generateTemplate((JSONObject)value);
                jsonObject.put("parentNode", key);
                template.put(templateKey, jsonObject);
            } else if (value instanceof JSONArray) {

                JSONArray valueArray = (JSONArray) value;
                int size = valueArray.size();

                for (int i = 0; size > 0 && i < 1; i++) {

                    JSONObject arrObject = valueArray.getJSONObject(i);
                    JSONObject jsonObject = this.generateTemplate(arrObject);

                    jsonObject.put("parentNode", key);

                    JSONArray array = new JSONArray();
                    array.add(jsonObject);
                    template.put(templateKey, array);
                }

            } else {
                template.put(templateKey, key);
            }
        }

        return template;
    }
}
