package model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hadoop on 16-12-12.
 */
public class PropertyBean {

    @JSONField(name = "LocalIpList")
    public List<NetInterfaceInfo> LocalIpList = new ArrayList<>();
    @JSONField(name = "RemoteIpList")
    public List<NetInterfaceInfo> RemoteIpList = new ArrayList<>();
    @JSONField(name = "ImagePath")
    public String ImagePath;
    @JSONField(name = "QkdPath")
    public String QkdPath;
    @JSONField(name = "RouterIp")
    public String RouterIp;
    @JSONField(name = "RouterPort")
    public int RouterPort;

  /*  @JSONType(serialzeFeatures = SerializerFeature.BeanToArray, parseFeatures = Feature.SupportArrayToBean)
    static class NetInterfaceInfo {
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
    }*/
}

