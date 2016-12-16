package test;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;

public class FrameModel {

	private final String FIN_IDX = "0,0";

	private final String RSV1_IDX = "0,1";

	private final String RSV2_IDX = "0,2";

	private final String RSV3_IDX = "0,3";

	private final String OPCODE_IDX = "0,4-7";

	private final String MASK_IDX = "1,0";

	private final String PAYLOAD_LEN_CHK_IDX = "1,1-7";

	private final String PAYLOAD_LEN_126_IDX = "2-3";

	private final String PAYLOAD_LEN_127_IDX = "2-9";

	private final String MASKING_KEY_IDX = "10-13";

	private final String PAYLOAD_DATA_IDX = "14-";



	private boolean finFlag = false;
	private boolean rsv1Flag = false;
	private boolean rsv2Flag = false;
	private boolean rsv3Flag = false;
	private String opcode = "";
	private boolean maskFlag = false;
	private int payloadLength = 0;
	private String maskingKey = "";

	private String payloadData = "";



	public FrameModel(List<String> byteStrList){

		//Fin
		finFlag = "1".equals(String.valueOf(byteStrList.get(0).charAt(0)));

		//rsv1
		rsv1Flag = "1".equals(String.valueOf(byteStrList.get(0).charAt(1)));

		//rsv2
		rsv2Flag = "1".equals(String.valueOf(byteStrList.get(0).charAt(2)));

		//rsv3
		rsv3Flag = "1".equals(String.valueOf(byteStrList.get(0).charAt(3)));
		
		//opcode
		opcode = byteStrList.get(0).substring(4, 8);
		
		//mask
		maskFlag = "1".equals(String.valueOf(byteStrList.get(1).charAt(0)));

		
		//payloadLength
		
		String payloadLengthBit = getBits(byteStrList, PAYLOAD_LEN_CHK_IDX);
		
		int len = Integer.parseUnsignedInt(payloadLengthBit, 2);
		System.out.println(len);
		
		if(len <= 125) {
			payloadLength = len;	
		} else if(len == 126) {
			payloadLength = Integer.parseUnsignedInt(getBits(byteStrList, PAYLOAD_LEN_126_IDX), 2);
		} else if(len == 127) {
			payloadLength = Integer.parseUnsignedInt(getBits(byteStrList, PAYLOAD_LEN_127_IDX), 2);
		}
		
		
		//maskingKey
		maskingKey = getBits(byteStrList, MASKING_KEY_IDX);
		
		payloadData = getBits(byteStrList, PAYLOAD_DATA_IDX);
		
		if(maskFlag) {
			// unmasking
			byte[] unmask = getUnMaskStr(payloadData, maskingKey);
			System.out.println("maskingKey : "+ maskingKey);
			System.out.println("origin : " + payloadData);
			System.out.println("unmask : " + unmask);

			byte[] bb = new byte[unmask.length / 2];
			System.out.println("!!!!!!!!!!!!!!!!!");
			for (int i = 0; i < bb.length; i++) {
				
				StringBuffer sb = new StringBuffer();
				String bitStr = String.format("%8s", Integer.toBinaryString(unmask[i*2] & 0xFF)).replace(' ', '0');
				sb.append(bitStr);
				System.out.println(bitStr);
				String bitStr2 = String.format("%8s", Integer.toBinaryString(unmask[i*2 + 1] & 0xFF)).replace(' ', '0');
				sb.append(bitStr2);
				System.out.println(bitStr2);
				bb[i] = (byte)Integer.parseUnsignedInt(sb.toString(), 2);
				
			}
			
			payloadData = new String(bb);
		}
		
		//bit -> bytes - > string
		
		byte[] payloadDataBytes = new byte[payloadData.length() / 8];
		
		for (int i = 0; i < payloadData.length() / 8; i++) {
			
			System.out.println(payloadData.substring(i * 8 , (i * 8) + 8 ));
			
//			byte b = Byte.parseByte(payloadData.substring(i * 8 , (i * 8) + 8 ), 2);
			
		
			byte b = (byte)Integer.parseInt(payloadData.substring(i * 8 , (i * 8) + 8 ), 2);

			System.out.println(b);
			payloadDataBytes[i] = b;
		}
		
		
		try {
			System.out.println(new String(payloadDataBytes, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
public static void main(String[] args) throws UnsupportedEncodingException{
		
//	byte extra_dop = (byte)Integer.parseInt("11100000", 2);
	
	String a = "YAY!";
	byte[] b = a.getBytes();
	for (int i = 0; i < b.length; i++) {
		System.out.println(b[i]);
	}
	
	System.out.println("---------");
	
	
	String maskingKey = "00000000001111111111111111111101";
	String origin = "000000000000110100000000010010110000000000011110";
	
	System.out.println(origin);
	System.out.println(new String(getUnMaskStr(origin, maskingKey), "UTF-8"));
	
	System.out.println("---------");
	
	byte[] b2 = getUnMaskStr(origin, maskingKey);
	
	System.out.println("---------");
	System.out.println("unmask  byte length : " + b2.length);
	for (int i = 0; i < b2.length; i++) {
		System.out.println(b2[i]);
	}
	System.out.println("---------");
	
	}
	
	
public static byte[] getBytesFromBitStr(String bitStr) {
	
	
	
	byte[] bytes = new byte[bitStr.length() / 8];
	
	for (int i = 0; i < bitStr.length() / 8; i++) {
		
		byte b = (byte)Integer.parseUnsignedInt(bitStr.substring(i * 8 , (i * 8) + 8 ), 2);

		System.out.println(b);
		bytes[i] = b;
	}
	return bytes;
}
	
public static byte[] getUnMaskStr(String origin, String mask){
		
		StringBuffer sb = new StringBuffer();
		
		byte[] originBytesArr = getBytesFromBitStr(origin);
		
		byte[] maskBytesArr = getBytesFromBitStr(mask);
		
		for (int i = 0; i < originBytesArr.length; i++) {
			
			byte unmastByte = (byte)(originBytesArr[i] ^ maskBytesArr[i % 4]);
			
			originBytesArr[i] = unmastByte;
			
		}
		
		return originBytesArr;
	}
	
	
public static String getBits(List<String> byteStrList , String rangeStr) {
		
		StringBuffer sb = new StringBuffer();
		String[] rangeStrArr = rangeStr.split(",");
		
		String listRangeStr = "";
		listRangeStr = rangeStrArr[0];
		
		String strRangrStr = "";
		if(rangeStrArr.length > 1) {
			strRangrStr = rangeStrArr[1];	
		}
		
		
		int listRangeStart = 0;
		int listRangeEnd = 0;
		
		if(listRangeStr.contains("-")) {
			
			String[] listRangeStrArr = listRangeStr.split("-");
			
			listRangeStart = Integer.parseInt(listRangeStrArr[0]);
			
			if(listRangeStrArr.length>1) {
				listRangeEnd = Integer.parseInt(listRangeStrArr[1]);	
			} else {
				listRangeEnd = byteStrList.size() -1;
			}
			
			
			
			
		} else {
			listRangeStart = Integer.parseInt(listRangeStr);
			listRangeEnd = Integer.parseInt(listRangeStr);
		}
		
		
		for (int i = listRangeStart; i < listRangeEnd+1; i++) {

			String byteStr = byteStrList.get(i);
			
			if(strRangrStr.length() != 0) {

				int strRangeStart = 0;
				int strRangeEnd = 0;
				
				if(strRangrStr.contains("-")) {

					String[] strRangeStrArr = strRangrStr.split("-");
					
					strRangeStart = Integer.parseInt(strRangeStrArr[0]);
					
					
					if(strRangeStrArr.length>1) {
						strRangeEnd = Integer.parseInt(strRangeStrArr[1]);	
					} else {
						strRangeEnd = strRangrStr.length() -1;
					}
					
				} else {
					strRangeStart = Integer.parseInt(strRangrStr);
					strRangeEnd = Integer.parseInt(strRangrStr);
				}
				
				
				byteStr = byteStr.substring(strRangeStart, strRangeEnd + 1);
				
			}
			
			sb.append(byteStr);
		}
		
		
		return sb.toString();
	}



	public boolean isFinFlag() {
		return finFlag;
	}



	public void setFinFlag(boolean finFlag) {
		this.finFlag = finFlag;
	}



	public boolean isRsv1Flag() {
		return rsv1Flag;
	}



	public void setRsv1Flag(boolean rsv1Flag) {
		this.rsv1Flag = rsv1Flag;
	}



	public boolean isRsv2Flag() {
		return rsv2Flag;
	}



	public void setRsv2Flag(boolean rsv2Flag) {
		this.rsv2Flag = rsv2Flag;
	}



	public boolean isRsv3Flag() {
		return rsv3Flag;
	}



	public void setRsv3Flag(boolean rsv3Flag) {
		this.rsv3Flag = rsv3Flag;
	}



	public String getOpcode() {
		return opcode;
	}



	public void setOpcode(String opcode) {
		this.opcode = opcode;
	}



	public boolean isMaskFlag() {
		return maskFlag;
	}



	public void setMaskFlag(boolean maskFlag) {
		this.maskFlag = maskFlag;
	}



	public int getPayloadLength() {
		return payloadLength;
	}



	public void setPayloadLength(int payloadLength) {
		this.payloadLength = payloadLength;
	}



	public String getMaskingKey() {
		return maskingKey;
	}



	public void setMaskingKey(String maskingKey) {
		this.maskingKey = maskingKey;
	}



	public String getPayloadData() {
		return payloadData;
	}



	public void setPayloadData(String payloadData) {
		this.payloadData = payloadData;
	}



	public String getFIN_IDX() {
		return FIN_IDX;
	}



	@Override
	public String toString() {
		return "FrameModel [finFlag=" + finFlag + ", rsv1Flag=" + rsv1Flag + ", rsv2Flag=" + rsv2Flag + ", rsv3Flag="
				+ rsv3Flag + ", opcode=" + opcode + ", maskFlag=" + maskFlag + ", payloadLength=" + payloadLength
				+ ", maskingKey=" + maskingKey + ", payloadData=" + payloadData + "]";
	}


	
	





}
