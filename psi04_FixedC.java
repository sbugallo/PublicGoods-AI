import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/**This class defines the agent that will play
 * as a fixed cooperator */
@SuppressWarnings("serial")
public class psi04_FixedC extends Agent {

	//Agent ID
	private int id;
	
	//Game parameters
	private int numberOfPlayers;
	private double money;
	private double moneyPut;
	private double interestFactor;
	private double fineFactor;
	private int maxInspectors;
	private int round = 0;
	private int numberOfRounds;

	public void setup() {
		
		//We print that we are ready
		System.out.println("[Coop]: Hi! Im " + getLocalName() + " and Im a Fixed Cooperator");
		
		//We add the behaviour
		this.addBehaviour(new FixedCooperatorBehaviour(this));

		//We register the service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Player");
		sd.setName("COOP");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
			//We print that we registered successfully
			System.out.println("[Coop]: Registered!");
		} catch (FIPAException fe) {
			//We print that we were not able to register
			fe.printStackTrace();
			System.out.println("[Coop]: Not registered!");
		}
	}

	protected void takeDown() {
		//We deregister from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		//We print a dismissal message
		System.out.println("[Coop]: terminating...");
	}

	// ############# GETTERS AND SETTERS #############

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

}
