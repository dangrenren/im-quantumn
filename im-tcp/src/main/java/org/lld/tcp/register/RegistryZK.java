package org.lld.tcp.register;


import io.netty.bootstrap.BootstrapConfig;
import org.lld.common.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
public class RegistryZK implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(RegistryZK.class);

    private ZKit zKit;

    private String ip;
    private int tcpPort;


    public RegistryZK(ZKit zKit, String ip, int port) {
        this.zKit = zKit;
        this.ip = ip;
        this.tcpPort = port;
    }

    @Override
    public void run() {
        zKit.createRootNode();
        String tcpPath = Constants.ImCoreZkRoot + Constants.ImCoreZkRootTcp + "/" + ip + ":" + tcpPort;
        zKit.createNode(tcpPath);
        logger.info("Registry zookeeper tcpPath success, msg=[{}]", tcpPath);
        String webPath =
                Constants.ImCoreZkRoot + Constants.ImCoreZkRootWeb + "/" + ip + ":" + tcpPort;
        zKit.createNode(webPath);
        logger.info("Registry zookeeper webPath success, msg=[{}]", webPath);

    }
}
