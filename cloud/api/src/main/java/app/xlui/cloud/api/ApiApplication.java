package app.xlui.cloud.api;

import feign.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients	// 利用 Feign 进行远程调用
public class ApiApplication {
	@Bean
	public static Request.Options requestOptions(ConfigurableEnvironment environment) {
		int readTimeOut = environment.getProperty("ribbon.ReadTimeOut", int.class, 60000);
		int connectTimeOut = environment.getProperty("ribbon.ConnectTimeOut", int.class, 60000);
		return new Request.Options(connectTimeOut, readTimeOut);
	}

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}
}
