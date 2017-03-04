# rocketmq-easy-tools
rocketmq客户端封装，参数配置化，调用方便。
<br/>
<br/>
<br/>
<b>参数配置:</b><br/>
src/resources/easymq.properties<br/>
参数明细<br/>
mqAddr eg:mqAddr=127.0.0.1:9876<br/>
topic名字.mqAddr<br/>
topic名字.producer.enable true|false|默认true<br/>
topic名字.consumer.enable true|false|默认true<br/>
consumer.enable true|false|默认true<br/>
producer.enable true|false|默认true<br/>

topic名字.producer.flag -1..|默认-1<br/>
topic名字.consumer.flag -1..|默认-1<br/>
consumer.flag -1..|默认-1<br/>
producer.flag -1..|默认-1<br/>
topic名字.(producer|consumer).name 实例名字 默认空<br/>

java -Dtopic名字.consumer=OrderGroupName -Dconsumer.enable=false -Dconsumer.flag=0 -Dproducer.flag=0  mqAddr=127.0.0.1:9876<br/>

windows/linux环境变量:<br/>
export topic名字_consumer=OrderGroupName topic名字2_consumer=CerficationGroupName    consumer_enable=false  consumer_flag=0 producer_flag=0<br/>
<br/>
配置参数优先级easymq.properties>-D>操作系统环境变量
<br/>
<br/>
<br/>
<b>使用例子:</b><br/>
com.zkevin.ToolsDemo<br/>
<pre>
        //初始化生产者
        MQService.getInstance().getProducer("test");
        //MQService.getInstance().sendMsg();
        //初始化并启动消费者
        MQService.getInstance().getConsumer("test","*").addListener(new ConsumerListener() {
            /**
             * 注册消息监听,返回false时消息消费不成功，可重新消费
             * @param msg
             * @return
             */
            @Override
            public boolean msgHandle(MessageExt msg) {
                //do something
                return true;
            }
        }).addListener(new ConsumerListener() {
            @Override
            public boolean msgHandle(MessageExt msg) {
                return false;
            }
        }).start();
 </pre>
