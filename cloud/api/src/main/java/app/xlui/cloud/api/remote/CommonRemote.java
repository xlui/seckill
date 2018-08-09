package app.xlui.cloud.api.remote;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "seckill-service")
public interface CommonRemote {
	@RequestMapping("/reset")
	void reset(@RequestParam(name = "itemId") Integer itemId);

	@RequestMapping("/customers")
	int customers();

	@RequestMapping("/succ")
	int successCount(@RequestParam(name = "itemId") int itemId);
}

