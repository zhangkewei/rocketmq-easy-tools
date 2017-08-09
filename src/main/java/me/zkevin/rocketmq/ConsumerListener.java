package me.zkevin.rocketmq;

import com.alibaba.rocketmq.common.message.MessageExt;
/**
 * 消息监听抽象类
 * 类名称：ConsumerListener  
 * 类描述：  
 * 创建人：张科伟  
 * 创建时间：2016年12月3日 下午4:31:31
 * @version
 */
public abstract class ConsumerListener {
	/**
	 * 消费者监听接到消息后会调用自定义ConsumerListener链，如果上一个自定义监听消费失败就会终止消费该消息并稍后重新消费
	 * @Title: ConsumerListener.msgHandle
	 * @param msg
	 * @return
	 * @return boolean
	 */
	public abstract boolean msgHandle(MessageExt msg);
}
