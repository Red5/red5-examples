package org.red5.demos.bwcheck;

import java.util.Map;

import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dan Rossi
 */
public class BandwidthDetection {

	protected static Logger log = LoggerFactory
			.getLogger(BandwidthDetection.class);

	public BandwidthDetection() {

	}

	public Map<String, Object> onClientBWCheck(Object[] params) {
		ClientServerDetection clientServer = new ClientServerDetection();
		return clientServer.onClientBWCheck(params);
	}

	public void onServerClientBWCheck(Object[] params) {
		IConnection conn = Red5.getConnectionLocal();
		ServerClientDetection serverClient = new ServerClientDetection();
		serverClient.checkBandwidth(conn);
	}

}
