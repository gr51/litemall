package org.linlinjava.litemall.member.util;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CopyPropertiesUtil {
    public CopyPropertiesUtil() {
    }

    public static <K, T> List<K> copySourceListToTargetList(List<T> sourceList, Class<K> clazz) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Collections.EMPTY_LIST;
        } else {
            List<K> targetList = (List)sourceList.stream().map((x) -> {
                try {
                    K obj = clazz.newInstance();
                    BeanUtils.copyProperties(x, obj);
                    return obj;
                } catch (Exception var3) {
                    return null;
                }
            }).collect(Collectors.toList());
            return targetList;
        }
    }

    public static <K, T> T copySourceObjToTargetObj(K k, Class<T> clazz) {
        T obj = null;
        if (!ObjectUtils.isEmpty(k)) {
            try {
                obj = clazz.newInstance();
                BeanUtils.copyProperties(k, obj);
            } catch (Exception var4) {
                return null;
            }
        }

        return obj;
    }
}