package com.tao.hammer.converter;

import net.sf.cglib.core.Converter;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;

/**
 * 内部使用，作隔离用
 *
 * @author tyq
 * @version 1.0, 2017/11/3
 */
class Inner {

    /**
     * 与commons-beanutils解耦
     */
    static class CommonsBeanUtilsConverterFactory {

        static Converter getConverter() {
            final ConvertUtilsBean convertUtilsBean = BeanUtilsBean.getInstance().getConvertUtils();
            return new Converter() {
                @Override
                public Object convert(Object o, Class targetClass, Object o1) {
                    org.apache.commons.beanutils.Converter converter = convertUtilsBean.lookup(o.getClass(), targetClass);
                    if (converter != null) {
                        return converter.convert(targetClass, o);
                    } else {
                        return null;
                    }
                }
            };
        }

    }
}
