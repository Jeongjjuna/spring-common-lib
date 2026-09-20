package yjh.ontongsal.common.web.filter;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;

import java.util.List;

public class MaskedJacksonModule extends SimpleModule {

    public MaskedJacksonModule() {
        super("MaskedJacksonModule");

        setSerializerModifier(new ValueSerializerModifier() {

            @Override
            public List<BeanPropertyWriter> changeProperties(
                    SerializationConfig config,
                    BeanDescription.Supplier beanDesc,
                    List<BeanPropertyWriter> beanProperties
            ) {
                for (BeanPropertyWriter property : beanProperties) {
                    if (property.getAnnotation(Masked.class) != null) {
                        property.assignSerializer(new MaskedValueSerializer());
                    }
                }

                return beanProperties;
            }
        });
    }

    private static class MaskedValueSerializer extends ValueSerializer<Object> {

        @Override
        public void serialize(
                Object value,
                JsonGenerator gen,
                SerializationContext ctxt
        ) throws JacksonException {
            gen.writeString("***");
        }
    }
}
