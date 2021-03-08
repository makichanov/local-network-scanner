
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws UnknownHostException, SocketException {

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        NetworkInterface i = interfaces.nextElement();
        System.out.println("Interface: " + i.getDisplayName());
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
                    System.out.println(toPing.getHostAddress() + " ");

                    Process arp = Runtime.getRuntime().exec("arp -a " + toPing.getHostName());

                    Scanner sc = new Scanner(arp.getInputStream());

                    //TODO fix mac of this computer
                    System.out.println(sc.findInLine("(\\w{2}:){5}\\w{2}"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }



            count--;
        }

/*            byte[] mac = i.getHardwareAddress();
            // null if localhost
            if (mac != null) {
                for (byte b : mac) {
                    System.out.print(b);
                }
            }*/
        System.out.println();

    }
}



