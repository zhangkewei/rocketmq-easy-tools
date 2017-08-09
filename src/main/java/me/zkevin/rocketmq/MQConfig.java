package me.zkevin.rocketmq;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import me.zkevin.rocketmq.dic.DefaultClientValue;
import me.zkevin.rocketmq.dic.MQConstants;
import org.apache.log4j.Logger;

/**
 * 生产者、消费者配置父类，承担抽象公共方法、参数初始化工作
 * 类名称：MQConfig  
 * 类描述：  
 * 创建人：张科伟  
 * 创建时间：2016年12月3日 下午4:32:01
 * @version
 */
public abstract class MQConfig  extends BaseConfig{
	protected static Logger logger=Logger.getLogger(MQConfig.class);
	private String nameAddr;
	private String groupName;
	private String instanceName;
	private String configModel;
	private Boolean enable;
	private int flag=-1;
	public MQConfig(String topic,String configModel){
		super(MQConstants.PROPERTIE_FILES_SPLIT.getValue().split(MQConstants.DEFAULT_PROPERTIE_FILES.getValue()), MQConstants.PROPERTIEKEY_APPEND.getValue());
		nameAddr=getConfig(topic+MQConstants.PROPERTIE_SPLIT.getValue()+MQConstants.CLIENT_NAME_ADDR.getValue(),getConfig(MQConstants.CLIENT_NAME_ADDR.getValue(),""));
		this.configModel=configModel;
		this.groupName=getConfig(topic+MQConstants.PROPERTIE_SPLIT.getValue()+configModel,null);
		if(null==groupName||groupName.trim().isEmpty()){
			if(configModel.equals(DefaultClientValue.CONFIG_MODEL_CONSUMER.getKey())){
				groupName=DefaultClientValue.DEFAULT_CONSUMER.getKey();
			}else if(configModel.equals(DefaultClientValue.CONFIG_MODEL_PRODUCER.getKey())){
				groupName=DefaultClientValue.DEFAULT_GROUP.getKey();
			}else{
				groupName=DefaultClientValue.getDefaultGroup(configModel);
			}
		}
		String name=getConfig(topic+MQConstants.PROPERTIE_SPLIT.getValue()+configModel+MQConstants.PROPERTIE_SPLIT.getValue()+MQConstants.CLIENT_INSTANCE.getValue(),"");
		name=null!=name&&!name.isEmpty()?"_"+name:"";
		try {
			this.instanceName=StringUrlTools.encoderStr(topic+"_"+configModel+"_"+groupName+name,null);
		} catch (Exception e) {
			logger.error("初始化"+configModel+" instance name error:"+e.getMessage());
			this.instanceName=topic+"_"+configModel+"_"+groupName+name;
		}
		this.enable=Boolean.valueOf(getConfig(topic+MQConstants.PROPERTIE_SPLIT.getValue()+configModel+MQConstants.PROPERTIE_SPLIT.getValue()+MQConstants.CLIENT_ENABLE.getValue(),
				getConfig(configModel+MQConstants.PROPERTIE_SPLIT.getValue()+MQConstants.CLIENT_ENABLE.getValue(),Boolean.toString(true))));
		String flagString=null;
		flagString=getConfig(topic+MQConstants.PROPERTIE_SPLIT.getValue()+configModel+MQConstants.PROPERTIE_SPLIT.getValue()+MQConstants.CLIENT_FLAG.getValue(),getConfig(configModel+MQConstants.PROPERTIE_SPLIT.getValue()+MQConstants.CLIENT_FLAG.getValue(),null));
		if(null!=flagString&&!flagString.trim().isEmpty()){
			try{
				flag=Double.valueOf(flagString).intValue();
			}catch(Exception e){
				flag=-1;
			}
		}
	}
	public String getNameAddr() {
		return nameAddr;
	}
	public String getGroupName() {
		return groupName;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public String getConfigModel() {
		return configModel;
	}
	public Boolean getEnable() {
		return enable;
	}
	public int getFlag() {
		return flag;
	}
	public void printProperties(){
		String info="[MQConfig]nameAddr="+nameAddr+",groupName="+groupName+",instanceName="+instanceName+",configModel="+configModel+",enable="+enable+",flag="+flag;
		logger.info(info);
		System.out.println(info);
		doPrintProperties();
	}
	public abstract void  doPrintProperties();
}
