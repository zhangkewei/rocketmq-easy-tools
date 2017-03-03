package com.zkevin.rocketmq;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
/**
 * 消息生产者
 * 类名称：ChitMQProducer  
 * 类描述：  
 * 创建人：张科伟  
 * 创建时间：2016年12月3日 下午4:38:02
 * @version
 */
public  class	EasyMQProducer extends MQConfig {
	protected DefaultMQProducer producer;
	private EasyMQProducer(String topic){
		super(topic,MQConstants.CONFIG_MODEL_PRODUCER.getKey());
	}
	protected static EasyMQProducer createProducer(String topic){
		final EasyMQProducer chitProducer=new EasyMQProducer(topic);
		chitProducer.producer=new DefaultMQProducer(chitProducer.getGroupName());
		chitProducer.producer.setInstanceName(chitProducer.getInstanceName());
		chitProducer.producer.setNamesrvAddr(chitProducer.getNameAddr());
		chitProducer.producer.setHeartbeatBrokerInterval(5*1000);
		chitProducer.producer.setPollNameServerInteval(5*1000);
		chitProducer.producer.setRetryAnotherBrokerWhenNotStoreOK(true);
		chitProducer.producer.setRetryTimesWhenSendFailed(5);
		try {
			//如果系统参数配置禁止启动则直接返回
			if(!chitProducer.getEnable()){
				logger.info("producer  disable start:"+chitProducer.producer.toString());
				chitProducer.printProperties();
				return chitProducer;
			}
			//启动生产者
			chitProducer.producer.start();
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	            public void run() {
	               chitProducer.producer.shutdown();
	            }
	        }));
			logger.info("producer start successed:"+chitProducer.producer.toString());
			chitProducer.printProperties();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
		return chitProducer;
	}
	protected SendResult send(Message msg) throws Exception {
		SendResult sendResult=null;
		if(!getEnable()) throw new Exception("consumer not start:"+this.toString());
		try {
			//消息标志，系统不做干预，完全由应用决定如何使用
			msg.setFlag(getFlag());
			sendResult = producer.send(msg);
		} catch (MQClientException e) {
			e.printStackTrace();
		} catch (RemotingException e) {
			e.printStackTrace();
		} catch (MQBrokerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sendResult;
	}
	@Override
	public void doPrintProperties() {
		String info="[ChitMQProducer]"+producer.toString();
		logger.info(info);
		System.out.println(info);
	}
}
