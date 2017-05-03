import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
public class Ipv4Client {

	public static void main(String[] args) throws UnknownHostException, IOException {
		try (Socket socket = new Socket("codebank.xyz", 38003)){
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);

			OutputStream os = socket.getOutputStream();
			PrintStream ps = new PrintStream(os);

			int size = 1;
			for (int i = 0; i < 11; ++i){
				size <<= 1;
				ps.write(getPackets(size));
				System.out.println(br.readLine());
			}
		}
	}
	public static byte[] getPackets(int size){
		byte[] arr = new byte[20+size];
		short length = (short) (20+size);

		arr[0] = 0x45; 	//ver and hlen
		arr[1] = 0x0;	//tos
		arr[2] = (byte) ((length >> 8) & 0xFF);	//head + data(0)?
		arr[3] = (byte) (length & 0xFF); 	//20+2*

		arr[4] = 0x0;	//ident
		arr[5] = 0x0;
		arr[6] = (0x1 << 6); 	// flag
		arr[7] = 0x0;	// 12 bit offset

		arr[8] = 0x32; 	// 50 ttl
		arr[9] = 0x6; 	// tcp = 6
		arr[10] = 0x0; 	// todo checksum
		arr[11] = 0x0;

		arr[12] = (byte) 192; 	//src
		arr[13] = (byte) 168;	
		arr[14] = (byte) 56;	
		arr[15] = (byte) 1;		

		arr[16] = (byte) 52;	//dest 52.37.88.154 || 192.168.1.229
		arr[17] = (byte) 37;
		arr[18] = (byte) 88;
		arr[19] = (byte) 154;

		short cksum = checksum(arr);
		arr[10] = (byte) ((cksum >> 8) & 0xFF);
		arr[11] = (byte) (cksum & 0xFF);

		return arr;
	}
	public static short checksum(byte[] b){
		long concat = 0x0;
		long sum = 0x0;
		for (int i = 0; i < b.length; i+=2){
			concat = (long) (b[i] & 0xFF);
			concat <<= 8;

			if ((i+1) < b.length){
				concat |= (b[i+1] & 0xFF);
			}
			sum = sum + concat;
			if (sum > 0xFFFF){
				sum &= 0xFFFF;
				sum ++;
			}
		}
		short checksum = (short) (~sum);
		System.out.println("Checksum calculated: 0x" + Integer.toHexString(checksum & 0xFFFF).toUpperCase());
		return (short) (~sum);
	}
}
