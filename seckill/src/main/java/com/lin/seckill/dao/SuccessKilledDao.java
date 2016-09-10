package com.lin.seckill.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lin.seckill.entity.Seckill;
import com.lin.seckill.entity.SuccessKilled;

public interface SuccessKilledDao {
	
	/**
	 * 插入秒杀明细记录,可通过联合主键过滤重复
	 * @param seckillId
	 * @param userPhone
	 * @return
	 */
	public int insertSuccessKilled(@Param("seckillId")long seckillId, 
			@Param("userPhone")long userPhone);
	
	/**
	 * 根据id查询秒杀明细(带有秒杀库存对象)
	 * @param successKillId 秒杀明细id
	 * @return 秒杀明细
	 */
	public SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId,
			@Param("userPhone")long userPhone);
	
}
