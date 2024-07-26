package com.fhfelipefh;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonConverter {

    private static final Logger logger = LoggerFactory.getLogger(JsonConverter.class);

    private String jsonInput;
    private JSONObject jsonObject;

    public JSONObject convert(String jsonInput) {
        try {
            // Determina se o JSON é um objeto ou um array
            if (jsonInput.trim().startsWith("{")) {
                jsonObject = new JSONObject(jsonInput);
            } else if (jsonInput.trim().startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonInput);
                jsonObject = new JSONObject();
                jsonObject.put("array", jsonArray);
            }
            logger.info("JsonConverter.convert: jsonObject: {}", jsonObject);
        } catch (Exception e) {
            logger.error("Error converting json: {}", e.getMessage());
            jsonObject = new JSONObject(); // Retorna um objeto vazio em caso de erro
        }
        return jsonObject;
    }
}
