
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SocketException {

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface i = interfaces.nextElement();
            if (i.getDisplayName().equals("lo"))
                continue;
            System.out.println("Interface: " + i.getDisplayName());
            byte[] thisMac = i.getHardwareAddress();
            List<InterfaceAddress> interfaceAddress = i.getInterfaceAddresses();
            short mask = interfaceAddress.get(1).getNetworkPrefixLength();

            InetAddress addr = interfaceAddress.get(1).getAddress();

            byte[] network = addr.getAddress();

            int count = (int) Math.pow(2, 32 - mask) - 1;

            byte last = 1;

            while (count != 0) {

                network[3] = last++;

                InetAddress toPing = null;
                try {
                    toPing = InetAddress.getByAddress(network);
                } catch (IOException e) {
                    System.err.println("Ошибка потока при подключении к " + Arrays.toString(network) + e);
                }

                try {
                    if (toPing.isReachable(500)) {
                        System.out.println("IP address: " + toPing.getHostAddress());

                        if (!toPing.equals(addr)) {
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

                count--;
            }
            System.out.println("-------------------------------------------------------------------------------------");
        }

    }
}



