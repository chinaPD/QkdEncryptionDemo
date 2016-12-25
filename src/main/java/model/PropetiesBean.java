package model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hadoop on 16-12-12.
 */
public class PropetiesBean {

    static private final String LocalIpList_Key = "LocalIpList";
    static private final String RemoteIpList_Key = "RemoteIpList";
    static private final String IP_KEY = "IP";
    static private final String PORT_KEY = "PORT";
    static private final String ALIAS_KEY = "ALIAS";

    List<IpInfo> LocalIpList = new ArrayList<>();
    List<IpInfo> RemoteIpList = new ArrayList<>();

    public void getPropetiesFromJson(String jsonString) {
        JSONObject rootJson = JSONObject.parseObject(jsonString);
        JSONArray jsonArray = rootJson.getJSONArray(LocalIpList_Key);
        int size = jsonArray.size();
        JSONObject subJson = null;
        IpInfo ipInfo = null;
        for (int i = 0; i < size; i++) {
            subJson = jsonArray.getJSONObject(i);
            ipInfo = new IpInfo();
            ipInfo.IP = subJson.getString(IP_KEY);
            ipInfo.PORT = subJson.getInteger(PORT_KEY);
            ipInfo.ALIAS = subJson.getString(ALIAS_KEY);
            if (ipInfo.ALIAS == null) {
                ipInfo.ALIAS = "default";
            }
            LocalIpList.add(ipInfo);
        }
        jsonArray = rootJson.getJSONArray(RemoteIpList_Key);
        size = jsonArray.size();
        for (int i = 0; i < size; i++) {
            subJson = jsonArray.getJSONObject(i);
            ipInfo = new IpInfo();
            ipInfo.IP = subJson.getString(IP_KEY);
            ipInfo.PORT = subJson.getInteger(PORT_KEY);
            ipInfo.ALIAS = subJson.getString(ALIAS_KEY);
            if (ipInfo.ALIAS == null) {
                ipInfo.ALIAS = "default";
            }
            RemoteIpList.add(ipInfo);
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(250);
        JSONObject rootJson = new JSONObject();
        Type type = new TypeReference<List<IpInfo>>() {}.getType();
        rootJson.put(LocalIpList_Key, LocalIpList);
//        JSONObject.toJSONString(new PropetiesBean());
        sb.append(rootJson.toJSONString());
        return sb.toString();
    }

    static class IpInfo {
        String IP;
        int PORT;
        String ALIAS;

        public IpInfo() {}

        public IpInfo (String ip, int port, String alias) {
            IP = ip;
            PORT = port;
            ALIAS = alias;
        }

        @Override
        public String toString() {
            return "NetInterfaceInfo{" +
                    "IP:\""+ IP + "\"" +
                    ",Port:" + PORT +
                    ",ALIAS:\"" + ALIAS + "\"" +
                    "}";
        }
    }
}
