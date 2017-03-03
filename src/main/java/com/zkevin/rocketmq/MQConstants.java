package com.zkevin.rocketmq;
/**
 * 消息队列常亮配置表
 * 类名称：MQConstants  
 * 类描述：  
 * 创建人：张科伟  
 * 创建时间：2016年12月3日 下午4:14:05
 * @version
 */
public enum MQConstants {
	CONFIG_MODEL_PRODUCER("producer"),//标识为生产者
	CONFIG_MODEL_CONSUMER("consumer"),//标识为消费者
	PROPERTIE_SPLIT("."),
	PROPERTIE_FILES("/easymq.properties"),//参数配置文件
	DEFAULT_PRODUCER("ProducerGroupName"),//生产者默认组名
	DEFAULT_CONSUMER("ConsumerGroupName"),//消费者默认组名
	DEFAULT_GROUP("CommonGroupName"),//生产者、消费者缺省组名
	MESSAGE_MODEL("msgModel"),//消息消费模式
	NAME_ADDR("mqAddr"),//消息队列地址
	ENABLE("enable"),//消费者、生产者是否起效，即是否启动并首发消息
	FLAG("flag"),//发送，接收消息添加的标识，用于实现简单消息过滤
	MQ_INSTANCE("name");//消息消费者、提供者实例名
	private String key;
	MQConstants(String key){
		this.key=key;
	}
	public String getKey() {
		return key;
	}
	public static String getDefaultGroup(String configModel){
		if(configModel.equals(CONFIG_MODEL_CONSUMER.getKey())){
			return DEFAULT_CONSUMER.getKey();
		}else  if(configModel.equals(CONFIG_MODEL_PRODUCER.getKey())){
			return DEFAULT_PRODUCER.getKey();
		}else {
			return DEFAULT_GROUP.getKey();
		}
	}
}
