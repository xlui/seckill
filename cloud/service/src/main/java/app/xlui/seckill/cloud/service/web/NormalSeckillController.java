package app.xlui.seckill.cloud.service.web;

import app.xlui.seckill.cloud.service.aop.ServiceLimit;
import app.xlui.seckill.cloud.service.entity.Response;
import app.xlui.seckill.cloud.service.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/normal")
public class NormalSeckillController {
	private final SeckillService seckillService;

    @Autowired
    public NormalSeckillController(SeckillService seckillService) {
        this.seckillService = seckillService;
    }

    @RequestMapping("/nolock")
	@ServiceLimit
	public Response nolock(int item, int user) {
		return seckillService.normal(item, user);
	}

	@RequestMapping("/sync")
	public Response sync(int item, int user) {
		return seckillService.syncLock(item, user);
	}

	@RequestMapping("/lock")
	public Response lock(int item, int user) {
		return seckillService.reentrantLock(item, user);
	}

	@RequestMapping("/aop")
	public Response aop(int item, int user) {
		return seckillService.aopLock(item, user);
	}

	@RequestMapping("/db_p_1")
	public Response dbPessimisticLock(int item, int user) {
		return seckillService.dbPessimisticLock(item, user);
	}

	@RequestMapping("/db_p_2")
	public Response dbPessimisticLock2(int item, int user) {
		return seckillService.dbPessimisticLock2(item, user);
	}

	@RequestMapping("/db_o")
	public Response dbOptimisticLock(int item, int user) {
		return seckillService.dbOptimisticLock(item, user);
	}
}
