package app.xlui.seckill.cloud.service.web;

import app.xlui.seckill.cloud.service.entity.Response;
import app.xlui.seckill.cloud.service.service.DistributedSeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/distribute")
public class DistributedSeckillController {
    private final DistributedSeckillService distributedSeckillService;

    @Autowired
    public DistributedSeckillController(DistributedSeckillService distributedSeckillService) {
        this.distributedSeckillService = distributedSeckillService;
    }

    @RequestMapping("/redis")
    public Response redis(int item, int user) {
        return distributedSeckillService.redisLock(item, user);
    }

    @RequestMapping("/zk")
//    public Response zk(int item, int user) {
    public Response zk() {
        return distributedSeckillService.zkLock(1, 1);
    }
}
