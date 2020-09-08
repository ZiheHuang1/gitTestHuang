package com.za;

import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huangzihe
 * @date 2020/8/10 3:03 下午
 */
public class PolicyCertUtil {
    public static List<String> duplicateRemove(List<String> policyCertNos) {
        HashSet<String> result = new HashSet<String>(policyCertNos);
        return new ArrayList<String>(result);
    }

    public static void main(String[] args) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {
        List<String> list = new ArrayList<>();
        list.add("360722199803300058");
        list.add("360722199803300058");
        list.add("3607221998033032132");
        list.add("36072219980330032132");

        String collect = list.stream().distinct().collect(Collectors.joining(","));
        System.out.println(collect);

    }

    public static void test(Person p) {
        p.setAge(5);
    }

    public static String generateQueryString(Map<String, Object>paramMap) {
        List<String> params = new ArrayList<>();
        for (String key : paramMap.keySet()) {
            Object value = paramMap.get(key);
            if (value == null) {
                continue;
            }
            if ("sign".equals(key)) {
                continue;
            }
            if ("sign_type".equals(key)) {
                continue;
            }
            String valueStr = "";
//List型的值，先排序再组织成字符串
            if(value instanceof  List) {
                List<String> listStrs = new ArrayList<String>();
                StringBuilder listStrSb = new StringBuilder();
                for(Map<String, Object> m : (List<Map>)value) {
                    if (listStrSb.length() >0) {
                        listStrSb.append(",{");
                    } else {
                        listStrSb.append("{");
                    }
                    listStrSb.append(generateQueryString((Map<String, Object>) m));
                    listStrSb.append("}");
                }
                valueStr = "["+listStrSb.toString()+"]";
            } else if(value instanceof Map) {
                valueStr = "{"+generateQueryString((Map<String, Object>) value)+"}";
            } else {
                valueStr = value.toString();
            }
            params.add(key.concat("=").concat(valueStr));
        }
        Collections.sort(params, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        StringBuilder sb = new StringBuilder();
        for (String param : params) {
            if (sb.length() >0) {
                sb.append('&');
            }
            sb.append(param);
        }
        return sb.toString();
    }
}
