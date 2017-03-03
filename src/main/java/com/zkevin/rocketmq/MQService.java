package com.zkevin.rocketmq;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
/**
 * 	 消息队列生产者、消费者门面方法  线程安全<br/>
 *   order.producer=生产组名称|ProducerGroupName|CommonGroupName<br/>
 *   order.consumer=消费组名称|ConsumerGroupName|CommonGroupName<br/>
 *   order.msgModel=BROADCASTING||CLUSTERING<br/>
 *   order.mqAddr<br/>
 *   cerfication.producer=生产组名称|ProducerGroupName|CommonGroupName<br/>
 *   cerfication.consumer=消费组名称|ConsumerGroupName|CommonGroupName<br/>
 *   cerfication.msgModel=BROADCASTING||CLUSTERING|默认CLUSTERING<br/>
 *   cerfication.mqAddr  <br/>
 *   mqAddr<br/>
 *   order.producer.enable true|false|默认true<br/>
 *   order.consumer.enable true|false|默认true<br/>
 *   consumer.enable true|false|默认true<br/>
 *   producer.enable true|false|默认true<br/>
 *   order.producer.flag -1..|默认-1<br/>
 *   order.consumer.flag -1..|默认-1<br/>
 *   consumer.flag -1..|默认-1<br/>
 *   producer.flag -1..|默认-1<br/>
 *   topic名字.(producer|consumer).name 实例名字 默认空<br/>
 *   java -Dorder.consumer=OrderGroupName  -Dcerfication.consumer=CerficationGroupName -Dconsumer.enable=false -Dconsumer.flag=0 -Dproducer.flag=0 <br/>
 *   export order_consumer=OrderGroupName cerfication_consumer=CerficationGroupName    consumer_enable=false  consumer_flag=0 producer_flag=0<br/>
 * 类名称：MQService  <br/>
 * 类描述：  <br/>
 * 创建人：张科伟  <br/>
 * 创建时间：2016年12月2日 下午1:17:04<br/>
 * @version
 */
public class MQService {
	private static transient Logger logger=Logger.getLogger(MQService.class);
	//生产者对象池
	private Map<String,EasyMQProducer> RODUCER_MAP=new ConcurrentHashMap<String, EasyMQProducer>();
	//消费者对象池
	private Map<String,EasyMQConsumer> CONSUMER_MAP=new ConcurrentHashMap<String, EasyMQConsumer>();
	/**
	 * 发送消息
	 * @Title: MQService.sendMsg
	 * @param topic
	 * @param tag
	 * @param key
	 * @param bodyMap
	 * @return
	 * @return SendResult
	 */
	@SuppressWarnings("rawtypes")
	public SendResult sendMsg(String topic,String tag,String key,Map bodyMap){
		String msg2Json=JSONObject.toJSONString(bodyMap);
		SendResult result=null;
		try{
	        String encoder=StringUrlTools.encoderStr(msg2Json, null);
	        result=sendMsg(new Message(topic,tag, null!=key?key.trim():"",encoder.getBytes()));
		}catch(Exception e){
			e.printStackTrace();
			logger.error("encoding String error:"+msg2Json,e);
		}
		return result;
	}
	/**
	 * 发送消息
	 * @Title: MQService.sendMsg
	 * @param msg
	 * @return
	 * @return SendResult
	 */
	public SendResult sendMsg(Message msg){
		SendResult result=null;
		try{
			logger.info("try decode String:"+StringUrlTools.decoder2str(new String(msg.getBody()),null));
			EasyMQProducer producer=getProducer(msg.getTopic());
			result=producer.send(msg);
		}catch(Exception e){
			e.printStackTrace();
		}
		logger.info("message send result:"+(null!=result?JSONObject.toJSONString(result):"null"));
		return result;
	}
	/**
	 * 第一次获取producer会做初始化动作
	 * @Title: MQService.getProducer
	 * @param topic
	 * @return
	 * @return ChitMQProducer
	 */
	public EasyMQProducer getProducer(String topic){
		if(RODUCER_MAP.containsKey(topic)){
			return RODUCER_MAP.get(topic);
		}else{
			synchronized (RODUCER_MAP) {
				EasyMQProducer producer=EasyMQProducer.createProducer(topic);
				RODUCER_MAP.put(topic,producer);
				return producer;
			}
		}
	}
	/**
	 * 获取原生真实消息生产者
	 * @Title: MQService.getNativeProducer
	 * @param topic
	 * @return
	 * @return DefaultMQProducer
	 */
	public DefaultMQProducer getNativeProducer(String topic){
		EasyMQProducer producer=getProducer(topic);
		return producer.producer;
	}
	/**
	 * 获取消费者
	 * @Title: MQService.getConsumer
	 * @param topic
	 * @param expressions
	 * @return
	 * @return ChitMQConsumer
	 */
	public EasyMQConsumer getConsumer(String topic,String expressions){
		String consumerKey=topic+"_"+expressions;
		if(CONSUMER_MAP.containsKey(consumerKey)){
			return CONSUMER_MAP.get(consumerKey);
		}else{
			synchronized(CONSUMER_MAP) {
				EasyMQConsumer consumer=EasyMQConsumer.createConsumer(topic, expressions);
				CONSUMER_MAP.put(consumerKey,consumer);
				return consumer;
			}
		}
	}
	/**
	 * 获取MQService单例类
	 * @Title: MQService.getInstance
	 * @return
	 * @return MQService
	 */
	public static MQService getInstance(){
		return MQServiceHelper.MQ_SERVICE;
	}
	private MQService(){
		
	}
	private static class MQServiceHelper{
		public static MQService MQ_SERVICE=new MQService();
	}
}
