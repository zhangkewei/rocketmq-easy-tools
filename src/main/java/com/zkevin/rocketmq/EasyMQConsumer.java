package com.zkevin.rocketmq;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
/**
 * 消息消费者
 * 类名称：ChitMQConsumer  
 * 类描述：  
 * 创建人：张科伟  
 * 创建时间：2016年12月3日 下午4:30:35
 * @version
 */
public class EasyMQConsumer extends MQConfig {
	private MessageModel messageModel;//消费模式
	private String topic;//主题
	private String tags;//标签
	private DefaultMQPushConsumer consumer;//真实的消费者
	private AtomicBoolean isStart=new AtomicBoolean(false);//消费者启动状态
	private List<ConsumerListener> listeners=new CopyOnWriteArrayList<ConsumerListener>(); 
	private EasyMQConsumer(String topic,String expressions) {
		super(topic,MQConstants.CONFIG_MODEL_CONSUMER.getKey());
		this.topic=topic;
		this.tags=expressions;
		String model=getConfig(topic+MQConstants.PROPERTIE_SPLIT.getKey()+MQConstants.MESSAGE_MODEL.getKey(),null);
		if(null!=model&&!model.trim().isEmpty()){
			this.messageModel=MessageModel.valueOf(model.trim());
		}
		if(null==this.messageModel){
			this.messageModel=MessageModel.CLUSTERING;
		}
	}
	protected static EasyMQConsumer createConsumer(String topic,String expressions){
		EasyMQConsumer wrapper=new EasyMQConsumer(topic,expressions);
		wrapper.consumer=new DefaultMQPushConsumer(wrapper.getGroupName());
		wrapper.consumer.setInstanceName(wrapper.getInstanceName());
		wrapper.consumer.setNamesrvAddr(wrapper.getNameAddr());
		wrapper.consumer.setMessageModel(wrapper.messageModel);
		wrapper.consumer.setPollNameServerInteval(5*1000);
		wrapper.consumer.setHeartbeatBrokerInterval(5*1000);
		wrapper.consumer.setPersistConsumerOffsetInterval(1000);
		wrapper.consumer.setPullInterval(2*1000);
		wrapper.consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		return wrapper;
	}
	/**
	 * 消息拉取成功消费回调
	 * @Title: ChitMQConsumer.addListener
	 * @param listener
	 * @return
	 * @return ChitMQConsumer
	 */
	public EasyMQConsumer addListener(ConsumerListener listener){
		if(!isStart.get()){
			synchronized (consumer) {
				listeners.add(listener);
			}
		}
		return this;
	}
	/**
	 * 启动消费者
	 * @Title: ChitMQConsumer.start
	 * @return
	 * @return ChitMQConsumer
	 */
	public EasyMQConsumer start(){
		//如果系统参数配置禁止启动则直接返回
		if(!getEnable()){
			logger.info("consumer disable start:"+consumer.toString());
			printProperties();
			return this;
		}
		//修改消费者启动状态
		if(isStart.compareAndSet(false,true)){
			synchronized (consumer) {
				try {
					consumer.subscribe(this.topic,this.tags);
					consumer.registerMessageListener(new MessageListenerConcurrently(){
						public ConsumeConcurrentlyStatus consumeMessage(
						List<MessageExt> messages,ConsumeConcurrentlyContext arg1) {
							try{
								for(MessageExt ext:messages){
									if(getFlag()>-1&&ext.getFlag()!=getFlag()){
										logger.warn("["+ext.getMsgId()+"]["+ext.getFlag()+"!="+getFlag()+"]Base on flag rule,break this msg.");
										break;
									}
									for(ConsumerListener l:listeners){
										if(!l.msgHandle(ext))return ConsumeConcurrentlyStatus.RECONSUME_LATER;
									}
								}
							}catch(Exception e){
								e.printStackTrace();
								return ConsumeConcurrentlyStatus.RECONSUME_LATER;
							}
							return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
						}
					});
					consumer.start();
					logger.info("consumer started:"+consumer.toString());
					printProperties();
				} catch (MQClientException e) {
					isStart.compareAndSet(true,false);
					consumer.shutdown();
				}
			}
		}
		return this;
	}
	@Override
	public void doPrintProperties() {
		String info="[ChitMQConsumer]messageModel="+messageModel+",topic="+topic+",tags="+tags+",isStart="+isStart+","+consumer.toString();
		logger.info(info);
		System.out.println(info);
	}
}
