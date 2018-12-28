package com.lgj.study.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author guangjie.Liao
 * @Title: ExampelService
 * @ProjectName spring-cloud-parent
 * @Description: TODO
 * @date 2018/12/2516:12
 */
@FeignClient("eureka-provider")
public interface ExampleService {

    @GetMapping(value = "/test/test_eureka")
    public String test();


}
