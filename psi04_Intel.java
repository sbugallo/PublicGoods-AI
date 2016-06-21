import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class psi04_Intel extends Agent {

	// Agent ID
	private int id;

	// Game parameters
	private int numberOfPlayers;
	private double money;
	private double moneyPut;
	private double interestFactor;
	private double fineFactor;
	private int maxInspectors;
	private int round;
	private int numberOfRounds;

	// Agent parameters
	private double coopProfitableness;
	private double defProfitableness;
	private double insProfitableness;
	private double overallNi;
	private double overallNc;
	private double overallNd;
	private int niLastRound;
	private int ncLastRound;
	private int ndLastRound;
	private int risk;
	private double gameEvolution;
	private int coopRoles;
	private int defRoles;
	private int insRoles;
	private int timesCaught;
	private int playersCaught;
	private double benefitLastRound;
	private int roleLastRound;
	
	public void setup() {

		// We print that we are ready
		System.out.println("[SergioBugallo]: Hi! Im " + getLocalName() + " and Im an Intelligent Player programmed by Sergio Bugallo");

		// We add the behaviour
		this.addBehaviour(new psi04_IntelBehaviour(this));

		// We register the service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Player");
		sd.setName("Intel");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
			// We print that we registered successfully
			System.out.println("[SergioBugallo]: Registered!");
		} catch (FIPAException fe) {
			// We print that we were not able to register
			fe.printStackTrace();
			System.out.println("[SergioBugallo]: Not registered!");
		}
	}

	protected void takeDown() {
		// We deregister from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// We print a dismissal message
		System.out.println("[SergioBugallo]: terminating...");
	}
	
	
	// This function initializes the parameters of the agent
	public void setUp(){
		round=1;
		coopProfitableness=0;
		defProfitableness=0;
		insProfitableness=0;
		overallNi=0;
		overallNc=0;
		overallNd=0;
		niLastRound=0;
		ncLastRound=0;
		ndLastRound=0;
		gameEvolution=0;
		coopRoles=0;
		defRoles=0;
		insRoles=0;
		timesCaught=0;
		playersCaught=0;
		benefitLastRound=0;
		risk=3;
	}

