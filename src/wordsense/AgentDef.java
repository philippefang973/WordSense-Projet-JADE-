package wordsense;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.*;
import java.net.*;
import java.util.regex.*;

public class AgentDef extends Agent {
	protected void setup() {
		System.out.println("AgentDef se lance!");
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				ACLMessage msg = myAgent.receive();
				if (msg != null) {
					try {
						URL url = new URL("https://www.larousse.fr/dictionnaires/francais/" + msg.getContent());
						BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
						String line;
						Pattern p1 = Pattern.compile("(.*)DivisionDefinition(.*)");
						Pattern p2 = Pattern.compile("(.*)TextePresentatif(.*)");
						boolean b = false;
						while ((line = in.readLine()) != null) {
							if (line.matches("(.*)</article>(.*)")) break; 
							Matcher m = p1.matcher(line);
							Matcher m2 = p2.matcher(line);
							if (m.find()) {
								b = true;
								if (!line.matches("\\s*")) {
									String def = line.trim().replaceAll("</p>", "/ ");
									def = def.replaceAll("<.*?>", "").replace("&nbsp;", "");
									ACLMessage reply = msg.createReply();
									reply.setPerformative(ACLMessage.PROPOSE);
									reply.setContent(msg.getContent() + "#\t-" + def);
									myAgent.send(reply);
								}
							} else if (m2.find()){
								if (!line.matches("\\s*")) {
									String def = line.trim().replaceAll("<.*?>", "").replace("&nbsp;", "");
									ACLMessage reply = msg.createReply();
									reply.setPerformative(ACLMessage.PROPOSE);
									reply.setContent(msg.getContent() + "#" + def);
									myAgent.send(reply);
								}
							} 
						}
						if (!b) {
							ACLMessage reply = msg.createReply();
							reply.setPerformative(ACLMessage.REFUSE);
							reply.setContent(msg.getContent() + "#\t(pas de definition)");
							myAgent.send(reply);
						}
						in.close();
					} catch (Exception e) {
					}
				} else
					block();
			}
		});
	}

	protected void takeDown() {
		System.out.println("AgentDef termine!");
	}
}
