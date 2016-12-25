package model;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Created by hadoop on 16-12-12.
 */
public class PropertyBeanWrapper {

    private File propertyFile;
    public PropertyBean propertyBean;

    public PropertyBeanWrapper() {
        propertyFile = new File(getClass().getResource(".").getFile(),
                "../properties/propertis.json");
        if (!propertyFile.exists()) {
            try {
                propertyFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        propertyFile.setWritable(true);
        StringBuilder sb = new StringBuilder();
        if (!propertyFile.exists()) {
            try {
                propertyFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sb.append("{}");
        } else {
            try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(propertyFile))) {
                byte[] bytes = new byte[500];
                while (bin.read(bytes) != -1) {
                    sb.append(new String(bytes, StandardCharsets.UTF_8));
                }
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }

        }

        propertyBean = JSONObject.parseObject(sb.toString(), PropertyBean.class, Feature.SupportArrayToBean);
//        JSONObject rootJson = JSONObject.parseObject(sb.toString());
//        PropertyBean bean = new PropertyBean();
//        bean.LocalIpList.add(new PropertyBean.NetInterfaceInfo("127.0.0.1", 6688, "localhost"));
//        bean.RemoteIpList.add(new PropertyBean.NetInterfaceInfo("127.0.0.1", 6688, "localhost"));
//        String temp = JSONObject.toJSONString(bean, SerializerFeature.PrettyFormat);
//        PropetiesBean bean = new PropetiesBean();
//        bean.LocalIpList.add(new PropetiesBean.NetInterfaceInfo("127.0.0.1", 6688, "localhost"));
//        bean.getPropetiesFromJson(sb.toString());
        System.out.println(JSONObject.toJSONString(propertyBean));
    }

    public void savePropertyBeanToJson() {
        propertyFile.setWritable(true);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(propertyFile));
            bos.write(JSONObject.toJSONBytes(propertyBean));
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
