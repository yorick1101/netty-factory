package me.yorick.network.message;

import me.yorick.network.message.DefinitionAnnotation;
import me.yorick.network.message.ReadField;
import me.yorick.network.message.WriteField;

@DefinitionAnnotation(title='O')
public interface Order {

	@WriteField(name="exchange")
	public void setExchange(String exchange) ;
	
	@ReadField(name="exchange")
	public String getExchange();

	@WriteField(name="symbol")
	public void setSymbol(String symbol);

	@ReadField(name="symbol")
	public String getSymobl();

	@WriteField(name="side")
	public void setSide(Side side);
	
	@ReadField(name="side")
	public Side getSide();

}
