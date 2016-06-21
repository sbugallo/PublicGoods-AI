import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.ShutdownPlatform;
import jade.lang.acl.ACLMessage;

//################## ABOUT LISTENER ##################
/**This listener shows the about window */
class AboutListener implements ActionListener {

	public void actionPerformed(ActionEvent event) {
		try {
			new AboutWindow();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

//################## RANKING LISTENER ##################
/**This listener shows the ranking window */
class RankingListener implements ActionListener {

	List<Object[]> players;
	
	public RankingListener(List<Object[]> players) {
		this.players = players;
	}

	public void actionPerformed(ActionEvent event) {
		try {
			new RankingWindow(players);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

//################## STATS LISTENER ##################
/**This listener shows the player stats window */
class StatsListener implements ActionListener {
	
	psi04_MainAg mainAgent;
	
	public StatsListener(psi04_MainAg mainAgent){
		this.mainAgent=mainAgent;
	}

	@SuppressWarnings("static-access")
	public void actionPerformed(ActionEvent event) {
		try {
			
			List<Object[]> players = mainAgent.getPlayers();
			//We get the player selected in the ComboBox
			Object[] player = players.get(mainAgent.getComboBoxIndex());
			new StatsWindow(player);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

// ################## CLOSE WINDOW LISTENER ##################
/**This listener close the window given in the parameter dialog*/
class CloseWindowListener implements ActionListener {

	private JDialog dialog;

	public CloseWindowListener(JDialog dialog) {
		super();
		this.dialog = dialog;
	}

	public void actionPerformed(ActionEvent arg0) {
		dialog.dispose();

	}

}

//################## HID DIALOG LISTENER ##################
/**This listener hids the dialog given in the parameter dialog*/
class HidDialogListener implements ActionListener{
	private JDialog dialog;
	
	public HidDialogListener(JDialog dialog){
		super();
		this.dialog = dialog;
	}
	
	public void actionPerformed(ActionEvent e) {
		dialog.setVisible(false);
	}
}

// ################## COMBO LISTENER ##################
/**This updates de data in the left panel of the GUI*/
class ComboListener implements ActionListener {
	
	//Elements
	int index;
	private JTextField textMoney;
	private JTextField textCooperator;
	private JTextField textDefector;
	private JTextField textInspector;
	private JTextField textCaught;
	private JTextField textNotCaught;
	private JTextField textPlayersCaught;
	private JTextField textType;
	private List<Object[]> players;
	private psi04_MainAg mainAgent;

	@SuppressWarnings("rawtypes")
	public ComboListener(List<Object[]> players, JComboBox id, JTextField textMoney, JTextField textCooperator,
			JTextField textDefector, JTextField textInspector, JTextField textCaught, JTextField textNotCaught,
			JTextField textPlayersCaught, JTextField textType, psi04_MainAg mainAgent) {
		this.players = players;
		this.index = id.getSelectedIndex();
		this.textMoney = textMoney;
		this.textCooperator = textCooperator;
		this.textDefector = textDefector;
		this.textInspector = textInspector;
		this.textCaught = textCaught;
		this.textNotCaught = textNotCaught;
		this.textPlayersCaught = textPlayersCaught;
		this.textType = textType;
		this.mainAgent = mainAgent;
	}

	@SuppressWarnings("rawtypes")
	public void actionPerformed(ActionEvent e) {

		//We update the data
		index = ((JComboBox) e.getSource()).getSelectedIndex();
		textMoney.setText(String.valueOf(players.get(index)[1]));
		textCooperator.setText(String.valueOf(players.get(index)[2]));
		textDefector.setText(String.valueOf(players.get(index)[3]));
		textInspector.setText(String.valueOf(players.get(index)[4]));
		textCaught.setText(String.valueOf(players.get(index)[5]));
		textNotCaught.setText(String.valueOf(players.get(index)[6]));
		textPlayersCaught.setText(String.valueOf(players.get(index)[7]));
		textType.setText(String.valueOf(players.get(index)[8]));
		mainAgent.setComboBoxIndex(((JComboBox) e.getSource()).getSelectedIndex());

	}
}

// ################## EXIT LISTENER ##################
/**This listener shuts down the agents and the platform*/
class ExitListener implements ActionListener {
	private psi04_MainAg agent;

	public ExitListener(psi04_MainAg agent) {
		this.agent = agent;
	}

	public void actionPerformed(ActionEvent e) {
		
		/**TODO
		 * explicar bien este codigo
		 */
		Codec codec = new SLCodec();
		Ontology jmo = JADEManagementOntology.getInstance();
		agent.getContentManager().registerLanguage(codec);
		agent.getContentManager().registerOntology(jmo);
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(agent.getAMS());
		msg.setLanguage(codec.getName());
		msg.setOntology(jmo.getName());

		try {
			agent.getContentManager().fillContent(msg, new Action(agent.getAID(), new ShutdownPlatform()));
			agent.send(msg);
		} catch (Exception ex) {
		}

	}

}

// ################## NEW GAME LISTENER ##################
/**This listener initialize a new game*/
class NewGameListener implements ActionListener {

	private DFAgentDescription MADescriptor;
	private psi04_MainAg agent;

	public NewGameListener(psi04_MainAg agent, DFAgentDescription MADescriptor) {
		super();
		this.MADescriptor = MADescriptor;
		this.agent = agent;
	}

	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent e) {
		
		agent.setPaused(false);
		agent.getMainWindow().getCpPauseButton().setEnabled(true);
		agent.getMainWindow().getCpResumeButton().setEnabled(false);
		agent.getMainWindow().getLpNewGameButton().setEnabled(false);

		ACLMessage message = new ACLMessage();
		message.setContent("NewGame");
		message.addReceiver(MADescriptor.getName());
		agent.setRestartGame(true);
		agent.send(message);
	}

}

// ################## PAUSE GAME LISTENER ##################
/**This listener pauses the game*/
class PauseGameListener implements ActionListener {

	private psi04_MainAg agent;
	private JButton pauseButton;
	private JButton resumeButton;
	private JButton newGameButton;

	public PauseGameListener(psi04_MainAg agent, JButton pauseButton, JButton resumeButton, JButton newGameButton) {
		super();
		this.agent = agent;
		this.pauseButton = pauseButton;
		this.resumeButton = resumeButton;
		this.newGameButton=newGameButton;
	}

	public void actionPerformed(ActionEvent e) {

		agent.setPaused(true);
		pauseButton.setEnabled(false);
		resumeButton.setEnabled(true);
		newGameButton.setEnabled(true);

	}

}

// ################## RESET PLAYERS LISTENER ##################
/**This listener reset the players data*/
class ResetPlayersListener implements ActionListener {

	psi04_MainAg agent;

	public ResetPlayersListener(psi04_MainAg agent) {
		super();
		this.agent = agent;
	}

	@SuppressWarnings("static-access")
	public void actionPerformed(ActionEvent e) {
		List<Object[]> players = agent.getPlayers();

		//We reset the data
		for (int i = 0; i < players.size(); i++) {
			players.get(i)[1] = agent.getInitialMoney();
			for (int j = 2; j < 8; j++) {
				players.get(i)[j] = (int) 0;
			}
		}
	}
}

// ################## RESUME GAME LISTENER ##################
/**This listener resumes a paused game*/
class ResumeGameListener implements ActionListener {

	private psi04_MainAg agent;
	private DFAgentDescription MADescriptor;
	private JButton pauseButton;
	private JButton resumeButton;
	private JButton newGameButton;

	public ResumeGameListener(psi04_MainAg agent,DFAgentDescription MADescriptor, JButton pauseButton, JButton resumeButton, JButton newGameButton) {
		super();
		this.agent = agent;
		this.MADescriptor = MADescriptor;
		this.pauseButton = pauseButton;
		this.resumeButton = resumeButton;
		this.newGameButton = newGameButton;
	}

	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent e) {

		agent.setPaused(false);
		pauseButton.setEnabled(true);
		resumeButton.setEnabled(false);
		newGameButton.setEnabled(false);
		
		ACLMessage message = new ACLMessage();
		message.setContent("ResumeGame");
		message.addReceiver(MADescriptor.getName());
		agent.send(message);

	}

}

// ################## MODIFY TEXT WITH SLIDER LISTENER ##################
/**This listener modifies a JTextField when a JSlider is modified*/
class ModifyTextWithSliderListener implements ChangeListener {

	JSlider slider;
	JTextField textField;

	public ModifyTextWithSliderListener(JSlider slider, JTextField textField) {
		this.slider = slider;
		this.textField = textField;
	}

	public void stateChanged(ChangeEvent arg0) {
		int value = slider.getValue();
		textField.setText(String.valueOf(value));
	}

}

// ################## MODIFY SLIDER WITH TEXT LISTENER ##################
/**This listener modifies a JSlider when a JTextField is modified*/
class ModifySliderWithTextListener implements KeyListener {

	JSlider slider;
	JTextField textField;
	int minimum;
	int maximum;

	public ModifySliderWithTextListener(JSlider slider, JTextField textField, int minimum, int maximum) {
		this.slider = slider;
		this.textField = textField;
		this.maximum = maximum;
		this.minimum = minimum;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		int value;
		if (textField.getText().equals("")) {
			value = minimum;
			textField.setText(String.valueOf(minimum));
		} else if (Integer.parseInt(textField.getText()) < minimum) {
			value = minimum;
			textField.setText(String.valueOf(minimum));
		} else if (Integer.parseInt(textField.getText()) > maximum) {
			value = maximum;
			textField.setText(String.valueOf(maximum));
		} else
			value = Integer.parseInt(textField.getText());
		slider.setValue(value);

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

}

// ################## VERBOSE LISTENER ##################
/**This listener enables and disables the game information messages*/
class VerboseListener implements ActionListener {

	private String mode;
	private psi04_MainAg agent;

	public VerboseListener(String mode, psi04_MainAg agent) {
		this.mode = mode;
		this.agent = agent;
	}

	public void actionPerformed(ActionEvent e) {
		if (mode.equals("ON"))
			agent.setVerbose(true);
		else
			agent.setVerbose(false);
	}

}

// ################## NUMBER OF ROUNDS LISTENER ##################
/**This listener shows the number of rounds Dialog*/
class NumberOfRoundsListener implements ActionListener {
	private psi04_MainWindow mainWindow;

	public NumberOfRoundsListener(psi04_MainWindow mainWindow) {
		super();
		this.mainWindow = mainWindow;
	}

	public void actionPerformed(ActionEvent e) {
		mainWindow.getRoundsWindow().setVisible(true);
	}
}


//################## OVERALL MONEY WINDOW LISTENER ##################
/**This listener shows the Game Evolution (money) Dialog*/
class OverallMoneyWindowListener implements ActionListener{

	psi04_MainAg agent;
	List<Object[]> data;
	
	public OverallMoneyWindowListener(psi04_MainAg agent, List<Object[]> data){
		this.agent = agent;
		this.data = data;
	}
	
	@SuppressWarnings("static-access")
	public void actionPerformed(ActionEvent e) {
		new OverallMoneyDialog(agent.getPlayers().size(),data);
		
	}
	
	
}
