# springcloud
```
spring-cloud-eureka
│
└───spring-cloud-server               //eureka 注册中心
└───spring-cloud-provider             //eureka 服务提供者
└───spring-cloud-test                 //测试用

```
## spring cloud eureka
###1.搭建服务注册中心 
   ####1.单节点模式
    
   #####1.1.pom.xml 引入jar包
   ```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
   ```
   #####1.2 配置eureka 
   ```yaml
    eureka:
      client:
        fetch-registry: false
        register-with-eureka: false
        service-url:
          defaultZone: http://localhost:${server.port}/eureka/
  ```
   #####1.3启动了加入@EnableEurekaServer注解
   ```java
    @SpringBootApplication
    @EnableEurekaServer
    public class SpringCloudEurekaServerApplication {
    	public static void main(String[] args) {
    		SpringApplication.run(SpringCloudEurekaServerApplication.class, args);
    	}
    }
```
   ####说明
   fetch-registry ：表示是否从Eureka Server获取注册信息，默认为true。
   register-with-eureka ：表示是否将自己注册到Eureka Server，默认为true。
   service-url ：设置与Eureka Server交互的地址，查询服务和注册服务都需要依赖这个地址。默认是http://localhost:8761/eureka ；多个地址可使用 , 分隔。
    
   ####2.高可用模式
   #####2.1 新建配置文件
   application-peer1.yml
   ```yaml
    server:
      port: 1111
    spring:
      profiles:
        active: peer1

    eureka:
      instance: peer1
      
      client:
        service-url:
          defaultZone: http://peer2:1112/eureka/
```
   application-peer2.yml
   ```yaml
    server:
      port: 1112
    spring:
      profiles:
        active: peer2

    eureka:
      instance: peer2
      client:
        service-url:
          defaultZone: http://peer1:1111/eureka/
```


   #####2.2 高可用部署
    打包: mvn clean package -P peer1
    运行：java -jar spring-cloud-eureka-server-0.0.1-SNAPSHOT
    
    打包: mvn clean package -P peer2
    运行：java -jar spring-cloud-eureka-server-0.0.1-SNAPSHOT
   
   #####说明
   spring cloud 官网提示，Eureka优先发布服务的IP地址而不是主机名,ip-address 强制指定ip，默认获取本机ip
   
   ```yaml
    eureka:
      instance:
        prefer-ip-address: true
        ip-address: 127.0.0.1
      client:
        service-url:
          defaultZone: http://${eureka.instance.ip-address}:1112/eureka/
   ```
   ###2.服务提供者
   ####2.1 新建项目spring-cloud-provider 加上pom配置
   ```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
```
    同样也将服务提供者注册进注册中心，application.yml配置如下
   ```yaml
    server:
      port: 9003
    spring:
      application:
         name: eureka-provider
    eureka:
      client:
        service-url:
          defaultZone: http://peer1:1111/eureka/,http://peer2:1112/eureka/
```
    启动类加上注解
   ```java
    @SpringBootApplication
    @EnableEurekaClient
    public class SpringCloudEurekaProviderApplication {
    	public static void main(String[] args) {
    		SpringApplication.run(SpringCloudEurekaProviderApplication.class, args);
    	}
    }
```
   ###3.服务调用 
   ####3.1 到入pom配置
  ```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-ribbon</artifactId>
    </dependency>
```
    启动类配置如下
   ```java
    @SpringBootApplication
    @EnableDiscoveryClient
    @EnableFeignClients
    public class SpringCloudTestApplication {
        @Bean
        @LoadBalanced
        RestTemplate restTemplate(){
            return new RestTemplate();
        }
        public static void main(String[] args) {
            SpringApplication.run(SpringCloudTestApplication.class, args);
        }
    }
```
   通过@EnableDiscoveryClient注解让该应用注册为Eureka客户端，以获取发现服务的能力
   同时在类中创建RestTemplate 实例，通过@LoadBalanced 开启负载均衡。
   
   在controller 配置如下
  ```java
    @RestController
    @RequestMapping(value = "/test")
    public class ExampleController {
        @Autowired
        private RestTemplate restTemplate;
        @RequestMapping(value = "/rest")
        public String testRestTemplate(){
            String url= "http://EUREKA-PROVIDER";
            return restTemplate.getForEntity(url+"/test/test_eureka",String.class).getBody();
        }
    }
```
    通过RestTemplate 来实现对EUREKA-PROVIDER（服务提供者的spring.applicatio.name）付的test_eureka接口调用，