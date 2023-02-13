# http-caller

为 `Spring-Boot` 提供了 HTTP 调用支持，同时包含了重试、负载均衡、熔断，解决非微服务架构跨项目/跨语言调用问题

好处是无需配置两套相同的配置，降低一丢丢配置量

## 使用说明

- 1、集成依赖（需先将该项目源码下载并打包）

```xml

<dependency>
    <groupId>com.mogudiandian</groupId>
    <artifactId>http-caller</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

- 2、使用工具类

```java
@Service
public class ExternalService {

    @Autowired
    private HttpCaller httpCaller;

    public void callUserServiceWithRetry(String userId) {
        // 下面代码用来示例，实际应该写在配置文件中
        List<String> hostList = ImmutableList.of("http://10.120.10.1:8087", "10.120.10.1:8087");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        Map<String, String> params = new HashMap<>(1, 1);
        params.put("userId", userId);
        HttpEntity<CallbackCensorshipResponse> requestParam = new HttpEntity<>(params, headers);
        UserDTO userDTO = httpCaller.call(hostList, "/user/info", null, HttpMethod.POST, requestParam, UserDTO.class);
        ...
    }
}
```

- 3、一些配置
```java
// 需要默认重试，可自己在configuration中声明一个RetryTemplate
@Bean
public RetryTemplate retryTemplate() {
    // TODO 声明RetryTemplate
}

// 自定义ClientHttpRequestFactory，可自己在configuration中声明一个ClientHttpRequestFactory
@Bean
public ClientHttpRequestFactory clientHttpRequestFactory() {
    // TODO 声明ClientHttpRequestFactory
}

// 自定义RestTemplate，可自己在configuration中声明一个RestTemplate
@Bean
public RestTemplate restTemplate() {
    // TODO 声明RestTemplate
}
```

```properties
# 默认连接时间(毫秒) 默认10秒
http.caller.default-connect-timeout=10000
# 默认读取时间(毫秒) 默认10秒
http.caller.default-read-timeout=10000
# 负载均衡器类型 默认随机 目前只有随机和轮询
http.caller.load-balancer-type=RANDOM;
# 断路器窗口大小 默认是10 表示调用10次后开始计数
http.caller.circuit-breaker-window-size=10
# 断路器熔断阈值 默认50% 表示失败超过50%则开启
http.caller.circuit-breaker-threshold-percent=0.5
# 断路器开启最小调用数量 默认是5 表示如果超过50%但又没到10次不开启
http.caller.circuit-breaker-open-min-calls=10
# 断路器每次开启时长(毫秒) 默认5秒
http.caller.circuit-breaker-open-duration=5000
```

## 依赖三方库

| 依赖            | 版本号           | 说明  |
|---------------|---------------|-----|
| spring-boot   | 2.3.4.RELEASE |     |
| fastjson      | 1.2.73        |     |
| commons-lang3 | 3.11          |     |
| guava         | 29.0-jre      |     |
| resilience4j  | 1.7.1         |     |
| lombok        | 1.18.16       |     |

## 使用前准备

- [Maven](https://maven.apache.org/) (构建/发布当前项目)
- Java 8 ([Download](https://adoptopenjdk.net/releases.html?variant=openjdk8))

## 构建/安装项目

使用以下命令:

`mvn clean install`

## 发布项目

修改 `pom.xml` 的 `distributionManagement` 节点，替换为自己在 `settings.xml` 中 配置的 `server` 节点，
然后执行 `mvn clean deploy`

举例：

`settings.xml`

```xml

<servers>
    <server>
        <id>snapshots</id>
        <username>yyy</username>
        <password>yyy</password>
    </server>
    <server>
        <id>releases</id>
        <username>xxx</username>
        <password>xxx</password>
    </server>
</servers>
```

`pom.xml`

```xml

<distributionManagement>
    <snapshotRepository>
        <id>snapshots</id>
        <url>http://xxx/snapshots</url>
    </snapshotRepository>
    <repository>
        <id>releases</id>
        <url>http://xxx/releases</url>
    </repository>
</distributionManagement>
```
