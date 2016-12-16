package test;

import java.util.ArrayList;
import java.util.List;

public class WebSocketFrame {


	
	
	public WebSocketFrame(){
		
	}
	
	
	public void setFrameData(int[] frameIntArr){
		
		List<String> byteStrList = new ArrayList<String>();
		
		for (int intNum : frameIntArr) {
			String byte2 = Integer.toBinaryString(intNum);
			
			StringBuffer sb = new StringBuffer(byte2);
			int twoByteLength = 16;
			if(byte2.length() < twoByteLength) {
				
				for (int i = 0; i < twoByteLength - byte2.length() ; i++) {
					sb.insert(0, "0");
				}
			}
			byteStrList.add(sb.substring(0, 8));
			byteStrList.add(sb.substring(8, 16));
			
			System.out.println(sb.substring(0, 8));
			System.out.println(sb.substring(8, 16));
		}
		
		FrameModel a = new FrameModel(byteStrList);
		System.out.println(a);
		
	}
	
public void setFrameData(List<Byte> byteList){
		
		List<String> byteStrList = new ArrayList<String>();
		for (int i = 0; i < byteList.size(); i++) {
			
			String bitStr = String.format("%16s", Integer.toBinaryString(byteList.get(i) & 0xFF)).replace(' ', '0');

			byteStrList.add(bitStr.substring(0, 8));
			byteStrList.add(bitStr.substring(8, 16));
			
			System.out.println(bitStr.substring(0, 8));
			System.out.println(bitStr.substring(8, 16));
		}
		
		
		FrameModel a = new FrameModel(byteStrList);
		System.out.println(a);
		
	}
	
	public static void main(String[] args){
		int a = 1;
		System.out.println(a);
		a |= 2;
		System.out.println(a);
		a |= 5;
		System.out.println(a);
	}
	
	
}
