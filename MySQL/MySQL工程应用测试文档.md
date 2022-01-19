# MySQL工程应用测试文档

## 1 Explain工具使用

```sql
DROP TABLE IF EXISTS `actor`;
  CREATE TABLE `actor` (
   `id` int(11) NOT NULL,
   `name` varchar(45) DEFAULT NULL,
   `update_time` datetime DEFAULT NULL,
   PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
	
INSERT INTO `actor` (`id`, `name`, `update_time`) VALUES (1,'a',NOW()), (2,'b',NOW()), (3,'c',NOW());

DROP TABLE IF EXISTS `film`;
CREATE TABLE `film` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` varchar(10) DEFAULT NULL,
PRIMARY KEY (`id`),
KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `film` (`id`, `name`) VALUES (3,'film0'),(1,'film1'),(2,'film2');

DROP TABLE IF EXISTS `film_actor`;
CREATE TABLE `film_actor` (
`id` int(11) NOT NULL,
`film_id` int(11) NOT NULL,
`actor_id` int(11) NOT NULL,
`remark` varchar(255) DEFAULT NULL,
PRIMARY KEY (`id`),
KEY `idx_film_actor_id` (`film_id`,`actor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `film_actor` (`id`, `film_id`, `actor_id`) VALUES (1,1,1),(2,1,2),(3,2,1);
```

1. id列
2. select_type列

* Simple

```sql
explain select * from film where id = 2;
```

![image-20220119120832411](../../img/image-20220119120832411.png)

* primary
* subquery
* derived

```sql
set session optimizer_switch='derived_merge=off'; #关闭mysql5.7新特性对衍生表的合并优化
explain select (select 1 from actor where id = 1) from (select * from film where id = 1) der;
set session optimizer_switch='derived_merge=on';
```

![image-20220119121148150](../../img/image-20220119121148150.png)

3. table列
4. type列

* NULL

```sql
explain select min(id) from film;
```

![image-20220119121428895](../../img/image-20220119121428895.png)

* const, system

```sql
set session optimizer_switch='derived_merge=off'; 
explain select * from (select * from film where id = 1) tmp;
set session optimizer_switch='derived_merge=on'; 
```

![image-20220119121758827](../../img/image-20220119121758827.png)

* eq_ref

```sql
explain select * from film_actor left join film on film_actor.film_id = film.id;
```

![image-20220119121919276](../../img/image-20220119121919276.png)

* ref

```sql
 explain select * from film where name = 'film1';
```

![image-20220119122022724](../../img/image-20220119122022724.png)

```sql
explain select film_id from film left join film_actor on film.id = film_actor.film_id;
```

![image-20220119122310749](../../img/image-20220119122310749.png)

* range

```sql
explain select * from actor where id > 1;
```

![image-20220119122347820](../../img/image-20220119122347820.png)

* index

```sql
explain select * from film;
```

![image-20220119122422764](../../img/image-20220119122422764.png)

* ALL

```sql
explain select * from actor;
```

![image-20220119122523004](../../img/image-20220119122523004.png)

5. possible_key

6. key
7. key_len

8. ref
9. rows
10. Extra

* Using index

```sql
 explain select film_id from film_actor where film_id = 1;
```

![image-20220119122813216](../../img/image-20220119122813216.png)

* Using where

```sql
explain select * from actor where name = 'a';
```

![image-20220119122851782](../../img/image-20220119122851782.png)

* Using Index condition

```sql
explain select * from film_actor where film_id > 1;
```

![image-20220119123455892](../../img/image-20220119123455892.png)

* Using filesort

```sql
explain select * from actor order by name; # name无索引
```

![image-20220119123622130](../../img/image-20220119123622130.png)

```sql
explain select * from film order by name; # name有索引
```

![image-20220119123710858](../../img/image-20220119123710858.png)

## 2 索引实践

```sql
CREATE TABLE `employees` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` varchar(24) NOT NULL DEFAULT '' COMMENT '姓名',
`age` int(11) NOT NULL DEFAULT '0' COMMENT '年龄',
`position` varchar(20) NOT NULL DEFAULT '' COMMENT '职位',
`hire_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入职时间',
PRIMARY KEY (`id`),
KEY `idx_name_age_position` (`name`,`age`,`position`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='员工记录表';

INSERT INTO employees(name,age,position,hire_time) VALUES('LiLei',22,'manager',NOW());
INSERT INTO employees(name,age,position,hire_time) VALUES('HanMeimei', 23,'dev',NOW());
INSERT INTO employees(name,age,position,hire_time) VALUES('Lucy',23,'dev',NOW());

‐‐ 插入一些示例数据
drop procedure if exists insert_emp;
delimiter ;;
create procedure insert_emp()
begin
	declare i int;
	set i=1;
	while(i<=100000)do
		insert into employees(name,age,position) values(CONCAT('zhuge',i),i,'dev');
	set i=i+1;
	end while;
end;;
delimiter ;
call insert_emp();

CREATE TABLE `employees_copy` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` varchar(24) NOT NULL DEFAULT '' COMMENT '姓名',
`age` int(11) NOT NULL DEFAULT '0' COMMENT '年龄',
`position` varchar(20) NOT NULL DEFAULT '' COMMENT '职位',
`hire_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入职时间',
PRIMARY KEY (`id`),
KEY `idx_name_age_position` (`name`,`age`,`position`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='员工记录表';

INSERT INTO employees_copy(name,age,position,hire_time) VALUES('LiLei',22,'manager',NOW());
INSERT INTO employees_copy(name,age,position,hire_time) VALUES('HanMeimei', 23,'dev',NOW());
INSERT INTO employees_copy(name,age,position,hire_time) VALUES('Lucy',23,'dev',NOW());


CREATE TABLE `t1` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `a` int(11) DEFAULT NULL,
 `b` int(11) DEFAULT NULL,
 PRIMARY KEY (`id`),
 KEY `idx_a` (`a`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 create table t2 like t1;


drop procedure if exists insert_t1; 
delimiter ;;
create procedure insert_t1()
begin
   declare i int;
   set i=1;
   while(i<=10000)do
     insert into t1(a,b) values(i,i);
     set i=i+1;
   end while;
 end;;
 delimiter ;
 call insert_t1();
 

drop procedure if exists insert_t2; 
delimiter ;;
create procedure insert_t2()
begin
  declare i int;
  set i=1;
  while(i<=100)do
    insert into t2(a,b) values(i,i);
    set i=i+1;
  end while;
end;;
delimiter ;
call insert_t2();
```

### 2.1 索引优化测试实例

1. 全值匹配

```sql
EXPLAIN SELECT * FROM employees WHERE name= 'LiLei';
```

![image-20220119124448712](../../img/image-20220119124448712.png)

```sql
EXPLAIN SELECT * FROM employees WHERE name= 'LiLei' AND age = 22;
```

![image-20220119124528596](../../img/image-20220119124528596.png)

```sql
EXPLAIN SELECT * FROM employees WHERE name= 'LiLei' AND age = 22 AND position ='manager';
```

![image-20220119124615451](../../img/image-20220119124615451.png)

2. 最左前缀法则

```sql
EXPLAIN SELECT * FROM employees WHERE name = 'Bill' and age = 31; # YES
EXPLAIN SELECT * FROM employees WHERE age = 30 AND position = 'dev'; # No
EXPLAIN SELECT * FROM employees WHERE position = 'manager'; # No
```

3. 不在索引列上做任何操作

```sql
EXPLAIN SELECT * FROM employees WHERE name = 'LiLei';	# YES
EXPLAIN SELECT * FROM employees WHERE left(name,3) = 'LiLei'; # NO
```

4. 不能使用索引中范围条件右边的列

```sql
EXPLAIN SELECT * FROM employees WHERE name= 'LiLei' AND age = 22 AND position ='manager'; # key_len=140
EXPLAIN SELECT * FROM employees WHERE name= 'LiLei' AND age > 22 AND position ='manager'; # key_len=78
```

5. 尽量使用覆盖索引(只访问索引的查询(索引列包含查询列))

```sql
EXPLAIN SELECT name,age FROM employees WHERE name= 'LiLei' AND age = 23 AND position ='manager'; # Using index
EXPLAIN SELECT * FROM employees WHERE name= 'LiLei' AND age = 23 AND position ='manager'; 
```

6. is null,is not null 一般情况下也无法使用索引

```sql
EXPLAIN SELECT * FROM employees WHERE name is null; 
```

7. like以通配符开头('$abc...')mysql索引失效会变成全表扫描操作

```sql
EXPLAIN SELECT * FROM employees WHERE name like '%Lei' # type=ALL
EXPLAIN SELECT * FROM employees WHERE name like 'Lei%' # type=range
```

8. mysql在使用不等于(!=或者<>)，not in ，not exists 的时候无法使用索引会导致全表扫描 < 小于、 > 大于、 <=、>= 这些，mysql内部优化器会根据检索比例、表大小等多个因素整体评估是否使用索引

```sql
EXPLAIN SELECT * FROM employees WHERE name != 'LiLei'; # type=ALL
```

9. 字符串不加单引号索引失效

```sql
EXPLAIN SELECT * FROM employees WHERE name = '1000'; # type=ref
EXPLAIN SELECT * FROM employees WHERE name = 1000; # type=ALL
```

10. 少用or或in，用它查询时，mysql不一定使用索引，mysql内部优化器会根据检索比例、表大小等多个因素整体评估是否使用索引。

```sql
EXPLAIN SELECT * FROM employees WHERE name = 'LiLei' or name = 'HanMeimei'; # type=ALL
```

![image-20220119130343350](../../img/image-20220119130343350.png)

11. order by 与 group by优化

* order by 走索引

说明：利用最左前缀法则:中间字段不能断，因此查询用到了name索引，从key_len=74也能看出，age索引列用在排序过程中，因为Extra字段里没有using filesort

```sql
explain select * from employees where name = 'LiLei' and position = 'dev' order by age;
```

![image-20220119143455320](../../img/image-20220119143455320.png)

* order by 未走索引

说明：从explain的执行结果来看:key_len=74，查询使用了name索引，由于用了position进行排序，跳过了 age，出现了Using filesort。

```sql
explain select * from employees where name = 'LiLei' order by position;
```

![image-20220119143604860](../../img/image-20220119143604860.png)

* order by 后面的字段符合最左前缀走索引

说明：查找只用到索引name，age和position用于排序，无Using filesort。

```sql
explain select * from employees where name = 'LiLei' order by age,position;
```

![image-20220119143836846](../../img/image-20220119143836846.png)

* order by 后面的字段不符合最左前缀走索引

说明：出现了Using filesort，因为索引的创建顺序为 name,age,position，但是排序的时候age和position颠倒位置了。

```sql
explain select * from employees where name = 'LiLei' order by position,age;
```

![image-20220119144002021](../../img/image-20220119144002021.png)

* order by 后面字段为常量，颠倒也可以走索引

说明：在Extra中并未出现Using filesort，因为age为常量，在排序中被优化，所以索引未颠倒， 不会出现Using filesort。

```sql
explain select * from employees where name = 'LiLei' and age = 22 order by position,age;
```

![image-20220119144145707](../../img/image-20220119144145707.png)

* order by 后面的排序方式不同，不走索引

说明：虽然排序的字段列与索引顺序一样，且order by默认升序，这里position desc变成了降序，导致与索引的 排序方式不同，从而产生Using filesort。Mysql8以上版本有降序索引可以支持该种查询方式。

```sql
explain select * from employees where name = 'LiLei' order by age asc,position desc;
```

![image-20220119144359181](../../img/image-20220119144359181.png)

* order by 前面为范围查询，不走索引

说明：对于排序来说，多个相等条件也是范围查询

```sql
explain select * from employees where name in ('LiLei','Yorick') order by age,position;
```

![image-20220119144555784](../../img/image-20220119144555784.png)

* order by 走索引代价高，不走索引

说明：索引第一个字段就是范围查找，并且要排序。直接全表扫描cost值更高。不走索引

```sql
explain select * from employees where name > 'a' order by name;
```

![image-20220119144853599](../../img/image-20220119144853599.png)

覆盖索引优化

```sql
explain select name,age,position from employees where name > 'a' order by name;
```

![image-20220119145017297](../../img/image-20220119145017297.png)

12. 分页查询优化

* 根据自增且连续的主键排序的分页查询

根据自增且连续主键排序的分页查询。表示从表 employees 中取出从 90001 行开始的 5行记录。看似只查询了 5条记录，实际这条 SQL 是先读取 90005 条记录，然后抛弃前 90000 条记录，然后读到后面 5 条想要的数据。因此要查询一张大表比较靠后的数据，执行效率 是非常低的。

```sql
explain select * from employees limit 90000,5;
```

![image-20220119154604381](../../img/image-20220119154604381.png)

因为主键是自增并且连续的，所以可以改写成按照主键去查询从第 90001开始的五行数据，显然改写后的 SQL 走了索引，而且扫描的行数大大减少，执行效率更高。但是，这条改写的SQL 在很多场景并不实用，因为表中可能某些记录被删后，主键空缺，导致结果不一致。

```sql
EXPLAIN select * from employees where id > 90000 limit 5;
```

![image-20220119154634227](../../img/image-20220119154634227.png)

* 根据非主键字段排序的分页查询

发现并没有使用 name 字段的索引(key 字段对应的值为 null)。扫描整个索引并查找到没索引 的行(可能要遍历多个索引树)的成本比扫描全表的成本更高，所以优化器放弃使用索引。

```sql
explain select * from employees order by name limit 90000,5;
```

![image-20220119154848369](../../img/image-20220119154848369.png)

可以让排序和分页操作先查出主键，然后根据主键查到对应的记录，需要的结果与原 SQL 一致，执行时间减少了一半以上。原 SQL 使用的是 filesort 排序，而优化后的 SQL 使用的是索引排序。

```sql
select * from employees e inner join (select id from employees order by name limit 90000,5) ed on e.id = ed.id;
```

![image-20220119155111578](../../img/image-20220119155111578.png)

13. join关联查询优化

*  嵌套循环连接 Nested-Loop Join(NLJ) 算法

一次一行循环地从第一张表(称为驱动表)中读取行，在这行数据中取到关联字段，根据关联字段在另一张表(被驱动表)里取出满足条件的行，然后取出两张表的结果合集。

```sql
EXPLAIN select * from t1 inner join t2 on t1.a= t2.a;
```

![image-20220119155648561](../../img/image-20220119155648561.png)

* 基于块的嵌套循环连接 Block Nested-Loop Join(BNL)算法

把驱动表的数据读入到 join_buffer 中，然后扫描被驱动表，把被驱动表每一行取出来跟 join_buffer 中的数据做对比。

```sql
EXPLAIN select * from t1 inner join t2 on t1.b= t2.b;
```

![image-20220119155838893](../../img/image-20220119155838893.png)

14. 补充例子

* 联合索引第一个字段用范围不会走索引

说明：mysql内部可能觉得第一个字段就用范围，结果集应该很大，回表效率不高，还不

如就全表扫描。

```sql
EXPLAIN SELECT * FROM employees WHERE name > 'LiLei' AND age = 22 AND position ='manager';
```

![image-20220119134725577](../../img/image-20220119134725577.png)

* 强制走索引

说明：虽然使用了强制走索引让联合索引第一个字段范围查找也走索引，扫描的行rows看上去也少了点，但是最终查找效率不一定比全表扫描高，因为回表效率不高。

```sql
EXPLAIN SELECT * FROM employees force index(idx_name_age_position) WHERE name > 'LiLei' AND age = 22 ND position ='manager';
```

![image-20220119134845224](../../img/image-20220119134845224.png)

```sql
-- 测试走索引和不走索引的执行效率
-- 关闭查询缓存
set global query_cache_size=0;
set global query_cache_type=0;
-- 执行时间0.095s
SELECT * FROM employees WHERE name > 'LiLei';
-- 执行时间0.105s 添加索引执行更慢
SELECT * FROM employees force index(idx_name_age_position) WHERE name > 'LiLei';
```

* 覆盖索引优化

上面查询全部数据字段需要回表使得走索引效率不高故综合cost得分未选择走索引，但是下面这个由于是覆盖索引，导致cost得分提升，最终MySQL选择走索引。

```sql
EXPLAIN SELECT name,age,position FROM employees WHERE name > 'LiLei' AND age = 22 AND position ='manager';
```

![image-20220119135700438](../../img/image-20220119135700438.png)

* in和or在表数据量比较大的情况会走索引，在表记录不多的情况下会选择全表扫描

```sql
-- in
EXPLAIN SELECT * FROM employees WHERE name in ('LiLei','HanMeimei','Lucy') AND age = 22 AND position ='manager';
-- or
EXPLAIN SELECT * FROM employees WHERE (name = 'LiLei' or name = 'HanMeimei') AND age = 22 AND position ='manager';
```

![image-20220119140841929](../../img/image-20220119140841929.png)

```sql
-- in 小表
EXPLAIN SELECT * FROM employees_copy WHERE name in ('LiLei','HanMeimei','Lucy') AND age = 22 AND position ='manager';
-- or 小表
EXPLAIN SELECT * FROM employees_copy WHERE (name = 'LiLei' or name = 'HanMeimei') AND age = 22 AND position ='manager';
```

![image-20220119141406372](../../img/image-20220119141406372.png)

* like KK% 一般情况都会走索引

```sql
-- 大表
EXPLAIN SELECT * FROM employees WHERE name like 'LiLei%' AND age = 22 AND position ='manager';
-- 小表
EXPLAIN SELECT * FROM employees WHERE name like 'LiLei%' AND age = 22 AND position ='manager';
```

![image-20220119141646519](../../img/image-20220119141646519.png)



### 2.2 索引选择底层原理测试Trace

```sql
EXPLAIN select * from employees where name > 'a' ; # 不走索引
EXPLAIN select * from employees where name > 'zzz' ; # 走索引
```

```sql
set session optimizer_trace="enabled=on",end_markers_in_json=on; # 开启trace
select * from employees where name > 'a';
SELECT * FROM information_schema.OPTIMIZER_TRACE;

select * from employees where name > 'zzz';
SELECT * FROM information_schema.OPTIMIZER_TRACE;
set session optimizer_trace="enabled=off"; # 关闭trace
```

```json
//  select * from employees where name > 'a';
{
   "considered_execution_plans": [
     {
       "plan_prefix": [
       ] /* plan_prefix */,
       "table": "`employees`",
       "best_access_path": {
         "considered_access_paths": [ // 最优访问路径
           {
             "rows_to_scan": 100140,
             "access_type": "scan", // 访问类型:为scan，全表扫描
             "resulting_rows": 100140,
             "cost": 20381,
             "chosen": true // 确定选择
           }
         ] /* considered_access_paths */
       } /* best_access_path */,
       "condition_filtering_pct": 100,
       "rows_for_plan": 100140,
       "cost_for_plan": 20381,
       "chosen": true
     }
   ] /* considered_execution_plans */
 }

// select * from employees where name > 'zzz';
{
  "considered_execution_plans": [ // 最优访问路径
    {
      "plan_prefix": [
      ] /* plan_prefix */,
      "table": "`employees`",
      "best_access_path": {
        "considered_access_paths": [
          {
            "rows_to_scan": 1,
            "access_type": "range", // 访问类型:为range，索引扫描
            "range_details": {
              "used_index": "idx_name_age_position" // 使用的索引
            } /* range_details */,
            "resulting_rows": 1,
            "cost": 2.41,
            "chosen": true // 确定选择
          }
        ] /* considered_access_paths */
      } /* best_access_path */,
      "condition_filtering_pct": 100,
      "rows_for_plan": 1,
      "cost_for_plan": 2.41,
      "chosen": true
    }
  ] /* considered_execution_plans */
}
```





