package me.zkevin.rocketmq;

import com.chit.mqclient.dic.MQConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * 整个项目参数集中初始化、读取类
 * 类名称：CacheConfig  
 * 类描述：  
 * 创建人：张科伟  
 * 创建时间：2017年3月9日 下午6:04:14
 * @version
 */
public class BaseConfig {
	private ConfigLoad configLoad;
	private String keyAppend;
	protected BaseConfig(String[] propertyFiles,String keyAppend){
		configLoad=new ConfigLoad(propertyFiles);
		this.keyAppend=keyAppend;
	}
	protected String getConfig(String key,String defaultValue){
		return getConfig(keyAppend,key, defaultValue);
	}
	private String getConfig(String keyAppend,String key,String defaultValue){
		String configValue=null;
		//先从XXXX.properties中拿值
		configValue=configLoad.getConfig(key,keyAppend);
		//如果拿不到值再从java -DXXX=XXX提取配置
		if((null==configValue||configValue.trim().isEmpty())&&null!=System.getProperty(key)){
			configValue=System.getProperty(key).trim();
		}
		//最后从环境变量中取值，由于操作系统环境变量设置.为特殊字符故需要特殊处理
		if(null==configValue||configValue.trim().isEmpty()){
			configValue=System.getenv(key.replace(MQConstants.PROPERTIE_SPLIT.getValue(),"_"));
		}
		return null==configValue||configValue.trim().isEmpty()?defaultValue:configValue;
	}
	private static class ConfigLoad{
		private Properties[] properties;
		private ConfigLoad(String[] files){
			properties=new Properties[files.length];
			for(int i=0;i<files.length;i++){
				String file=files[i];
				try {
					InputStream is=BaseConfig.class.getResourceAsStream(file);
					if(null!=is){
						properties[i]=new Properties();
						properties[i].load(is);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		private String getConfig(String key,String keyAppend){
			String value=null;
			try {
				for(Properties p:properties){
					if(null!=p){
						String evnMark=null;
						if(null!=keyAppend&&!keyAppend.trim().isEmpty())evnMark=p.getProperty(keyAppend);
						if(null!=evnMark&&!evnMark.trim().isEmpty()){
							value=p.getProperty(evnMark+"."+key);
						}else{
							value=p.getProperty(key);
						}
						if(null!=value&&!value.trim().isEmpty())break;
					}
				}
			} catch (Exception e) {
			}
			return value;
		}
	}
}
