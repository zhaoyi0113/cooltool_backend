package com.cooltoo.go2nurse.chart.converter;

import java.util.List;

/**
 * Created by zhaolisong on 19/01/2017.
 */
public interface Converter<S, T> {

    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    T convert(S source);

    /**
     * Convert the source object list of type {@code S} to target type {@code T} list.
     * @param sources the source object list to convert, the element must be an instance of {@code S} (never {@code null})
     * @return the converted object list, the element must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    List<T> convert(List<S> sources);
}
