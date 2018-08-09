package app.xlui.cloud.api.remote;

import app.xlui.cloud.api.entity.Response;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "seckill-service")
@RequestMapping("/normal")
public interface NormalSeckillRemote {
	@RequestMapping("/nolock")
	Response nolock(@RequestParam(name = "item") Integer item, @RequestParam(name = "user") Integer user);

	@RequestMapping("/sync")
	Response sync(@RequestParam(name = "item") int item, @RequestParam(name = "user") int user);

	@RequestMapping("/lock")
	Response lock(@RequestParam(name = "item") int item, @RequestParam(name = "user") int user);

	@RequestMapping("/aop")
	Response aop(@RequestParam(name = "item") int item, @RequestParam(name = "user") int user);

	@RequestMapping("/db_p_1")
	Response dbPessimisticLock(@RequestParam(name = "item") int item, @RequestParam(name = "user") int user);

	@RequestMapping("/db_p_2")
	Response dbPessimisticLock2(@RequestParam(name = "item") int item, @RequestParam(name = "user") int user);

	@RequestMapping("/db_o")
	Response dbOptimisticLock(@RequestParam(name = "item") int item, @RequestParam(name = "user") int user);
}
