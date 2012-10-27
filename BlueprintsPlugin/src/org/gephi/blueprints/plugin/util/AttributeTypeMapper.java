package org.gephi.blueprints.plugin.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.gephi.data.attributes.api.AttributeType;

/**
 * Maps java types to Gephi {@link AttributeType}s.
 * 
 * @author Timmy Storms (timmy.storms@gmail.com)
 * @author Davy Suvee (dsuvee@its.jnj.com)
 * @author Davy Suvee (info@datablend.be)
 */
public final class AttributeTypeMapper {

    /** Map that contains all mappable types. */
    private static final Map<Class<?>, AttributeType> MAPPER = new HashMap<Class<?>, AttributeType>();

    private AttributeTypeMapper() {}

    static {
        MAPPER.put(Byte.class, AttributeType.BYTE);
        MAPPER.put(Short.class, AttributeType.SHORT);
        MAPPER.put(Integer.class, AttributeType.INT);
        MAPPER.put(Long.class, AttributeType.LONG);
        MAPPER.put(Float.class, AttributeType.FLOAT);
        MAPPER.put(Double.class, AttributeType.DOUBLE);
        MAPPER.put(Boolean.class, AttributeType.BOOLEAN);
        MAPPER.put(Character.class, AttributeType.CHAR);        
        MAPPER.put(String.class, AttributeType.STRING);
        MAPPER.put(Date.class, AttributeType.TIME_INTERVAL);
    }

    /**
     * Returns the {@link AttributeType} that maps to a given object.
     * @param obj the object that should be mapped
     * @return the {@link AttributeType}
     */
    public static AttributeType map(final Object obj) {
        return MAPPER.get(obj.getClass());
    }
    
}
