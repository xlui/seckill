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

	@RequestMapping("/sync")
	public Response sync(int itemId) {
		return ControllerUtils.mock(
				"Synchronized Lock",
				itemId,
				LOGGER,
				executor,
				commonRemote,
				(user) -> LOGGER.info("user {}: {}", user, normalSeckillRemote.sync(itemId, user).getKey("msg"))
		);
	}

	@RequestMapping("/lock")
	public Response lock(int itemId) {
		return ControllerUtils.mock(
				"Reentrant Lock",
				itemId,
				LOGGER,
				executor,
				commonRemote,
				(user) -> LOGGER.info("user {}: {}", user, normalSeckillRemote.lock(itemId, user).getKey("msg"))
		);
	}

	@RequestMapping("/aop")
	public Response aop(int itemId) {
		return ControllerUtils.mock(
				"AOP based lock",
				itemId,
				LOGGER,
				executor,
				commonRemote,
				(user) -> LOGGER.info("user {}: {}", user, normalSeckillRemote.aop(itemId, user).getKey("msg"))
		);
	}

	@RequestMapping("/db_p_1")
	public Response dbPessimisticLock1(int itemId) {
		return ControllerUtils.mock(
				"Database Pessimistic Lock Version 1",
				itemId,
				LOGGER,
				executor,
				commonRemote,
				(user) -> LOGGER.info("user {}: {}", user, normalSeckillRemote.dbPessimisticLock(itemId, user).getKey("msg"))
		);
	}

	@RequestMapping("db_p_2")
	public Response dbPessimisticLock2(int itemId) {
		return ControllerUtils.mock(
				"Database Pessimistic Lock Version 2",
				itemId,
				LOGGER,
				executor,
				commonRemote,
				(user) -> LOGGER.info("user {}: {}", user, normalSeckillRemote.dbPessimisticLock2(itemId, user).getKey("msg"))
		);
	}

	@RequestMapping("db_o")
	public Response dbOptimisticLock(int itemId) {
		return ControllerUtils.mock(
				"Database Optimistic Lock",
				itemId,
				LOGGER,
				executor,
				commonRemote,
				(user) -> LOGGER.info("user {}: {}", user, normalSeckillRemote.dbOptimisticLock(itemId, user).getKey("msg"))
		);
	}
}
