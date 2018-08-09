package app.xlui.cloud.api.remote;

import app.xlui.cloud.api.entity.Response;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "seckill-service")
@RequestMapping("/distribute")
public interface DistributedSeckillRemote {
    @RequestMapping("/redis")
    Response redis(@RequestParam(name = "item") int item, @RequestParam("user") int user);

    @RequestMapping("/zk")
    Response zk(@RequestParam(name = "item") int item, @RequestParam("user") int user);
}
