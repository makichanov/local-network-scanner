
import java.io.IOException;
import java.net.*;
import java.util.*;

public class Main {

    static void launchScan() {
        Enumeration<NetworkInterface> interfaces = getInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface currentInterface = interfaces.nextElement();
            if (currentInterface.getDisplayName().equals("lo"))
                continue;

            System.out.println("Interface: " + currentInterface.getDisplayName());
            LocalNetworkScanner scanner = new LocalNetworkScanner(currentInterface);
            LinkedList<NetworkNode> localNodes = scanner.getAvailableNodes();

            for (NetworkNode node : localNodes) {
                System.out.println("IP: " + node.getIp() + " MAC: " + node.getMac());
            }

            System.out.println("-----------------------------------------------------------------");
        }
    }

    static Enumeration<NetworkInterface> getInterfaces() {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            System.err.println("Error while getting network interfaces " + e);
            e.printStackTrace();
        }
        return interfaces;
    }

    static byte[] getThisMac(NetworkInterface i) {
        byte[] mac = null;
        try {
            mac = i.getHardwareAddress();
        } catch (SocketException e) {
            System.err.println("Error while getting hardware address of " + i.getDisplayName() + e);
            e.printStackTrace();
        }
        return mac;
    }

    static InetAddress getInet(byte[] network) {
        InetAddress toPing = null;
        try {
            toPing = InetAddress.getByAddress(network);
        } catch (IOException e) {
            System.err.println("Ошибка потока при подключении к " + Arrays.toString(network) + e);
        }
        return toPing;
    }

    static void ping(InetAddress toPing, InetAddress currentPC, byte[] thisMac) {
        try {
            if (toPing.isReachable(250)) {
                System.out.println("IP address: " + toPing.getHostAddress());

                if (!toPing.equals(currentPC)) {
                    Process arp = Runtime.getRuntime().exec("arp -a " + toPing.getHostName());

                    Scanner sc = new Scanner(arp.getInputStream());

                    System.out.println("MAC address: " + sc.findInLine("(\\w{2}:){5}\\w{2}"));
                } else {
                    System.out.print("MAC address: ");
                    for (byte b : thisMac)
                        System.out.print(Integer.toHexString(Byte.toUnsignedInt(b)) + ":");
                    System.out.println();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        launchScan();

        /*Enumeration<NetworkInterface> interfaces = getInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface i = interfaces.nextElement();
            if (i.getDisplayName().equals("lo"))
                continue;
            System.out.println("Interface: " + i.getDisplayName());
            byte[] thisMac = getThisMac(i);
            List<InterfaceAddress> interfaceAddress = i.getInterfaceAddresses();
            short mask = interfaceAddress.get(1).getNetworkPrefixLength();

            InetAddress addr = interfaceAddress.get(1).getAddress();

            byte[] network = addr.getAddress();

            int count = (int) Math.pow(2, 32 - mask) - 1;

            byte last = 1;

            while (count != 0) {

                network[3] = last++;

                InetAddress toPing = getInet(network);

                ping(toPing, addr, thisMac);

                count--;
            }
            System.out.println("-------------------------------------------------------------------------------------");
        }*/

    }
}



