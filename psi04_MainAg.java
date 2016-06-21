import java.util.ArrayList;
import java.util.List;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;


/**This class defines the Main Agent
 * that will coordinate the game*/
@SuppressWarnings("serial")
public class psi04_MainAg extends Agent {

	// Players DF Description
	private DFAgentDescription[] agents = null;

	// MainAgent DF Description
	private DFAgentDescription MADescription = null;

	/*Players Data
	* player = [ID,Money,Cooperator,Defector,Inspector,Caught,NotCaught,Players Caught,Descriptor]
	* moneyData = [Money of player0, Money of player1, ...]
	*/
	private static List<Object[]> players = new ArrayList<Object[]>();
	private List<Object[]> overallMoneyData = new ArrayList<Object[]>();
	private boolean restartGame = false;
	
	//Game Parameters
	private int moneyPut = 1;
	private int initialMoney = 100;
	private int interestFactor = 120;
	private int fineFactor = 30;
	private int maxInspectors = 5;
	private int numberOfRounds = 10;
	private int round = 1;
	private boolean verbose = true;
	private boolean paused = false;

	// GUI
	private psi04_MainWindow mainWindow;
	private String log = "";
	private int comboBoxIndex=0;

	public void setup() {

		//We print that we are ready
		System.out.println("[MA]: Hi! Im " + getLocalName() + " and Im the Main Agent");

		// We get the MainAgent Description
		MADescription = new DFAgentDescription();
		MADescription.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("MainAgent");
		sd.setName("MA");
		MADescription.addServices(sd);
		try {
			//We print that we registered successfully
			DFService.register(this, MADescription);
			System.out.println("[MA]: Registered!");
		} catch (FIPAException fe) {
			//We print that we were not able to register
			fe.printStackTrace();
			System.out.println("[MA]: Not registered!");
		}

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// We get the Players Description
		DFAgentDescription template = new DFAgentDescription();
		sd = new ServiceDescription();
		sd.setType("Player");
		template.addServices(sd);
		try {
			agents = DFService.search(this, template);
			System.out.println("[MA]: I have found " + agents.length + " agents");

			int id = 0;

			// We add the Players to the Player List 'players'
			for (int i = 0; i < agents.length; i++) {

				//We print the player we have found
				System.out.println("[MA]: agent " + i + "> " + agents[i].getName());
				
				//We add a new entry for every player
				Object[] player = { id++, (double) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int) 0,
						(String) agents[i].getName().getLocalName(), agents[i] };
				players.add(player);
			}
			
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		//We launch the GUI
		System.out.println("[MA]: Launching GUI...");

		mainWindow = new psi04_MainWindow("Public Goods Game", players, this);
		mainWindow.setVisible(true);

		//We add the behaviour
		addBehaviour(new psi04_MainAgBehaviour(this));

	}

	protected void takeDown() {
		//We deregister from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		//We print a dismissal message
		System.out.println("[MA]: terminating...");
	}

	// ############# GETTERS AND SETTERS #############
	
	public DFAgentDescription[] getAgents() {
		return agents;
	}

	public void setAgents(DFAgentDescription[] agents) {
		this.agents = agents;
	}

	public static List<Object[]> getPlayers() {
		return players;
	}

	public static void setPlayers(List<Object[]> players) {
		psi04_MainAg.players = players;
	}

	public int getMoneyPut() {
		return moneyPut;
	}

	public void setMoneyPut(int moneyPut) {
		this.moneyPut = moneyPut;
	}

	public int getInitialMoney() {
		return initialMoney;
	}

	public void setInitialMoney(int initialMoney) {
		this.initialMoney = initialMoney;
	}

	public int getInterestFactor() {
		return interestFactor;
	}

	public void setInterestFactor(int interestFactor) {
		this.interestFactor = interestFactor;
	}

	public int getFineFactor() {
		return fineFactor;
	}

	public void setFineFactor(int fineFactor) {
		this.fineFactor = fineFactor;
	}

	public int getMaxInspectors() {
		return maxInspectors;
	}

	public void setMaxInspectors(int maxInspectors) {
		this.maxInspectors = maxInspectors;
	}

	public int getNumberOfRounds() {
		return numberOfRounds;
	}

	public void setNumberOfRounds(int numberOfRounds) {
		this.numberOfRounds = numberOfRounds;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public psi04_MainWindow getMainWindow() {
		return mainWindow;
	}

	public void setMainWindow(psi04_MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public DFAgentDescription getMADescription() {
		return MADescription;
	}

	public void setMADescription(DFAgentDescription mADescription) {
		MADescription = mADescription;
	}

	public List<Object[]> getOverallMoneyData() {
		return overallMoneyData;
	}

	public void setOverallMoneyData(List<Object[]> overallMoneyData) {
		this.overallMoneyData = overallMoneyData;

	}
	
	public int getComboBoxIndex() {
		return comboBoxIndex;
	}

	public void setComboBoxIndex(int comboBoxIndex) {
		this.comboBoxIndex = comboBoxIndex;
	}

	public boolean isRestartGame() {
		return restartGame;
	}

	public void setRestartGame(boolean restartGame) {
		this.restartGame = restartGame;
	}

}
