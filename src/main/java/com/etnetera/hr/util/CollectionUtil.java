package com.etnetera.hr.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CollectionUtil {

    public static <T, R> List<R> mapAll(Iterable<T> inputs, Function<T, R> mapping) {
        return StreamSupport.stream(inputs.spliterator(), false)
                            .map(mapping)
                            .collect(Collectors.toList());
    }

    public static <T> List<T> mapToList(Iterable<T> inputs) {
        return mapAll(inputs, i -> i);
    }
}
