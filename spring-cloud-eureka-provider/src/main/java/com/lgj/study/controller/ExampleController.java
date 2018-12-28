package com.lgj.study.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guangjie.Liao
 * @Title: ExampleController
 * @ProjectName spring-cloud-parent
 * @Description: TODO
 * @date 2018/12/2514:49
 */
@RestController
@RequestMapping(value = "/test")
public class ExampleController {

    @RequestMapping(value = "/test_eureka")
    public String test(){
        System.out.println("example provider----------");
       return "Eureka";
    }
}
