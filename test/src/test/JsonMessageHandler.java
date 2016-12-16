package test;

public abstract class JsonMessageHandler extends MessageHandler<String> {

	@Override
	public String parser(String inputMessage) {
		// TODO Auto-generated method stub
		return inputMessage;
	}

	@Override
	public void handlingAction(String parsedMessage) {
		System.out.println(parsedMessage);
		action(parsedMessage);
	}
	
	public abstract void action(String message);

}
