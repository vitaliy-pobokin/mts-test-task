package ru.mts.test.model.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampSerializer extends JsonSerializer<Long> {

    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");

    @Override
    public void serialize(Long aLong, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(FORMAT.format(new Date(aLong)));
    }
}
