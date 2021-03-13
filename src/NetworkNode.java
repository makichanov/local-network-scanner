import java.net.InetAddress;

public class NetworkNode {

    private InetAddress ip;
    private String mac;

    public NetworkNode(InetAddress ip, String mac) {
        this.ip = ip;
        this.mac = mac;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
