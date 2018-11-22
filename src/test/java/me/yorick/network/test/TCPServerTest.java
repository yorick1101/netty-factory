package me.yorick.network.test;

import me.yorick.network.server.TCPServer;

public class TCPServerTest {

	public static void main(String[] args) throws Exception {
		System.out.println("Server start");
		TCPServer server = new TCPServer(1234);
		server.start();
	}
}
