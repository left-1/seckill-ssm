package seckill;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lin.seckill.dto.Exposer;
import com.lin.seckill.dto.SeckillExecution;
import com.lin.seckill.entity.Seckill;
import com.lin.seckill.exception.RepeatKillException;
import com.lin.seckill.exception.SeckillCloseException;
import com.lin.seckill.service.SeckillService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
					   "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillService seckillService;
	
	@Test
	public void testGetSeckillList() {
		
		List<Seckill> list = seckillService.getSeckillList();
		
		logger.info("list={}", list);
		
	}

	@Test
	public void testGetById() {
		
		Seckill seckill = seckillService.getById(1000);
		
		logger.info("seckill={}", seckill);
		
	}

	@Test
	public void testExportSeckillUrl() {
		
		long seckillId = 1000;
		
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		
		logger.info("exposer={}", exposer);
		
	}

	@Test
	public void testExecuteSeckill() {
		
		long seckillId = 1000;
		
		long userPhone = 18318260032L;
		
		String md5 = "62d41a36fae539a3382cb40779998fb9";
		
		try {
			
			SeckillExecution execution = seckillService.executeSeckill(seckillId, userPhone, md5);
		
			logger.info("execution={}", execution);		
			
		} catch (RepeatKillException e) {
			
			logger.error(e.getMessage());
			
		} catch (SeckillCloseException e) {
			
			logger.error(e.getMessage());
			
		}
		
	}
	
	@Test
	public void testSeckillLogic() {
		
		long seckillId = 1001;
		
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		
		if(exposer.isExposed()){
			
			logger.info("exposer={}", exposer);
			
			long userPhone = 18318260032L;
			
			String md5 = exposer.getMd5();
			
			try {
				
				SeckillExecution execution = seckillService.executeSeckill(seckillId, userPhone, md5);
			
				logger.info("execution={}", execution);		
				
			} catch (SeckillCloseException e) {
				
				logger.error(e.getMessage());
				
			} catch (RepeatKillException e) {
				
				logger.error(e.getMessage());
				
			}
			
		} else{
			
			logger.warn("exposer={}", exposer);
			
		}
		
	}

}
