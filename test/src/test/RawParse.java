package test;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class RawParse
{
    

    public static Frame parse(byte raw[])
    {
        // easier to do this via ByteBuffer
        ByteBuffer buf = ByteBuffer.wrap(raw);

        // Fin + RSV + OpCode byte
        Frame frame = new Frame();
        byte b = buf.get();
        frame.fin = ((b & 0x80) != 0);
        boolean rsv1 = ((b & 0x40) != 0);
        boolean rsv2 = ((b & 0x20) != 0);
        boolean rsv3 = ((b & 0x10) != 0);
        frame.opcode = (byte)(b & 0x0F);

        // TODO: add control frame fin validation here
        // TODO: add frame RSV validation here

        // Masked + Payload Length
        b = buf.get();
        boolean masked = ((b & 0x80) != 0);
        int payloadLength = (byte)(0x7F & b);
        int byteCount = 0;
        if (payloadLength == 0x7F)
        {
            // 8 byte extended payload length
            byteCount = 8;
        }
        else if (payloadLength == 0x7E)
        {
            // 2 bytes extended payload length
            byteCount = 2;
        }

        // Decode Payload Length
        while (--byteCount > 0)
        {
            b = buf.get();
            payloadLength |= (b & 0xFF) << (8 * byteCount);
        }

        // TODO: add control frame payload length validation here

        byte maskingKey[] = null;
        if (masked)
        {
            // Masking Key
            maskingKey = new byte[4];
            buf.get(maskingKey,0,4);
        }

        // TODO: add masked + maskingkey validation here

        // Payload itself
        frame.payload = new byte[payloadLength];
        buf.get(frame.payload,0,payloadLength);

        // Demask (if needed)
        if (masked)
        {
            for (int i = 0; i < frame.payload.length; i++)
            {
                frame.payload[i] ^= maskingKey[i % 4];
            }
        }

        return frame;
    }

    public static void main(String[] args)
    {
        Charset UTF8 = Charset.forName("UTF-8");

        Frame closeFrame = parse(hexToByteArray("8800"));
        System.out.printf("closeFrame.opcode = %d%n",closeFrame.opcode);
        System.out.printf("closeFrame.payload.length = %d%n",closeFrame.payload.length);

        // Examples from http://tools.ietf.org/html/rfc6455#section-5.7
        Frame unmaskedTextFrame = parse(hexToByteArray("810548656c6c6f"));
        System.out.printf("unmaskedTextFrame.opcode = %d%n",unmaskedTextFrame.opcode);
        System.out.printf("unmaskedTextFrame.payload.length = %d%n",unmaskedTextFrame.payload.length);
        System.out.printf("unmaskedTextFrame.payload = \"%s\"%n",new String(unmaskedTextFrame.payload,UTF8));

        Frame maskedTextFrame = parse(hexToByteArray("818537fa213d7f9f4d5158"));
        System.out.printf("maskedTextFrame.opcode = %d%n",maskedTextFrame.opcode);
        System.out.printf("maskedTextFrame.payload.length = %d%n",maskedTextFrame.payload.length);
        System.out.printf("maskedTextFrame.payload = \"%s\"%n",new String(maskedTextFrame.payload,UTF8));
    }

    public static byte[] hexToByteArray(String hstr)
    {
        if ((hstr.length() < 0) || ((hstr.length() % 2) != 0))
        {
            throw new IllegalArgumentException(String.format("Invalid string length of <%d>",hstr.length()));
        }

        int size = hstr.length() / 2;
        byte buf[] = new byte[size];
        byte hex;
        int len = hstr.length();

        int idx = (int)Math.floor(((size * 2) - (double)len) / 2);
        for (int i = 0; i < len; i++)
        {
            hex = 0;
            if (i >= 0)
            {
                hex = (byte)(Character.digit(hstr.charAt(i),16) << 4);
            }
            i++;
            hex += (byte)(Character.digit(hstr.charAt(i),16));

            buf[idx] = hex;
            idx++;
        }

        return buf;
    }
}