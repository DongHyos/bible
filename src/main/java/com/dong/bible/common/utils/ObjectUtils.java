package com.dong.bible.common.utils;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

public class ObjectUtils {
    /**
     * Copy properties if not empty
     *
     * @param source Object
     * @param target Object
     */
    public static void copyProperties(Object source, Object target){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setFieldMatchingEnabled(true)
                .setMatchingStrategy(MatchingStrategies.LOOSE)
                .setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(source, target);
    }

    /**
     * Object mapping
     * <br/> required no-argument-constructor of the target class
     *
     * @param source Source class instance
     * @param targetType Target class type
     * @return mapped class instance
     * @param <T> Target class
     */
    public static <T> T  map(Object source, Class<T> targetType){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true);
        return modelMapper.map(source, targetType);
    }

}
