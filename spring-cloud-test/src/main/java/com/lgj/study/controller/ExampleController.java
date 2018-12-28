package com.lgj.study.controller;

import com.lgj.study.service.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author guangjie.Liao
 * @Title: ExampleController
 * @ProjectName spring-cloud-parent
 * @Description: TODO
 * @date 2018/12/2110:18
 */
@RestController
@RequestMapping(value = "/test")
public class ExampleController {

    @Autowired
    private ExampleService exampleService;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/eureka")
    public String testEureka(){
        return exampleService.test();
    }

    @RequestMapping(value = "/rest")
    public String testRestTemplate(){
        String url= "http://EUREKA-PROVIDER";
        return restTemplate.getForEntity(url+"/test/test_eureka",String.class).getBody();
    }


}
