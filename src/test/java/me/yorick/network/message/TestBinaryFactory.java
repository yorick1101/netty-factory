package me.yorick.network.message;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBinaryFactory {

	private static Logger logger = LoggerFactory.getLogger(TestBinaryFactory.class);
	
	@Test
	public void testSerialize() throws Exception{

		String exchange = "GDAX";
		String symbol = "ETH/BTC";
		Side side = Side.Bid;
		
		
		Class<?> generatedClass = BinaryImplementationFactory.generate(Order.class);
		Order order = (Order) generatedClass.newInstance();
		order.setExchange(exchange);
		order.setSymbol(symbol);
		order.setSide(side);
		
		byte[] data = ((BinaryMessage) order).getData();
		print(data);
		
		Order order2 = (Order) generatedClass.newInstance();
		((BinaryMessage) order2).setBuffer(data);
		
		Assertions.assertEquals(exchange, order2.getExchange());
		Assertions.assertEquals(symbol, order2.getSymobl());
		Assertions.assertEquals(side, order2.getSide());
	}
	
	private void print(byte[] data) {
		for(byte b : data) System.out.print(b);
		logger.debug("");
	}
}
