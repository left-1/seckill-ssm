package seckill;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lin.seckill.dao.SeckillDao;
import com.lin.seckill.dto.Exposer;
import com.lin.seckill.dto.SeckillExecution;
import com.lin.seckill.entity.Seckill;
import com.lin.seckill.service.SeckillService;

/*
 * 配置spring和junit整合,让junit启动时加载spring的IOC容器
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-*.xml")
public class SeckillDaoTest {
	
	//注入Dao实现类
	@Resource
	private SeckillDao seckillDao;
	
	@Resource
	private SeckillService seckillService;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Test
	public void TestQueryById() throws Exception{
		
		long id = 1000;
		
		Seckill seckill = seckillDao.queryById(id);
		
		System.out.println(seckill);
		
	}
	
	@Test
	public void TestQueryAll() throws Exception{
		
		List<Seckill> seckills = seckillDao.queryAll(0, 5);
		
		for(Seckill seckill : seckills){
		
			System.out.println(seckill);
			
		}
		
	}
	
	@Test
	public void TestReduceNumber() throws Exception{
		
		Date killTime = new Date();
		
		int updateCount = seckillDao.reduceNumber(1000L, killTime);
		
		System.out.println(updateCount);
		
	}
	
	@Test
	public void TestKillByProceduce() throws Exception{
		
		long seckillId = 1001;
		
		long phone = 18318260033L;
		
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		
		if(exposer.isExposed()){
			
			String md5 = exposer.getMd5();
			
			SeckillExecution execution = 
				seckillService.executeSeckillProcedure(seckillId, phone, md5);

			System.out.println(execution.getStateInfo());
			
		}
		
		
	}
	
}
