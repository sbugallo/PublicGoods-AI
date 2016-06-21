import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneLayout;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


/** This class launches a window for the
 *  Public Goods game
 */
@SuppressWarnings("serial")
public class psi04_MainWindow extends JFrame implements ActionListener, Runnable {

	// ###################### PARAMETERS ###############################

	/*These parameters will be used by the main agent so we need to
	 declare them as global*/
	private JTextField textMoneyPut = new JTextField(5);
	private int moneyPut = 1;
	private JTextField textInitialMoney = new JTextField(5);
	private int initialMoney = 100;
	private JTextField textInterestFactor = new JTextField(5);
	private int interestFactor = 120;
	private JTextField textFineFactor = new JTextField(5);
	private int fineFactor = 30;
	private JTextField textMaxInspectors = new JTextField(5);
	private int MaxInspectors = 5;
	private JTextField textNumberOfRounds = new JTextField(5);
	private int numberOfRounds = 10;
	private JTextField textRound = new JTextField(5);
	private int round = 0;
	private JTextArea cpGameInfo = new JTextArea();
	JProgressBar cpProgressBar = new JProgressBar(0, 100);
	JTable table = new JTable();
	private JTextField textMoney = new JTextField(5);
	private JTextField textCooperator = new JTextField(5);
	private JTextField textDefector = new JTextField(5);
	private JTextField textInspector = new JTextField(5);
	private JTextField textCaught = new JTextField(5);
	private JTextField textNotCaught = new JTextField(5);
	private JTextField textPlayersCaught = new JTextField(5);
	private JTextField textType = new JTextField(5);
	private JComboBox rpPlayerComboBox;
	private JButton lpNewGameButton;
	private JButton cpResumeButton = new JButton("Resume");
	private JButton cpPauseButton = new JButton("Pause");
	private JSlider sliderNumberOfRounds;

	private List<Object[]> players = new ArrayList<Object[]>();
	private List<Object[]> overallMoneyData = new ArrayList<Object[]>();
	private String[] playersIDs;

