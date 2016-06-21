import java.util.Random;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

//############# FIXED COOPERATOR #############
/**This class defines the behaviour
 * of our fixed cooperator. */
@SuppressWarnings("serial")
class FixedCooperatorBehaviour extends Behaviour {

	psi04_FixedC agent;

	public FixedCooperatorBehaviour(psi04_FixedC agent) {
		super(agent);
		this.agent = agent;
	}

	public void action() {
		
		//We wait for the messages
		ACLMessage receivedMessage = agent.blockingReceive();
		String messageContent = receivedMessage.getContent();

		//We obtain the fields
		String[] messageFields = messageContent.split("#");
		ACLMessage replyMessage = receivedMessage.createReply();

		//We look for the type of the message (NewGame,NewRound,...)
		if (messageFields[0].trim().equals("Id")) {
			agent.setId(Integer.parseInt(messageFields[1].trim()));

			//We get the parameters
			String[] parameters = messageFields[2].split(",");

			//We save them into the agents fields
			agent.setNumberOfPlayers(Integer.parseInt(parameters[0].trim()));
			agent.setMoney(Double.parseDouble(parameters[1].trim()));
			agent.setMoneyPut(Double.parseDouble(parameters[2].trim()));
			agent.setInterestFactor(Double.parseDouble(parameters[3].trim()));
			agent.setFineFactor(Double.parseDouble(parameters[4].trim()));
			agent.setMaxInspectors(Integer.parseInt(parameters[5].trim()));
			agent.setNumberOfRounds(Integer.parseInt(parameters[6].trim()));

		} else if (messageFields[0].trim().equals("NewRound")) {
			//We update the round number
			agent.setRound(agent.getRound() + 1);

		} else if (messageFields[0].trim().equals("Inspector")) {
			//We answer the main agent that we are not going to play as an inspector
			replyMessage.setContent("Inspector#No");
			replyMessage.setPerformative(ACLMessage.INFORM);
			agent.send(replyMessage);

		} else if (messageFields[0].trim().equals("Role")) {
			
			//We answer the main agent that we will play as a cooperator
			replyMessage.setContent("MyRole#C");
			replyMessage.setPerformative(ACLMessage.INFORM);
			agent.send(replyMessage);
		} else if (messageFields[0].trim().equals("Benefit")) {
			
			//We get the benefit
			String[] results = messageFields[1].split("#");
			Double benefit = Double.parseDouble(results[0]);
			agent.setMoney(agent.getMoney() + benefit);
		}

	}

	public boolean done() {

		return false;
	}

}

//############# FIXED DEFECTOR #############
/**This class defines the behaviour
 * of our fixed defector. */
@SuppressWarnings("serial")
class FixedDefectorBehaviour extends Behaviour {

	psi04_FixedD agent;

	public FixedDefectorBehaviour(psi04_FixedD agent) {
		super(agent);
		this.agent = agent;
	}

	public void action() {
		
		//We wait for the messages
		ACLMessage receivedMessage = agent.blockingReceive();
		String messageContent = receivedMessage.getContent();
		
		//We obtain the fields
		String[] messageFields = messageContent.split("#");
		ACLMessage replyMessage = receivedMessage.createReply();

		//We look for the type of the message (NewGame,NewRound,...)
		if (messageFields[0].trim().equals("Id")) {

			//We get the parameters
			agent.setId(Integer.parseInt(messageFields[1].trim()));
			String[] parameters = messageFields[2].split(",");

			//We save them into the agents fields
			agent.setNumberOfPlayers(Integer.parseInt(parameters[0].trim()));
			agent.setMoney(Double.parseDouble(parameters[1].trim()));
			agent.setMoneyPut(Double.parseDouble(parameters[2].trim()));
			agent.setInterestFactor(Double.parseDouble(parameters[3].trim()));
			agent.setFineFactor(Double.parseDouble(parameters[4].trim()));
			agent.setMaxInspectors(Integer.parseInt(parameters[5].trim()));
			agent.setNumberOfRounds(Integer.parseInt(parameters[6].trim()));

		} else if (messageFields[0].trim().equals("NewRound")) {
			//We update the round number
			agent.setRound(agent.getRound() + 1);

		} else if (messageFields[0].trim().equals("Inspector")) {
			//We answer the main agent that we are not going to play as an inspector
			replyMessage.setContent("Inspector#No");
			replyMessage.setPerformative(ACLMessage.INFORM);
			agent.send(replyMessage);

		} else if (messageFields[0].trim().equals("Role")) {
			//We answer the main agent that we are going to play as a defector
			replyMessage.setContent("MyRole#D");
			replyMessage.setPerformative(ACLMessage.INFORM);
			agent.send(replyMessage);
		} else if (messageFields[0].trim().equals("Benefit")) {
			//we get the benefit
			String[] results = messageFields[1].split("#");
			Double benefit = Double.parseDouble(results[0]);
			agent.setMoney(agent.getMoney() + benefit);
		}

	}

