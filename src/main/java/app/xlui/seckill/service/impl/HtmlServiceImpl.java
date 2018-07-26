package app.xlui.seckill.service.impl;

import app.xlui.seckill.config.Const;
import app.xlui.seckill.config.SeckillProperties;
import app.xlui.seckill.entity.Item;
import app.xlui.seckill.entity.resp.Response;
import app.xlui.seckill.repository.ItemRepository;
import app.xlui.seckill.service.HtmlService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class HtmlServiceImpl implements HtmlService {
	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
			Const.processors,    // pool size
			Const.processors,    // maximum pool size
			0,        // keep-alive time
			TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(1000),    // blocking queue
			Executors.defaultThreadFactory(),        // thread factory
			new ThreadPoolExecutor.AbortPolicy()    // rejected execution handler
	);
	private final Configuration configuration;
	private final ItemRepository itemRepository;
	private final SeckillProperties seckillProperties;

	@Autowired
	public HtmlServiceImpl(Configuration configuration, ItemRepository itemRepository, SeckillProperties seckillProperties) {
		this.configuration = configuration;
		this.itemRepository = itemRepository;
		this.seckillProperties = seckillProperties;
	}

	@Override
	public Response generate() {
		List<Item> items = itemRepository.findAll();
		final List<Future<String>> results = new ArrayList<>();
		items.forEach(item -> results.add(executor.submit(new Create(item))));
		for (Future<String> result : results) {
			try {
				System.out.println(result.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		return new Response(HttpStatus.OK.value(), "Successfully create html pages for item");
	}

	private final class Create implements Callable<String> {
		private Item item;

		public Create(Item item) {
			this.item = item;
		}

		@Override
		public String call() throws Exception {
			Template template = configuration.getTemplate("goods.ftl");
			File file = new File(seckillProperties.getHtml() + item.getItemId() + ".html");
			template.process(item, new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			return "success";
		}
	}
}
