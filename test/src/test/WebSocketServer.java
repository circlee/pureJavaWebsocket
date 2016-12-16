package test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import test.RawMaker.SendFrame;

public class WebSocketServer extends Thread {

	private int idNum = 0;
	
	private ServerSocket serverSocket = null;
	
	private Map<String, WebSocket> webSocketMap = new HashMap<String, WebSocket>();
	
	
	private WebSocketServer(){
		// nothing
	}
	
	public WebSocketServer(int port) {
		
		try {
			System.out.println("WebSocket Server Start port["+port +"]");
			this.serverSocket = new ServerSocket(port);
			
			this.start();
			
			while(true) {
				Socket socket = serverSocket.accept();
				WebSocket webSocket = new WebSocket(socket);
				webSocket.start();
				
				webSocketMap.put(getIdNum(), webSocket);
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		System.out.println("ready to broadcast msg!");
		Scanner sc = new Scanner(System.in);
		while(true) {
			String inputMsg = sc.nextLine();
			try{
				SendFrame f = RawMaker.make(inputMsg);
				byte[] data = f.frameData;
				
				Set<java.util.Map.Entry<String, WebSocket>> smSet = webSocketMap.entrySet();
				Iterator<java.util.Map.Entry<String, WebSocket>> smIt = smSet.iterator();
				
				while(smIt.hasNext()) {
					java.util.Map.Entry<String, WebSocket> en = smIt.next();
					WebSocket ws = en.getValue();
					try {
						OutputStream os = ws.getOutputStream();
						os.write(data);
						os.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	private String getIdNum(){
		idNum ++;
		return Integer.toString(idNum);
	}
	
	
}