	public boolean done() {

		return false;
	}

}

//############# FIXED INSPECTOR #############
/**This class defines the behaviour
 * of our fixed cooperator. */
@SuppressWarnings("serial")
class FixedInspectorBehaviour extends Behaviour {

	psi04_FixedI agent;

	public FixedInspectorBehaviour(psi04_FixedI agent) {
		super(agent);
		this.agent = agent;
	}

	public void action() {
		//We wait for the messages
		ACLMessage receivedMessage = agent.blockingReceive();
		String messageContent = receivedMessage.getContent();
		
		//We obtain the fields
		String[] messageFields = messageContent.split("#");
		ACLMessage replyMessage = receivedMessage.createReply();
		
		//We look for the type of the message (NewGame,NewRound,...)
		if (messageFields[0].trim().equals("Id")) {

			//We get the parameters
			agent.setId(Integer.parseInt(messageFields[1].trim()));
			String[] parameters = messageFields[2].split(",");

			//We save them into the agents fields
			agent.setNumberOfPlayers(Integer.parseInt(parameters[0].trim()));
			agent.setMoney(Double.parseDouble(parameters[1].trim()));
			agent.setMoneyPut(Double.parseDouble(parameters[2].trim()));
			agent.setInterestFactor(Double.parseDouble(parameters[3].trim()));
			agent.setFineFactor(Double.parseDouble(parameters[4].trim()));
			agent.setMaxInspectors(Integer.parseInt(parameters[5].trim()));
			agent.setNumberOfRounds(Integer.parseInt(parameters[6].trim()));

		} else if (messageFields[0].trim().equals("NewRound")) {
			//We update the round number
			agent.setRound(agent.getRound() + 1);

		} else if (messageFields[0].trim().equals("Inspector")) {
			//We answer the main agent that we are going to play as an inspector
			replyMessage.setContent("Inspector#Yes");
			replyMessage.setPerformative(ACLMessage.INFORM);
			agent.send(replyMessage);

		} else if (messageFields[0].trim().equals("Role")) {
			
			//We get the IDs of the inspectors
			String[] inspectors = { String.valueOf(agent.getId()) };
			try {
				inspectors = messageFields[1].trim().split(",");
			} catch (IndexOutOfBoundsException e) {
			}
			
			boolean repeat = true;
			int crosshair = 0;

			//We generate a random ID till we get a different one of every inspector
			while (repeat) {
				//We generate the random ID
				Random random = new Random();
				crosshair = (int)(random.nextDouble()*agent.getNumberOfPlayers());
				repeat = false;
				
				//We check if the random ID is the same of any of the inspectors IDs
				for (int i = 0; i < inspectors.length; i++) {
					if (crosshair == Integer.parseInt(inspectors[i]))
						repeat = true;
				}

			}

			//We tell the main agent which player we are going to inspect
			replyMessage.setContent("MyRole#I#" + crosshair);
			replyMessage.setPerformative(ACLMessage.INFORM);
			agent.send(replyMessage);

		} else if (messageFields[0].trim().equals("Benefit")) {
			
			//We get the benefit
			String[] results = messageFields[1].split("#");
			Double benefit = Double.parseDouble(results[0]);
			agent.setMoney(agent.getMoney() + benefit);
		}

	}

