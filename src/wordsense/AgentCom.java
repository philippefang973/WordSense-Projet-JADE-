package wordsense;

import java.io.File;
import java.util.Scanner;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AgentCom extends Agent {
	protected void setup() {
		System.out.println("AgentCom setup!");
		addBehaviour(new OneShotBehaviour(this) {
			public void action() {
				try {
					Object[] args = getArguments();
					File f = new File((String) args[0]);
					Scanner sc = new Scanner(f);
					while (sc.hasNext()) {
						ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
						m.addReceiver(getAID("AgentDef"));
						m.setContent(sc.next().replaceAll("[^\\p{L}-'_]", ""));
						myAgent.send(m);
					}
				} catch (Exception e) {
				}
			}
		});
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				ACLMessage msg = myAgent.receive();
				if (msg != null) {
					System.out.println(msg.getContent());
				} else
					block();
			}
		});
	}

	protected void takeDown() {
		System.out.println("AgentCom taken down!");
	}
}
