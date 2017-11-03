package com.tao.hammer.utils;

import com.tao.hammer.converter.ConverterFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.DataBinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author tyq
 * @version 1.0, 2017/11/3
 */
public class BeanUtils {

    private static ConcurrentMap<Key, Holder<BeanCopier>> beanCopierMap = new ConcurrentHashMap<Key, Holder<BeanCopier>>();

    private static Converter converter = ConverterFactory.getConverter();

    private static Map<Class, Object> primitiveDefaultValues = new HashMap<Class, Object>();

    static {
        primitiveDefaultValues.put(int.class, 0);
        primitiveDefaultValues.put(boolean.class, false);
        primitiveDefaultValues.put(char.class, ' ');
        primitiveDefaultValues.put(long.class, 0L);
        primitiveDefaultValues.put(short.class, 0);
        primitiveDefaultValues.put(double.class, 0D);
        primitiveDefaultValues.put(float.class, 0f);
    }


    /**
     * 对象拷贝，默认情况下在对象的属性名相同，属性类型不同的情况会进行转换
     *
     * @param source 原始对象
     * @param target 目标对象
     * @param <T>    返回的目标对象类型
     * @return 目标对象，通过再次返回目标对象可以方便额外的编程
     */
    public static <T> T copyProperties(Object source, T target) {
        return copyProperties(source, target, true);
    }

    /**
     * 对象拷贝，默认情况下在对象的属性名相同，属性类型不同的情况会进行转换
     *
     * @param source 原始对象
     * @param objectCreator 目标对象创建器
     * @param <T>    返回的目标对象类型
     * @return 目标对象，通过再次返回目标对象可以方便额外的编程
     * @since 1.1.0
     */
    public static <T> T copyProperties(Object source, ObjectCreator<T> objectCreator) {
        return copyProperties(source, objectCreator, true);
    }


    /**
     * 对象拷贝，默认情况下在对象的属性名相同，属性类型不同的情况会进行转换
     *
     * @param source 原始对象
     * @param objectCreator 目标对象创建器
     * @param convert 是否默认转换类型
     * @param <T>    返回的目标对象类型
     * @return 目标对象，通过再次返回目标对象可以方便额外的编程
     * @since 1.1.0
     */
    public static <T> T copyProperties(Object source, ObjectCreator<T> objectCreator, boolean convert) {
        try {
            return copyProperties(source, objectCreator.create(), convert);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 对象拷贝
     *
     * @param source  原始对象
     * @param target  目标对象
     * @param convert 拷贝时属性不一致，是否需要转换
     * @param <T>     返回的目标对象类型
     * @return 目标对象，通过再次返回目标对象可以方便额外的编程
     */
    public static <T> T copyProperties(Object source, T target, boolean convert) {
        Key beanKey = genKey(source.getClass(), target.getClass(), convert);
        Holder<BeanCopier> holder = beanCopierMap.get(beanKey);
        if (holder == null) {
            holder = new Holder<BeanCopier>();
            Holder<BeanCopier> old = beanCopierMap.putIfAbsent(beanKey, holder);
            if (old != null) {
                holder = old;
            }
        }
        if (holder.value == null) {
            synchronized (holder) {
                if (holder.value == null) {
                    holder.value = BeanCopier.create(source.getClass(),
                            target.getClass(), convert);
                }
            }
        }
        holder.value.copy(source, target, converter);
        return target;
    }

    /**
     * 将Map中的键值对设置到对象中
     *
     * @param source 源键值对
     * @param target 目标对象，需要被设置值的对象
     * @param <T>    返回目标对象，使用时不需要转型
     * @return 目标对象
     */
    public static <T> T populate(Map<String, ? extends Object> source, T target) {
        DataBinder dataBinder = new DataBinder(target);
        dataBinder.bind(new MutablePropertyValues(source));
        return target;
    }

    public static <T> T populate(Map<String, ? extends Object> source, T target, Map<String, String> nameMap) {
        Map<String, Object> newProperties = new HashMap<String, Object>();
        for (Map.Entry<String, ? extends Object> entry : source.entrySet()) {
            String name = nameMap.get(entry.getKey());
            if (name == null) {
                name = entry.getKey();
            }
            Object value = entry.getValue();
            newProperties.put(name, value);
        }
        return populate(newProperties, target);
    }

    /**
     * 同{@link BeanUtils#copyProperties(Object, Object)}
     * <br>
     * 无非一个是普通对象，一个是列表
     *
     * @param sourceList  源列表
     * @param targetClazz 目标对象类型
     * @param convert     类型不匹配，是否进行转换
     * @param <T>         目标对象类型
     * @return 目标对象列表
     * @see BeanUtils#copyProperties(Object, Object)
     */
    public static <T> List<T> transform(List<?> sourceList, final Class<T> targetClazz, boolean convert) {
        return transform(sourceList, new ObjectCreator<T>() {
            @Override
            public T create() throws Exception {
                return targetClazz.newInstance();
            }
        }, convert);
    }

    public static <T> List<T> transform(List<?> sourceList, final Class<T> targetClazz) {
        return transform(sourceList, targetClazz, true);
    }

    public static <T> List<T> transform(List<?> sourceList, ObjectCreator<T> objectCreator) {
        return transform(sourceList, objectCreator, true);
    }

    public static <T> List<T> transform(List<?> sourceList, ObjectCreator<T> objectCreator, boolean convert) {
        List<T> targetList = new ArrayList<T>(sourceList.size());
        for (Object source : sourceList) {
            try {
                T target = objectCreator.create();
                targetList.add(copyProperties(source, target, convert));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return targetList;
    }

    private static Key genKey(Class<?> source, Class<?> target, boolean convert) {
        return new Key(source, target, convert);
    }

    /**
     * 对象创建器
     *
     * @param <T>
     */
    public interface ObjectCreator<T> {
        T create() throws Exception;
    }

    @Data
    @AllArgsConstructor
    static class Key {
        private Class source;
        private Class target;
        private boolean convert;
    }

    static class Holder<T> {
        private T value;
    }

}
