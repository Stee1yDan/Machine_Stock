package com.example.wessocketstockapp.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonMapper
{
    public static String convertToJsonString(Object object)
    {
        try
        {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(object);
        }
        catch (JsonProcessingException e)
        {
            System.out.println("Could parse the string");
            e.printStackTrace();
            return null;
        }

    }

    public static Object convertFromJsonString(String str, Class<com.example.wessocketstockapp.model.BaseStockInfo> template)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(str, template);
        }
        catch (JsonProcessingException e)
        {
            System.out.println("Could parse the string");
            e.printStackTrace();
            return null;
        }
    }
}
