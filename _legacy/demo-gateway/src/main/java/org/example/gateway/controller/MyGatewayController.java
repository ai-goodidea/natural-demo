package org.example.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 */
@RestController
public class MyGatewayController {

    @RequestMapping(value = "/test")
    public Map test() {
        Map ar = new HashMap();
        ar.put("gateway", "你好00");
        return ar;
    }

    @RequestMapping(value = "/hello")
    public Map hello() {
        Map ar = new HashMap();
        ar.put("gateway", "你好hello");
        return ar;
    }
}
