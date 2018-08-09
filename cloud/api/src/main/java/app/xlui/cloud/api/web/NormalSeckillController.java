package app.xlui.cloud.api.web;

import app.xlui.cloud.api.entity.Response;
import app.xlui.cloud.api.remote.CommonRemote;
import app.xlui.cloud.api.remote.NormalSeckillRemote;
import app.xlui.cloud.api.utils.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class NormalSeckillController {
	private static final Logger LOGGER = LoggerFactory.getLogger(NormalSeckillController.class);
	private final CommonRemote commonRemote;
	private final NormalSeckillRemote normalSeckillRemote;
	private ExecutorService executor = Executors.newCachedThreadPool();

	@Autowired
	public NormalSeckillController(NormalSeckillRemote normalSeckillRemote, CommonRemote commonRemote) {
		this.normalSeckillRemote = normalSeckillRemote;
		this.commonRemote = commonRemote;
	}

	@RequestMapping("/nolock")
	public Response nolock(int itemId) {
		return ControllerUtils.mock(
				"No Lock And No Synchronization",
				itemId,
				LOGGER,
				executor,
				commonRemote,
				(user) -> LOGGER.info("user {}: {}", user, normalSeckillRemote.nolock(itemId, user).getKey("msg"))
		);
	}
}
