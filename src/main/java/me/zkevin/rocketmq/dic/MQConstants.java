package me.zkevin.rocketmq.dic;

/**
 * 客户端常量
 */
public enum  MQConstants {
    DEFAULT_PROPERTIE_FILES("readFromFiles","/mqclient.properties","要读取的配置文件"),
    PROPERTIE_FILES_SPLIT("readFromFilesSplit",",","要读取的配置文件分割自负"),
    PROPERTIE_SPLIT("keySplit",".","配置文件key组织字符"),
    PROPERTIEKEY_APPEND("runEnv","evn_mark","环境"),
    CLIENT_MESSAGE_MODEL("msgModel","msgModel","消息消费模式"),
    CLIENT_NAME_ADDR("mqAddr","mqAddr","消息队列地址"),
    CLIENT_ENABLE("enable","enable","消费者、生产者是否起效，即是否启动并首发消息"),
    CLIENT_FLAG("flag","flag","发送，接收消息添加的标识，用于实现简单消息过滤"),
    CLIENT_INSTANCE("name","name","消息消费者、提供者实例名");
    private String key;
    private String value;
    private String desc;
    private MQConstants(String key,String value,String desc){
        this.desc=desc;
        this.key=key;
        this.value=value;
    }

    public String getDesc() {
        return desc;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
