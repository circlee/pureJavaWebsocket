package test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class RawMaker
{
    public static class SendFrame
    {
        byte[] frameData;
    }

    public static SendFrame make(String msg) throws UnsupportedEncodingException
    {
    	SendFrame frame = new SendFrame();
    	
    	
    	byte[] msgPayload = msg.getBytes("UTF-8");
    	
    
    	List<Byte> byteList = new ArrayList<Byte>();
    	
    	
    	byte frame0 = (byte)0;
    	//fin
    	frame0 |= 0x80;
    	//opcode
    	frame0 |= 0x01;
    	byteList.add(frame0);
    	
    	byte frame1 = (byte)0;
    	//mask
    	frame1 |= 0x00; // none mask
    	//payload length
    	
    	
    	
    	int arrayLength = msgPayload.length;
    	int payloadLength = arrayLength * 8;
    	String payloadLengthBit = Integer.toBinaryString(payloadLength);
    	int payloadLengthBitlen = payloadLengthBit.length();
    	
    	byte[] extendsPayload = null;
    	if(payloadLengthBitlen <= 7) {
    		frame1 |= Byte.parseByte(Integer.toHexString(payloadLength),16);
    	} else if(payloadLengthBitlen <= 16){
    		extendsPayload = BigInteger.valueOf(payloadLength).toByteArray();
    	} else if(payloadLengthBitlen <= 64){
    		extendsPayload = BigInteger.valueOf(payloadLength).toByteArray();
    	}


    	byteList.add(frame1);
    	
    	if(extendsPayload != null) {
    		
    		int eplen = extendsPayload.length;
    		
    		int fixLen = 2;
    		if(eplen > 2) {
    			fixLen = 6;
    		}
    		
    		for (int i = 0; i < fixLen - eplen; i++) {
    			byteList.add((byte)0);
			}
    		
    		for (int i = 0; i < extendsPayload.length; i++) {
    			byteList.add(extendsPayload[i]);
			}
    		
    	}
    	
    	
    	byte[] maskingKey = new byte[]{
    			(byte)0
    			,(byte)0
    			,(byte)0
    			,(byte)0
    	};
    	
    	for (int i = 0; i < maskingKey.length; i++) {
    		byteList.add(maskingKey[i]);
		}
        
    	
    	for (int i = 0; i < msgPayload.length; i++) {
    		byteList.add(msgPayload[i]);
		}

    	
    	byte[] framebytes = new byte[byteList.size()];
    	for (int i = 0; i < byteList.size(); i++) {
    		framebytes[i] = byteList.get(i);
    		
    		System.out.println(String.format("%8s", Integer.toBinaryString(byteList.get(i) & 0xFF)).replace(' ', '0'));
		}
    	
    	frame.frameData = framebytes;
    	
    	return frame;
    }
    
    public static void main(String[] args ) {
    	SendFrame f;
		try {
			f = RawMaker.make("test12");
			
			System.out.println(f.frameData);
	    	
	    	for (int i = 0; i < f.frameData.length; i++) {
				System.out.println(f.frameData[i]);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}