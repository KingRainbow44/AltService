package moe.seikimo.altservice.script;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public final class ScriptSerializer {
    private static final Map<Class<?>, MethodAccess> methodAccessCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>, ConstructorAccess<?>> constructorCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Map<String, FieldMeta>> fieldMetaCache = new ConcurrentHashMap<>();

    @Data
    @AllArgsConstructor
    static class FieldMeta {
        private String name;
        private String setter;
        private int index;
        private Class<?> type;
        private @Nullable Field field;
    }

    /**
     * Serializes a Lua value to a Java List.
     *
     * @param obj The Lua value to serialize.
     * @param type The type of the value to serialize.
     * @return A List containing the serialized value.
     */
    public <T> List<T> toList(Object obj, Class<T> type) {
        List<T> list = new ArrayList<>();
        if (!(obj instanceof LuaTable table)) return list;

        try {
            var keys = table.keys();
            for (var k : keys) {
                try {
                    LuaValue keyValue = table.get(k);

                    T object; if (keyValue.istable()) {
                        object = this.serialize(type, null, keyValue.checktable());
                    } else if (keyValue.isint()) {
                        object = (T) (Integer) keyValue.toint();
                    } else if (keyValue.isnumber()) {
                        object = (T) (Float) keyValue.tofloat();
                    } else if (keyValue.isstring()) {
                        object = (T) keyValue.tojstring();
                    } else if (keyValue.isboolean()) {
                        object = (T) (Boolean) keyValue.toboolean();
                    } else {
                        object = (T) keyValue;
                    }

                    if (object != null) {
                        list.add(object);
                    }
                } catch (Exception ignored) { }
            }
        } catch (Exception ignored) { }

        return list;
    }

    /**
     * Serializes a Lua value to a Java object.
     *
     * @param obj The Lua value to serialize.
     * @param type The type of the value to serialize.
     * @return The serialized value.
     */
    public <T> T toObject(Object obj, Class<T> type) {
        return this.serialize(type, null, (LuaTable) obj);
    }

    /**
     * Serializes a Lua value to a Java Map.
     *
     * @param obj The Lua value to serialize.
     * @param type The type of the value to serialize.
     * @return A Map containing the serialized value.
     */
    public <T> Map<String, T> toMap(Object obj, Class<T> type) {
        Map<String, T> map = new HashMap<>();
        if (!(obj instanceof LuaTable table)) return map;

        try {
            var keys = table.keys();
            for (var k : keys) {
                try {
                    var keyValue = table.get(k);

                    T object; if (keyValue.istable()) {
                        object = serialize(type, null, keyValue.checktable());
                    } else if (keyValue.isint()) {
                        object = (T) (Integer) keyValue.toint();
                    } else if (keyValue.isnumber()) {
                        object = (T) (Float) keyValue.tofloat(); // terrible...
                    } else if (keyValue.isstring()) {
                        object = (T) keyValue.tojstring();
                    } else if (keyValue.isboolean()) {
                        object = (T) (Boolean) keyValue.toboolean();
                    } else {
                        object = (T) keyValue;
                    }

                    if (object != null) {
                        map.put(String.valueOf(k), object);
                    }
                } catch (Exception ignored) { }
            }
        } catch (Exception ignored) { }

        return map;
    }

    /**
     * Fetches the List type.
     *
     * @param type The type to fetch the List type from.
     * @param field The field to fetch the List type from.
     * @return The List type.
     */
    private static Class<?> getListType(Class<?> type, @Nullable Field field) {
        if (field == null) {
            return type.getTypeParameters()[0].getClass();
        }

        var fieldType = field.getGenericType();
        if (fieldType instanceof ParameterizedType paramType) {
            return (Class<?>) paramType.getActualTypeArguments()[0];
        }

        return null;
    }

    /**
     * Serializes a Lua value to a Java value.
     *
     * @param type The type of the value to serialize.
     * @param field The field to serialize.
     * @param table The Lua value to serialize.
     * @return The serialized value.
     */
    private <T> T serialize(Class<T> type, @Nullable Field field, LuaTable table) {
        T object = null;

        if (type == List.class) {
            try {
                var listType = getListType(type, field);
                return (T) this.toList(table, listType);
            } catch (Exception ignored) {
                return null;
            }
        }

        try {
            if (!methodAccessCache.containsKey(type)) {
                cacheType(type);
            }

            var methodAccess = methodAccessCache.get(type);
            var fieldMetaMap = fieldMetaCache.get(type);

            object = (T) constructorCache.get(type).newInstance();
            if (table == null) {
                return object;
            }

            var keys = table.keys();
            for (var k : keys) {
                try {
                    var keyName = k.checkjstring();
                    if (!fieldMetaMap.containsKey(keyName)) {
                        continue;
                    }

                    var fieldMeta = fieldMetaMap.get(keyName);
                    var keyValue = table.get(k);

                    if (keyValue.istable()) {
                        methodAccess.invoke(
                                object,
                                fieldMeta.index,
                                serialize(fieldMeta.getType(), fieldMeta.getField(), keyValue.checktable()));
                    } else if (fieldMeta.getType().equals(float.class)) {
                        methodAccess.invoke(object, fieldMeta.index, keyValue.tofloat());
                    } else if (fieldMeta.getType().equals(int.class)) {
                        methodAccess.invoke(object, fieldMeta.index, keyValue.toint());
                    } else if (fieldMeta.getType().equals(String.class)) {
                        methodAccess.invoke(object, fieldMeta.index, keyValue.tojstring());
                    } else if (fieldMeta.getType().equals(boolean.class)) {
                        methodAccess.invoke(object, fieldMeta.index, keyValue.toboolean());
                    } else {
                        methodAccess.invoke(object, fieldMeta.index, keyValue.tojstring());
                    }
                } catch (Exception ignored) { }
            }
        } catch (Exception ignored) { }

        return object;
    }

    /**
     * Caches a type's data.
     *
     * @param type The type to cache.
     * @return The cached data.
     */
    private static <T> Map<String, FieldMeta> cacheType(Class<T> type) {
        if (fieldMetaCache.containsKey(type)) {
            return fieldMetaCache.get(type);
        }
        if (!constructorCache.containsKey(type)) {
            constructorCache.putIfAbsent(type, ConstructorAccess.get(type));
        }
        var methodAccess =
                Optional.ofNullable(methodAccessCache.get(type)).orElse(MethodAccess.get(type));
        methodAccessCache.putIfAbsent(type, methodAccess);

        var fieldMetaMap = new HashMap<String, FieldMeta>();
        var methodNameSet = new HashSet<>(Arrays.stream(methodAccess.getMethodNames()).toList());

        Arrays.stream(type.getDeclaredFields())
                .filter(field -> methodNameSet.contains(getSetterName(field.getName())))
                .forEach(
                        field -> {
                            var setter = getSetterName(field.getName());
                            var index = methodAccess.getIndex(setter);
                            fieldMetaMap.put(
                                    field.getName(),
                                    new FieldMeta(field.getName(), setter, index, field.getType(), field));
                        });

        Arrays.stream(type.getFields())
                .filter(field -> !fieldMetaMap.containsKey(field.getName()))
                .filter(field -> methodNameSet.contains(getSetterName(field.getName())))
                .forEach(
                        field -> {
                            var setter = getSetterName(field.getName());
                            var index = methodAccess.getIndex(setter);
                            fieldMetaMap.put(
                                    field.getName(),
                                    new FieldMeta(field.getName(), setter, index, field.getType(), field));
                        });

        fieldMetaCache.put(type, fieldMetaMap);
        return fieldMetaMap;
    }

    /**
     * Gets the name of a field's setter.
     *
     * @param fieldName The name of the field.
     * @return The name of the method.
     */
    private static String getSetterName(String fieldName) {
        if (fieldName == null || fieldName.length() == 0) {
            return null;
        }

        if (fieldName.length() == 1) {
            return "set" + fieldName.toUpperCase();
        }

        return "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }
}