	private psi04_MainAg mainAgent;
	private SetRoundsWindow roundsWindow;
	private OverallMoneyDialog overallMoneyWindow;

	
	//####################### CONSTRUCTOR #######################
	public psi04_MainWindow(String title, List<Object[]> players, psi04_MainAg mainAgent) {
		super(title);
		this.players = players;
		this.mainAgent = mainAgent;
		String[] ids = new String[players.size()];

		for (int i = 0; i < players.size(); i++) {

			ids[i] = String.valueOf(players.get(i)[0]);
		}

		Dimension screenDimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize((int) (screenDimension.getWidth()), (int) (screenDimension.getHeight() * 0.95));
																																								// la
																									
		setLocation(new Point(0, 0)); 

		// ####################### MENU BAR ################################
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Edit");
		JMenuItem menuItem = new JMenuItem("Reset Players", 'R');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.ALT_MASK));
		menuItem.addActionListener(new ResetPlayersListener(mainAgent));
		menu.add(menuItem);
		menuItem = new JMenuItem("Exit", 'E');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.ALT_MASK));
		menuItem.addActionListener(new ExitListener(mainAgent));
		menu.add(menuItem);
		menuBar.add(menu);

		menu = new JMenu("Run");
		menuItem = new JMenuItem("New Game", 'N');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.ALT_MASK));
		menuItem.addActionListener(new NewGameListener(mainAgent, mainAgent.getMADescription()));
		menu.add(menuItem);
		menuItem = new JMenuItem("Pause Game", 'P');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.ALT_MASK));
		menuItem.addActionListener(new PauseGameListener(mainAgent,cpPauseButton,cpResumeButton,lpNewGameButton));
		menu.add(menuItem);

		menuItem = new JMenuItem("Resume Game", 'C');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.ALT_MASK));
		menuItem.addActionListener(new ResumeGameListener(mainAgent,mainAgent.getMADescription(),cpPauseButton,cpResumeButton,lpNewGameButton));
		menu.add(menuItem);
		menuItem = new JMenuItem("Number of Rounds", 'O');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.ALT_MASK));
		menuItem.addActionListener(new NumberOfRoundsListener(this));
		menu.add(menuItem);
		menuBar.add(menu);

		menu = new JMenu("Window");
		menuItem = new JMenuItem("Verbose ON", 'V');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.ALT_MASK));
		menuItem.addActionListener(new VerboseListener("ON", mainAgent));
		menu.add(menuItem);
		menuItem = new JMenuItem("Verbose OFF", 'B');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.ALT_MASK));
		menuItem.addActionListener(new VerboseListener("OFF", mainAgent));
		menu.add(menuItem);
		menuBar.add(menu);

		menu = new JMenu("Help");
		menuItem = new JMenuItem("About", 'A');
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.ALT_MASK));
		menuItem.addActionListener(new AboutListener());
		menu.add(menuItem);
		menuBar.add(menu);

		setJMenuBar(menuBar);

		// ####################### BASE PANEL ################################
		GridBagLayout basePanelLayout = new GridBagLayout();
		basePanelLayout.columnWeights = new double[] { 1.0 };
		basePanelLayout.rowWeights = new double[] { 0.90, 0.15 };
		setLayout(basePanelLayout);

		// ####################### INFERIOR PANEL ############################

		Object[][] horizontalTableData = new Object[2][ids.length];
		String[] horizontalTableColumns = new String[ids.length];
		for (int i = 0; i < ids.length; i++) {
			horizontalTableData[0][i] = players.get(i)[1];
			horizontalTableData[1][i] = players.get(i)[8];
			horizontalTableColumns[i] = "Player " + ids[i];
		}

		table = new JTable(horizontalTableData, horizontalTableColumns);
		table.setEnabled(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane inferiorPanel = new JScrollPane(table);
		ScrollPaneLayout inferiorPanelLayout = new ScrollPaneLayout();
		inferiorPanelLayout.setHorizontalScrollBarPolicy(ScrollPaneLayout.HORIZONTAL_SCROLLBAR_ALWAYS);
		inferiorPanelLayout.setVerticalScrollBarPolicy(ScrollPaneLayout.VERTICAL_SCROLLBAR_NEVER);
		inferiorPanel.setLayout(inferiorPanelLayout);
		GridBagConstraints inferiorPanelConstraints = new GridBagConstraints();
		inferiorPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 2), "Current Money"));
		inferiorPanel.setBackground(Color.white);
		inferiorPanelConstraints.fill = GridBagConstraints.BOTH;
		inferiorPanelConstraints.gridx = 0;
		inferiorPanelConstraints.gridy = 1;

		add(inferiorPanel, inferiorPanelConstraints);

		// ####################### SUPERIOR PANEL #######################
		GridBagConstraints superiorPanelConstraints = new GridBagConstraints();
		JPanel superiorPanel = new JPanel();
		superiorPanel.setBackground(Color.white);
		superiorPanelConstraints.fill = GridBagConstraints.BOTH;
		superiorPanelConstraints.gridx = 0;
		superiorPanelConstraints.gridy = 0;

		add(superiorPanel, superiorPanelConstraints);

		GridBagLayout superiorPanelLayout = new GridBagLayout();
		superiorPanelLayout.columnWeights = new double[] { 0.05, 0.5, 0.1 };
		superiorPanelLayout.rowWeights = new double[] { 1.0 };
		superiorPanel.setLayout(superiorPanelLayout);

		// ####################### LEFT PANEL ###########################
		GridBagConstraints leftPanelConstraints = new GridBagConstraints();
		JPanel leftPanel = new JPanel();
		leftPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 2), "Preferences"));
		leftPanel.setBackground(Color.white);
		leftPanelConstraints.fill = GridBagConstraints.BOTH;
		leftPanelConstraints.gridx = 0;
		leftPanelConstraints.gridy = 0;

		superiorPanel.add(leftPanel, leftPanelConstraints);

		GridBagLayout leftPanelLayout = new GridBagLayout();
		leftPanelLayout.columnWeights = new double[] { 0.75, 0.25 };
		leftPanelLayout.rowWeights = new double[] { 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08, 0.08,
				0.08, 0.08, 0.08, 0.08 };
		leftPanel.setLayout(leftPanelLayout);

		// LEFT PANEL ELEMENTS
		GridBagConstraints lpInitialMoneyLabelConstraints = new GridBagConstraints();
		JLabel lpInitialMoneyLabel = new JLabel("Initial Money:");
		lpInitialMoneyLabelConstraints.gridx = 0;
		lpInitialMoneyLabelConstraints.gridy = 0;
		leftPanel.add(lpInitialMoneyLabel, lpInitialMoneyLabelConstraints);

		GridBagConstraints lpMoneyPutLabelConstraints = new GridBagConstraints();
		JLabel lpMoneyPutLabel = new JLabel("Money Put By The Players:");
		lpMoneyPutLabelConstraints.gridx = 0;
		lpMoneyPutLabelConstraints.gridy = 2;
		leftPanel.add(lpMoneyPutLabel, lpMoneyPutLabelConstraints);

		GridBagConstraints lpInterestLabelConstraints = new GridBagConstraints();
		JLabel lpInterestLabel = new JLabel("Interest Factor:");
		lpInterestLabelConstraints.gridx = 0;
		lpInterestLabelConstraints.gridy = 4;
		leftPanel.add(lpInterestLabel, lpInterestLabelConstraints);

		GridBagConstraints lpFineLabelConstraints = new GridBagConstraints();
		JLabel lpFineLabel = new JLabel("Fine Factor:");
		lpFineLabelConstraints.gridx = 0;
		lpFineLabelConstraints.gridy = 6;
		leftPanel.add(lpFineLabel, lpFineLabelConstraints);

		GridBagConstraints lpMaxInspectorsLabelConstraints = new GridBagConstraints();
		JLabel lpMaxInspectorsLabel = new JLabel("Max Inspectors:");
		lpMaxInspectorsLabelConstraints.gridx = 0;
		lpMaxInspectorsLabelConstraints.gridy = 8;
		leftPanel.add(lpMaxInspectorsLabel, lpMaxInspectorsLabelConstraints);

		GridBagConstraints lpRoundsLabelConstraints = new GridBagConstraints();
		JLabel lpRoundsLabel = new JLabel("Number Of Rounds:");
		lpRoundsLabelConstraints.gridx = 0;
		lpRoundsLabelConstraints.gridy = 10;
		lpRoundsLabel.setVisible(false);
		leftPanel.add(lpRoundsLabel, lpRoundsLabelConstraints);

		GridBagConstraints lpInitialMoneyTextConstraints = new GridBagConstraints();
		textInitialMoney.setText(String.valueOf(initialMoney));
		lpInitialMoneyTextConstraints.gridx = 1;
		lpInitialMoneyTextConstraints.gridy = 0;
		leftPanel.add(textInitialMoney, lpInitialMoneyTextConstraints);

		GridBagConstraints lpMoneyPutTextConstraints = new GridBagConstraints();
		textMoneyPut.setText(String.valueOf(moneyPut));
		lpMoneyPutTextConstraints.gridx = 1;
		lpMoneyPutTextConstraints.gridy = 2;
		leftPanel.add(textMoneyPut, lpMoneyPutTextConstraints);

		GridBagConstraints lpInterestTextConstraints = new GridBagConstraints();
		textInterestFactor.setText(String.valueOf(interestFactor));
		lpInterestTextConstraints.gridx = 1;
		lpInterestTextConstraints.gridy = 4;
		leftPanel.add(textInterestFactor, lpInterestTextConstraints);

		GridBagConstraints lpFineTextConstraints = new GridBagConstraints();
		textFineFactor.setText(String.valueOf(fineFactor));
		lpFineTextConstraints.gridx = 1;
		lpFineTextConstraints.gridy = 6;
		leftPanel.add(textFineFactor, lpFineTextConstraints);

		GridBagConstraints lpMaxInspectorsTextConstraints = new GridBagConstraints();
		textMaxInspectors.setText(String.valueOf(MaxInspectors));
		lpMaxInspectorsTextConstraints.gridx = 1;
		lpMaxInspectorsTextConstraints.gridy = 8;
		leftPanel.add(textMaxInspectors, lpMaxInspectorsTextConstraints);

		GridBagConstraints lpRoundsTextConstraints = new GridBagConstraints();
		textNumberOfRounds.setText(String.valueOf(numberOfRounds));
		lpRoundsTextConstraints.gridx = 1;
		lpRoundsTextConstraints.gridy = 10;
		leftPanel.add(textNumberOfRounds, lpRoundsTextConstraints);

		GridBagConstraints lpInitialMoneySliderConstraints = new GridBagConstraints();
		lpInitialMoneySliderConstraints.gridwidth = 2;
		lpInitialMoneySliderConstraints.gridx = 0;
		lpInitialMoneySliderConstraints.gridy = 1;
		JSlider sliderInitalMoney = new JSlider();
		sliderInitalMoney.addChangeListener(new ModifyTextWithSliderListener(sliderInitalMoney, textInitialMoney));
		textInitialMoney
				.addKeyListener(new ModifySliderWithTextListener(sliderInitalMoney, textInitialMoney, 10, 1000));
		sliderInitalMoney.setValue(initialMoney);
		sliderInitalMoney.setMinimum(10);
		sliderInitalMoney.setMinorTickSpacing(1);
		sliderInitalMoney.setMaximum(1000);
		sliderInitalMoney.setBackground(Color.white);
		leftPanel.add(sliderInitalMoney, lpInitialMoneySliderConstraints);

		GridBagConstraints lpMoneyPutSliderConstraints = new GridBagConstraints();
		lpMoneyPutSliderConstraints.gridwidth = 2;
		lpMoneyPutSliderConstraints.gridx = 0;
		lpMoneyPutSliderConstraints.gridy = 3;
		JSlider sliderMoneyPut = new JSlider();
		sliderMoneyPut.addChangeListener(new ModifyTextWithSliderListener(sliderMoneyPut, textMoneyPut));
		textMoneyPut.addKeyListener(new ModifySliderWithTextListener(sliderMoneyPut, textMoneyPut, 1, 9999));
		sliderMoneyPut.setMinimum(1);
		sliderMoneyPut.setMaximum(9999);
		sliderMoneyPut.setMinorTickSpacing(1);
		sliderMoneyPut.setValue(moneyPut);
		sliderMoneyPut.setBackground(Color.white);
		leftPanel.add(sliderMoneyPut, lpMoneyPutSliderConstraints);

		GridBagConstraints lpInterestSliderConstraints = new GridBagConstraints();
		lpInterestSliderConstraints.gridwidth = 2;
		lpInterestSliderConstraints.gridx = 0;
		lpInterestSliderConstraints.gridy = 5;
		JSlider sliderInterestFactor = new JSlider();
		sliderInterestFactor
				.addChangeListener(new ModifyTextWithSliderListener(sliderInterestFactor, textInterestFactor));
		textInterestFactor
				.addKeyListener(new ModifySliderWithTextListener(sliderInterestFactor, textInterestFactor, 100, 200));
		sliderInterestFactor.setMinorTickSpacing(1);
		sliderInterestFactor.setMinimum(100);
		sliderInterestFactor.setMaximum(200);
		sliderInterestFactor.setValue(interestFactor);
		sliderInterestFactor.setBackground(Color.white);
		leftPanel.add(sliderInterestFactor, lpInterestSliderConstraints);

		GridBagConstraints lpFineSliderConstraints = new GridBagConstraints();
		lpFineSliderConstraints.gridwidth = 2;
		lpFineSliderConstraints.gridx = 0;
		lpFineSliderConstraints.gridy = 7;
		JSlider sliderFineFactor = new JSlider();
		sliderFineFactor.addChangeListener(new ModifyTextWithSliderListener(sliderFineFactor, textFineFactor));
		textFineFactor.addKeyListener(new ModifySliderWithTextListener(sliderFineFactor, textFineFactor, 0, 100));
		sliderFineFactor.setMinorTickSpacing(1);
		sliderFineFactor.setMinimum(0);
		sliderFineFactor.setMaximum(100);
		sliderFineFactor.setValue(fineFactor);
		sliderFineFactor.setBackground(Color.white);
		leftPanel.add(sliderFineFactor, lpFineSliderConstraints);

		GridBagConstraints lpMaxInspectorsSliderConstraints = new GridBagConstraints();
		lpMaxInspectorsSliderConstraints.gridwidth = 2;
		lpMaxInspectorsSliderConstraints.gridx = 0;
		lpMaxInspectorsSliderConstraints.gridy = 9;
		JSlider sliderMaxInspectors = new JSlider();
		sliderMaxInspectors.addChangeListener(new ModifyTextWithSliderListener(sliderMaxInspectors, textMaxInspectors));
		textMaxInspectors
				.addKeyListener(new ModifySliderWithTextListener(sliderMaxInspectors, textMaxInspectors, 10, 100));
		sliderMaxInspectors.setValue(5);
		sliderMaxInspectors.setMinorTickSpacing(1);
		sliderMaxInspectors.setMaximum(10);
		sliderMaxInspectors.setMaximum(100);
		sliderMaxInspectors.setValue(MaxInspectors);
		sliderMaxInspectors.setBackground(Color.white);
		leftPanel.add(sliderMaxInspectors, lpMaxInspectorsSliderConstraints);

		GridBagConstraints lpRoundsSliderConstraints = new GridBagConstraints();
		lpRoundsSliderConstraints.gridwidth = 2;
		lpRoundsSliderConstraints.gridx = 0;
		lpRoundsSliderConstraints.gridy = 11;
		sliderNumberOfRounds = new JSlider();
		sliderNumberOfRounds
				.addChangeListener(new ModifyTextWithSliderListener(sliderNumberOfRounds, textNumberOfRounds));
		textNumberOfRounds
				.addKeyListener(new ModifySliderWithTextListener(sliderNumberOfRounds, textNumberOfRounds, 1, 10000));
		sliderNumberOfRounds.setValue(500);
		sliderNumberOfRounds.setMaximum(10000);
		sliderNumberOfRounds.setMinimum(1);
		sliderNumberOfRounds.setValue(numberOfRounds);
		sliderNumberOfRounds.setBackground(Color.white);
		leftPanel.add(sliderNumberOfRounds, lpRoundsSliderConstraints);

		GridBagConstraints lpNewGameButtonConstraints = new GridBagConstraints();
		lpNewGameButtonConstraints.gridwidth = 2;
		lpNewGameButtonConstraints.gridx = 0;
		lpNewGameButtonConstraints.gridy = 12;
		lpNewGameButtonConstraints.fill = GridBagConstraints.BOTH;
		lpNewGameButton = new JButton();
		lpNewGameButton.addActionListener(new NewGameListener(mainAgent, mainAgent.getMADescription()));
		lpNewGameButton.setText("New Game");
		lpNewGameButton.setEnabled(true);
		leftPanel.add(lpNewGameButton, lpNewGameButtonConstraints);

		GridBagConstraints lpRankingButtonConstraints = new GridBagConstraints();
		lpRankingButtonConstraints.gridwidth = 2;
		lpRankingButtonConstraints.gridx = 0;
		lpRankingButtonConstraints.gridy = 13;
		lpRankingButtonConstraints.fill = GridBagConstraints.BOTH;
		JButton rankingButton = new JButton();
		rankingButton.addActionListener(new RankingListener(this.players));
		rankingButton.setText("Ranking");
		leftPanel.add(rankingButton, lpRankingButtonConstraints);

		// ####################### CENTER PANEL ###########################
		GridBagConstraints centerPanelConstraints = new GridBagConstraints();
		JPanel centerPanel = new JPanel();
		centerPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 2), "Game Information"));
		centerPanel.setBackground(Color.white);
		centerPanelConstraints.fill = GridBagConstraints.BOTH;
		centerPanelConstraints.gridx = 1;
		centerPanelConstraints.gridy = 0;

		superiorPanel.add(centerPanel, centerPanelConstraints);

		GridBagLayout centerPanelLayout = new GridBagLayout();
		centerPanelLayout.columnWeights = new double[] { 0.33, 0.33, 0.33 };
		centerPanelLayout.rowWeights = new double[] { 0.13, 0.61, 0.13, 0.13 };
		centerPanel.setLayout(centerPanelLayout);

		// CENTER PANEL ELEMENTS
		GridBagConstraints cpRoundLabelConstraints = new GridBagConstraints();
		JLabel cpRoundLabel = new JLabel("Round: ");
		cpRoundLabelConstraints.gridx = 0;
		cpRoundLabelConstraints.gridy = 0;
		centerPanel.add(cpRoundLabel, cpRoundLabelConstraints);

		GridBagConstraints cpRoundTextConstraints = new GridBagConstraints();
		textRound.setText(String.valueOf(round));
		textRound.setEditable(false);
		cpRoundTextConstraints.gridx = 1;
		cpRoundTextConstraints.gridy = 0;
		centerPanel.add(textRound, cpRoundTextConstraints);

		GridBagConstraints overallMoneyButtonConstraints = new GridBagConstraints();
		JButton overallMoneyButton = new JButton("Game Evolution");
		overallMoneyButtonConstraints.gridwidth = 2;
		overallMoneyButtonConstraints.gridx = 2;
		overallMoneyButtonConstraints.gridy = 0;
		overallMoneyButton
				.addActionListener(new OverallMoneyWindowListener(mainAgent, mainAgent.getOverallMoneyData()));
		centerPanel.add(overallMoneyButton, overallMoneyButtonConstraints);

		GridBagConstraints cpGameInfoConstraintsPanel = new GridBagConstraints();
		JScrollPane cpGameInfoPanel = new JScrollPane(cpGameInfo);
		cpGameInfoPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 1), "Messages"));
		cpGameInfoPanel.setBackground(Color.white);
		cpGameInfoConstraintsPanel.fill = GridBagConstraints.BOTH;
		cpGameInfoConstraintsPanel.gridwidth = 3;
		cpGameInfoConstraintsPanel.gridx = 0;
		cpGameInfoConstraintsPanel.gridy = 1;
		centerPanel.add(cpGameInfoPanel, cpGameInfoConstraintsPanel);

		GridBagConstraints cpProgressBarConstraints = new GridBagConstraints();
		cpProgressBar.setValue(round / numberOfRounds);
		cpProgressBar.setStringPainted(true);
		cpProgressBarConstraints.fill = GridBagConstraints.HORIZONTAL;
		cpProgressBarConstraints.gridwidth = 3;
		cpProgressBarConstraints.gridx = 0;
		cpProgressBarConstraints.gridy = 2;
		centerPanel.add(cpProgressBar, cpProgressBarConstraints);

		GridBagConstraints cpPauseButtonConstraints = new GridBagConstraints();
		cpPauseButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
		cpPauseButtonConstraints.gridx = 0;
		cpPauseButtonConstraints.gridy = 3;
		cpPauseButton.addActionListener(new PauseGameListener(mainAgent,cpPauseButton,cpResumeButton,lpNewGameButton));
		centerPanel.add(cpPauseButton, cpPauseButtonConstraints);

		GridBagConstraints cpResumeButtonConstraints = new GridBagConstraints();
		cpResumeButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
		cpResumeButtonConstraints.gridx = 1;
		cpResumeButtonConstraints.gridy = 3;
		cpResumeButton.addActionListener(new ResumeGameListener(mainAgent,mainAgent.getMADescription(),cpPauseButton,cpResumeButton,lpNewGameButton));
		centerPanel.add(cpResumeButton, cpResumeButtonConstraints);

		GridBagConstraints cpExitButtonConstraints = new GridBagConstraints();
		JButton cpExitButton = new JButton("Exit");
		cpExitButton.addActionListener(new ExitListener(mainAgent));
		cpExitButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
		cpExitButtonConstraints.gridx = 2;
		cpExitButtonConstraints.gridy = 3;
		centerPanel.add(cpExitButton, cpExitButtonConstraints);

		// ####################### RIGHT PANEL ###############################
		GridBagConstraints rightPanelConstraints = new GridBagConstraints();
		JPanel rightPanel = new JPanel();
		rightPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 2), "Player Data"));
		rightPanel.setBackground(Color.white);
		rightPanelConstraints.fill = GridBagConstraints.BOTH;
		rightPanelConstraints.gridx = 2;
		rightPanelConstraints.gridy = 0;

		superiorPanel.add(rightPanel, rightPanelConstraints);

		GridBagLayout rightPanelLayout = new GridBagLayout();
		rightPanelLayout.columnWeights = new double[] { 0.75, 0.25 };
		rightPanelLayout.rowWeights = new double[] { 0.11, 0.11, 0.11, 0.11, 0.11, 0.11, 0.11, 0.11, 0.11, 0.11, 0.11,
				0.11 };
		rightPanel.setLayout(rightPanelLayout);

		// RIGHT PANEL ELEMENTS
		GridBagConstraints rpPlayerLabelConstraints = new GridBagConstraints();
		JLabel rpPlayerLabel = new JLabel("Player:");
		rpPlayerLabelConstraints.gridx = 0;
		rpPlayerLabelConstraints.gridy = 0;
		rightPanel.add(rpPlayerLabel, rpPlayerLabelConstraints);

		GridBagConstraints rpMoneyLabelConstraints = new GridBagConstraints();
		JLabel rpMoneyLabel = new JLabel("Money:");
		rpMoneyLabelConstraints.gridx = 0;
		rpMoneyLabelConstraints.gridy = 1;
		rightPanel.add(rpMoneyLabel, rpMoneyLabelConstraints);

		GridBagConstraints rpCooperatorLabelConstraints = new GridBagConstraints();
		JLabel rpCooperatorLabel = new JLabel("Cooperator:");
		rpCooperatorLabelConstraints.gridx = 0;
		rpCooperatorLabelConstraints.gridy = 2;
		rightPanel.add(rpCooperatorLabel, rpCooperatorLabelConstraints);

		GridBagConstraints rpDefectorLabelConstraints = new GridBagConstraints();
		JLabel rpDefectorLabel = new JLabel("Defector:");
		rpDefectorLabelConstraints.gridx = 0;
		rpDefectorLabelConstraints.gridy = 3;
		rightPanel.add(rpDefectorLabel, rpDefectorLabelConstraints);

		GridBagConstraints rpInspectorLabelConstraints = new GridBagConstraints();
		JLabel rpInspectorLabel = new JLabel("Inspector:");
		rpInspectorLabelConstraints.gridx = 0;
		rpInspectorLabelConstraints.gridy = 4;
		rightPanel.add(rpInspectorLabel, rpInspectorLabelConstraints);

		GridBagConstraints rpCaughtLabelConstraints = new GridBagConstraints();
		JLabel rpCaughtLabel = new JLabel("Times Caught:");
		rpCaughtLabelConstraints.gridx = 0;
		rpCaughtLabelConstraints.gridy = 5;
		rightPanel.add(rpCaughtLabel, rpCaughtLabelConstraints);

		GridBagConstraints rpNotCaughtLabelConstraints = new GridBagConstraints();
		JLabel rpNotCaughtLabel = new JLabel("Times Not Caught:");
		rpNotCaughtLabelConstraints.gridx = 0;
		rpNotCaughtLabelConstraints.gridy = 6;
		rightPanel.add(rpNotCaughtLabel, rpNotCaughtLabelConstraints);

		GridBagConstraints rpPlayersCaughtLabelConstraints = new GridBagConstraints();
		JLabel rpPlayersCaughtLabel = new JLabel("Players Caught:");
		rpPlayersCaughtLabelConstraints.gridx = 0;
		rpPlayersCaughtLabelConstraints.gridy = 7;
		rightPanel.add(rpPlayersCaughtLabel, rpPlayersCaughtLabelConstraints);

		GridBagConstraints rpTypeLabelConstraints = new GridBagConstraints();
		JLabel rpTypeLabel = new JLabel("Type:");
		rpTypeLabelConstraints.gridx = 0;
		rpTypeLabelConstraints.gridy = 8;
		rightPanel.add(rpTypeLabel, rpTypeLabelConstraints);

		GridBagConstraints rpPlayerComboBoxConstraints = new GridBagConstraints();
		rpPlayerComboBox = new JComboBox(ids);
		rpPlayerComboBox.setSelectedIndex(0);
		rpPlayerComboBox.addActionListener(new ComboListener(players, rpPlayerComboBox, textMoney, textCooperator,
				textDefector, textInspector, textCaught, textNotCaught, textPlayersCaught, textType, mainAgent));
		rpPlayerComboBoxConstraints.gridx = 1;
		rpPlayerComboBoxConstraints.gridy = 0;
		rightPanel.add(rpPlayerComboBox, rpPlayerComboBoxConstraints);

		GridBagConstraints rpMoneyTextConstraints = new GridBagConstraints();
		textMoney.setEditable(false);
		textMoney.setText(String.valueOf(players.get(0)[1]));
		rpMoneyTextConstraints.gridx = 1;
		rpMoneyTextConstraints.gridy = 1;
		rightPanel.add(textMoney, rpMoneyTextConstraints);

		GridBagConstraints rpCooperatorTextConstraints = new GridBagConstraints();
		textCooperator.setEditable(false);
		textCooperator.setText(String.valueOf(players.get(0)[2]));
		rpCooperatorTextConstraints.gridx = 1;
		rpCooperatorTextConstraints.gridy = 2;
		rightPanel.add(textCooperator, rpCooperatorTextConstraints);

		GridBagConstraints rpDefectorTextConstraints = new GridBagConstraints();
		textDefector.setEditable(false);
		textDefector.setText(String.valueOf(players.get(0)[3]));
		rpDefectorTextConstraints.gridx = 1;
		rpDefectorTextConstraints.gridy = 3;
		rightPanel.add(textDefector, rpDefectorTextConstraints);

		GridBagConstraints rpInspectorTextConstraints = new GridBagConstraints();
		textInspector.setEditable(false);
		textInspector.setText(String.valueOf(players.get(0)[4]));
		rpInspectorTextConstraints.gridx = 1;
		rpInspectorTextConstraints.gridy = 4;
		rightPanel.add(textInspector, rpInspectorTextConstraints);

		GridBagConstraints rpCaughtTextConstraints = new GridBagConstraints();
		textCaught.setEditable(false);
		textCaught.setText(String.valueOf(players.get(0)[5]));
		rpCaughtTextConstraints.gridx = 1;
		rpCaughtTextConstraints.gridy = 5;
		rightPanel.add(textCaught, rpCaughtTextConstraints);

		GridBagConstraints rpNotCaughtTextConstraints = new GridBagConstraints();
		textNotCaught.setEditable(false);
		textNotCaught.setText(String.valueOf(players.get(0)[6]));
		rpNotCaughtTextConstraints.gridx = 1;
		rpNotCaughtTextConstraints.gridy = 6;
		rightPanel.add(textNotCaught, rpNotCaughtTextConstraints);

		GridBagConstraints rpPlayersCaughtTextConstraints = new GridBagConstraints();
		textPlayersCaught.setEditable(false);
		textPlayersCaught.setText(String.valueOf(players.get(0)[7]));
		rpPlayersCaughtTextConstraints.gridx = 1;
		rpPlayersCaughtTextConstraints.gridy = 7;
		rightPanel.add(textPlayersCaught, rpPlayersCaughtTextConstraints);

		GridBagConstraints rpTypeTextConstraints = new GridBagConstraints();
		textType.setEditable(false);
		textType.setText(String.valueOf(players.get(0)[8]));
		rpTypeTextConstraints.gridx = 1;
		rpTypeTextConstraints.gridy = 8;
		rightPanel.add(textType, rpTypeTextConstraints);

		GridBagConstraints rpResetPlayerButtonConstraints = new GridBagConstraints();
		rpResetPlayerButtonConstraints.gridwidth = 2;
		rpResetPlayerButtonConstraints.gridx = 0;
		rpResetPlayerButtonConstraints.gridy = 9;
		rpResetPlayerButtonConstraints.fill = GridBagConstraints.BOTH;
		JButton resetPlayerButton = new JButton();
		resetPlayerButton.addActionListener(new ResetPlayersListener(mainAgent));
		resetPlayerButton.setText("Reset Players");
		rightPanel.add(resetPlayerButton, rpResetPlayerButtonConstraints);

		GridBagConstraints rpStatsButtonConstraints = new GridBagConstraints();
		rpStatsButtonConstraints.gridwidth = 2;
		rpStatsButtonConstraints.gridx = 0;
		rpStatsButtonConstraints.gridy = 10;
		rpStatsButtonConstraints.fill = GridBagConstraints.BOTH;
		JButton statsButton = new JButton();
		statsButton.addActionListener(
				new StatsListener(this.mainAgent));
		statsButton.setText("Player Stats");
		rightPanel.add(statsButton, rpStatsButtonConstraints);

		roundsWindow = new SetRoundsWindow(this);

	}

	//####################### GETTERS AND SETTERS #######################
	
	public JTextField getTextMoneyPut() {
		return textMoneyPut;
	}

	public void setTextMoneyPut(JTextField textMoneyPut) {
		this.textMoneyPut = textMoneyPut;
	}

	public int getMoneyPut() {
		return moneyPut;
	}

	public void setMoneyPut(int moneyPut) {
		this.moneyPut = moneyPut;
	}

	public JTextField getTextInitialMoney() {
		return textInitialMoney;
	}

	public void setTextInitialMoney(JTextField textInitialMoney) {
		this.textInitialMoney = textInitialMoney;
	}

	public int getInitialMoney() {
		return initialMoney;
	}

	public void setInitialMoney(int initialMoney) {
		this.initialMoney = initialMoney;
	}

	public JTextField getTextInterestFactor() {
		return textInterestFactor;
	}

	public void setTextInterestFactor(JTextField textInterestFactor) {
		this.textInterestFactor = textInterestFactor;
	}

	public int getInterestFactor() {
		return interestFactor;
	}

	public void setInterestFactor(int interestFactor) {
		this.interestFactor = interestFactor;
	}

	public JTextField getTextFineFactor() {
		return textFineFactor;
	}

	public void setTextFineFactor(JTextField textFineFactor) {
		this.textFineFactor = textFineFactor;
	}

	public int getFineFactor() {
		return fineFactor;
	}

	public void setFineFactor(int fineFactor) {
		this.fineFactor = fineFactor;
	}

	public JTextField getTextMaxInspectors() {
		return textMaxInspectors;
	}

	public void setTextMaxInspectors(JTextField textMaxInspectors) {
		this.textMaxInspectors = textMaxInspectors;
	}

	public int getMaxInspectors() {
		return MaxInspectors;
	}

	public void setMaxInspectors(int maxInspectors) {
		MaxInspectors = maxInspectors;
	}

	public JTextField getTextNumberOfRounds() {
		return textNumberOfRounds;
	}

	public void setTextNumberOfRounds(JTextField textNumberOfRounds) {
		this.textNumberOfRounds = textNumberOfRounds;
	}

	public int getNumberOfRounds() {
		return numberOfRounds;
	}

	public void setNumberOfRounds(int numberOfRounds) {
		this.numberOfRounds = numberOfRounds;
	}

	public JTextField getTextRound() {
		return textRound;
	}

	public void setTextRound(JTextField textRound) {
		this.textRound = textRound;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public JTextField getTextMoney() {
		return textMoney;
	}

	public void setTextMoney(JTextField textMoney) {
		this.textMoney = textMoney;
	}

	public JTextField getTextCooperator() {
		return textCooperator;
	}

	public void setTextCooperator(JTextField textCooperator) {
		this.textCooperator = textCooperator;
	}

	public JTextField getTextDefector() {
		return textDefector;
	}

	public void setTextDefector(JTextField textDefector) {
		this.textDefector = textDefector;
	}

	public JTextField getTextInspector() {
		return textInspector;
	}

	public void setTextInspector(JTextField textInspector) {
		this.textInspector = textInspector;
	}

	public JTextField getTextCaught() {
		return textCaught;
	}

	public void setTextCaught(JTextField textCaught) {
		this.textCaught = textCaught;
	}

	public JTextField getTextNotCaught() {
		return textNotCaught;
	}

	public void setTextNotCaught(JTextField textNotCaught) {
		this.textNotCaught = textNotCaught;
	}

	public JTextField getTextPlayersCaught() {
		return textPlayersCaught;
	}

	public void setTextPlayersCaught(JTextField textPlayersCaught) {
		this.textPlayersCaught = textPlayersCaught;
	}

	public JTextField getTextType() {
		return textType;
	}

	public void setTextType(JTextField textType) {
		this.textType = textType;
	}

	public JTextArea getCpGameInfo() {
		return cpGameInfo;
	}

	public void setCpGameInfo(JTextArea cpGameInfo) {
		this.cpGameInfo = cpGameInfo;
	}

	public List<Object[]> getPlayers() {
		return players;
	}

	public void setPlayers(List<Object[]> players) {
		this.players = players;
	}

	public String[] getPlayersIDs() {
		return playersIDs;
	}

	public void setPlayersIDs(String[] playersIDs) {
		this.playersIDs = playersIDs;
	}

	public psi04_MainAg getMainAgent() {
		return mainAgent;
	}

	public void setMainAgent(psi04_MainAg mainAgent) {
		this.mainAgent = mainAgent;
	}

	public JProgressBar getCpProgressBar() {
		return cpProgressBar;
	}

	public void setCpProgressBar(JProgressBar cpProgressBar) {
		this.cpProgressBar = cpProgressBar;
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public JComboBox getRpPlayerComboBox() {
		return rpPlayerComboBox;
	}

	public void setRpPlayerComboBox(JComboBox rpPlayerComboBox) {
		this.rpPlayerComboBox = rpPlayerComboBox;
	}

	public JButton getLpNewGameButton() {
		return lpNewGameButton;
	}

	public void setLpNewGameButton(JButton lpNewGameButton) {
		this.lpNewGameButton = lpNewGameButton;
	}

	public JSlider getSliderNumberOfRounds() {
		return sliderNumberOfRounds;
	}

	public void setSliderNumberOfRounds(JSlider sliderNumberOfRounds) {
		this.sliderNumberOfRounds = sliderNumberOfRounds;
	}

	public SetRoundsWindow getRoundsWindow() {
		return roundsWindow;
	}

	public void setRoundsWindow(SetRoundsWindow roundsWindow) {
		this.roundsWindow = roundsWindow;
	}

	public OverallMoneyDialog getOverallMoneyWindow() {
		return overallMoneyWindow;
	}

	public void setOverallMoneyWindow(OverallMoneyDialog overallMoneyWindow) {
		this.overallMoneyWindow = overallMoneyWindow;
	}

	public List<Object[]> getOverallMoneyData() {
		return overallMoneyData;
	}

	public void setOverallMoneyData(List<Object[]> overallMoneyData) {
		this.overallMoneyData = overallMoneyData;
	}

	public JButton getCpResumeButton() {
		return cpResumeButton;
	}

	public void setCpResumeButton(JButton cpResumeButton) {
		this.cpResumeButton = cpResumeButton;
	}

	public JButton getCpPauseButton() {
		return cpPauseButton;
	}

	public void setCpPauseButton(JButton cpPauseButton) {
		this.cpPauseButton = cpPauseButton;
	}

	@Override
	public void run() {

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

}

@SuppressWarnings("serial")
class AboutWindow extends JDialog {
	public AboutWindow() throws IOException {
		setTitle("About");
		setSize(600, 400);

		GridBagLayout basePanelLayout = new GridBagLayout();
		basePanelLayout.columnWeights = new double[] { 1.0 };
		basePanelLayout.rowWeights = new double[] { 0.20, 0.20, 0.20, 0.20, 0.20 };
		setLayout(basePanelLayout);

		GridBagConstraints nameLabelConstraints = new GridBagConstraints();
		nameLabelConstraints.gridx = 0;
		nameLabelConstraints.gridy = 0;
		JLabel nameLabel = new JLabel();
		nameLabel.setText("Name: Sergio Bugallo Enjamio");
		add(nameLabel, nameLabelConstraints);

		GridBagConstraints dniLabelConstraints = new GridBagConstraints();
		dniLabelConstraints.gridx = 0;
		dniLabelConstraints.gridy = 1;
		JLabel dniLabel = new JLabel();
		dniLabel.setText("DNI: 47387704E");
		add(dniLabel, dniLabelConstraints);

		GridBagConstraints accountLabelConstraints = new GridBagConstraints();
		accountLabelConstraints.gridx = 0;
		accountLabelConstraints.gridy = 2;
		JLabel accountLabel = new JLabel();
		accountLabel.setText("Account: psi4");
		add(accountLabel, accountLabelConstraints);

		GridBagConstraints bannerConstraints = new GridBagConstraints();
		bannerConstraints.gridx = 0;
		bannerConstraints.gridy = 4;
		JLabel bannerLabel = new JLabel();
		ImageIcon icon = new ImageIcon("banner.png");
		bannerLabel.setIcon(icon);
		add(bannerLabel, bannerConstraints);

		setVisible(true);

	}
}

@SuppressWarnings("serial")
class SetRoundsWindow extends JDialog {
	public SetRoundsWindow(psi04_MainWindow mainWindow) {
		setTitle("Set Number Of Rounds");
		setSize(300, 200);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		GridBagLayout basePanelLayout = new GridBagLayout();
		basePanelLayout.columnWeights = new double[] { 0.5, 0.5 };
		basePanelLayout.rowWeights = new double[] { 0.25, 0.25, 0.25, 0.25 };
		setLayout(basePanelLayout);

		GridBagConstraints roundsLabelConstraints = new GridBagConstraints();
		JLabel roundsLabel = new JLabel("Number Of Rounds:");
		roundsLabelConstraints.gridx = 0;
		roundsLabelConstraints.gridy = 0;
		add(roundsLabel, roundsLabelConstraints);

		GridBagConstraints roundsTextConstraints = new GridBagConstraints();
		roundsTextConstraints.gridx = 1;
		roundsTextConstraints.gridy = 0;
		add(mainWindow.getTextNumberOfRounds(), roundsTextConstraints);

		GridBagConstraints roundsSliderConstraints = new GridBagConstraints();
		roundsSliderConstraints.gridwidth = 2;
		roundsSliderConstraints.gridx = 0;
		roundsSliderConstraints.gridy = 1;

		add(mainWindow.getSliderNumberOfRounds(), roundsSliderConstraints);

		GridBagConstraints okButtonConstraints = new GridBagConstraints();
		okButtonConstraints.gridwidth = 2;
		okButtonConstraints.gridx = 0;
		okButtonConstraints.gridy = 2;

		JButton okButton = new JButton();
		okButton.addActionListener(new HidDialogListener(this));
		okButton.setText("OK");
		okButton.setEnabled(true);
		add(okButton, okButtonConstraints);

		mainWindow.getSliderNumberOfRounds().setVisible(true);
		mainWindow.getTextNumberOfRounds().setVisible(true);

	}
}

// ####################### AUXILIAR CLASSES #######################

/**
 * This class will display a dialog with the
 * game evolution (money)
 */
@SuppressWarnings("serial")
class OverallMoneyDialog extends JDialog {

	public OverallMoneyDialog(int numberOfPlayers, List<Object[]> data) {

		setTitle("Game Evolution");
		setSize(500, 500);

		//We create the chart
		JFreeChart chart = ChartFactory.createXYLineChart("Overall Money", "Round", "Money",
				updateData(numberOfPlayers, data), PlotOrientation.VERTICAL, true, true, false);

		//We create the panel
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));

		//We print the values
		final XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		for (int i = 0; i < numberOfPlayers; i++) {

			// We assign a random color
			Random random = new Random();
			int color1 = (int)(random.nextDouble()*255);
			int color2 = (int)(random.nextDouble()*255);
			int color3 = (int)(random.nextDouble()*255);
			renderer.setSeriesPaint(i, new Color(color1, color2, color3));
			renderer.setSeriesStroke(i, new BasicStroke(1.0f));
		}

		plot.setRenderer(renderer);
		setContentPane(chartPanel);

		setVisible(true);

	}

	/**
	 * This function will update the data in the chart
	 */
	public XYSeriesCollection updateData(int numberOfPlayers, List<Object[]> data) {
		final XYSeriesCollection dataset = new XYSeriesCollection();

		for (int i = 0; i < numberOfPlayers; i++) {

			final XYSeries player = new XYSeries("Player " + i);

			for (int j = 0; j < data.size(); j++) {
				player.add(j + 1, (Double) data.get(j)[i]);
			}

			dataset.addSeries(player);
		}

		return dataset;
	}

}

