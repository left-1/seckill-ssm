package com.lin.seckill.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.lin.seckill.entity.Seckill;

public interface SeckillDao {
	
	/**
	 * 减少库存
	 * @param seckillId 商品库存id
	 * @param killTime 秒杀时间
	 * @return 影响行数
	 */
	public int reduceNumber(@Param("seckillId")long seckillId,
			@Param("killTime")Date killTime);
	
	/**
	 * 根据id查询商品库存
	 * @param seckillId 商品库存id
	 * @return 商品库存
	 */
	public Seckill queryById(long seckillId);
	
	/**
	 * 根据偏移量查询秒杀库存列表
	 * @param offset 偏移量
	 * @param limit 条数
	 * @return
	 */
	public List<Seckill> queryAll(@Param("offset") int offset, 
			@Param("limit") int limit);
	
	/**
	 * 使用存储过程执行秒杀
	 * @param paramMap
	 */
	public void killByProceduce(Map<String, Object> paramMap);
}
