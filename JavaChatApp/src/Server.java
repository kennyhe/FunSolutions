import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Observable implements Runnable {
	//	 @Override	  
	public void run() {
		sockets = new ArrayList<Socket>();
		try{
			serversocket = new ServerSocket(0);
			port = serversocket.getLocalPort();
			//waiting for client connection
			Socket sock;
	 		while (true) {
	 			sock = serversocket.accept();
	 			ServerThread st = new ServerThread();
	 			st.setSocket(sock);
	 			sockets.add(sock);
	 			st.setObservable(this);
				st.start();
			}
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}
	
	
	public void closeASocket (Socket sock) {
		sockets.remove(sock);
		try {
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void closeServer() {
		try{
			Iterator<Socket> it = sockets.iterator();
			while (it.hasNext()) {
				it.next().close();
			}
			
	 		serversocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Server socket closed.");
	}
	
	
	public String getPort() {
		while (port == 0)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return String.valueOf(port);
	}
	

	public void update(Message msg) {
        setChanged();
        notifyObservers(msg);
	}
	

	int port = 0;
	ServerSocket serversocket;
	ArrayList<Socket> sockets;
}
		  


class ServerThread extends Thread {
	public void run() {
		String line = "";
		boolean forceClosed;
		while (true) {
		    try {
				line = read.readLine();
				write.println("ACK");
				write.flush();
				if (line.equals("quitquit") || line.isEmpty()) {
					forceClosed = false;
		        	break;
				}
				Message msg = new Message();
				msg.client = client;
				msg.msg = line;
		        obs.update(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				forceClosed = true;
				break;
			}
		}
		if (! forceClosed)
			obs.closeASocket(sock);
	}
	
	public void setSocket(Socket sock) {
	    try {
	    	this.sock = sock;
	    	client = "" + sock.getRemoteSocketAddress();
			read = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			write= new PrintWriter(sock.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setObservable(Server ob) {
		this.obs = ob;
	}
	
	BufferedReader read;
	PrintWriter write;
	Socket sock;
	Server obs;
	String client;
}


class Message {
	String client;
	String msg;
}