package com.exactprosystems.webchannels.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

	@Override
	public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider) 
			throws IOException, JsonProcessingException {

        if (value.compareTo(BigDecimal.ZERO) == 0) {
            jgen.writeString("0");
        } else {
            jgen.writeString(value.stripTrailingZeros().toPlainString());
        }
		
	}

}
