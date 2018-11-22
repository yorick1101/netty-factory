package me.yorick.network.test;

import me.yorick.network.client.TCPClient;

public class TCPClientTest {

	public static void main(String[] args) throws InterruptedException {
		TCPClient client = new TCPClient();
		client.connect();
	}
}
