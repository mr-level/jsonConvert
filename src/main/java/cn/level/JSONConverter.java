package cn.level;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * Created by mr_level on 17-5-14.
 */
public class JSONConverter {

    private final String splitChar = ".";
    private final String parentNode = "parentNode";


    public JSONObject convert(String sourceData, String templateData) {

        JSONObject sourceObj = JSONObject.parseObject(sourceData);
        JSONObject template = JSONObject.parseObject(templateData);

        return this.convert(sourceObj, template);
    }


    public JSONObject convert(JSONObject source, JSONObject template) {

        Map<String, Object> sourceMap = Maps.newHashMap();

        this.readDataIntoMap(sourceMap, source, "");
        JSONObject targetJSON = this.createTargetJSON(sourceMap, template, "");

        return targetJSON;
    }

    private void readDataIntoMap(Map<String, Object> sourceMap, JSONObject sourceObj, String prefiex) {

        prefiex += splitChar;

        for (Map.Entry<String, Object> entry : sourceObj.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();

            String prefiexKey = prefiex + key;
            if (value instanceof JSONObject) {
                this.readDataIntoMap(sourceMap, (JSONObject) value, prefiexKey);
            } else if (value instanceof JSONArray) {

                JSONArray jsonArray = (JSONArray) value;
                int size = jsonArray.size();

                for (int i = 0; size > 0 && i < size; i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    this.readDataIntoMap(sourceMap, jsonObject, prefiexKey + splitChar + (i + 1));

                }

            } else {
                sourceMap.put(prefiexKey, value);
            }
        }
    }

    private JSONObject createTargetJSON(Map<String, Object> sourceMap, JSONObject template, String prefix)  {

        JSONObject resultObject = new JSONObject();
        String parentNodeName = template.getString(parentNode);

        if (parentNodeName != null) {
            prefix += splitChar + parentNodeName;
        }

        //size用于计算此返回值JSONObject是否全为空
        int size = template.size();

        for (Map.Entry<String, Object> entry : template.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof JSONObject) {

                JSONObject targetJSON = this.createTargetJSON(sourceMap, (JSONObject) value, prefix);
                if (targetJSON != null) {
                    resultObject.put(key, targetJSON);
                } else {
                    resultObject.put(key, new JSONObject());
                    size--;
                }

            } else if (value instanceof JSONArray) {

                JSONArray targetJSONArray = this.createTargetArray(sourceMap, prefix, (JSONArray) value);
                if (targetJSONArray != null) {
                    resultObject.put(key, targetJSONArray);
                } else {
                    resultObject.put(key, new JSONArray());
                    size--;
                }

            } else {

                String sourceKey = value.toString();
                if (sourceKey.startsWith("=>")) {

                    size--;
                    sourceKey = splitChar + sourceKey.substring(2);
                    Object targetValue = sourceMap.get(sourceKey);

                    resultObject.put(key, targetValue == null ? "" : targetValue);

                } else {

                    sourceKey = prefix + splitChar + sourceKey;
                    Object targetValue = sourceMap.get(sourceKey);

                    if (targetValue != null) {
                        resultObject.put(key, targetValue);
                    } else {
                        resultObject.put(key, "");
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

    private JSONArray createTargetArray(Map<String, Object> sourceMap, String prefix, JSONArray templateArray) {

        JSONArray resultArray = new JSONArray();
        JSONObject templateObject = templateArray.getJSONObject(0);

        String parentNodeName = templateObject.getString(parentNode);
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

            JSONObject resultObject = new JSONObject();
            int size = templateObject.size();

            for (Map.Entry<String, Object> entry : templateObject.entrySet()) {

                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof JSONObject) {

                    JSONObject targetJSONObject = this.createTargetJSON(sourceMap, (JSONObject) value, nextPrefix);

                    if (targetJSONObject != null && !targetJSONObject.isEmpty()) {
                        resultObject.put(key, targetJSONObject);
                    } else {
                        resultObject.put(key, new JSONObject());
                        size--;
                    }

                } else if (value instanceof JSONArray) {

                    JSONArray targetJSONArray = this.createTargetArray(sourceMap, nextPrefix, (JSONArray) value);

                    if (targetJSONArray != null && !targetJSONArray.isEmpty()) {
                        resultObject.put(key, targetJSONArray);
                    } else {
                        resultObject.put(key, new JSONArray());
                        size--;
                    }

                } else if (parentNode.equals(key)) {
                    size--;
                } else {

                    String sourceKey = value.toString();

                    if (sourceKey.startsWith("=>")) {

                        size--;
                        sourceKey = splitChar + sourceKey.substring(2);
                        Object sourceValue = sourceMap.get(sourceKey);

                        resultObject.put(key, sourceValue == null ? "" : sourceValue);

                    } else {

                        sourceKey = nextPrefix + splitChar + sourceKey;
                        Object sourceValue = sourceMap.get(sourceKey);

                        if (sourceValue != null) {
                            resultObject.put(key, sourceValue);
                        } else {
                            resultObject.put(key, "");
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

            if (!resultObject.isEmpty()) {
                resultArray.add(resultObject);
            }
        }

        return resultArray;
    }

}
