import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.StringJoiner;

public class LocalNetworkScanner {

    private NetworkInterface networkInterface;

    public LocalNetworkScanner(NetworkInterface i) {
        this.networkInterface = i;
    }

    public LinkedList<NetworkNode> getAvailableNodes() {
        LinkedList<NetworkNode> available = new LinkedList<>();
        InterfaceAddress interfaceAddress = getInterfaceAddress();
        InetAddress thisPCip = interfaceAddress.getAddress();
        short mask = interfaceAddress.getNetworkPrefixLength();
        byte[] network = thisPCip.getAddress();
        int count = (int) Math.pow(2, 32 - mask) - 1;
        int totalCount = count;
        byte last = 1;
        while (count != 0) {
            network[3] = last++;
            InetAddress toPing = getInet(network);
            if (isPingable(toPing)) {
                NetworkNode node = (!toPing.equals(thisPCip))
                        ? new NetworkNode(toPing, getMac(toPing))
                        : new NetworkNode(toPing, getThisPCmac());
                available.add(node);
            }
            count--;
            if (count % 10 == 0)
                displayProgress(totalCount, count);
        }
        return available;
    }

    static boolean isPingable(InetAddress toPing) {
        boolean reachable = false;
        try {
            reachable = toPing.isReachable(250);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reachable;
    }

    private InterfaceAddress getInterfaceAddress() {
        InterfaceAddress interfaceAddress = null;

        for (InterfaceAddress addr : networkInterface.getInterfaceAddresses()) {
            if (addr.getAddress().getAddress().length == 4) {
                interfaceAddress = addr;
                break;
            }
        }

        return interfaceAddress;
    }

    private InetAddress getInet(byte[] network) {
        InetAddress toPing = null;
        try {
            toPing = InetAddress.getByAddress(network);
        } catch (IOException e) {
            System.err.println(Arrays.toString(network) + e);
        }
        return toPing;
    }

    private String getMac(InetAddress ip) {
        Process arp = null;
        try {
            arp = Runtime.getRuntime().exec("arp -a " + ip.getHostName());
        } catch (IOException e) {
            System.err.println("Error while executing ARP request " + e);
        }
        Scanner sc = new Scanner(arp.getInputStream());
        return sc.findInLine("(\\w{2}:){5}\\w{2}");
    }

    private String getThisPCmac() {
        byte[] mac = null;
        try {
            mac = networkInterface.getHardwareAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        StringJoiner macString = new StringJoiner(":");
        for (byte b : mac) {
            macString.add(Integer.toHexString(Byte.toUnsignedInt(b)));
        }
        return macString.toString();
    }

    private void displayProgress(double totalCount, double currentCount) {
        System.out.println(String.format("%.2f", (1 - currentCount / totalCount) * 100 ) + "%");
    }

}