/** This class will display the game ranking
 */
@SuppressWarnings("serial")
class RankingWindow extends JDialog {
	public RankingWindow(List<Object[]> players) throws IOException {
		setTitle("Ranking");
		setSize(150, 150);

		//We create the layout
		GridBagLayout basePanelLayout = new GridBagLayout();
		basePanelLayout.columnWeights = new double[] { 1.0 };
		basePanelLayout.rowWeights = new double[] { 0.33, 0.33, 0.33 };
		setLayout(basePanelLayout);

		String[] ranking = new String[players.size()];

		//We calculate the ranking
		for (int i = 0; i < players.size(); i++) {

			int place = 0;
			Object[] player = players.get(i);

			for (int j = 0; j < players.size(); j++) {

				Object[] compared = players.get(j);
				if (((Double) player[1]).compareTo((Double) compared[1]) < 0)
					place++;
			}

			ranking[place] = (place + 1) + ". Player " + player[0];
		}

		//We create the ranking
		GridBagConstraints firstConstraints = new GridBagConstraints();
		firstConstraints.gridx = 0;
		firstConstraints.gridy = 0;
		JLabel firstLabel = new JLabel();

		GridBagConstraints secondConstraints = new GridBagConstraints();
		secondConstraints.gridx = 0;
		secondConstraints.gridy = 1;
		JLabel secondLabel = new JLabel();

		GridBagConstraints thirdConstraints = new GridBagConstraints();
		thirdConstraints.gridx = 0;
		thirdConstraints.gridy = 2;
		JLabel thirdLabel = new JLabel();

		if (players.size() > 0) {

			firstLabel.setText(ranking[0]);

			if (players.size() > 1) {

				secondLabel.setText(ranking[1]);

				if (players.size() > 2) {

					thirdLabel.setText(ranking[2]);
				} else
					thirdLabel.setText("3. NONE");

			} else {
				secondLabel.setText("2. NONE");
				thirdLabel.setText("3. NONE");
			}

		} else {
			firstLabel.setText("1. NONE");
			secondLabel.setText("2. NONE");
			thirdLabel.setText("3. NONE");
		}

		//We add the ranking
		add(firstLabel, firstConstraints);
		add(secondLabel, secondConstraints);
		add(thirdLabel, thirdConstraints);

		setVisible(true);

	}
}

