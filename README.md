# flinkConnectDemo
演示如何应对广播流来源与其他来源合并场景


## CollectionByFindSource
如果没有广播源的对应数据, 此时从 来源 找一次 , 多次合并数据未匹配需要多次查找

## CollectionByValueState
如果没有广播源的对应数据, 此时从 来源 找一次 , 多次合并数据未匹配只需要一次查找

## 更优化, 但涉及后端演示困难的方案
利用zookeeper的分布式锁和broadcastStream,
(或者其他后端, 只要设计好 合并流和broadcast流可以满足分布式式定位到同一把锁)
在合并流的来源上添加事件, 轮询获取锁状态 ,
等待broadcast流加载完毕后, 再修改zookeeper的数据,
此时才开始加载合并流数据

~~~
添加了 flink-runtime-web 依赖
本地测试可以通过webUI查看具体任务状态
~~~