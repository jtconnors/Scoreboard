/*
 * Copyright (c) 2013, Jim Connors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of this project nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package scoreboard.fx2.framework;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;


public class IPAddrKludge {
    
    static class InetAddrMacAddrMap {
        InetAddress inetAddr;
        byte[] macAddr;
        
        public InetAddrMacAddrMap(InetAddress inetAddr, byte[] macAddr) {
            this.inetAddr = inetAddr;
            this.macAddr = macAddr;
        }
        
        public InetAddress getInetAddr() {
            return inetAddr;
        }
        
        public byte[] getMacAddr() {
            return macAddr;
        }
    }
    
    /*
     * List of known MAC Address patterns used by popular virtualization
     * packages.  This will need to be updated over time.
     */
    public final static byte[] MAC_VMWARE_ESX3 = { 0x00, 0x50, 0x56 };
    public final static byte[] MAC_VMWARE_SERVER = { 0x00, 0x0C, 0x29 };
    public final static byte[] MAC_VMWARE_PLAYER = { 0x00, 0x05, 0x69 };
    public final static byte[] MAC_MICROSOFT_VIRTUAL = { 0x00, 0x03, -1 };
    public final static byte[] MAC_PARALLELS = { 0x00, 0x1C, 0x42 };
    public final static byte[] MAC_VIRTUAL_IRON = { 0x00, 0x0F, 0x4B };
    public final static byte[] MAC_VIRTUALBOX = { 0x08, 0x00, 0x27 };
    public final static byte[] MAC_XENSOURCE = { 0x00, 0x16, 0x3E };
    
    public final static byte[] MAC_REDHAT_XEN = MAC_XENSOURCE;
    public final static byte[] MAC_ORACLE_VM = MAC_XENSOURCE;
    public final static byte[] MAC_NOVELL_XEN = MAC_XENSOURCE;
    
    public final static byte[][] KNOWN_MAC_PATTERNS = {
        MAC_VMWARE_ESX3,
        MAC_VMWARE_SERVER,
        MAC_VMWARE_PLAYER,
        MAC_MICROSOFT_VIRTUAL,
        MAC_PARALLELS,
        MAC_VIRTUAL_IRON,
        MAC_VIRTUALBOX,
        MAC_XENSOURCE
    };
    
    /*
     * Check to see if MAC address belongs to one of the known virtualization
     * MAC address patterns.
     */
    private static boolean isVirtualizationMAC(byte[] macAddr) {
        for (byte[] pat : KNOWN_MAC_PATTERNS) {
           if (macAddr[0] == pat[0] && macAddr[1] == pat[1]
                   && macAddr[2] == pat[2]) {
               return true;
           }    
        }
        return false;    
    }
    
    
    /*
     * The method below has been created because there is no real way in Java
     * to differentiate between a hosts's "real" ip address and one of it's
     * "virtual" ip address.  Multiple virtual network interfaces are created,
     * for example, when virtualization products like VMware or VirtualBox
     * are used.
     * 
     * The kludge here is to get all of the configured network intefaces
     * and compare them against a known set of Hardware MAC addresses that
     * each of the virtualization packages use.  We select the interface
     * that doesn't match any of the known virtualization MAC address patterns.
     */
    public static InetAddress getLocalAddress() {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
            return null;
        }
        /*
         * Loop through network interfaces, and get list of potential
         * candidates.
         */
        ArrayList<InetAddrMacAddrMap> candidates =
                new ArrayList<InetAddrMacAddrMap>();
        InetAddress loopbackAddress = null;
        for (NetworkInterface nic : Collections.list(interfaces)) {
            Enumeration<InetAddress> addresses = nic.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (!address.isLoopbackAddress()) {
                    try {                       
                        byte[] macAddr = nic.getHardwareAddress();
                        candidates.add(new InetAddrMacAddrMap(
                                address, macAddr));
System.out.println("   candidate: " + address.toString() + " MAC: " + MacAddrToString(macAddr));
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                } else if (address instanceof Inet4Address) {
                    loopbackAddress = address;
                }
            }
        }
        /*
         * If there are no candidates, just choose the loopback. If there
         * is one candidate interface, then return that address.  There's
         * no ambiguity in this case.
         */
        if (candidates.isEmpty()) {
            return loopbackAddress;
        } else if (candidates.size() == 1) {
            return candidates.get(0).getInetAddr();
        }
        /*
         * In the case where there are multiple candidates, select the first
         * interface which doesn't have a MAC address matching the known
         * patterns of the popular virtualization packages.
         */
        for (InetAddrMacAddrMap candidate : candidates) {
            if (!isVirtualizationMAC(candidate.getMacAddr())) {
                return candidate.getInetAddr();
            }
        }
        return loopbackAddress;
    }
    
    public static String MacAddrToString(byte[] macAddr) {
        return macAddr[0] + ":" + macAddr[1] + ":" + macAddr[2] +":" +
                macAddr[3] + ":" + macAddr[4] + ":" + macAddr[5];
    }
}
