package model;

import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Created by hadoop on 16-12-22.
 */
@JSONType(serialzeFeatures = SerializerFeature.BeanToArray, parseFeatures = Feature.SupportArrayToBean)
public class NetInterfaceInfo {
    public String IP;
    public int PORT;
    public String ALIAS;

    public NetInterfaceInfo() {
    }

    public NetInterfaceInfo(String ip, int port, String alias) {
        IP = ip;
        PORT = port;
        ALIAS = alias;
    }
}
