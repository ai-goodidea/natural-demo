package org.example.core.lang;


import com.alibaba.fastjson2.JSONValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

/**
 * @author Administrator
 */
@Component
public class LangUtils {


    private static final String JSON_REGEX = "\\s*\\{.*?\\}\\s*|\\s*\\[.*?\\]\\s*";

    public static final char UNDERLINE = '_';

    public static String generateRandomNumber(int length) {
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid;
    }

    public static void copyProperties(Map from, Map to) {
        Map<Object, Object> map = from;
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (LangUtils.isNotEmpty(value)) {
                to.put(key, value);
            }
        }

    }

    public static List<String> split(String str, String separator) {
        List<String> stringList = new ArrayList<>();
        while (true) {
            int k = str.indexOf(separator);
            if (k < 0) {
                stringList.add(str);
                break;
            }
            String s = str.substring(0, k);
            stringList.add(s);
            str = str.substring(k + 1);
        }
        return stringList;
    }

    public static List asList(Object item) {
        List list = new ArrayList();
        list.add(item);
        return list;
    }


    public static Object isJSONObject(String json) {
        return JSONValidator.from(json).validate();
    }


    public static Object isEmpty(Object obj, Object def) {
        if (LangUtils.isEmpty(obj)) {
            return def;
        } else {
            return obj;
        }

    }

    public static boolean isNotEmpty(Object obj) {
        return !LangUtils.isEmpty(obj);
    }

    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return ((String) obj).trim().equals("");
        }
        if (obj instanceof Object[]) {
            return ((Object[]) obj).length == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }
        return false;
    }

    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    public static Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            //ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            //ex.printStackTrace();
        }
        return obj;
    }

    public static Date getNowDate() {
        Date date = new Date();
        return date;
    }

    /**
     * 将日期字符串转换成日期，只支持如下格式：
     * yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss。
     * 转换出错返回null。
     */
    public static Date parseDate(String str) {
        if (LangUtils.isEmpty(str)) {
            return null;
        }
        try {
            if (str.contains("T")) {
                // 解析为 Instant (UTC 时间)
                Instant instant = Instant.parse(str);
                return Date.from(instant);
            }

            String f = null;
            if (str.length() == 8) {
                f = "yyyyMMdd";
            } else if (str.length() == 10) {
                f = "yyyy-MM-dd";
            } else {
                f = "yyyy-MM-dd HH:mm:ss";
            }
            return new SimpleDateFormat(f).parse(str);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String formatDateDaY(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static String formatTime(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

    /**
     * 將日期轉換成dateFormat格式的字符串
     *
     * @param date
     * @param dateFormat，格式可以是yyyy-MM-dd HH:mm:ss 或yyyy-MM-dd
     * @return
     */
    public static String formatDate(Date date, String dateFormat) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(dateFormat).format(date);
    }

    /**
     * 驼峰格式字符串转换为下划线格式字符串
     *
     * @param param
     * @return
     */
    public static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线格式字符串转换为驼峰格式字符串
     *
     * @param param
     * @return
     */
    public static String underlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static Map<String, Object> bean2Map(Object javaBean) {
        Map<String, Object> map = new HashMap<>();
        Method[] methods = javaBean.getClass().getMethods(); // 获取所有方法
        for (Method method : methods) {
            if (method.getName().startsWith("get")) {
                String field = method.getName(); // 拼接属性名
                field = field.substring(field.indexOf("get") + 3);
                field = field.toLowerCase().charAt(0) + field.substring(1);
                Object value = null; // 执行方法
                try {
                    value = method.invoke(javaBean, (Object[]) null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                map.put(field, value);
            }
        }
        return map;
    }

    public static String getNextStr(String str, String split, String keyword) {
        String rs = null;
        String[] array = str.split(split);
        for (int i = 0; i < array.length; i++) {
            String s = array[i];
            if (keyword != null) {
                if (keyword.equals(s)) {
                    if (i < array.length) {
                        rs = array[i + 1];
                        return rs;
                    }

                } else if (s.indexOf(keyword) > -1) {
                    rs = s.substring(s.indexOf(keyword) + keyword.length());
                    return rs;
                }
            }
        }
        return null;
    }

    public static <D> List<D> convertList(List<?> list, Class<D> dtoClass) {
        List<D> items = new ArrayList<>();
        try {
            if (LangUtils.isNotEmpty(list)) {
                for (Object it : list) {
                    D item = dtoClass.newInstance();
                    BeanUtils.copyProperties(it, item);
                    items.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public static <D> D convertEntity(Object it, Class<D> dtoClass) {
        try {
            if (LangUtils.isNotEmpty(it)) {
                D item = dtoClass.newInstance();
                BeanUtils.copyProperties(it, item);
                return item;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getListCommaStr(List list) {
        if (LangUtils.isEmpty(list)) {
            return null;
        }
        return StringUtils.join(list, ',');
    }

    public static String formatMessage(String message, Object... params) {
        if (params != null && message.contains("{}")) {
            for (Object param : params) {
                message = message.replaceFirst("\\{\\}", String.valueOf(param));
            }
            return message;
        } else {
            return message;
        }
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static void copyPropertiesIgnoreNull(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }


    public static <T> T or(Object one, Object tow) {
        if (LangUtils.isNotEmpty(one)) {
            return (T) one;
        }
        return (T) tow;
    }
}
