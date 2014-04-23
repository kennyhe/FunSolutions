import java.util.*;

public class MessageManager {

	MessageManager() {
		server = new Server();
		new Thread(server).start();
		System.out.println("local port:" + server.getPort());
		clients = new Hashtable<String, Client>();
	}
	
	
	private Client addClient(String addr, String port) {
		
		String key = addr + "_" + port;
		if (clients.containsKey(key)) {
			return clients.get(key);
		}

		Client client = new Client();
		
		if (client.connect(addr, port)) {
			clients.put(key, client);
			return client;
		}
		
		return null;
	}
	   
	
	
	public boolean sendMsg(String addr, String port, String msg) {
		Client client  = addClient(addr, port);
		
		if (client == null) {
			System.out.println("Can't connect to server: " + addr + ":" + port);
			return false;
		}
		
		if (! client.sendMsg(msg)) {
			clients.remove(client);
			client  = addClient(addr, port);
			
			if (client == null) {
				System.out.println("Can't connect to server: " + addr + ":" + port);
				return false;
			}
			
			if (! client.sendMsg(msg)) {
				System.out.println("Failed to send the message [" + msg + "]");
			}
		}
		
		return true;
	}
	
	
	public void closeClients() {
		Iterator<Client> it = clients.values().iterator();
		while (it.hasNext()) {
			it.next().closeSocket();
		}
	}
	
	
	public void addObserver(Observer o) {
		server.addObserver(o);
	}
	
	public String getServerPort() {
		return server.getPort();
	}
	
	public void quitLocalServer() {
		server.closeServer();
	}
    
    
	Server server;
	Map<String, Client> clients;
}
