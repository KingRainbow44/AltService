package moe.seikimo.altservice.utils.objects.game;

import org.cloudburstmc.protocol.bedrock.data.AttributeData;

import java.util.Collection;
import java.util.Map;

public interface Attributable {
    /**
     * @return A list of attributes for this entity.
     */
    Map<String, AttributeData> getAttributes();

    /**
     * Updates all attributes.
     *
     * @param attributes The new attributes.
     */
    default void updateAttributes(Collection<AttributeData> attributes) {
        for (var attribute : attributes) {
            this.getAttributes().put(attribute.getName(), attribute);
        }
    }

    /**
     * Fetches the value of an attribute.
     *
     * @param name The name of the attribute.
     * @param fallback The fallback value.
     * @return The value of the attribute, or the fallback value if the attribute is not present.
     */
    default float getAttributeValue(String name, float fallback) {
        var attribute = this.getAttributes().get(name);
        if (attribute == null) {
            return fallback;
        }
        return attribute.getValue();
    }
}
