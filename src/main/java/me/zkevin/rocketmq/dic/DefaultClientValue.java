package me.zkevin.rocketmq.dic;
/**
 * MQ客户端默认配置参数
 * 类名称：DefaultClientValue
 * 类描述：  
 * 创建人：张科伟  
 * 创建时间：2016年12月3日 下午4:14:05
 * @version
 */
public enum DefaultClientValue {
	CONFIG_MODEL_PRODUCER("producer"),//标识为生产者
	CONFIG_MODEL_CONSUMER("consumer"),//标识为消费者
	DEFAULT_PRODUCER("ProducerGroupName"),//生产者默认组名
	DEFAULT_CONSUMER("ConsumerGroupName"),//消费者默认组名
	DEFAULT_GROUP("CommonGroupName");//生产者、消费者缺省组名

	private String key;
	DefaultClientValue(String key){
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
