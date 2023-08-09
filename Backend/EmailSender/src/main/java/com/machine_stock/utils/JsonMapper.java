package com.machine_stock.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapper
{
    public static Object convertFromJsonString(String str, Class template)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(str, template);
        }
        catch (JsonProcessingException e)
        {
            System.out.println("Could not parse the string");
            e.printStackTrace();
            return null;
        }
    }
}
