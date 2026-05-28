package org.example.gateway.helper;


import cn.hutool.core.util.StrUtil;
import com.goodidea.core.cache.CacheTemplate;
import com.goodidea.gateway.constant.AuthConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.*;

@Slf4j
@Component
public class AuthenticationHelper {

    public static String AUTH_PERMISSION_ROLES_UUID = "";
    public static Map<String,List> AUTH_PERMISSION_ROLES_MAP = new HashMap();


    @Autowired(required = false)
    private CacheTemplate cacheTemplate;

    public void reloadRolePermission() {
        String uuid = cacheTemplate.get(CacheTemplate.PERMISSION_ROLES_UUID);
        if (uuid != null && !AUTH_PERMISSION_ROLES_UUID.equals(uuid)) {
            AUTH_PERMISSION_ROLES_UUID = uuid;
            Map<String,String> map = cacheTemplate.get(CacheTemplate.PERMISSION_ROLES_MAP,Map.class);
            if (map != null) {
                Map<String,List> newMap=new HashMap<>();
                for(Map.Entry<String,String> entry:map.entrySet()){
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if(StrUtil.isNotEmpty(value)){
                        String[] split = value.split(",");
                        List<String> roles = Arrays.asList(split);
                        newMap.put(key,roles);
                    }
                }
                AUTH_PERMISSION_ROLES_MAP = newMap;
            }
            log.info("权限映射关系加载成功,UUID：{} ", uuid);
        }
    }


    /**
     * 根据当前URL获取 需要的权限编码列表
     * 现在没有实现 admin
     * @param uri
     * @return
     */
    public List<String> getAuthorities(URI uri) {
        String path = uri.getPath();
        List list = new ArrayList();

        list.add(AuthConstant.AUTHORITY_PREFIX + "admin");

        return list;
    }


}
