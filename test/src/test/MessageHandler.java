package test;

public abstract class MessageHandler<T> {

	
	public abstract T parser(String inputMessage);
	
	public abstract void handlingAction(T parsedMessage);
	
	private void handler(String inputMessage){
		handlingAction(parser(inputMessage));
	}
	
}
