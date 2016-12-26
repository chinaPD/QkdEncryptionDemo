package model;

import sun.rmi.runtime.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

import static java.net.NetworkInterface.getNetworkInterfaces;

/**
 * Created by hadoop on 16-12-21.
 */
public class NetCardMgr {

    public NetCardMgr() {

    }

    public static List<NetInterfaceInfo> getNetCardInfoList(List<NetInterfaceInfo> netInfos) {
        List<NetInterfaceInfo> netInfoList = new ArrayList<>();
        for (NetInterfaceInfo info : netInfos) {
            netInfoList.add(info);
        }
        Set<Map<String, Object>> localNetSet = getLocalInetMac();
        NetInterfaceInfo info = findNetInfoByIp(netInfos, "127.0.0.1");
        if (info == null) {
            netInfoList.add(new NetInterfaceInfo("127.0.0.1", 6688, "localhost"));
        }

        if (localNetSet == null) return netInfoList;
        for (Map<String, Object> localNet : localNetSet) {
            String arch = (String) localNet.get(NETWORK_NAME);
            String ip = (String) localNet.get(IP);

            NetInterfaceInfo netInfo = findNetInfoByIp(netInfos, ip);
            if (netInfo == null) {
                netInfo = new NetInterfaceInfo(ip, 6688, arch);
            } else {
                if (netInfo.ALIAS == null){
                    netInfo.ALIAS = arch;
                }
            }

            netInfoList.add(netInfo);
        }

        return netInfoList;
    }

    public static NetInterfaceInfo findNetInfoByAlias(List<NetInterfaceInfo> netInfos, String alias) {
        for (NetInterfaceInfo info : netInfos) {
            if (alias.equals(info.ALIAS)) {
                return info;
            }
        }
        return null;
    }

    public static NetInterfaceInfo findNetInfoByIp(List<NetInterfaceInfo> netInfos, String ip) {
        for (NetInterfaceInfo info : netInfos) {
            if (ip.equals(info.IP)) {
                return info;
            }
        }
        return null;
    }

    public static Set<Map<String, Object>> getLocalInetMac() {
        Set<Map<String, Object>> ipMacInfoSet = new HashSet<Map<String, Object>>();

        Map<String, Object> ipMacInfo = null;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                System.out.println(networkInterface.toString());
                Enumeration<InetAddress> inetAddres = networkInterface.getInetAddresses();

                while (inetAddres.hasMoreElements()) {
                    InetAddress inetAddr = inetAddres.nextElement();
                    ipMacInfo = pickInetAddress(inetAddr, networkInterface);
                    if (ipMacInfo != null) {
                        System.out.println(ipMacInfo);
                        ipMacInfoSet.add(ipMacInfo);
//                        return ipMacInfo;
                    }
                }
            }
            return ipMacInfoSet;
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static final String HOST_NAME = "hostname";
    public static final String IP = "ip";
    public static final String IP_NET = "ipnet";
    public static final String OS = "os";
    public static final String MAC = "mac";
    public static final String CPU_ARCH = "cpu-arch";
    public static final String NETWORK_ARCH = "network-arch";
    public static final String NETWORK_NAME = "network-name";

    private static Map<String, Object> pickInetAddress(InetAddress inetAddress, NetworkInterface ni) {
        try {
            String name = ni.getDisplayName();
            if (name.contains("Adapter") || name.contains("Virtual") || name.contains("VMnet") || name.contains("#")) {
                return null;
            }
            if (ni.isVirtual() || !ni.isUp() || !ni.supportsMulticast()) {
                return null;
            }

            if (inetAddress.isSiteLocalAddress()) {
                Formatter formatter = new Formatter();
                String sMAC = null;
                byte[] macBuf = ni.getHardwareAddress();
                for (int i=0; i<macBuf.length; i++) {
                    sMAC = formatter.format(Locale.getDefault(), "%02X%s", macBuf[i],
                            (i < macBuf.length - 1)?"-":"").toString();
                }
                formatter.close();
                System.out.println(sMAC);
                Map<String, Object> ipMacInfo = new HashMap<String, Object>();
                ipMacInfo.put(HOST_NAME, inetAddress.getHostName()); //系统当前hostname
                ipMacInfo.put(IP, inetAddress.getHostAddress()); //ip地址
                ipMacInfo.put(IP_NET, inetAddressTypeName(inetAddress)); //网络类型
                ipMacInfo.put(OS, System.getProperty("os.name")); //系统名称
                ipMacInfo.put(MAC, sMAC); //mac 地址
                ipMacInfo.put(CPU_ARCH, System.getProperty("os.arch")); //cpu架构
                ipMacInfo.put(NETWORK_ARCH, ni.getDisplayName()); //网卡名称
                ipMacInfo.put(NETWORK_NAME, ni.getName());

                return ipMacInfo;
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String inetAddressTypeName(InetAddress inetAddress) {
        return (inetAddress instanceof Inet4Address) ? "ipv4" : "ipv6";
    }
}
