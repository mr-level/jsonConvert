package cn.level;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.*;

import java.util.Map;
import java.util.Set;

/**
 * Created by bfliu on 2017/5/15.
 */
public class JSONConverterWithGson {

    private final String splitChar = ".";
    private final String parentNode = "parentNode";
    JsonParser jsonParser = new JsonParser();
    Gson gson = new Gson();

    public String convert(String sourceData, String templateData) {

        JsonObject sourceObj = jsonParser.parse(sourceData).getAsJsonObject();
        JsonObject template = jsonParser.parse(templateData).getAsJsonObject();

        return this.convert(sourceObj, template);
    }


    public String convert(JsonObject source, JsonObject template) {

        Map<String, JsonElement> sourceMap = Maps.newHashMap();

        this.readDataIntoMapWithGson(sourceMap, source, "");
        System.out.println(gson.toJson(sourceMap));

        JsonObject targetJSON = this.createTargetJSONWithGson(sourceMap, template, "");

        return targetJSON.toString();

    }

    private void readDataIntoMapWithGson(Map<String, JsonElement> sourceMap, JsonObject sourceObj, String prefix) {

        prefix += splitChar;
        for (Map.Entry<String, JsonElement> entry : sourceObj.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            String prefixKey = prefix + key;
            if (value.isJsonObject()) {
                this.readDataIntoMapWithGson(sourceMap, (JsonObject) value, prefixKey);
            } else if (value.isJsonArray()) {
                JsonArray jsonArray = value.getAsJsonArray();
                int size = jsonArray.size();
                for (int i = 0; size > 0 && i < size; i++) {
                    JsonElement jsonElement = jsonArray.get(i);
                    this.readDataIntoMapWithGson(sourceMap, jsonElement.getAsJsonObject(), prefixKey + splitChar + (1 + i));
                }
            } else {
                sourceMap.put(prefixKey, value);
            }
        }

    }

    private JsonObject createTargetJSONWithGson(Map<String, JsonElement> sourceMap, JsonObject template, String prefix) {

        JsonObject resultObject = new JsonObject();

        JsonElement jsonElement = template.get(parentNode);
        if(jsonElement != null) {
            String parentNodeName = jsonElement.getAsString();
            prefix += splitChar + parentNodeName;
        }

        //size用于计算此返回值JSONObject是否全为空
        int size = template.size();

        for (Map.Entry<String, JsonElement> entry : template.entrySet()) {

            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if (value instanceof JsonObject) {

                JsonObject targetJSON = this.createTargetJSONWithGson(sourceMap, (JsonObject) value, prefix);
                if (targetJSON != null) {
                    resultObject.add(key, targetJSON);
                } else {
                    resultObject.add(key, new JsonObject());
                    size--;
                }

            } else if (value instanceof JsonArray) {

                JsonArray targetJSONArray = this.createTargetArrayWithGson(sourceMap, prefix, (JsonArray) value);
                if (targetJSONArray != null) {
                    resultObject.add(key, targetJSONArray);
                } else {
                    resultObject.add(key, new JsonArray());
                    size--;
                }

            } else {

                String sourceKey = value.getAsString();
                if (sourceKey.startsWith("=>")) {

                    size--;
                    sourceKey = splitChar + sourceKey.substring(2);
                    JsonElement targetValue = sourceMap.get(sourceKey);

                    resultObject.add(key, targetValue == null ? null : targetValue);

                } else {

                    sourceKey = prefix + splitChar + sourceKey;
                    JsonElement targetValue = sourceMap.get(sourceKey);

                    if (targetValue != null) {
                        resultObject.add(key, targetValue);
                    } else {
                        resultObject.add(key, null);
                        size--;
                    }
                }
//                System.out.println("sKey: " + sourceKey);

            }
        }

        if (size == 0) {
            return null;
        } else if (size < 0) {
            throw new RuntimeException("data error！");
        }

        return resultObject;
    }

    private JsonArray createTargetArrayWithGson(Map<String, JsonElement> sourceMap, String prefix, JsonArray templateArray) {

        JsonArray resultArray = new JsonArray();
        JsonElement templateArrayElement = templateArray.get(0);
        JsonObject templateObject = templateArrayElement.getAsJsonObject();

        JsonElement templateObjElement = templateObject.get(parentNode);
        String parentNodeName = templateObjElement.getAsString();

        if (Strings.isNullOrEmpty(parentNodeName)) {
            throw new RuntimeException(prefix + "parentNode cannot be empty!");
        }

        if (prefix.length() == 1) {
            prefix += parentNodeName;
        } else {
            prefix += splitChar + parentNodeName;
        }

        outer:
        for (int i = 1; ; i++) {
            String nextPrefix = prefix + splitChar + i;

            JsonObject resultObject = new JsonObject();
            int size = templateObject.size();

            for (Map.Entry<String, JsonElement> entry : templateObject.entrySet()) {

                String key = entry.getKey();
                JsonElement value = entry.getValue();

                if (value instanceof JsonObject) {

                    JsonObject targetJSONObject = this.createTargetJSONWithGson(sourceMap, (JsonObject) value, nextPrefix);

                    if (targetJSONObject != null && targetJSONObject.size() != 0) {
                        resultObject.add(key, targetJSONObject);
                    } else {
                        resultObject.add(key, new JsonObject());
                        size--;
                    }

                } else if (value instanceof JsonArray) {

                    JsonArray targetJSONArray = this.createTargetArrayWithGson(sourceMap, nextPrefix, (JsonArray) value);

                    if (targetJSONArray != null && targetJSONArray.size() != 0) {
                        resultObject.add(key, targetJSONArray);
                    } else {
                        resultObject.add(key, new JsonArray());
                        size--;
                    }

                } else if (parentNode.equals(key)) {
                    size--;
                } else {

                    String sourceKey = value.getAsString();

                    if (sourceKey.startsWith("=>")) {

                        size--;
                        sourceKey = splitChar + sourceKey.substring(2);
                        JsonElement sourceValue = sourceMap.get(sourceKey);

                        resultObject.add(key, sourceValue == null ? null : sourceValue);

                    } else {

                        sourceKey = nextPrefix + splitChar + sourceKey;
                        JsonElement sourceValue = sourceMap.get(sourceKey);

                        if (sourceValue != null) {
                            resultObject.add(key, sourceValue);
                        } else {
                            resultObject.addProperty(key, "");
                            size--;
                        }
                    }

//                    System.out.println("sKey: " + sourceKey);

                }
                if (size == 0) {
                    break outer;
                } else if (size < 0) {
                    throw new RuntimeException("data error！");
                }
            }

            if (resultObject.size() != 0) {
                resultArray.add(resultObject);
            }
        }

        return resultArray;
    }

}
