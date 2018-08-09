package app.xlui.cloud.api.web;

import app.xlui.cloud.api.entity.Response;
import app.xlui.cloud.api.remote.CommonRemote;
import app.xlui.cloud.api.remote.DistributedSeckillRemote;
import app.xlui.cloud.api.utils.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class DistributedSeckillController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedSeckillController.class);
    private final CommonRemote commonRemote;
    private final DistributedSeckillRemote distributedSeckillRemote;

    @Autowired
    public DistributedSeckillController(DistributedSeckillRemote distributedSeckillRemote, CommonRemote commonRemote) {
        this.distributedSeckillRemote = distributedSeckillRemote;
        this.commonRemote = commonRemote;
    }

    @RequestMapping("/redis")
    public Response redis(int itemId) {
        return ControllerUtils.mock(
                "Redis Distributed Lock",
                itemId,
                LOGGER,
                Executors.newCachedThreadPool(),
                commonRemote,
                (user) -> LOGGER.info("user {}: {}", user, distributedSeckillRemote.redis(itemId, user).getKey("msg"))
        );
    }

    @RequestMapping("/zk")
    public Response zk(int itemId) {
        return ControllerUtils.mock(
                "ZooKeeper Distributed Lock",
                itemId,
                LOGGER,
                Executors.newCachedThreadPool(),
                commonRemote,
                (user) -> LOGGER.info("user {}: {}", user, distributedSeckillRemote.zk(itemId, user).getKey("msg"))
        );
    }
}
