package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	public SimpleServer(int port) {
		super(port);

	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		Message message = (Message) msg;
		String request = message.getMessage();
		try {
			//we got an empty message, so we will send back an error message with the error details.
			if (request.isBlank()){
				message.setMessage("Error! we got an empty message");
				client.sendToClient(message);
			}
			//we got a request to change submitters IDs with the updated IDs at the end of the string, so we save
			// the IDs at data field in Message entity and send back to all subscribed clients a request to update
			//their IDs text fields. An example of use of observer design pattern.
			//message format: "change submitters IDs: 123456789, 987654321"
			else if(request.startsWith("change submitters IDs:")){
				message.setData(request.substring(23));
				message.setMessage("update submitters IDs");
				sendToAllClients(message);
			}
			//we got a request to add a new client as a subscriber.
			else if (request.equals("add client")){
				SubscribedClient connection = new SubscribedClient(client);
				SubscribersList.add(connection);
				message.setMessage("client added successfully");
				client.sendToClient(message);
			}
			//we got a message from client requesting to echo Hello, so we will send back to client Hello world!
			else if(request.startsWith("echo Hello")){
				message.setMessage("Hello World!");
				client.sendToClient(message);
			} else if(request.startsWith("send Submitters IDs")){
				message.setMessage("323032383, 212689889");
				client.sendToClient(message);
			} else if (request.startsWith("send Submitters")){
				message.setMessage("Majd, Adam");
				client.sendToClient(message);
			} else if (request.equals("what’s the time?")) {
				DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
				Date date = new Date();
				message.setMessage(dateFormat.format(date));
				client.sendToClient(message);

			} else if (request.startsWith("multiply")){
				message.setData(request.substring(9));
				String expression=message.getData();
				int num1=0,num2=0,i=0,res,size;
				size=expression.length();
				while(expression.charAt(i)!='\0' && expression.charAt(i)>='0' && expression.charAt(i)<='9') {
					num1*=10;
					num1+=(expression.charAt(i++)-'0');
				}
				i++;
				while(i<size && expression.charAt(i)!='\0' && expression.charAt(i)>='0' && expression.charAt(i)<='9') {
					num2*=10;
					num2+=(expression.charAt(i++)-'0');
				}
				res=num1*num2;
				message.setMessage(Integer.toString(res));
				client.sendToClient(message);

			}else{
				message.setMessage(request);
				sendToAllClients(message);

			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void sendToAllClients(Message message) {
		try {
			for (SubscribedClient SubscribedClient : SubscribersList) {
				SubscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
