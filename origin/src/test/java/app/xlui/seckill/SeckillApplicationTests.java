package app.xlui.seckill;

import app.xlui.seckill.service.SeckillService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SeckillApplicationTests {
	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private SeckillService seckillService;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void hello() {
		System.out.println("This is a test and you pass it!");
	}

	@Test
	public void v1() throws Exception {
		long item = 1;
		mockMvc.perform(MockMvcRequestBuilders.get("/v1?itemId=" + item))
				.andExpect(MockMvcResultMatchers.status().isOk());
		Assert.assertNotEquals(seckillService.successCount(item), 300);
	}
}
