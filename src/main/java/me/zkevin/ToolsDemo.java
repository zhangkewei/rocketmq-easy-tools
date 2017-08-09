package me.zkevin;

import com.alibaba.rocketmq.common.message.MessageExt;
import me.zkevin.rocketmq.ConsumerListener;
import me.zkevin.rocketmq.MQService;

/**
 * 客户端调用demo
 */
public class ToolsDemo {
    public static  void main(String[] args){
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
                return true;
            }
        }).addListener(new ConsumerListener() {
            @Override
            public boolean msgHandle(MessageExt msg) {
                return false;
            }
        }).start();
    }
}
