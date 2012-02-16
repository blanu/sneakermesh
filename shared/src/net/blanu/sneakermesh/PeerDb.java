package net.blanu.sneakermesh;

import java.util.List;
import java.util.Set;

public interface PeerDb
{
	void addPeer(String peer);
	List<String> getPeers();
	
	void putHave(String peer, String hash);
	void putHave(String peer, Set<String> hashes);
	void deleteHave(String peer);
	List<String> getHave(String peer);
	List<String> getPeersThatHave(String hash);
	List<String> getHaveExcept(String peer1, String peer2);

	void putWant(String peer, String hash);
	void putWant(String peer, Set<String> hashes);
	void deleteWant(String peer);
	void deletePeerWant(String peer, String hash);
	List<String> getPeersThatWant(String hash);
	List<String> getWant(String peer);	
	List<String> getWantExcept(String peer1, String peer2);
	
	List<String> getWantAndHave(String peer1, String peer2);	
}