// ####################### GETTERS & SETTERS #######################
	
	// This function returns the average benefit of a cooperator
	public double getCoopBenefit() {
		return (moneyPut * (overallNc+1) * (interestFactor) / (overallNd + overallNc +1)) - moneyPut;
	}

	// This function returns the average benefit of a defector
	public double getDefBenefit() {
		return (moneyPut * overallNc * (interestFactor) / (overallNd + overallNc+1));
	}
	
	// This function returns the average benefit of an inspector 
	public double getInsBenefit() {
		return ((fineFactor) * (moneyPut * overallNc * (interestFactor) / (overallNd + overallNc-1)));
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getMoneyPut() {
		return moneyPut;
	}

	public void setMoneyPut(double moneyPut) {
		this.moneyPut = moneyPut;
	}

	public double getInterestFactor() {
		return interestFactor;
	}

	public void setInterestFactor(double interestFactor) {
		this.interestFactor = interestFactor;
	}

	public double getFineFactor() {
		return fineFactor;
	}

	public void setFineFactor(double fineFactor) {
		this.fineFactor = fineFactor;
	}

	public int getMaxInspectors() {
		return maxInspectors;
	}

	public void setMaxInspectors(int maxInspectors) {
		this.maxInspectors = maxInspectors;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getNumberOfRounds() {
		return numberOfRounds;
	}

	public void setNumberOfRounds(int numberOfRounds) {
		this.numberOfRounds = numberOfRounds;
	}

	public double getCoopProfitableness() {
		return coopProfitableness;
	}

	public void setCoopProfitableness(double coopProfitableness) {
		this.coopProfitableness = coopProfitableness;
	}

	public double getDefProfitableness() {
		return defProfitableness;
	}

	public void setDefProfitableness(double defProfitableness) {
		this.defProfitableness = defProfitableness;
	}

	public double getInsProfitableness() {
		return insProfitableness;
	}

	public void setInvProfitableness(double invProfitableness) {
		this.insProfitableness = invProfitableness;
	}

	public double getOverallNi() {
		return overallNi;
	}

	public void setOverallNi(double overallNi) {
		this.overallNi = overallNi;
	}

	public double getOverallNc() {
		return overallNc;
	}

	public void setOverallNc(double overallNc) {
		this.overallNc = overallNc;
	}

	public double getOverallNd() {
		return overallNd;
	}

	public void setOverallNd(double overallNd) {
		this.overallNd = overallNd;
	}

	public int getNiLastRound() {
		return niLastRound;
	}

	public void setNiLastRound(int niLastRound) {
		this.niLastRound = niLastRound;
		this.overallNi=(double)((overallNi*(round-1)+niLastRound)/round);
	}

	public int getNcLastRound() {
		return ncLastRound;
	}

	public void setNcLastRound(int ncLastRound) {
		this.ncLastRound = ncLastRound;
		this.overallNc=(double)((overallNc*(round-1)+ncLastRound)/round);
	}

	public int getNdLastRound() {
		return ndLastRound;
	}

	public void setNdLastRound(int ndLastRound) {
		this.ndLastRound = ndLastRound;
		this.overallNd=(double)((overallNd*(round-1)+ndLastRound)/round);
	}

	public int getRisk() {
		return risk;
	}

	public void setRisk(int risk) {
		this.risk = risk;
	}

	public double getGameEvolution() {
		return gameEvolution;
	}

	public void setGameEvolution(double gameEvolution) {
		this.gameEvolution = gameEvolution;
	}

	public int getCoopRoles() {
		return coopRoles;
	}

	public void setCoopRoles(int coopRoles) {
		this.coopRoles = coopRoles;
	}

	public int getDefRoles() {
		return defRoles;
	}

	public void setDefRoles(int defRoles) {
		this.defRoles = defRoles;
	}

	public int getInsRoles() {
		return insRoles;
	}

	public void setInsRoles(int insRoles) {
		this.insRoles = insRoles;
	}

	public int getTimesCaught() {
		return timesCaught;
	}

	public void setTimesCaught(int timesCaught) {
		this.timesCaught = timesCaught;
	}

	public int getPlayersCaught() {
		return playersCaught;
	}

	public void setPlayersCaught(int playersCaught) {
		this.playersCaught = playersCaught;
	}

	public double getBenefitLastRound() {
		return benefitLastRound;
	}

	public void setBenefitLastRound(double benefitLastRound) {
		this.benefitLastRound = benefitLastRound;
		
		// We update the stats
		switch(roleLastRound){
		case 1: if(benefitLastRound>0){
					coopProfitableness++;
					gameEvolution++;
				}
				else if(benefitLastRound<0){
					coopProfitableness--;
				}
				break;
				
		case 2: if(benefitLastRound>0){
					defProfitableness++;
					gameEvolution++;
				}
				else if(benefitLastRound<0){
					defProfitableness--;
					timesCaught++;
				}
				break;
				
		case 3: if(benefitLastRound>0){
					insProfitableness++;
					playersCaught++;
					gameEvolution++;
				}
				else{
					insProfitableness--;
				}
				break;
		}
		
		// We update the performance and the risk
		double performance = (gameEvolution/round);
		
		if(performance>0.8){
			setRisk(1);
		} else{
			if(performance>0.2) setRisk(2);
			else setRisk(3);
		}
	}
	
	public int getRoleLastRound() {
		return roleLastRound;
	}

	public void setRoleLastRound(int roleLastRound) {
		this.roleLastRound = roleLastRound;
		
		switch(roleLastRound){
		case 1: coopRoles++;
			break;
		case 2: defRoles++;
			break;
		case 3: insRoles++;
			break;
		}
	}
}


/**
 * This class defines the behaviour of the intelligent player
 */
@SuppressWarnings("serial")
class psi04_IntelBehaviour extends Behaviour {

	psi04_Intel agent;
	int nextRole;
	int lastCrosshair;
	
	// [Player | Times Inspected | Times Caught]
	List<int[]> defectorList = new ArrayList<int[]>();

	// [Role | Profitableness]
	List<int[]> rolesProfitableness = new ArrayList<int[]>();

	// [Role | Max Benefit]
	List<int[]> rolesMaxBenefit = new ArrayList<int[]>();

	// [Role | Max Benefit]
	List<Object[]> rolesRatio = new ArrayList<Object[]>();

	public psi04_IntelBehaviour(psi04_Intel agent) {
		super(agent);
		this.agent = agent;
		agent.setRisk(2);
	}

	public void action() {

		// We wait for the messages
		ACLMessage receivedMessage = agent.blockingReceive();
		String messageContent = receivedMessage.getContent();

		// We obtain the fields
		String[] messageFields = messageContent.split("#");
		ACLMessage replyMessage = receivedMessage.createReply();

		// We look for the type of the message (NewGame,NewRound,...)
		if (messageFields[0].trim().equals("Id")) {
			agent.setId(Integer.parseInt(messageFields[1].trim()));

			// We get the parameters
			String[] parameters = messageFields[2].split(",");

			// We save them into the agents fields
			agent.setNumberOfPlayers(Integer.parseInt(parameters[0].trim()));
			agent.setMoney(Double.parseDouble(parameters[1].trim()));
			agent.setMoneyPut(Double.parseDouble(parameters[2].trim()));
			agent.setInterestFactor(Double.parseDouble(parameters[3].trim()));
			agent.setFineFactor(Double.parseDouble(parameters[4].trim()));
			agent.setMaxInspectors(Integer.parseInt(parameters[5].trim()));
			agent.setNumberOfRounds(Integer.parseInt(parameters[6].trim()));

			// We create a list with all the players
			for (int i = 0; i < agent.getNumberOfPlayers(); i++) {

				if (i != agent.getId()) {
					int[] defector = { i, 0, 0 };
					defectorList.add(defector);
				}
			}

			// We set up the agent
			agent.setUp();

		} else if (messageFields[0].trim().equals("NewRound")) {

			// We update the profitableness of the roles
			rolesProfitableness = new ArrayList<int[]>();

			int[] coopProfitableness = { 1, (int) agent.getCoopProfitableness() };
			int[] defProfitableness = { 1, (int) agent.getDefProfitableness() };
			int[] insProfitableness = { 1, (int) agent.getInsProfitableness() };

			rolesProfitableness.add(coopProfitableness);
			rolesProfitableness.add(defProfitableness);
			rolesProfitableness.add(insProfitableness);

			// We update the maximum benefit of the roles
			rolesMaxBenefit = new ArrayList<int[]>();
			int[] coopMaxBenefit = { 1, (int) (agent.getCoopBenefit() * 10) };
			int[] defMaxBenefit = { 2, (int) (agent.getDefBenefit() * 10) };
			int[] insMaxBenefit = { 3, (int) (agent.getInsBenefit() * 10) };
			rolesMaxBenefit.add(coopMaxBenefit);
			rolesMaxBenefit.add(defMaxBenefit);
			rolesMaxBenefit.add(insMaxBenefit);

			// We update the ratio Wins/Times Played of the roles
			rolesRatio = new ArrayList<Object[]>();
			Object[] coopRatio = new Object[2];
			Object[] defRatio = new Object[2];
			Object[] insRatio = new Object[2];
			if (agent.getCoopRoles() == 0) {
				coopRatio[0] = (Integer) 1;
				coopRatio[1] = (Double) 0.0;
			} else {
				coopRatio[0] = (Integer) 1;
				coopRatio[1] = (Double) (agent.getCoopProfitableness() / agent.getCoopRoles());
			}
			if (agent.getDefRoles() == 0) {
				defRatio[0] = (Integer) 2;
				defRatio[1] = (Double) 0.0;
			} else {
				defRatio[0] = (Integer) 2;
				defRatio[1] = (Double) (agent.getDefProfitableness() / agent.getDefRoles());
			}
			if (agent.getInsRoles() == 0) {
				insRatio[0] = (Integer) 3;
				insRatio[1] = (Double) 0.0;
			} else {
				insRatio[0] = (Integer) 3;
				insRatio[1] = (Double) (agent.getInsProfitableness() / agent.getInsRoles());
			}
			rolesRatio.add(coopRatio);
			rolesRatio.add(defRatio);
			rolesRatio.add(insRatio);

			// We calculate the nex role
			nextRole = getNextRole();
			agent.setRoleLastRound(nextRole);

		} else if (messageFields[0].trim().equals("Inspector")) {
			/*
			 * We answer the main agent that we are going/not going to play as
			 * an inspector
			 */

			if (nextRole != 3) {
				replyMessage.setContent("Inspector#No");
				replyMessage.setPerformative(ACLMessage.INFORM);
				agent.send(replyMessage);
			} else {
				replyMessage.setContent("Inspector#Yes");
				replyMessage.setPerformative(ACLMessage.INFORM);
				agent.send(replyMessage);
			}

		} else if (messageFields[0].trim().equals("Role")) {

			// We answer the main agent what role will we play

			if (nextRole == 1)
				replyMessage.setContent("MyRole#C");
			else {

				// If we generate a 2, we will play as a defector
				if (nextRole == 2)
					replyMessage.setContent("MyRole#D");
				else

				// If we generate a 3, we will play as an inspector
				if (nextRole == 3) {
					String[] inspectors = new String[agent.getNumberOfPlayers()];

					// We get the inspectors IDs
					try {
						inspectors = messageFields[1].trim().split(",");
					} catch (IndexOutOfBoundsException e) {
					}

					// We calculate wich player we will inspect
					int crosshair = getCrosshair(inspectors);
					lastCrosshair = crosshair;
					
					// We update the defector list with the new stats
					for (int i = 0; i < defectorList.size(); i++) {

						// [Player | Times Inspected | Times Caught]
						int[] defector = defectorList.get(i);
						if (defector[0] == lastCrosshair) {
							defector[1]++;
							defectorList.set(i, defector);
							break;
						}
					}

					// We answer the main agent
					replyMessage.setContent("MyRole#I#" + crosshair);
				}
			}

			// We answer the main agent
			replyMessage.setPerformative(ACLMessage.INFORM);
			agent.send(replyMessage);
		} else if (messageFields[0].trim().equals("Benefit")) {

			// We update the round
			agent.setRound(agent.getRound() + 1);
			
			// We get the benefit
			String[] results = messageFields[1].split("#");
			Double benefit = Double.parseDouble(results[0].trim());

			// We update the player stats
			agent.setMoney(agent.getMoney() + benefit);
			agent.setBenefitLastRound(benefit);

			String[] lastRoles = messageFields[2].trim().split(",");

			agent.setNcLastRound(Integer.parseInt(lastRoles[0]));
			agent.setNdLastRound(Integer.parseInt(lastRoles[1]));
			agent.setNiLastRound(Integer.parseInt(lastRoles[2]));

			// We update the defector list with the new stats
			if (nextRole == 3 && benefit > 0) {
				for (int i = 0; i < defectorList.size(); i++) {

					// [Player | Times Inspected | Times Caught]
					int[] defector = defectorList.get(i);
					if (defector[0] == lastCrosshair) {
						defector[2]++;
						defectorList.set(i, defector);
						break;
					}
				}
			}

		}
	}

	/** 
	 * This function calculates the best player to inspect
	 * @param inspectors
	 * @return
	 */
	private int getCrosshair(String[] inspectors) {

		// First we will calculate the players that we caught the most
		List<int[]> positions = new ArrayList<int[]>();
		
		for (int i = 0; i < defectorList.size(); i++) {

			/*  Count will be the defector position
			 *  crosshair will be the defector
			 *  ratio will be the times caught/times inspected ratio
			 */
			int count = 0;
			int[] crosshair = defectorList.get(i);
			double ratio;

			// if we never caught the defector, ratio will be zero
			if (crosshair[1] == 0)
				ratio = 0;
			else
				ratio = crosshair[2] / crosshair[1];

			// We calculate the position of the player
			for (int j = 0; j < defectorList.size(); j++) {

				int[] newCrosshair = defectorList.get(j);
				double newRatio;

				if (newCrosshair[1] == 0)
					newRatio = 0;
				else
					newRatio = newCrosshair[2] / newCrosshair[1];

				/*  If the times caught/times inspected ratio is higher
				 *  and we caught the player at least twice as often
				 *  
				 *  or
				 *  
				 *  If the ratio the same but we caught him more times
				 *  
				 *  it means that this player defects less
				 */
				if ((ratio < newRatio && 2 * crosshair[1] > newCrosshair[1])
						|| (ratio == newRatio && crosshair[1] > newCrosshair[1]))
					count++;

			}

			// We add the player+position to the list
			int[] position = { crosshair[0], count };
			positions.add(position);

		}
		
		// We sort the list by the position
		Collections.sort(positions, new Comparator<int[]>() {

			public int compare(int[] def1, int[] def2) {
				return ((Integer) def1[1]).compareTo((Integer) def2[1]);
			}

		});

		// Now we will shuffle the players with the same position
		List<int[]> shuffledList = new ArrayList<int[]>();

		int bestPosition = positions.get(0)[1];

		int count=0;
		
		/*  We will repeat the process till we have at least one player to inspect
		 *  (maybe the best players to inspect play as inspectors this round)
		 */
		while(shuffledList.size()==0){
			
			// We iterate the list
			for (int w = 0; w < positions.size(); w++) {
				
				// Equal will indicate if the player is an inspector this round
				boolean equal = false;				
				
				/* If the player has the same position as the higher one,
				 * we will add him to the list
				 */
				if (positions.get(w)[1] == (bestPosition+count)) {
	
					// We check if the player is an inspector
					for (int j = 0; j < inspectors.length; j++) {
	
						int inspector = Integer.parseInt(inspectors[j]);
						
						/* If the player is an inspector this round, we 
						 * exclude him from the list
						 */
						if (positions.get(w)[0] == inspector)
							equal = true;
					}
	
					if (!equal) shuffledList.add(positions.get(w));
				}
			}
			
			// count will update the requirements
			count++;
		}

		// We randomly choose a player to inspect from the best options
		Random random = new Random();
		int crosshair = shuffledList.get((int) (random.nextDouble() * shuffledList.size()))[0];
		return crosshair;
	}


	/**
	 * This function will return the best role depending the risk: 
	 * RISK=1 -> most profitable role, 
	 * RISK=2 -> best profitableness/benefit role, 
	 * RISK=3 -> most beneficial role
	 * @return
	 */
	private int getNextRole() {

		int nextRole = 0;

		switch (agent.getRisk()) {

		case 1:
			nextRole = getSafeRole();
			break;

		case 2:
			nextRole = getNormalRole();
			break;

		case 3:
			nextRole = getRiskyRole();
			break;
		}

		return nextRole;
	}

	/**
	 * This function will return the most beneficial role
	 * @return
	 */
	private int getRiskyRole() {
		int role;

		// We sort the list by the benefit
		Collections.sort(rolesMaxBenefit, new Comparator<int[]>() {

			public int compare(int[] role1, int[] role2) {

				int comparation = ((Integer) role1[1]).compareTo((Integer) role2[1]);

				// We add priority to the defector role and the inspector role
				if (comparation == 0) {

					if ((Integer) role1[0] == 2)
						return 1;
					else if ((Integer) role2[0] == 2)
						return -1;
					else if ((Integer) role1[0] == 3 && (Integer) role2[0] == 3)
						return 0;
					else if ((Integer) role1[0] == 3 && (Integer) role2[0] == 1)
						return 1;
					else if ((Integer) role1[0] == 1 && (Integer) role2[0] == 3)
						return -1;
					else if ((Integer) role1[0] == 1 && (Integer) role2[0] == 1)
						return 0;
					else
						return 0;

				} else
					return comparation;

			}

		});

		// [ROLE | BENEFIT]
		role = rolesMaxBenefit.get(2)[0];

		return role;
	}

	/**
	 * This function will calculate the role with the best ratio
	 * @return
	 */
	private int getNormalRole() {
		int role;

		// We sort the list
		Collections.sort(rolesRatio, new Comparator<Object[]>() {

			public int compare(Object[] role1, Object[] role2) {

				int comparation = ((Double) role1[1]).compareTo((Double) role2[1]);

				// We add priority to the defector and inspector roles
				if (comparation == 0) {

					if ((Integer) role1[0] == 2)
						return 1;
					else if ((Integer) role2[0] == 2)
						return -1;
					else if ((Integer) role1[0] == 3 && (Integer) role2[0] == 3)
						return 0;
					else if ((Integer) role1[0] == 3 && (Integer) role2[0] == 1)
						return 1;
					else if ((Integer) role1[0] == 1 && (Integer) role2[0] == 3)
						return -1;
					else if ((Integer) role1[0] == 1 && (Integer) role2[0] == 1)
						return 0;
					else
						return 0;

				} else
					return comparation;

			}

		});

		role = (Integer) rolesRatio.get(2)[0];

		return role;
	}

	/**
	 * This function will calculate the role with the most
	 * profitableness
	 * 
	 * @return
	 */
	private int getSafeRole() {

		int role;

		// We sort the list by the profitableness
		Collections.sort(rolesProfitableness, new Comparator<int[]>() {

			public int compare(int[] role1, int[] role2) {

				int comparation = ((Integer) role1[1]).compareTo((Integer) role2[1]);

				// We add priority to the defector and inspector roles
				if (comparation == 0) {

					if ((Integer) role1[0] == 2)
						return 1;
					else if ((Integer) role2[0] == 2)
						return -1;
					else if ((Integer) role1[0] == 3 && (Integer) role2[0] == 3)
						return 0;
					else if ((Integer) role1[0] == 3 && (Integer) role2[0] == 1)
						return 1;
					else if ((Integer) role1[0] == 1 && (Integer) role2[0] == 3)
						return -1;
					else if ((Integer) role1[0] == 1 && (Integer) role2[0] == 1)
						return 0;
					else
						return 0;

				} else
					return comparation;

			}

		});

		role = rolesProfitableness.get(2)[0];

		return role;
	}

	public boolean done() {

		return false;
	}
}
