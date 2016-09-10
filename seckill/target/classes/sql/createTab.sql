-- 创建数据库
create database seckill;

-- 使用数据库
use seckill;

-- 创建秒杀库存表
create table seckill(
`seckill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
`name` varchar(120) NOT NULL COMMENT '商品名称',
`number` int NOT NULL COMMENT '库存数量',
`start_time` timestamp NOT NULL COMMENT '开始时间',
`end_time` timestamp NOT NULL COMMENT '结束时间',
`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
PRIMARY KEY(seckill_id),
key index_start_time(start_time),
key index_end_time(end_time),
key index_create_time(create_time)
)ENGINE = InnoDB AUTO_INCREMENT = 1000 DEFAULT CHARSET = utf8 COMMENT  '秒杀库存表';

-- 初始化数据
insert into
	seckill(name,number,start_time,end_time)
values
	('1000元秒杀Iphone6',100,'2015-11-01 00:00:00','2015-11-01 01:00:00'),
    ('1000元秒杀Iphone5s',200,'2015-11-01 00:00:00','2015-11-01 01:00:00'),
    ('1000元秒杀Iphone6s',300,'2015-11-01 00:00:00','2015-11-01 01:00:00'),
    ('1000元秒杀Iphone6plus',300,'2015-11-01 00:00:00','2015-11-01 01:00:00');


-- 成功秒杀明细
create table success_killed(
`seckill_id` bigint NOT NULL COMMENT '秒杀商品id',
`user_phone` bigint NOT NULL COMMENT '用户手机号',
`state` tinyint NOT NULL DEFAULT -1 COMMENT '状态(-1:无效,0:成功,1:已付款,2:已发货)',
`create_time` timestamp NOT NULL COMMENT '创建时间',
primary key(seckill_id,user_phone),-- 联合主键
key index_create_time(create_time)
)ENGINE = InnoDB DEFAULT CHARSET = utf8 COMMENT '秒杀成功明细表';
