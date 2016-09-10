package com.lin.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.junit.experimental.theories.Theories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.lin.seckill.dao.SeckillDao;
import com.lin.seckill.dao.SuccessKilledDao;
import com.lin.seckill.dao.cache.RedisDao;
import com.lin.seckill.dto.Exposer;
import com.lin.seckill.dto.SeckillExecution;
import com.lin.seckill.entity.Seckill;
import com.lin.seckill.entity.SuccessKilled;
import com.lin.seckill.enums.SeckillStateEnum;
import com.lin.seckill.exception.RepeatKillException;
import com.lin.seckill.exception.SeckillCloseException;
import com.lin.seckill.exception.SeckillException;
import com.lin.seckill.service.SeckillService;

@Service("SeckillService")
public class SeckillServiceImpl implements SeckillService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillDao seckillDao;
	
	@Autowired
	private SuccessKilledDao successKilledDao;
	
	@Autowired
	private RedisDao redisDao;
	
	//md5盐值字符串，用于混淆
	private final String salt = "ghdfnjkgahnjkfsadnfkj2r389";
	
	@Override
	public List<Seckill> getSeckillList() {
		
		return seckillDao.queryAll(0, 4);
		
	}

	@Override
	public Seckill getById(long seckillId) {
		
		return seckillDao.queryById(seckillId);
		
	}

	@Override
	public Exposer exportSeckillUrl(long seckillId) {
		// 优化点：缓存优化
		//访问redis
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill == null){
			//访问数据库
			seckill = seckillDao.queryById(seckillId);
			if(seckill == null){
				return new Exposer(false, seckillId);
			} else{
				//放入redis
				redisDao.putSeckill(seckill);
			}
		}	
		
		if(seckill == null){
			
			return new Exposer(false, seckillId);
			
		}
		
		Date startTime = seckill.getStartTime();
		
		Date endTime = seckill.getEndTime();
		
		Date nowTime = new Date();
		
		if(nowTime.getTime() < startTime.getTime()
				|| nowTime.getTime() > endTime.getTime()){
			
			return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(),
					endTime.getTime());
			
		}
		
		//md5字符串做密钥
		String md5 = getMD5(seckillId);
		
		System.out.println(md5);
		
		return new Exposer(true, md5, seckillId);
		
	}

	@Override
	@Transactional
	/**
	 * 使用注解控制事务方法的优点：
	 * 1、开发团队达成一致约定，明确标注事务的方法
	 * 2、保证事务方法的执行时间尽可能短，不要穿插其他网络操作(RPC/HTTP请求)，或者剥离到事务方法外
	 * 3、不是所有的方法都要事务，如只有一条修改操作、只读操作不需要事务控制
	 */
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			
			throw new SeckillException("data rewrite");
			
		}
		
		try {
			//记录购买行为
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			
			//重复秒杀(seckillId, userPhone唯一)
			if(insertCount <= 0 ){
				
				throw new RepeatKillException("seckill repeated");
				
			} else {
				
				//执行秒杀逻辑: 减库存 + 记录秒杀行为
				//减库存,竞争
				int updataCount = seckillDao.reduceNumber(seckillId, new Date());
				
				if(updataCount <= 0){
					
					//没有更新到(秒杀结束)
					throw new SeckillCloseException("seckill is closed");
					
				} else {
					
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					
					return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
					
				}
			
			}
			
		} catch (RepeatKillException e1){
			
			throw e1;
			
		} catch (SeckillCloseException e2){
			
			throw e2;
			
		} catch (Exception e) {
		
			//将编译期异常转化为运行期异常
			logger.error(e.getMessage(), e);
			
			throw new SeckillException("seckill inner error:" + e.getMessage());
			
		}
		
	}

	private String getMD5(long seckillId){
		
		String base = seckillId + "/" + salt;
		
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		
		return md5;
		
	}

	@Override
	public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5)
	{
		if(md5 == null || !md5.equals(seckillId)){
			return new SeckillExecution(seckillId, SeckillStateEnum.DATE_REWRITE);
		}
		Date killtime = new Date();
		Map<String, Object> map = 
				new HashMap<String, Object>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killtime);
		map.put("result", null);
		//执行存储过程，result被赋值
		try{
			seckillDao.killByProceduce(map);
			int result = MapUtils.getInteger(map, "result", -2);
			if(result == 1){
				SuccessKilled sk = 
					successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, sk);
			} else {
				return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
		}
	}
}
