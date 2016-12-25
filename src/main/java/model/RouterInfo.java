package model;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by hadoop on 16-12-25.
 */
public class RouterInfo {

    @JSONField(name = "FromFlag")
    public int FromFlag = 0;  // 0 or 1
    @JSONField(name = "UserNums")
    public int UserNums = 1;
    @JSONField(name = "SourceIP")
    public String SourceIP = "127.0.0.1";
    @JSONField(name = "QuantumDesIP")
    public String QuantumDesIP = "127.0.0.1";
    @JSONField(name = "ClassicDesIP")
    public String ClassicDesIP = "127.0.0.1";
    @JSONField(name = "SynOptical")
    public double SynOptical = 1529.55;
    @JSONField(name = "QuantumOptical")
    public double QuantumOptical = 1553.33;
    @JSONField(name = "ClassicOptical")
    public double ClassicOptical = 1550;

    @Override
    public String toString() {
        String jsonStr = JSONObject.toJSONString(this);
        System.out.println("RouterInfo:\n"
                + "      " + jsonStr);
        return jsonStr;
    }
}