	public boolean done() {

		return false;
	}

}

//############# RANDOM PLAYER #############
/**This class defines the behaviour
 * of our fixed cooperator. */
@SuppressWarnings("serial")
class RandomBehaviour extends Behaviour {

	psi04_Random agent;
	int nextRole;

	public RandomBehaviour(psi04_Random agent) {
		super(agent);
		this.agent = agent;
	}

	public void action() {
		//We wait for the messages
		ACLMessage receivedMessage = agent.blockingReceive();
		String messageContent = receivedMessage.getContent();
		
		//We obtain the fields
		String[] messageFields = messageContent.split("#");
		ACLMessage replyMessage = receivedMessage.createReply();

		//We look for the type of the message (NewGame,NewRound,...)
		if (messageFields[0].trim().equals("Id")) {

			//We get the parameters
			agent.setId(Integer.parseInt(messageFields[1].trim()));
			String[] parameters = messageFields[2].split(",");

			//We save them into the agents fields
			agent.setNumberOfPlayers(Integer.parseInt(parameters[0].trim()));
			agent.setMoney(Double.parseDouble(parameters[1].trim()));
			agent.setMoneyPut(Double.parseDouble(parameters[2].trim()));
			agent.setInterestFactor(Double.parseDouble(parameters[3].trim()));
			agent.setFineFactor(Double.parseDouble(parameters[4].trim()));
			agent.setMaxInspectors(Integer.parseInt(parameters[5].trim()));
			agent.setNumberOfRounds(Integer.parseInt(parameters[6].trim()));

		} else if (messageFields[0].trim().equals("NewRound")) {
			
			//We update the round number
			agent.setRound(agent.getRound() + 1);
			/*We generate the next role 
			 * [1->Cooperator]
			 * [2->Defector]
			 * [3->Inspector]*/
			Random random = new Random();
			nextRole = (int)((random.nextDouble())*3)+1;

		} else if (messageFields[0].trim().equals("Inspector")) {
			
			//If we generate a 3, we will play as Inspectors, so we answer yes
			if (nextRole == 3)
				replyMessage.setContent("Inspector#Yes");
			
			//In other case, we will answer no
			else
				replyMessage.setContent("Inspector#No");
			
			//We answer the main agent
			replyMessage.setPerformative(ACLMessage.INFORM);
			agent.send(replyMessage);

		} else if (messageFields[0].trim().equals("Role")) {
			
			//If we generate a 1, we will play as a cooperator
			if (nextRole == 1)
				replyMessage.setContent("MyRole#C");
			else {
				
				//If we generate a 2, we will play as a defector
				if (nextRole == 2)
					replyMessage.setContent("MyRole#D");
				else 
					
					//If we generate a 3, we will play as an inspector
					if (nextRole == 3) {
						String[] inspectors = { String.valueOf(agent.getId()) };
						
						//We get the inspectors IDs
						try {
							inspectors = messageFields[1].trim().split(",");
						} catch (IndexOutOfBoundsException e) {
						}
						boolean repeat = true;
						int crosshair = 0;
	
						//We generate a random ID to inspect
						while (repeat) {
							Random random = new Random();
							crosshair = (int)(random.nextDouble()*agent.getNumberOfPlayers());
							repeat = false;
							
							//We check if the generated ID is the same of any of the inspectors IDs
							for (int i = 0; i < inspectors.length; i++) {
								if (crosshair == Integer.parseInt(inspectors[i]))
									repeat = true;
							}
	
						}

						replyMessage.setContent("MyRole#I#" + crosshair);
					}
			}

			//We answer the main agent
			replyMessage.setPerformative(ACLMessage.INFORM);
			agent.send(replyMessage);

		} else if (messageFields[0].trim().equals("Benefit")) {
			
			//We get the benefit
			String[] results = messageFields[1].split("#");
			Double benefit = Double.parseDouble(results[0]);
			agent.setMoney(agent.getMoney() + benefit);
		}

	}

	public boolean done() {

		return false;
	}

}
