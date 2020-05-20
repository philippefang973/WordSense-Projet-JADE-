package wordsense;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.sql.*;

public class AgentCom extends Agent {
	HashSet<String> translated = new HashSet<String>();
	HashSet<String> read = new HashSet<String>();
	
	protected void setup() {
		try {
		System.out.println("AgentCom se lance!");
		Object[] args = getArguments();
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+(String)args[1]+"?useSSL=false",(String)args[2],(String)args[3]);
		Statement st = c.createStatement();
		st.executeUpdate("drop table if exists definitions");
		st.executeUpdate("create table definitions( mot text, defs text)");
		System.out.println("Base de données prête à stocker");
		addBehaviour(new OneShotBehaviour(this) {
			public void action() {
				try { 
					File f = new File((String) args[0]);
					Scanner sc = new Scanner(f); 
					System.out.println("Mots à traiter:");
					while (sc.hasNext()) {
						String mot = sc.next().replaceAll("[^\\p{L}-'_]", "");
						if (read.contains(mot)) continue;
						read.add(mot);	
						System.out.print(mot+" ");
						PreparedStatement pst = c.prepareStatement("insert into definitions(mot,defs) values(?,'')");
						pst.setString(1,mot);
						pst.executeUpdate();
						ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
						m.addReceiver(getAID("AgentDef"));
						m.setContent(mot);
						myAgent.send(m);
					}
					System.out.println();
					sc.close();
			} catch (Exception e) { System.out.println(e); }
			}
		});
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
			try { 
				ACLMessage msg = myAgent.receive();
				if (msg != null) {
					String [] res = msg.getContent().split("#");
					if (!translated.contains(res[0])) {
						System.out.println("Definitions de "+res[0]+":");
						translated.add(res[0]);
					}
					System.out.println(res[1]);
					PreparedStatement pst = c.prepareStatement("update definitions set defs=concat(defs,?) where mot=?");
					pst.setString(1, "\n"+res[1]);
					pst.setString(2, res[0]);
					pst.executeUpdate();
				} else
					block();
			} catch (Exception e) { System.out.println(e); }
			}
		});
		} catch (Exception e) { System.out.println(e);}
	}

	protected void takeDown() {
		System.out.println("AgentCom termine!");
	}
}
