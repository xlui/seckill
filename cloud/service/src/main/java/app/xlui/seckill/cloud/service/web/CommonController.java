package app.xlui.seckill.cloud.service.web;

import app.xlui.seckill.cloud.service.config.SeckillProperties;
import app.xlui.seckill.cloud.service.entity.Response;
import app.xlui.seckill.cloud.service.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {
	@Autowired
	private SeckillService seckillService;
	@Autowired
	private SeckillProperties seckillProperties;

	@RequestMapping("/reset")
	public void reset(int itemId) {
		seckillService.reset(itemId);
	}

	@RequestMapping("/customers")
	public int customers() {
		return seckillProperties.getCustomers();
	}

	@RequestMapping("/succ")
	public int successCount(int itemId) {
		return seckillService.successCount(itemId);
	}
}
