package me.zkevin.rocketmq;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 生产者、消费者配置父类，承担抽象公共方法、参数初始化工作
 * 类名称：MQConfig  
 * 类描述：  
 * 创建人：张科伟  
 * 创建时间：2016年12月3日 下午4:32:01
 * @version
 */
public abstract class MQConfig {
	protected static Logger logger=Logger.getLogger(MQConfig.class);
	private String nameAddr;
	private String groupName;
	private String instanceName;
	private String configModel;
	private Boolean enable; 
	private int flag=-1;
	public MQConfig(String topic,String configModel){
		nameAddr=getConfig(topic+MQConstants.PROPERTIE_SPLIT.getKey()+MQConstants.NAME_ADDR.getKey(),getConfig(MQConstants.NAME_ADDR.getKey(),""));
		this.configModel=configModel;
		this.groupName=getConfig(topic+MQConstants.PROPERTIE_SPLIT.getKey()+configModel,null);
		if(null==groupName||groupName.trim().isEmpty()){
			if(configModel.equals(MQConstants.CONFIG_MODEL_CONSUMER.getKey())){
				groupName=MQConstants.DEFAULT_CONSUMER.getKey();
			}else if(configModel.equals(MQConstants.CONFIG_MODEL_PRODUCER.getKey())){
				groupName=MQConstants.DEFAULT_GROUP.getKey();
			}else{
				groupName=MQConstants.getDefaultGroup(configModel);
			}
		}
		String name=getConfig(topic+MQConstants.PROPERTIE_SPLIT.getKey()+configModel+MQConstants.PROPERTIE_SPLIT.getKey()+MQConstants.MQ_INSTANCE.getKey(),"");
		name=null!=name&&!name.isEmpty()?"_"+name:"";
		try {
			this.instanceName=StringUrlTools.encoderStr(topic+"_"+configModel+"_"+groupName+name,null);
		} catch (Exception e) {
			logger.error("初始化"+configModel+" instance name error:"+e.getMessage());
			this.instanceName=topic+"_"+configModel+"_"+groupName+name;
		}
		this.enable=Boolean.valueOf(getConfig(topic+MQConstants.PROPERTIE_SPLIT.getKey()+configModel+MQConstants.PROPERTIE_SPLIT.getKey()+MQConstants.ENABLE.getKey(),
				getConfig(configModel+MQConstants.PROPERTIE_SPLIT.getKey()+MQConstants.ENABLE.getKey(),Boolean.toString(true))));
		String flagString=null;
		flagString=getConfig(topic+MQConstants.PROPERTIE_SPLIT.getKey()+configModel+MQConstants.PROPERTIE_SPLIT.getKey()+MQConstants.FLAG.getKey(),getConfig(configModel+MQConstants.PROPERTIE_SPLIT.getKey()+MQConstants.FLAG.getKey(),null));
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
	protected String getConfig(String key,String defaultValue){
		String configValue=null;
		//先从XXXX.properties中拿值
		configValue=ConfigLoad.getInstance().getConfig(key);
		//如果拿不到值再从java -DXXX=XXX提取配置
		if((null==configValue||configValue.trim().isEmpty())&&null!=System.getProperty(key)){
			configValue=System.getProperty(key).trim();
		}
		//最后从环境变量中取值，由于操作系统环境变量设置.为特殊字符故需要特殊处理
		if(null==configValue||configValue.trim().isEmpty()){
			configValue=System.getenv(key.replace(".","_"));
		}
		return null==configValue||configValue.trim().isEmpty()?defaultValue:configValue;
	}
	private static class ConfigLoad{
		private static ConfigLoad cLoad=new ConfigLoad();
		private Properties properties;
		private ConfigLoad(){
			properties = new Properties();
			try {
				InputStream is=MQConfig.class.getResourceAsStream(MQConstants.PROPERTIE_FILES.getKey());
				if(null!=is)properties.load(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public String getConfig(String key){
			try {
				return null!=properties&&properties.containsKey(key)?properties.getProperty(key,null):null;
			} catch (Exception e) {
				return null;
			}
		}
		public static ConfigLoad getInstance(){
			return cLoad;
		}
    }
	public void printProperties(){
		String info="[MQConfig]nameAddr="+nameAddr+",groupName="+groupName+",instanceName="+instanceName+",configModel="+configModel+",enable="+enable+",flag="+flag;
		logger.info(info);
		System.out.println(info);
		doPrintProperties();
	}
	public abstract void  doPrintProperties();
}
