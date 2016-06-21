import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
/**This class defines the behaviour of the Main ASgent*/
public class psi04_MainAgBehaviour extends Behaviour {

	//Main Agent
	psi04_MainAg agent;

	public psi04_MainAgBehaviour(psi04_MainAg agent) {
		super(agent);
		this.agent = agent;
	}

	//Here we implement the behavior itself
	@SuppressWarnings({ "static-access", "deprecation" })
	public void action() {

		// We define the window, players, money stats and log
		psi04_MainWindow mainWindow = agent.getMainWindow();
		List<Object[]> players = agent.getPlayers();
		List<Object[]> overallMoneyData = agent.getOverallMoneyData();
		FileWriter fichero = null;
		PrintWriter pw = null;
		String logName;
		
		try {
			

			//=================== NEW GAME PHASE ===================

			
			/* With this variable we prevent the game from
			 * starting till the 'New Game' button is pressed */
			boolean doNotStartGame = true;

			//We wait for a New Game message
			while (doNotStartGame && !agent.isRestartGame()) {
				
				//We enable the new game button
				mainWindow.getLpNewGameButton().setEnabled(true);
				mainWindow.getCpPauseButton().setEnabled(false);
				mainWindow.getCpResumeButton().setEnabled(false);
				//We wait for the message
				ACLMessage startGameMessage = agent.blockingReceive();
				//If the message content is New Game, we get out of the loop 
				String startGameMessageContent = startGameMessage.getContent();
				if (startGameMessageContent.equals("NewGame"))
					doNotStartGame = false;
			}
			agent.setRestartGame(false);
			/* We have to disable the New Game button to prevent the user from starting a
			new game meanwhile there is another one running */
			mainWindow.getLpNewGameButton().setEnabled(false);
			
			// We have to enable the Pause Game button
			mainWindow.getCpPauseButton().setEnabled(true);
			
			//We set the log with name format: log_year_month_day_hour_minute_second
			GregorianCalendar calendar = new GregorianCalendar();
			logName = "log_"+calendar.get(Calendar.YEAR)+"_"+(calendar.get(Calendar.MONTH)+1)+"_"+calendar.get(Calendar.DAY_OF_MONTH)+"_"+calendar.get(Calendar.HOUR_OF_DAY)+"_"+calendar.get(Calendar.MINUTE)+"_"+calendar.get(Calendar.SECOND)+".txt";
			File file = new File(logName);
			file.createNewFile();				    	
			fichero = new FileWriter(file);
			pw = new PrintWriter(fichero);

			//The first thing to do is delete the previous games data
			resetData();

			//And we set the preferences chosen by the user
			agent.setInitialMoney(Integer.valueOf(mainWindow.getTextInitialMoney().getText()));
			agent.setMoneyPut(Integer.valueOf(mainWindow.getTextMoneyPut().getText()));
			agent.setInterestFactor(Integer.valueOf(mainWindow.getTextInterestFactor().getText()));
			agent.setFineFactor(Integer.valueOf(mainWindow.getTextFineFactor().getText()));
			agent.setMaxInspectors(Integer.valueOf(mainWindow.getTextMaxInspectors().getText()));
			agent.setNumberOfRounds(Integer.valueOf(mainWindow.getTextNumberOfRounds().getText()));

			mainWindow.getCpProgressBar().setMaximum(agent.getNumberOfRounds());
			mainWindow.getCpProgressBar().setMinimum(0);

			//We update the log
			pw.println("\n\n>> Initilizing Game");

			/*With these instructions, we update the game messages on the
			game window*/
			String log = agent.getLog();
			if (agent.isVerbose()) {
				log = agent.getLog();
				agent.setLog("> Initializing Game.\n> " + players.size() + " players found.");
				mainWindow.getCpGameInfo().setText(agent.getLog());
			}
			
			//We send the ID messages to every player
			for (int i = 0; i < players.size(); i++) {
				
				ACLMessage message = new ACLMessage();
				
				//Message format: [ID#PlayerID#NumberOfPlayers,InitialMoney,MoneyPut,InterestFactor,FineFactor,MaxInspector,NumberOfRounds]
				message.setContent("Id#" + players.get(i)[0] + "#" + players.size() + "," + agent.getInitialMoney()
						+ "," + agent.getMoneyPut() + "," + ((double)agent.getInterestFactor()/100) + "," +((double)agent.getFineFactor()/100)
						+ "," + agent.getMaxInspectors() + "," + agent.getNumberOfRounds());
				message.setPerformative(ACLMessage.INFORM);
				message.addReceiver(((DFAgentDescription) players.get(i)[9]).getName());
				agent.send(message);
				
				//We update the log
				pw.println(">> Sended initialization message to " + ((DFAgentDescription) players.get(i)[9]).getName());
				pw.println(message);
			}
			
			//We update the game window messages
			if (agent.isVerbose()) {
				log = agent.getLog();
				agent.setLog(log + "\n> Initialization messages sended to the players.");
				mainWindow.getCpGameInfo().setText(agent.getLog());
			}
			
			// We set the round to zero
			int round = 0;
			agent.setRound(round);

			// We do a loop to play all the rounds
			for (agent.getRound(); agent.getRound() < agent.getNumberOfRounds(); round++) {

				//We do this in order to be able to play only 1 round
				if(round==0) round++;
				
				//=================== NEW ROUND PHASE ===================
				
				//We wait for the new round message
				
				ACLMessage pauseMessage=new ACLMessage();
				
				while (agent.isPaused() == true) {
					pauseMessage = agent.blockingReceive();
				}

				//We check if the user wants to restart the game
				if(pauseMessage.getContent() != null){
					if(pauseMessage.getContent().equals("NewGame")){
						break;
					}
				}
				
				/* We initialize an array where we will save the money data
				 * for this round */
				Object[] actualRoundMoney = new Object[players.size()];
				
				//We update the log
				pw.println("\n\n>> Starting Round " + round);
				agent.setRound(round);
				
				//We update the Round text label in the game window
				mainWindow.getTextRound().setText(String.valueOf(agent.getRound()));

				//We update the progress bar
				mainWindow.getCpProgressBar().setValue(round);

				//We update the game window messages
				if (agent.isVerbose()) {
					log = agent.getLog();
					agent.setLog(log + "\n> Starting round " + round + ".");
					mainWindow.getCpGameInfo().setText(agent.getLog());
				}
				
				// We send the New Round message to the players
				for (int i = 0; i < players.size(); i++) {
					ACLMessage message = new ACLMessage();
					message.setContent("NewRound");
					message.setPerformative(ACLMessage.INFORM);
					message.addReceiver(((DFAgentDescription) players.get(i)[9]).getName());
					agent.send(message);

					//We update the log
					pw.println(">> Sended new round message to " + ((DFAgentDescription) players.get(i)[9]).getName());
					pw.print(message);
				}

				//We update the game window messages
				if (agent.isVerbose()) {
					log = agent.getLog();
					agent.setLog(log + "\n> New round messages sended to the players.");
					mainWindow.getCpGameInfo().setText(agent.getLog());
				}
				
				//=================== INSPECTOR PHASE ===================
				
				//We update the log
				pw.println("\n\nStarting Inspector phase");
				int numInspectors = 0;

				//We will save the roles data received into this variables
				List<Object[]> inspectors = new ArrayList<Object[]>();
				List<Object[]> otherRoles = new ArrayList<Object[]>();
				String investigatorsID = "";

				/*We will repeat the next block till the number of inspectors
				 is less or equal the maximum permitted 
				 */
				do {

					/* We ask the players if they are going to play as
					 Inspectors this round*/
					for (int i = 0; i < players.size(); i++) {
						ACLMessage message = new ACLMessage();
						message.setContent("Inspector");
						message.setPerformative(ACLMessage.REQUEST);
						message.addReceiver(((DFAgentDescription) players.get(i)[9]).getName());
						agent.send(message);

						//We update the log
						pw.println(
								">> Sended inspector message to " + ((DFAgentDescription) players.get(i)[9]).getName());
						pw.print(message);
					}

					//We update the game window messages
					if (agent.isVerbose()) {
						log = agent.getLog();
						agent.setLog(log + "\n> Asking for inspectors.");
						mainWindow.getCpGameInfo().setText(agent.getLog());
					}

					
					/*We read the message of each player and we look if they
					are inspectors*/
					for (int i = 0; i < players.size(); i++) {
						ACLMessage message = agent.blockingReceive();

						/*If the sender is an inspector, we save the message and the 
						 sender and we increment the number of inspectors 
						 */
						if (checkInspectorReply(message)) {
							Object[] inspector = { message, message.getSender() };
							inspectors.add(inspector);
							numInspectors++;

							//We update the game window messages
							if (agent.isVerbose()) {
								log = agent.getLog();
								agent.setLog(
										log + "\n> " + message.getSender().getLocalName() + " will be an Inspector.");
								mainWindow.getCpGameInfo().setText(agent.getLog());
							}

							//We update the log
							pw.println(">> Received Inspector reply from "
									+ ((DFAgentDescription) players.get(i)[9]).getName());
							pw.print(message);

						} else {
							
							/*If the sender its not an inspector, we save the message and
							 the sender for replying later.
							 */
							pw.println(">> Received Not-Inspector reply from "
									+ ((DFAgentDescription) players.get(i)[9]).getName());
							pw.print(message);

							Object[] other = { message, message.getSender() };
							otherRoles.add(other);
						}
					}

					// We write the inspectors IDs
					for (int i = 0; i < inspectors.size(); i++) {
						
						/*We check if this iteration is the last one so we
						don't add the ',' */
						if (i == inspectors.size() - 1)
							investigatorsID = investigatorsID + getIDfromAID((AID) inspectors.get(i)[1]);
						else
							investigatorsID = investigatorsID + getIDfromAID((AID) inspectors.get(i)[1]) + ",";
					}

				//We check if the number of inspectors is bigger than the maximum
				} while (numInspectors > agent.getMaxInspectors());
				
				
				//=================== ROLE PHASE ===================
				
				//We update the log
				pw.println("\n\n>> Starting Role Phase");

				/* We ask all the Inspectors which players they will
				inspect */
				for (int i = 0; i < inspectors.size(); i++) {
					ACLMessage message = ((ACLMessage) inspectors.get(i)[0]).createReply();
					message.setContent("Role#" + investigatorsID);
					message.setPerformative(ACLMessage.REQUEST);
					agent.send(message);

					//We update the log
					pw.println(
							">> Sended Inspector Role message to " + ((ACLMessage) inspectors.get(i)[0]).getSender());
					pw.print(message);
				}

				//We ask the other players which roles they will be playing
				for (int i = 0; i < otherRoles.size(); i++) {
					ACLMessage message = ((ACLMessage) otherRoles.get(i)[0]).createReply();
					message.setContent("Role#");
					message.setPerformative(ACLMessage.REQUEST);
					agent.send(message);

					//We update the log
					pw.println(">> Sended Not-Inspector Role message to "
							+ ((ACLMessage) otherRoles.get(i)[0]).getSender());
					pw.print(message);
				}

				//We update the game window messages
				if (agent.isVerbose()) {
					log = agent.getLog();
					agent.setLog(log + "\n> Roles messages sended.");
					mainWindow.getCpGameInfo().setText(agent.getLog());
				}

				//=================== BENEFIT PHASE ===================
				
				//We update the log
				pw.println("\n\n>> Starting Benefit Phase");

				//We will save the data in this variables
				List<Object[]> playersRoles = new ArrayList<Object[]>();
				int Nc = 0;
				int Nd = 0;
				int Ni = 0;

				//We read the role messages
				for (int i = 0; i < players.size(); i++) {
					ACLMessage message = agent.blockingReceive();

					//We update the log
					pw.println(">> Received role message from " + message.getSender());
					pw.print(message);

					//We get the sender ID
					int id = getIDfromAID(message.getSender());

					//We get the message fields
					String[] messageFields = message.getContent().split("#");

					/* We check the role and set the role variable. Then 
					 * we update the number of players that will play
					 * that role and we save the id, role and message */
					
					if (messageFields[1].equals("C")) {
						int role = 1;
						Nc++;
						Object[] playerRole = { id, role, message };
						playersRoles.add(playerRole);
						
					} else if (messageFields[1].equals("D")) {
						int role = 2;
						;
						Nd++;
						Object[] playerRole = { id, role, message };
						playersRoles.add(playerRole);
						
					} else if (messageFields[1].equals("I")) {
						int role = 3;
						Ni++;
						
						/*If the player is an inspector we will also save
						the player it will inspect*/
						
						int crosshair = Integer.valueOf(messageFields[2]);
						Object[] playerRole = { id, role, crosshair, message };
						playersRoles.add(playerRole);
					}

				}
				
				//We update the game window messages
				if (agent.isVerbose()) {
					log = agent.getLog();
					agent.setLog(log + "\n> In this round there were:\n\t" + Nc + " Cooperators,\n\t" + Nd
							+ " Defectors,\n\t" + Ni + " Investigators.");
					mainWindow.getCpGameInfo().setText(agent.getLog());
				}

				//We will save the data in these variables
				Object[] player;
				List<Object[]> actualRound = new ArrayList<Object[]>();
				ACLMessage message;

				//Here we save the players that were investigated
				List<int[]> investigated = getInvestigatedPlayers(playersRoles);

				//We will calculate the benefit for every player
				for (int i = 0; i < playersRoles.size(); i++) {
					
					double benefit = 0;
					boolean caught = false;
					
					/*First we will see its role
					 * COOPERATOR = 1
					 * DEFECTOR = 2
					 * INSPECTOR = 3
					 */
					switch ((Integer) playersRoles.get(i)[1]) {
					
					//Cooperator case
					case 1:
						
						//First we will calculate the benefit
						benefit = calculateCoopBenefit(Nc, Nd);
						
						//Second we round the benefit
						benefit = roundBenefit(benefit);
						
						/*Third we get the player and update it
						 * player[1] = money
						 * player[2] = times being a cooperator*/
						player = players.get((Integer) playersRoles.get(i)[0]);
						player[1] = (roundBenefit(Double.parseDouble(String.valueOf(player[1])) + benefit));
						player[2] = (Integer) player[2] + 1;
						actualRound.add(player);
						
						//We update the game window messages
						if (agent.isVerbose()) {
							log = agent.getLog();
							agent.setLog(log + "\n> Player " + playersRoles.get(i)[0] + " was a Cooperator and got "
									+ benefit + ".");
							mainWindow.getCpGameInfo().setText(agent.getLog());
						}

						//We send the benefit message
						message = ((ACLMessage) playersRoles.get(i)[2]).createReply();
						message.setContent("Benefit#" + benefit + "#" + Nc + "," + Nd + "," + Ni);
						message.setPerformative(ACLMessage.INFORM);
						agent.send(message);

						//We update the log
						pw.println(">> Sended Benefit message to " + ((ACLMessage) playersRoles.get(i)[2]).getSender());
						pw.print(message);

						break;

					//Defector case	
					case 2:
						
						//First we check if the player was caught
						caught = gotCaught((Integer) playersRoles.get(i)[0], playersRoles);
						
						//Second we calculate the benefit
						benefit = calculateDefBenefit(Nc, Nd, caught);
						
						//Third we round the benefit
						benefit = roundBenefit(benefit);
						
						/*Fourth we update the player
						 * player[1] = money
						 * player[3] = times being a defector
						 * player[5] = times caught
						 * player[6] = times not caught
						 */
						player = players.get((Integer) playersRoles.get(i)[0]);
						player[1] = (roundBenefit(Double.parseDouble(String.valueOf(player[1])) + benefit));
						player[3] = (Integer) player[3] + 1;
					
						if (caught)
							player[5] = (Integer) player[5] + 1;
						else
							player[6] = (Integer) player[6] + 1;
						
						actualRound.add(player);
						
						//We update the game window messages
						if (agent.isVerbose()) {
							log = agent.getLog();
							agent.setLog(log + "\n> Player " + playersRoles.get(i)[0] + " was a Defector and got "
									+ benefit + ".");
							mainWindow.getCpGameInfo().setText(agent.getLog());
						}

						//We send the benefit message
						message = ((ACLMessage) playersRoles.get(i)[2]).createReply();
						message.setContent("Benefit#" + benefit + "#" + Nc + "," + Nd + "," + Ni);
						message.setPerformative(ACLMessage.INFORM);
						agent.send(message);

						//We update the log
						pw.println(">> Sended Benefit message to " + ((ACLMessage) playersRoles.get(i)[2]).getSender());
						pw.print(message);

						break;

					//Inspector case	
					case 3:
						
						//First we check if the player caught someone
						caught = caughtSomeone((Integer) playersRoles.get(i)[2], playersRoles);
						
						//Second we check if other inspectors inspected the same player
						int count = 1;
						for (int j = 0; j < investigated.size(); j++) {
							if (investigated.get(j)[0] == (Integer) playersRoles.get(i)[2]) {
								count = investigated.get(j)[1];
								break;
							}

						}
						
						//Third we calculate the benefit
						benefit = calculateInvBenefit(Nc, Nd, caught, count);
						
						//Fourth we round the benefit
						benefit = roundBenefit(benefit);
						
						/*Fifth we update the player
						 * player[1] = money
						 * player[4] = times being an inspector
						 * player[7] = players caught
						 */
						player = players.get((Integer) playersRoles.get(i)[0]);
						player[1] = (roundBenefit(Double.parseDouble(String.valueOf(player[1])) + benefit));
						player[4] = (Integer) player[4] + 1;
						actualRound.add(player);
						if (caught)
							player[7] = (Integer) player[7] + 1;
						
						//We update the game window messages
						if (agent.isVerbose()) {
							log = agent.getLog();
							agent.setLog(log + "\n> Player " + playersRoles.get(i)[0] + " investigated "
									+ playersRoles.get(i)[2] + " and got " + benefit + ".");
							mainWindow.getCpGameInfo().setText(agent.getLog());
						}

						//We send the benefit message
						message = ((ACLMessage) playersRoles.get(i)[3]).createReply();
						message.setContent("Benefit#" + benefit + "#" + Nc + "," + Nd + "," + Ni);
						message.setPerformative(ACLMessage.INFORM);
						agent.send(message);

						//We update the log
						pw.println(">> Sended Benefit message to " + ((ACLMessage) playersRoles.get(i)[3]).getSender());
						pw.print(message);

						break;
					}
				}
				
				//We update the players data in the main agent
				agent.setPlayers(orderListByID(actualRound));
				players = orderListByID(actualRound);
				
				//We update the players data in the game window
				mainWindow.setPlayers(agent.getPlayers());

				// We update the stadistics of the players
				int index = mainWindow.getRpPlayerComboBox().getSelectedIndex();

				//We update the game window elements
				mainWindow.getTextMoney().setText(String.valueOf(agent.getPlayers().get(index)[1]));
				mainWindow.getTextCooperator().setText(String.valueOf(agent.getPlayers().get(index)[2]));
				mainWindow.getTextDefector().setText(String.valueOf(agent.getPlayers().get(index)[3]));
				mainWindow.getTextInspector().setText(String.valueOf(agent.getPlayers().get(index)[4]));
				mainWindow.getTextCaught().setText(String.valueOf(agent.getPlayers().get(index)[5]));
				mainWindow.getTextNotCaught().setText(String.valueOf(agent.getPlayers().get(index)[6]));
				mainWindow.getTextPlayersCaught().setText(String.valueOf(agent.getPlayers().get(index)[7]));
				mainWindow.getTextType().setText(String.valueOf(agent.getPlayers().get(index)[8]));

				//Here we will save the table data
				Object[][] horizontalTableData = new Object[2][players.size()];
				String[] horizontalTableColumns = new String[players.size()];

				//We update the table data
				for (int i = 0; i < actualRound.size(); i++) {
					actualRoundMoney[Integer.parseInt(String.valueOf(orderListByID(actualRound).get(i)[0]))] = Double.parseDouble(String.valueOf(orderListByID(actualRound).get(i)[1]));
					horizontalTableData[0][i] = orderListByID(actualRound).get(i)[1];
					horizontalTableData[1][i] = orderListByID(actualRound).get(i)[8];
					horizontalTableColumns[i] = "Player " + orderListByID(actualRound).get(i)[0];
				}
				
				//We update the money data
				overallMoneyData.add(actualRoundMoney);
				
				//We update the money data in the main agent and the game window
				agent.setOverallMoneyData(overallMoneyData);
				mainWindow.setOverallMoneyData(overallMoneyData);

				//We update the table data in the game window
				mainWindow.getTable().setModel(getTableModel(horizontalTableData, horizontalTableColumns));
				mainWindow.repaint();

				// We end the round
			}

			// We end the game
		} catch (IOException e) {
		}
		
		//We update the log
		pw.println("\n\n>> Game Finished\n\n");
		pw.close();

		try {
			//We close the log file
			fichero.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	// ############# AUXILIAR FUNCTIONS #############

	/**This function resets the players data for a new game.
	 * The data money reset doesn't works, though.
	 */
	@SuppressWarnings({ "static-access", "unused" })
	private void resetData() {
		
		// We get the players data
		List<Object[]> players = agent.getPlayers();

		/* We reset their data
		 * player = [ID,Money,Cooperator,Defector,Inspector,Caught,NotCaught,Players Caught,Descriptor]
		 * moneyData = [Money of player0, Money of player1, ...]
		 */
		for (int i = 0; i < players.size(); i++) {
			
			players.get(i)[1] = agent.getInitialMoney();
			
			for (int j = 2; j < 8; j++) {
				players.get(i)[j] = (int) 0;
			}
		}
		
		List<Object[]> moneyData = new ArrayList<Object[]>();

	}

	/**This function looks if an inspector caught a defector
	 * 
	 * @param crosshair
	 * @param playersRoles
	 * @return
	 */
	private boolean caughtSomeone(Integer crosshair, List<Object[]> playersRoles) {
		
		// We look the PlayerRoles list
		for (int i = 0; i < playersRoles.size(); i++) {
			
			/* if the player is a defector and he was investigated
			 * we return true
			 */
			if (((Integer) playersRoles.get(i)[1]).equals(2) && ((Integer) playersRoles.get(i)[0]).equals(crosshair))
				return true;
			
		}
		
		//In other case we return false
		return false;
	}

	/**This function looks if a defector got caught by an inspector
	 * 
	 * @param id
	 * @param playersRoles
	 * @return
	 */
	private boolean gotCaught(Integer id, List<Object[]> playersRoles) {
		
		//We check if an inspector inspected the player
		for (int i = 0; i < playersRoles.size(); i++) {
			if (((Integer) playersRoles.get(i)[1]).equals(3) && ((Integer) playersRoles.get(i)[2]).equals(id))
				return true;
		}
		return false;
	}

	/**This function calculates the benefit of a Cooperator
	 * 
	 * @param nc
	 * @param nd
	 * @return
	 */
	private double calculateCoopBenefit(int nc, int nd) {

		//We get the parameters
		double moneyPut = agent.getMoneyPut();
		double interestFactor = agent.getInterestFactor();
		double cooperators = nc;
		double defectors = nd;

		//We return the benefit
		return (moneyPut * cooperators * (interestFactor / 100) / (cooperators + defectors)) - moneyPut;
	}

	/**This function calculates the benefit of a Defector
	 * 
	 * @param nc
	 * @param nd
	 * @param caught
	 * @return
	 */
	private double calculateDefBenefit(int nc, int nd, boolean caught) {
		
		//We get the parameters
		double moneyPut = agent.getMoneyPut();
		double interestFactor = agent.getInterestFactor();
		double fineFactor = agent.getFineFactor();
		double cooperators = nc;
		double defectors = nd;

		//We return the benefit
		if (caught)
			return (fineFactor / -100) * (moneyPut * cooperators * interestFactor / (cooperators + defectors));
		else
			return (moneyPut * cooperators * (interestFactor / 100) / (cooperators + defectors));
	}

	/**This function calculates the benefit of an Inspector
	 * 
	 * @param nc
	 * @param nd
	 * @param caught
	 * @param investigated
	 * @return
	 */
	private double calculateInvBenefit(int nc, int nd, boolean caught, int investigated) {
		
		//We get the parameters
		double moneyPut = agent.getMoneyPut();
		double interestFactor = agent.getInterestFactor();
		double fineFactor = agent.getFineFactor();
		double cooperators = nc;
		double defectors = nd;
		double count = investigated;

		//We return the benefit
		if (caught)
			return ((fineFactor / 100) * (moneyPut * cooperators * interestFactor / (cooperators + defectors)) / count);
		else
			return 0;
	}

	/**This function rounds the benefit.
	 * 
	 * @param i
	 * @return
	 */
	private double roundBenefit(double i) {
		
		//We round the decimal
		BigDecimal bd = new BigDecimal(i);
		
		//3 decimals and rounding half up mode
		bd = bd.setScale(3, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	/**This function checks if a message sender will play as an inspector
	 * 
	 * @param message
	 * @return
	 */
	private boolean checkInspectorReply(ACLMessage message) {
		
		//We get the message content and its fields
		String content = message.getContent();
		String[] fields = content.split("#");
		
		//We check if the answer is yes or no
		if (fields[1].equals("Yes"))
			return true;
		else
			return false;
	}

	/**This function orders our players list by the IDs
	 * 
	 * @param players
	 * @return
	 */
	private List<Object[]> orderListByID(List<Object[]> players) {

		List<Object[]> orderedList = new ArrayList<Object[]>();
		List<int[]> order = new ArrayList<int[]>();

		
		for (int i = 0; i < players.size(); i++) {

			int place = 0;
			
			//We calculate the place of each player
			Object[] player = players.get(i);
			for (int j = 0; j < players.size(); j++) {
				Object[] subPlayer = players.get(j);
				
				//If the id is bigger we add 1 to the player position
				int comparation = ((Integer) player[0]).compareTo((Integer) subPlayer[0]);
				if (comparation == 1)
					place++;
			}

			int[] position = { i, place };
			order.add(position);
		}

		//Now we add the players in order
		for (int i = 0; i < order.size(); i++) {
			for (int j = 0; j < order.size(); j++) {
				if (order.get(j)[1] == i)
					orderedList.add(players.get(j));
			}
		}

		return orderedList;
	}

	/**This function obtains the ID of a player from his AID
	 * 
	 * @param aid
	 * @return
	 */
	@SuppressWarnings("static-access")
	private int getIDfromAID(AID aid) {
		
		//We get the players
		List<Object[]> players = agent.getPlayers();
		
		//We get the id of the player
		for (int i = 0; i < players.size(); i++) {
			DFAgentDescription agent = (DFAgentDescription) players.get(i)[9];
			if (aid.getName().equals(agent.getName().getName()))
				return i;
		}

		return -1;
	}

	/**This function transforms the data into a TableModel
	 * 
	 * @param horizontalTableData
	 * @param horizontalTableColumns
	 * @return
	 */
	private static TableModel getTableModel(Object[][] horizontalTableData, String[] horizontalTableColumns) {
		
		//We create the fields of the table
		DefaultTableModel model = new DefaultTableModel(horizontalTableColumns, 0);
		Object[] row = new Object[horizontalTableColumns.length];

		//Now we create the values for each column
		for (int i = 0; i < horizontalTableColumns.length; i++) {
			row[i] = horizontalTableData[0][i];
		}
		model.addRow(row);

		for (int i = 0; i < horizontalTableColumns.length; i++) {
			row[i] = horizontalTableData[1][i];
		}
		model.addRow(row);

		return model;
	}

	/**This function returns a list with the players that were
	 * investigated and the number of players that investigated
	 * them
	 * 
	 * @param playersRoles
	 * @return
	 */
	private List<int[]> getInvestigatedPlayers(List<Object[]> playersRoles) {
		
		List<int[]> investigated = new ArrayList<int[]>();
		
		//We check every player role
		for (int i = 0; i < playersRoles.size(); i++) {
			int id = (Integer) playersRoles.get(i)[0];
			int count = 0;
			
			//We count the number of investigators who investigated the player
			for (int j = 0; j < playersRoles.size(); j++) {
				if (((Integer) playersRoles.get(j)[1]) == 3 && ((Integer) playersRoles.get(j)[2]) == id)
					count++;
			}

			//We add the player and the number of investigators
			int[] investigatedPlayer = { id, count };
			investigated.add(investigatedPlayer);
		}

		return investigated;

	}

	public boolean done() {

		return false;
	}
	
}
