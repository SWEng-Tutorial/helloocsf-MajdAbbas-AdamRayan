package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		Message message = (Message) msg;
		if(message.getMessage().equals("update submitters IDs")){
			EventBus.getDefault().post(new UpdateMessageEvent(message));
		}else if(message.getMessage().equals("client added successfully")){
			EventBus.getDefault().post(new NewSubscriberEvent(message));
		}else if(message.getMessage().equals("Error! we got an empty message")){
			EventBus.getDefault().post(new ErrorEvent(message));
		}else {
			EventBus.getDefault().post(new MessageEvent(message));
		}
	}
	
	public static SimpleClient getClient() throws UnknownHostException {
		if (client == null) {
			InetAddress ip;
			try {
				ip = InetAddress.getLocalHost();
				client = new SimpleClient(ip.getHostAddress(), 3020);
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}

		}
		return client;
	}

}
