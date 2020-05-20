package wordsense;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.*;
import java.net.*;
import java.util.regex.*;
public class AgentDef extends Agent {
	protected void setup() { 
		System.out.println("AgentDef setup!");
		addBehaviour(new CyclicBehaviour(this){
			public void action() {
				ACLMessage msg = myAgent.receive();
				if (msg!=null) {
					try {
					    URL url = new URL("https://www.larousse.fr/dictionnaires/francais/"+msg.getContent());
					    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
					    String line;
					    Pattern pattern = Pattern.compile("(.*)DivisionDefinition(.*)");
					    boolean b1 = false;
					    boolean b2 = false;
					    int i = 1;
					    while ((line = in.readLine()) != null && b1==b2) {
					    	Matcher m = pattern.matcher(line);
					    	if (m.find()) {
					    		b2 = true;
					    		b1 = true;
					    		if (!line.matches("\\s*")) {
					    			String def = line.trim().replaceAll("</p>", "/ ");
					    			def = def.replaceAll("<.*?>","").replace("&nbsp;","").split(":")[0];
					    			ACLMessage reply = msg.createReply();
					    			reply.setPerformative(ACLMessage.PROPOSE);
					    			reply.setContent(msg.getContent()+":"+String.valueOf(i++)+":"+def);
					    			myAgent.send(reply);
					    		}
					    	} else b1 = false;		
					    }
					    i = 0;
					    if (!b1 && !b2) {
					    	ACLMessage reply = msg.createReply();
			    			reply.setPerformative(ACLMessage.REFUSE);
			    			reply.setContent(msg.getContent()+":"+String.valueOf(i++)+":(pas de definition)");
			    			myAgent.send(reply);
					    }
					    in.close();
					} catch (Exception e) {} 
				} else block();
			}
		});
	}
	protected void takeDown() { System.out.println("AgentDef taken down!");  }  
}
