package seckill;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lin.seckill.dao.SeckillDao;
import com.lin.seckill.dao.SuccessKilledDao;
import com.lin.seckill.entity.Seckill;
import com.lin.seckill.entity.SuccessKilled;

/*
 * 配置spring和junit整合,让junit启动时加载spring的IOC容器
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SuccessKilledDaoTest {
	
	//注入Dao实现类
	@Resource
	private SuccessKilledDao successKilledDao;
	
	@Test
	public void TestInsertSuccessKilled() throws Exception{
		
		long id = 1001L;
		
		long phone = 18318260031L;
		
		int insertCount = successKilledDao.insertSuccessKilled(id, phone);
		
		System.out.println(insertCount);
		
	}
	
	@Test
	public void TestQueryByIdWithSeckill() throws Exception{
		
		long id = 1000L;
		
		long phone = 18318260031L;
		
		SuccessKilled successKilled = 
				successKilledDao.queryByIdWithSeckill(id, phone);
		
		System.out.println(successKilled);
		
		System.out.println(successKilled.getSeckill());
		
	}
	
}
