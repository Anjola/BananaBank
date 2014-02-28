//Anjololuwa Olayemi
//CS 283- Assignment 2-Banana Bank
//BananaBank_simpleClient - demonstrates server functionality
//adopts code from BananaBank_benchmarkClient

package bananabank.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class BananaBankSimpleClient{
	public static final int TRANSACTIONS_NUM = 100;
	public static final int AMOUNTS[] = { 1, 2, 5, 10, 15, 120};
	public static final int ACCOUNT_NUMBERS[] = new int[]{ 11111, 22222,
		33333, 44444, 55555, 66666, 77777, 88888, 10101, 12121, 14141, 45454, 38383};

	public static void main(String[] args) throws InterruptedException {

		System.out.println("Client worker thread (thread id="+Thread.currentThread().getId()+") started");
		try {
			Random rand = new Random();
			
			// connect to the server
			Socket socket = new Socket("localhost", 2000);
			// set up input and output streams
			PrintStream ps = new PrintStream(socket.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			// request TRANSACTIONS_NUM transactions from the server
			for (int i = 0; i < TRANSACTIONS_NUM; i++) {
				// generate random source and destination account numbers
				int srcAccountNumber = ACCOUNT_NUMBERS[rand.nextInt(ACCOUNT_NUMBERS.length)];
				int dstAccountNumber = ACCOUNT_NUMBERS[rand.nextInt(ACCOUNT_NUMBERS.length)];
				int amount = AMOUNTS[rand.nextInt(AMOUNTS.length)]; 
				while (dstAccountNumber == srcAccountNumber)
					dstAccountNumber = BananaBankSimpleClient.ACCOUNT_NUMBERS[rand.nextInt(BananaBankSimpleClient.ACCOUNT_NUMBERS.length)];
				
				// ask the server to transfer $1 from source to destination account
				String line = "" + amount + " " + srcAccountNumber + " " + dstAccountNumber;
				ps.println(line);
				System.out.println("SENT: "+line);
				
				// read back the server's response and print it
				line = br.readLine();
				System.out.println("RECEIVED: "+line);
			}
			ps.println("SHUTDOWN");
			String line = br.readLine();
			int total = Integer.parseInt(line);
			System.out.println("Total amount of money in bank:" + total);
			
			// close the print stream (and the socket, implicitly)
			ps.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
}
