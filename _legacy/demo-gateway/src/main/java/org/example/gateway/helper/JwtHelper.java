package org.example.gateway.helper;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.nimbusds.jose.JWSObject;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 */
@Component
public class JwtHelper {

    public JWSObject getJWSObject(String token) {
        try {
            String realToken = token.replace("bearer ", "");
            JWSObject jwsObject = JWSObject.parse(realToken);
            return jwsObject;
        }catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    public Map<String,Object> getJwtPayloadMap(String token){
        if(StrUtil.isNotEmpty(token)){
            JWSObject jwsObject=this.getJWSObject(token);
            String userStr = jwsObject.getPayload().toString();
            if(StrUtil.isNotEmpty(userStr)){
                Map map = JSON.parseObject(userStr, Map.class);
                return map;
            }
        }
        return new HashMap();
    }

    public boolean isTokenExpired(String token){
        Map<String,Object> map =this.getJwtPayloadMap(token);
        Integer expired =(Integer)map.get("exp");
        if(expired!=null&&expired>System.currentTimeMillis()/1000){
            return false;
        }
        return true;
    }

}
