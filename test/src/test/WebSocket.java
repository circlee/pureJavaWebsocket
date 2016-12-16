package test;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSocket  extends Thread{

	private Socket socket = null;
	
	private InputStream socketInputStream = null;
	private OutputStream socketOutputStream = null;
	
	
	private WebSocket(){
		//nothing
	}
	
	public WebSocket(Socket socket){
		this.socket = socket;
	}
	
	public OutputStream getOutputStream() throws IOException{
		return this.socket.getOutputStream();
	}
	
	public void run() {
		
		System.out.println("WebSocket Thread Run");
		
		try{
			
		
			InputStream socketInputStream = this.socket.getInputStream();
			Scanner inputScanner = new Scanner(socketInputStream,"UTF-8").useDelimiter("\\r\\n\\r\\n");
			BufferedWriter outBufWriter  = new BufferedWriter( new OutputStreamWriter(this.socket.getOutputStream()));
			

			// handshake
			String data = inputScanner.next();
			Matcher get = Pattern.compile("^GET").matcher(data);
			System.out.println("HANDSHAKE-REQUEST");
			System.out.println(data);
			if (get.find()) {				    
					//accept-key 생성
					Pattern p = Pattern.compile("Sec-WebSocket-Key: (.*)");
					Matcher match = p.matcher(data);
					match.find();
					String key = match.group(1).toString().trim();
					String magicString = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
					String acceptKey = Sha1Digest.makeSha1(key+magicString);
					
					//response 작성
					StringBuffer outStr = new StringBuffer();
					outStr.append("HTTP/1.1 101 Switching Protocols");
					outStr.append("\r\n");
					outStr.append("Upgrade: websocket");
					outStr.append("\r\n");
					outStr.append("Connection: Upgrade");
					outStr.append("\r\n");
					outStr.append("Sec-WebSocket-Accept: "+acceptKey);
					outStr.append("\r\n");
					outStr.append("\r\n");
					
					//response out
					outBufWriter.write(outStr.toString());
					outBufWriter.flush();
					
					// response print
					System.out.println();
					System.out.println("HANDSHAKE-RESPONSE");
					System.out.println(outStr.toString());
					System.out.println();
			}
			
			
			//after handshake frame listening
			DataInputStream dis = new DataInputStream(this.socket.getInputStream());
			
//			BufferedInputStream bis = new BufferedInputStream(this.socket.getInputStream());
			List<Byte> byteList = new ArrayList<Byte>();
			while(true) {

				if(dis.available() == 0 && byteList.size() != 0) {
//					WebSocketFrame wsf = new WebSocketFrame();
//					wsf.setFrameData(byteList);
//					
					Byte[] framebyteArr = byteList.toArray(new Byte[byteList.size()]);
					byte[] framebyteArr2 = new byte[framebyteArr.length];
					for (int i = 0; i < framebyteArr.length; i++) {
						framebyteArr2[i] = framebyteArr[i];
					}
					Frame f = RawParse.parse(framebyteArr2);
					System.out.println("From Client : " + new String(f.payload));
					
					byteList = new ArrayList<Byte>();
					
				}
				byte frameByte = dis.readByte();
				byteList.add(frameByte);

			}
			
		
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
	}
}
