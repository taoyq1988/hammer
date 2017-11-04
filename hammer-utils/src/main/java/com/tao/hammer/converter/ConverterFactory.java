package com.tao.hammer.converter;

import com.tao.hammer.utils.EnumUtils;
import net.sf.cglib.core.Converter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tyq
 * @version 1.0, 2017/11/3
 */
public class ConverterFactory {

    private static boolean isApacheCommonsBeanUtilsAvailable
            = ClassUtils.isPresent("org.apache.commons.beanutils.BeanUtilsBean", null);

    private static final Converter CONVERTER = initConverter();

    private static final Object NULL_OBJECT = new Object();

    public static Converter getConverter() {
        return CONVERTER;
    }

    private static Converter initConverter() {
        return initCompositeConverterConverter(true);
    }

    private static Converter initEnumConverter() {
        Converter compositeConverter = initCompositeConverterConverter(false);
        return new EnumConverter(compositeConverter);
    }

    /**
     * 生成组合的转换器
     * @param registerEnumConverter 是否添加枚举转换器
     */
    private static Converter initCompositeConverterConverter(boolean registerEnumConverter) {

        CompositeConverter compositeConverter = new CompositeConverter();

        if (registerEnumConverter) {
            compositeConverter.registerFirst(initEnumConverter());
        }

        if (isApacheCommonsBeanUtilsAvailable) {
            //在测试的时候，发现commons-beanutils的性能要比spring的好，放在前面
            //apache commons-beanutils的类型转换
            Converter beanUtilsConverter = Inner.CommonsBeanUtilsConverterFactory.getConverter();
            compositeConverter.register(beanUtilsConverter);
        }

        compositeConverter.register(new SpringConverter());
        return compositeConverter;
    }


    private static class CompositeConverter implements Converter {

        private List<Converter> converters = new ArrayList<Converter>();

        @Override
        public Object convert(Object value, Class target, Object context) {
            if (value == null) {
                /*if (target.isPrimitive()) {
                    return primitiveDefaultValues.get(target);
                } else {
                    return null;
                }*/
                return null;
            }
            for (Converter converter : converters) {
                Object convertedValue = converter.convert(value, target, null);
                if (convertedValue == NULL_OBJECT) {
                    return null;
                }
                if (convertedValue != null) {
                    return convertedValue;
                }
            }
            return value;
        }

        void register(Converter converter) {
            converters.add(converter);
        }

        void registerFirst(Converter converter) {
            converters.add(0, converter);
        }
    }

    static class SpringConverter implements Converter {

        private final ConversionService conversionService = new DefaultConversionService();

        @Override
        public Object convert(Object value, Class target, Object context) {
            if (conversionService.canConvert(value.getClass(), target)) {
                return conversionService.convert(value, target);
            } else {
                return null;
            }
        }

    }

    /**
     * 枚举转换器
     *
     * @since 1.1.0
     */
    static class EnumConverter implements Converter {

        private Map<Class, Map<String, Enum>> globalStringToEnumMap = new ConcurrentHashMap<Class, Map<String, Enum>>();

        private Map<Class, Map<Enum, Object>> globalEnumToCodeMap = new ConcurrentHashMap<Class, Map<Enum, Object>>();

        private Converter converter;

        public EnumConverter(Converter converter) {
            this.converter = converter;
        }

        @Override
        public Object convert(Object value, Class target, Object context) {
            if (target.isEnum()) {
                Map<String, Enum> enumMap = globalStringToEnumMap.get(target);
                if (enumMap == null) {
                    loadEnumMap(target);
                    enumMap = globalStringToEnumMap.get(target);
                }
                // 直接返回null, 交给后续的converter
                if (enumMap.isEmpty()) {
                    return null;
                }
                Object targetValue;
                if (value == null) {
                    targetValue = enumMap.get(null);
                } else {
                    targetValue = enumMap.get(String.valueOf(value));
                }
                // 如为空，返回NULL_OBJECT, 不再交给后续的converter
                return targetValue == null ? NULL_OBJECT : targetValue;
            } else if (value.getClass().isEnum()) {
                Class sourceClass = value.getClass();
                Map<Enum, Object> enumToObjectMap = globalEnumToCodeMap.get(sourceClass);
                if (enumToObjectMap == null) {
                    loadEnumMap(sourceClass);
                    enumToObjectMap = globalEnumToCodeMap.get(sourceClass);
                }
                // 如为空，则为不满足要求的枚举类型，后面的converter继续处理
                if (enumToObjectMap.isEmpty()) {
                    return null;
                }
                Object targetValue = enumToObjectMap.get(value);
                if (targetValue != null) {
                    if (target.isAssignableFrom(targetValue.getClass())) {
                        return targetValue;
                    } else {
                        targetValue = converter.convert(targetValue, target, context);
                        if (targetValue == null) {
                            return NULL_OBJECT;
                        } else {
                            return targetValue;
                        }
                    }
                }
                return null;
            }
            return null;
        }

        private void loadEnumMap(Class target) {
            Field field = EnumUtils.findCodeField(target);
            Map<Enum, Object> enumToObjectMap = new HashMap<Enum, Object>();
            if (field != null) {
                Map<Object, Enum> stringToEnumMap = EnumUtils.getCodeToEnumMap(field);
                for (Map.Entry<Object, Enum> enumEntry : stringToEnumMap.entrySet()) {
                    enumToObjectMap.put(enumEntry.getValue(), enumEntry.getKey());
                }
            }
            Map<String, Enum> enumMap = EnumUtils.getStringCodeToEnumMap(field);
            globalStringToEnumMap.put(target, enumMap);
            globalEnumToCodeMap.put(target, enumToObjectMap);
        }
    }

}
