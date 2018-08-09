package app.xlui.seckill.cloud.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
	@Value("${value}")
	private String value;

	@RequestMapping("/hello")
	public String index(@RequestParam String name) {
		return "hello " + name + ", this is the " + value + " page";
	}
}
