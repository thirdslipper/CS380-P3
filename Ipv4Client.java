/**
 * Author: Colin Koo
 * Professor: Davarpanah
 * Description: The program sends IPv4 packets of various sizes to a specified server.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Ipv4Client {
	/**
	 * The main method opens a socket connection to a server, then calls getPackets() with sizes in power of 2 from 2 to 4096
	 * to represent sending packets of various sizes to the server.
	 * @param args
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws UnknownHostException{
		try (Socket socket = new Socket("codebank.xyz", 38003)){
			
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);

			OutputStream os = socket.getOutputStream();
			PrintStream ps = new PrintStream(os);

			int size = 1;
			for (int i = 0; i < 12; ++i){
				size <<= 1;
				System.out.println("Data length: " + size);
				ps.write(getPackets(size));
				System.out.println(br.readLine() + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * This method creates the byte array that represents a IPv4 packet, with fields including:
	 * Version, header length, type of service, header + data, identifier fragment, flags, 
	 * offset, time to live, protocol, checksum, source address, destination address, then data, disregarding
	 * options and padding entirely.  
	 * The size of the packet created varies depending on the input size, but the affected fields are the array
	 * slots representing the "header + data", checksum, and packet size (which in turn represents the data fields which are defaulted
	 * to 0's).
	 * The TOS, Ident, Offset, Checksum, and Data are initially defined to be 0 for clarity. 
	 * @param size
	 * @return Byte array containing data that represents the IPv4 packet.
	 */
	public static byte[] getPackets(int size){
		short length = (short) (20+size);
		byte[] arr = new byte[length];

		arr[0] = 0x45; 	//Version, HLen
		arr[1] = 0x0;	//TOS
		arr[2] = (byte) ((length >> 8) & 0xFF);	//Header + Data(0)?
		arr[3] = (byte) (length & 0xFF); 		//20+2*

		arr[4] = 0x0;	//ident
		arr[5] = 0x0;
		arr[6] = (0x1 << 6); 	// flag
		arr[7] = 0x0;	// offset

		arr[8] = 0x32; 	// 50 TTL
		arr[9] = 0x6; 	// TCP = 6
		arr[10] = 0x0; 	// todo Checksum
		arr[11] = 0x0;

		arr[12] = (byte) 76; 	//Src public IP Address
		arr[13] = (byte) 175;	
		arr[14] = (byte) 85;	
		arr[15] = (byte) 174;		

		arr[16] = (byte) 52;	//Dest socket inet address : 52.37.88.154
		arr[17] = (byte) 37;
		arr[18] = (byte) 88;
		arr[19] = (byte) 154;

		short cksum = checksum(arr);
		arr[10] = (byte) ((cksum >> 8) & 0xFF);
		arr[11] = (byte) (cksum & 0xFF);
		
		// Data fields are defaulted to 0 in the instantiation of the array.
		for (int i = 20; i < length; ++i){
			arr[i] = 0x0;
		}
		return arr;
	}
	/**
	 * This method is the same method from exercise 3.
	 * It concatenates every 2 bytes in the byte array and adds them together one at a time.
	 * After each addition operation, if there is overflow, it is cleared and added to sum.
	 * @param b
	 * @return short representing the one's complement of the sum.
	 */
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
//		System.out.println("Checksum calculated: 0x" + Integer.toHexString(checksum & 0xFFFF).toUpperCase());
		return (short) (~sum);
	}
}