/** This class will display a dialog with stats
 *  of the players
 */
@SuppressWarnings("serial")
class StatsWindow extends JDialog {
	public StatsWindow(Object[] data) throws IOException {
		setTitle("Player Stats");
		setSize(700, 700);

		//We create the layout
		GridBagLayout basePanelLayout = new GridBagLayout();
		basePanelLayout.columnWeights = new double[] { 0.5, 0.5 };
		basePanelLayout.rowWeights = new double[] { 0.5, 0.5 };
		setLayout(basePanelLayout);

		// ROLES BAR CHART
		GridBagConstraints barConstraints = new GridBagConstraints();
		barConstraints.gridwidth = 2;
		barConstraints.gridx = 0;
		barConstraints.gridy = 1;

		barConstraints.fill = GridBagConstraints.BOTH;

		//We create the chart
		JFreeChart barChart = ChartFactory.createBarChart("ROLES PLAYED", "Role", "Number of Times", updateDataRoleChart(data),
				PlotOrientation.HORIZONTAL, true, true, false);

		ChartPanel rolesChartPanel = new ChartPanel(barChart);
		rolesChartPanel.setPreferredSize(new java.awt.Dimension(560, 367));

		add(rolesChartPanel, barConstraints);
		
		// DEFECTOR PIE CHART
		GridBagConstraints defectorConstraints = new GridBagConstraints();
		defectorConstraints.gridx = 0;
		defectorConstraints.gridy = 0;
		defectorConstraints.fill = GridBagConstraints.BOTH;

		//We create the chart
		JFreeChart defectorChart = ChartFactory.createPieChart(      
		         "DEFECTOR STATS",  
		         updateDataDefectorChart(data),           
		         true, true, false);
		
		ChartPanel defectorChartPanel = new ChartPanel(defectorChart);
		defectorChartPanel.setPreferredSize(new java.awt.Dimension(560, 367));

		add(defectorChartPanel, defectorConstraints);
		
		// INSPECTOR PIE CHART
		GridBagConstraints inspectorConstraints = new GridBagConstraints();
		inspectorConstraints.gridx = 1;
		inspectorConstraints.gridy = 0;
		inspectorConstraints.fill = GridBagConstraints.BOTH;
		
		//We create the chart
		JFreeChart inspectorChart = ChartFactory.createPieChart(      
		         "INSPECTOR STATS",  
		         updateDataInspectorChart(data),           
		         true, true, false);
		
		ChartPanel inspectorChartPanel = new ChartPanel (inspectorChart);
		inspectorChartPanel.setPreferredSize(new java.awt.Dimension(560, 367));

		add(inspectorChartPanel, inspectorConstraints);
		
		setVisible(true);

	}

