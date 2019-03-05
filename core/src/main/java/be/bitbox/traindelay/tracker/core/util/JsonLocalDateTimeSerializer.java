package be.bitbox.traindelay.tracker.core.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class JsonLocalDateTimeSerializer extends StdSerializer<LocalDateTime> {

    public JsonLocalDateTimeSerializer() {
        super(LocalDateTime.class);
    }
    
    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(localDateTime.atZone(ZoneId.of("Z")).toString());
    }
}
