package net.debreczeni.remoteserver.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;

import static java.util.Collections.list;

public final class InetAddress {
    private static java.net.InetAddress inetAddress;

    static {
        try {
            inetAddress = java.net.InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static String getLocalAddress(){
        try {
            for (NetworkInterface neti : list(NetworkInterface.getNetworkInterfaces())) {
                if(neti.isLoopback() || !neti.isUp()){
                    continue;
                }
                for (java.net.InetAddress address : list(neti.getInetAddresses())) {
                    if (address instanceof Inet4Address) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getHostName(){
        return inetAddress.getHostName();
    }

    public static String getPublicAddress() {
        try (BufferedReader sc =
                     new BufferedReader(
                             new InputStreamReader(
                                     new URL("http://bot.whatismyipaddress.com").openStream()
                             )
                     )
        ) {
            return sc.readLine().trim();
        } catch (Exception e) {
            return "Un known";
        }
    }
}
