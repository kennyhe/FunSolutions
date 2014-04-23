import java.io.*;
import java.net.Socket;


public class Client {
	
	public Client() {
		
	}
	
	public boolean connect(String addr, String port) {
        try {
           // Connect to Chat Server
           socket = new Socket(addr, Integer.parseInt(port));
           in = new BufferedReader(
               new InputStreamReader(socket.getInputStream()));
           out = new PrintWriter(
               new OutputStreamWriter(socket.getOutputStream()));
           //System.out.println("Connected to server " + addr + ":" + port);
        } catch (IOException ioe) {
           System.err.println("Can not establish connection to " +
                   addr + ":" + port);
           ioe.printStackTrace();
           return false;
        }
        return true;
	}
 

	public boolean sendMsg(String msg) {
        try {
        	out.println(msg);
        	out.flush();
        	//System.out.println("Message flushed out.");
        	
        	// Read messages from the server and print them
            String message;
            message=in.readLine();
            if (message.equals("ACK")) {
            	//System.out.println("Message successfully delivered");
            } else {
            	return false;
            }
            
        } catch (IOException ioe) {
           System.err.println("Connection to server broken.");
           ioe.printStackTrace();
           return false;
        }
 		
		return true;
	}
	
	public void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private BufferedReader in = null;
    private PrintWriter out = null;
    private Socket  socket = null;
	
}
