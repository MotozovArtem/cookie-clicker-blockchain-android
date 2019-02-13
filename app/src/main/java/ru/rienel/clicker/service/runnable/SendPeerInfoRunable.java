package ru.rienel.clicker.service.runnable;

import ru.rienel.clicker.common.Utility;
import ru.rienel.clicker.service.NetworkService;

public class SendPeerInfoRunable implements Runnable {
	private NetworkService.PeerInfo peerInfo;
	private NetworkService networkService;

	public SendPeerInfoRunable(NetworkService.PeerInfo peerInfo, NetworkService netService) {
		this.peerInfo = peerInfo;
		this.networkService = netService;
	}

	@Override
	public void run() {
		if (Utility.sendPeerInfo(peerInfo.host, peerInfo.port))
			networkService.postSendPeerInfoResult(0);
		else
			networkService.postSendPeerInfoResult(-1);
	}
}
