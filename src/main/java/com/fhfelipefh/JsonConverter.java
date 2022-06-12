package com.fhfelipefh;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonConverter {

    private static final Logger logger = LoggerFactory.getLogger(JsonConverter.class);

    private String jsonInput;
    private JSONObject jsonObject;

    public JSONObject convert(String jsonInput) {
        try {
            jsonObject = new JSONObject(jsonInput);
            logger.info("JsonConverter.convert: jsonObject: {}", jsonObject);
        } catch (Exception e) {
            logger.error("Error converting json: {}", e.getMessage());
            jsonObject = new JSONObject();
        }
        return jsonObject;
    }


}
