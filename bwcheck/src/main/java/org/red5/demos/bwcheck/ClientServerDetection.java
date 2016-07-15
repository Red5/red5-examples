package org.red5.demos.bwcheck;

import java.util.HashMap;
import java.util.Map;

import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.stream.IStreamCapableConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dan Rossi
 */
public class ClientServerDetection implements IPendingServiceCallback {

	protected static Logger log = LoggerFactory
			.getLogger(ClientServerDetection.class);

	public ClientServerDetection() {

	}

	/**
	 * Handle callback from service call.
	 */

	public void resultReceived(IPendingServiceCall call) {

	}

	private IStreamCapableConnection getStats() {
		IConnection conn = Red5.getConnectionLocal();
		if (conn instanceof IStreamCapableConnection) {
			return (IStreamCapableConnection) conn;
		}
		return null;
	}

	public Map<String, Object> onClientBWCheck(Object[] params) {
		final IStreamCapableConnection stats = this.getStats();

		Map<String, Object> statsValues = new HashMap<String, Object>();
		Integer time = (Integer) (params.length > 0 ? params[0] : 0);
		statsValues.put("cOutBytes", stats.getReadBytes());
		statsValues.put("cInBytes", stats.getWrittenBytes());
		statsValues.put("time", time);
		
		log.info("cOutBytes: {} cInBytes: {} time: {}", new Object[]{stats.getReadBytes(), stats.getWrittenBytes(), time});

		return statsValues;

	}
}
