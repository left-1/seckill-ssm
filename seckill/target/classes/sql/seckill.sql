CREATE DEFINER=`root`@`localhost` PROCEDURE `execute_seckill`(in v_seckill_id bigint, in v_phone bigint,
		in v_kill_time timestamp, out r_result int)
BEGIN
		DECLARE insert_count int DEFAULT 0;
        START TRANSACTION;
        insert ignore into success_killed 
			(seckill_Id, user_phone, create_time)
            values (v_seckill_id, v_phone, v_kill_time);
		select row_count() into insert_count;
        IF(insert_count = 0) THEN
			ROLLBACK;
            set r_result = -1;
		ELSEIF(insert_count < 0) THEN
			ROLLBACK;
            set r_result = -2;
		ELSE
			update seckill
			set number = number - 1
            where seckill_id = v_seckill_id
            and end_time > v_kill_time
            and start_time < v_kill_time
            and number > 0;
            select row_count() into insert_count;
            IF(insert_count = 0) THEN
				ROLLBACK;
                set r_result = 0;
			ELSEIF(insert_count < 0) THEN
				ROLLBACK;
                set r_result = -2;
			ELSE
				COMMIT;
                set r_result = 1;
			END IF;
		END IF;
	END
	
-- 存储过程
-- 1.存储过程优化: 事务行级锁持有时间
-- 2.不要过度依赖存储过程（简单的逻辑可以应用）