	/**This function updates the roles pie chart data
	 * 
	 * @param data
	 * @return
	 */
	public CategoryDataset updateDataRoleChart(Object[] data) {

		final String cooperator = "Cooperator";
		final String defector = "Defector";
		final String inspector = "Inspector";
		final String playerName = "Player " + (Integer) data[0];
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		dataset.addValue((Integer) data[2], cooperator, playerName);
		dataset.addValue((Integer) data[3], defector, playerName);
		dataset.addValue((Integer) data[4], inspector, playerName);

		return dataset;

	}

	/**This function updates the defector caught/not caught pie chart
	 * 
	 * @param data
	 * @return
	 */
	public PieDataset updateDataDefectorChart(Object[] data) {

		DefaultPieDataset dataset = new DefaultPieDataset( );
		dataset.setValue("Times Caught", (Integer) data[5]);
		dataset.setValue("Times Not Caught", (Integer) data[6]);
		return dataset;

	}

	/**This function updates the inspector caught/not caught pie chart
	 * 
	 * @param data
	 * @return
	 */
	public PieDataset updateDataInspectorChart(Object[] data) {

		DefaultPieDataset dataset = new DefaultPieDataset( );
		dataset.setValue("Caught Someone", (Integer) data[7]);
		dataset.setValue("Not Caught Someone", ((Integer)data[4])-((Integer) data[6]));
		return dataset;


	}
}
