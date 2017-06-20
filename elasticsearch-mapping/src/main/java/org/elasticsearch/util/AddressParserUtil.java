package org.elasticsearch.util;

import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressParserUtil {

    private static final Logger LOGGER = Logger.getLogger(AddressParserUtil.class.toString());

    public static List<InetSocketTransportAddress> parseHostCsvList(String hostCsvList) {
        List<InetSocketTransportAddress> adresses = new ArrayList<InetSocketTransportAddress>();
        if (hostCsvList == null) {
            return adresses;
        }
        String[] hostArr = hostCsvList.split(",");
        if (hostArr.length > 0) {
            Pattern pattern = Pattern.compile("(\\S+):(\\d+)");
            for (String hostArrEl : hostArr) {
                Matcher matcher = pattern.matcher(hostArrEl.trim());
                if (matcher.matches()) {
                    String host = matcher.group(1);
                    Integer port = Integer.valueOf(matcher.group(2));
                    InetSocketTransportAddress address = null;
                    try {
                        address = new InetSocketTransportAddress(InetAddress.getByName(host), port);
                    } catch (UnknownHostException e) {
                        LOGGER.log(Level.WARNING, "Unknown host: <" + host + ">");
                    }
                    adresses.add(address);
                } else {
                    LOGGER.log(Level.WARNING, "Host address not recognized : <" + hostArrEl + ">");
                }
            }
        }
        return adresses;
    }

}
