package edu.ncsu.chord;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by amit on 24/2/17.
 */
public class NetworkUtils {
  /* Tries to find the ethernet or wireless address which is not loopback and prefers IPv4 address over IPv6 */
  public static InetAddress getMyEthernetIP() {
    Inet4Address ipv4 = null;
    Inet6Address ipv6 = null;
    /* Interface name used for Docker containers */
    String interfaceName = "wlan1";
    try {
      /* TODO: Make this generic. I am assuming that all nodes have eth0@if32 interface */
      NetworkInterface iface = NetworkInterface.getByName(interfaceName);
      Enumeration<InetAddress> addrList = iface.getInetAddresses();
      while (addrList.hasMoreElements()) {
        InetAddress address = addrList.nextElement();
        if (address instanceof Inet4Address) {
          ipv4 = (Inet4Address) address;
        } else if (address instanceof Inet6Address) {
          ipv6 = (Inet6Address) address;
        }
      }
    } catch (Exception e) {
      //e.printStackTrace();
    }
    return ipv4 != null ? ipv4 : ipv6;
  }

}
