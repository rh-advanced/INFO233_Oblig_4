package no.uib.info233.v2017.obe006.oblig4;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.jgoodies.forms.factories.DefaultComponentFactory;

/**
 * Responsible for UI of the game and interaction with the UI
 * 
 * @author obe006
 *
 */
public class UI {

	public JFrame frame;
	Gamemaster gamemaster;
	TextArea textArea;// large text area
	JButton fightChosenPlayerButton;// fight chosen player
	JButton newGameButton;// make new game
	JButton makeMoveButton;
	JComboBox comboBox;
	HashMap<String, Integer> p1NameMap;
	int currentPos = 0;
	boolean playing = true;
	TextField yourEnergy;
	TextField opponentEnergy;
	JButton playVsAIButton;
	private JButton makeMoveVsAIButton;
	TextArea debugArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UI window = new UI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UI() {
		initialize();
	}

	/**
	 * Used to add text to the textarea in UI
	 * 
	 * @param text
	 *            to be added
	 */
	public void addText(String text) {
		textArea.setText(textArea.getText() + "\n" + text);
		textArea.setCaretPosition(50000);
	}

	/**
	 * Add text to debugger
	 * 
	 * @param text
	 */
	public void addDebugText(String text) {
		debugArea.setText(debugArea.getText() + "\n" + text);
		debugArea.setCaretPosition(50000);
	}

	/**
	 * Clears text from debugger
	 */
	public void clearDebugger() {
		debugArea.setText(null);
	}

	/**
	 * Adds a player to the dropdown menu
	 * 
	 * @param player
	 */
	@SuppressWarnings("unchecked")
	public void addToSelBox(String player) {
		comboBox.addItem(player);
	}

	public boolean debug() {
		if (debugArea.isVisible()) {
			return true;
		} else
			return false;
	}

	/**
	 * Checks what you currently have selected on the dropdown menu and uses
	 * that to join the chosen players game.
	 * 
	 * @throws InterruptedException
	 */
	public void doSelectionCheckAndJoin() throws InterruptedException {
		String selectedItem = comboBox.getSelectedItem().toString();
		if (debug()) {
			addDebugText("Selected item: " + selectedItem);
		}
		int player1RandomArrayid = p1NameMap.get(selectedItem);
		if (debug()) {
			addDebugText("player1RandomArrayID:" + player1RandomArrayid);
		}
		gamemaster.joinOpenGame(player1RandomArrayid);
		if (debug()) {
			addDebugText("Setting the opponentrandom to: " + gamemaster.p1RandomList.get(player1RandomArrayid));
			addDebugText("Setting the opponentname to: " + gamemaster.p1NamesList.get(player1RandomArrayid));
			addDebugText(
					"Updating open_games in the database with \n OpponentName OpponentRandom playerName playerRandom ");
			addDebugText(gamemaster.getOpponent().getName() + " " + gamemaster.getOpponent().getRandom() + " "
					+ gamemaster.getHumanPlayer().getName() + " " + gamemaster.getHumanPlayer().getRandom());
		}
		comboBox.setVisible(false);
		int timeout = 0;
		if (debug()) {
			addDebugText("checking if the opponent has started the game..");
		}
		while (gamemaster.didOpponentStartTheGame() == false) {
			addText("Waiting for opponent to start the game");
			timeout++;
			TimeUnit.SECONDS.sleep(2);
			if (timeout == 5) {
				String answer = JOptionPane.showInputDialog(null,
						"Do you want to wait more? Hit ok if you do, cancel if no.", "yes");
				if (answer != null && answer.toLowerCase().contains("yes")) {
					timeout = 0;
					if (debug()) {
						addDebugText("Player chose to wait more and timeout is reset and set to: " + timeout);
					}
				} else {
					if (debug()) {
						addDebugText("Player chose to leave. Not starting game.");
					}
					fightChosenPlayerButton.setVisible(false);
					addText("You chose to leave.");
					return;
				}
			}
		}
		fightChosenPlayerButton.setVisible(false);
		makeMoveButton.setVisible(true);
		gamemaster.setHost(false);
		if (debug()) {
			addDebugText("Updating UI buttons and host should be false: " + gamemaster.isHost());
		}
		addText("You chose " + selectedItem + " as your opponent. Good luck!");
		updateUIEnergy();
	}

	/**
	 * Switch case to print current position of the game
	 * 
	 * @param currentPos
	 */
	public void showCurrentPos(int currentPos) {
		if (debug()) {
			addDebugText("The current position is " + gamemaster.getHumanPlayer().getPosition());
		}
		switch (currentPos) {
		case -3:
			addText("|>O<  O  O  O  O  O  O|");
			break;
		case -2:
			addText("|O  >O<  O  O  O  O  O|");
			break;
		case -1:
			addText("|O  O  >O<  O  O  O  O|");
			break;
		case 0:
			addText("|O  O  O  >O<  O  O  O|");
			break;
		case 1:
			addText("|O  O  O  O  >O<  O  O|");
			break;
		case 2:
			addText("|O  O  O  O  O  >O<  O|");
			break;
		case 3:
			addText("|O  O  O  O  O  O  >O<|");
			break;
		default:
			break;
		}
	}

	/**
	 * Called when clicking the make new game button. Creates a new game and
	 * waits for an opponent to join.
	 * 
	 * @throws SQLException
	 */
	public void createANewGame() throws SQLException {
		int timeOut = 0;
		comboBox.setVisible(false);
		fightChosenPlayerButton.setVisible(false);
		newGameButton.setVisible(false);
		gamemaster.makeNewGame(gamemaster.getHumanPlayer());
		if (debug()) {
			addDebugText("Inserting a new game in to open_games with PlayerName and PlayerRandom");
			addDebugText(gamemaster.getHumanPlayer().getName() + " " + gamemaster.getHumanPlayer().getRandom());
		}
		while (gamemaster.checkForOpponent(gamemaster.getHumanPlayer().getRandom()) == false) {
			if (debug()) {
				addDebugText("Checking DB if an opponent has joined.");
			}
			try {
				addText("Checking for other player...");
				TimeUnit.SECONDS.sleep(2);
				timeOut++;
				if (timeOut == 10) {
					if (debug()) {
						addDebugText("Waited 20 seconds and nobody joined.");
					}
					break;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (gamemaster.checkForOpponent(gamemaster.getHumanPlayer().getRandom()) == true) {
			if (debug()) {
				addDebugText("An opponent has joined the game with the name: " + gamemaster.getOpponent().getName());
				addDebugText("Their random id is: " + gamemaster.getOpponent().getRandom());
			}
			textArea.setText(null);
			gamemaster.hostOpenGame();
			if (debug()) {
				addDebugText("Host should be true: " + gamemaster.isHost());
				addDebugText("Player homeposition should be -3: " + gamemaster.getHumanPlayer().getHomePosition());
				addDebugText("Opponent home position should be 3: " + gamemaster.getOpponent().getHomePosition());
				addDebugText("Creating a game in progress in the DB.");
			}
			addText(gamemaster.getOpponent().getName() + " has joined the game. And you are hosting it. Good luck.");
			gamemaster.registerPlayers();
			if (debug()) {
				addDebugText("Registering the players in the gamemaster. Updating UI energy. Showing move button.");
			}
			makeMoveButton.setVisible(true);
			updateUIEnergy();
		} else {
			textArea.setText(null);
			comboBox.setVisible(true);
			newGameButton.setVisible(true);
			addText("Unable to find an opponent");
		}
	}

	/**
	 * Prompts the user to choose a name and uses that name in the gamemaster.
	 */
	public boolean chooseName() {
		String name = JOptionPane.showInputDialog(null, "Enter your name");
		if (debug()) {
			addDebugText("The chosen name is: " + name);
		}
		boolean success = false;
		if (name != null) {
			if (debug()) {
				addDebugText("checking that length of name is more than or == 1. It is: " + name.length());
			}
			if (name.length() >= 1) {
				addText("Your name is set to: " + name);
				gamemaster = new Gamemaster(name);
				success = true;
			}
		}
		if (debug()) {
			addDebugText("gamemaster should be made and success is true?: " + success);
		}
		return success;
	}

	/**
	 * updates the energy levels shown in the ui
	 */
	public void updateUIEnergy() {
		int nrg = gamemaster.getHumanPlayer().getEnergy();
		yourEnergy.setText(Integer.toString(nrg));
		if (gamemaster.isCPUGame() == false) {
			int onrg = gamemaster.getOpponent().getEnergy();
			opponentEnergy.setText(Integer.toString(onrg));
		} else {
			int orng = gamemaster.getRobot().getEnergy();
			opponentEnergy.setText(Integer.toString(orng));
		}
		if (debug()) {
			addDebugText("Updating the UI with correct energylevels.");
		}

	}

	/**
	 * Starts a game vs a robot
	 */
	public void makeGameVsAI() {
		newGameButton.setVisible(false);
		makeMoveButton.setVisible(false);
		fightChosenPlayerButton.setVisible(false);
		gamemaster.setHost(true);
		gamemaster.makeRobot();
		gamemaster.registerPlayers();
		addText("starting up a game");
		if (debug()) {
			addDebugText("Starting a game vs the Robot and registering the players. Host should be true: "
					+ gamemaster.isHost());
		}

	}

	/**
	 * Player makes their move, gamemaster makes the robot make a move and the
	 * round is evaluated.
	 * 
	 * @param move
	 */
	public void oneRoundVsAI(int move) {
		if (debug()) {
			addDebugText("Player energy: " + gamemaster.getHumanPlayer().getEnergy());
			addDebugText("Robot energy: " + gamemaster.getRobot().getEnergy());
			addDebugText("");
		}
		gamemaster.playAIRound(move);
		if (debug()) {
			addDebugText("Your chosen move was : " + move);
			addDebugText("Your actual move made was: " + gamemaster.getHumanPlayer().getMove());
			addDebugText("The robot's move was : " + gamemaster.getRobot().getMove());
			addDebugText("Player energy: " + gamemaster.getHumanPlayer().getEnergy());
			addDebugText("Robot energy : " + gamemaster.getRobot().getEnergy());
		}
		showCurrentPos(gamemaster.getHumanPlayer().getPosition());
		addText(gamemaster.whoWonRound());
		updateUIEnergy();
	}

	/**
	 * Makes the actual action that the player make
	 * 
	 * @param move
	 *            the move that the player makes
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	public void doAction(int move) throws SQLException, InterruptedException {
		if (debug()) {
			addDebugText("Clearing moves");
		}
		gamemaster.clearMoves();
		int timeOut = 0;
		if (gamemaster.gameOver() == false) {
			if (debug()) {
				addDebugText("Is the game over? " + gamemaster.gameOver());
			}
			gamemaster.getHumanPlayer().makeNextMove(move);
			if (debug()) {
				addDebugText("Player made the move: " + move);
			}
			try {
				gamemaster.makeHumanMove(gamemaster.getHumanPlayer().getMove());
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if (debug()) {
				addDebugText("Actual move made: " + gamemaster.getHumanPlayer().getMove());
			}
			try {
				addText("Waiting for opponent to make their move...");
				if (debug()) {
					addDebugText("Checking if the opponent has made a move.");
				}
				while (gamemaster.waitForOpponent() == false) {
					TimeUnit.SECONDS.sleep(2);
					addText("Still waiting...");
					timeOut++;
					if (timeOut == 15) {
						addText("Opponent timed out.");
						break;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (gamemaster.isHost() == false) {
				if (debug()) {
					addDebugText("I am a host? false: " + gamemaster.isHost());
				}
				try {
					while (gamemaster.waitForOpponent() == false) {
						TimeUnit.SECONDS.sleep(2);
						addText("Still waiting...");
						timeOut++;
						if (timeOut == 15) {
							addText("Opponent timed out.");
							break;
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					TimeUnit.SECONDS.sleep(2);
					if (debug()) {
						addDebugText("Getting the opponents move from the database.");
						addDebugText(
								"Parameters used gameID: " + gamemaster.getGameID() + " Rounds: " + gamemaster.rounds);
					}
					gamemaster.getOpponentMoveDB(gamemaster.getGameID(), gamemaster.rounds);
					if (debug()) {
						addDebugText(
								"Updating the state of the gamemaster. setting correct energylevels and positions.");
					}
					gamemaster.updateGamemasterState();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				addText(gamemaster.whoWonRound());
				currentPos = gamemaster.getHumanPlayer().getPosition();
				showCurrentPos(currentPos);
				gamemaster.rounds++;
				if (debug()) {
					addDebugText("Rounds: " + gamemaster.rounds);
				}
			} else {
				try {
					if (debug()) {
						addDebugText(
								"Updating the state of the gamemaster. setting correct energylevels and positions.");
					}
					gamemaster.updateGamemasterState();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					if (debug()) {
						addDebugText("Evaluating the positions of the players. Current pos: "
								+ gamemaster.getHumanPlayer().getPosition());
					}
					gamemaster.evaluatePositions();
					gamemaster.makeNewGameInProgressInsertion();
					if (debug()) {
						addDebugText("Position now: " + gamemaster.getHumanPlayer().getPosition());
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				addText(gamemaster.whoWonRound());
				currentPos = gamemaster.getHumanPlayer().getPosition();
				showCurrentPos(currentPos);

				gamemaster.rounds++;
				if (debug()) {
					addDebugText("Rounds: " + gamemaster.rounds);
				}
			}
		}
		if (gamemaster.gameOver()) {
			if (debug()) {
				addDebugText("Game is over.");
				addDebugText("Updating state of gamemaster.");
			}
			try {
				gamemaster.updateGamemasterState();
			} catch (

			SQLException e) {
				e.printStackTrace();
			}
			currentPos = gamemaster.getHumanPlayer().getPosition();
			gamemaster.scoreGiver();
			gamemaster.updateRanking();
			if (debug()) {
				addDebugText("Player score: " + gamemaster.getHumanPlayer().getEarnedPoints());
				addDebugText("Opponent score: " + gamemaster.getOpponent().getEarnedPoints());
			}
			addText("Game is over");
			if (gamemaster.isHost()) {
				if (debug()) {
					addDebugText("Waiting 4sec for opponent to finish reading from db.");
				}
				TimeUnit.SECONDS.sleep(4);// wait a moment so the opponent can
											// still read from db

				gamemaster.removeGameFromDB();
				if (debug()) {
					addDebugText("Removing game from game_in_progress");
				}
				addText("Submitting scores to the database.");
			}
			gamemaster.getConnector().conn.close();
			newGameButton.setVisible(true);
			makeMoveButton.setVisible(false);
		}
		updateUIEnergy();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("The best game ever");
		frame.setBounds(100, 100, 1347, 759);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setFont(new Font("Segoe UI", Font.PLAIN, 30));
		menuBar.setBounds(0, 0, 144, 53);
		frame.getContentPane().add(menuBar);

		JMenu mnNewMenu = new JMenu("Menu");
		mnNewMenu.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		menuBar.add(mnNewMenu);

		JMenuItem mntmNewMenuItem = new JMenuItem("New game");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				comboBox.setVisible(false);
				comboBox.removeAllItems();
				textArea.setText("Welcome!");
				if (chooseName()) {
					if (gamemaster.getConnector().conn == null) {
						addText("There was an issue with your connection to the database.");
						return;
					}
					addText("Choose one of the opponents from the dropdown menu above");
					comboBox.setVisible(true);
					fightChosenPlayerButton.setVisible(true);
					try {
						if (gamemaster.canJoinOpenGame()) {
							int y = 0;
							int i = 0;
							p1NameMap = new HashMap<String, Integer>();
							while (y < gamemaster.joinable.size()) {
								i = gamemaster.joinable.get(y);
								addToSelBox(gamemaster.p1NamesList.get(i));
								p1NameMap.put(gamemaster.p1NamesList.get(i), i);
								y++;
							}
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(frame, "You must choose a name", "Error", JOptionPane.ERROR_MESSAGE);
					addText("Make sure to choose a name first");
					fightChosenPlayerButton.setVisible(false);
					return;
				}
			}
		});
		mntmNewMenuItem.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		mnNewMenu.add(mntmNewMenuItem);

		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Load game");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				makeGameVsAI();
				String name = JOptionPane.showInputDialog(null,
						"What name do you want to give the game? You need this when you want to load it.", "Game1");
				try {
					gamemaster.loadGame(name);
					addText("Attempting to load game");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		mntmNewMenuItem_1.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		mnNewMenu.add(mntmNewMenuItem_1);

		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Exit");
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (gamemaster.getConnector().conn != null) {
					try {
						gamemaster.getConnector().conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				System.exit(0);
			}
		});

		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Save game");
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String name = JOptionPane.showInputDialog(null,
						"What name do you want to give the game? You need this when you want to load it.", "Game1");
				gamemaster.saveGame(name);
			}
		});
		mntmNewMenuItem_3.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		mnNewMenu.add(mntmNewMenuItem_3);
		mntmNewMenuItem_2.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		mnNewMenu.add(mntmNewMenuItem_2);

		textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setBounds(237, 115, 469, 595);
		frame.getContentPane().add(textArea);
		// choose player to fight against
		fightChosenPlayerButton = new JButton("FIGHT");
		fightChosenPlayerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					doSelectionCheckAndJoin();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		fightChosenPlayerButton.setBounds(237, 69, 119, 40);
		frame.getContentPane().add(fightChosenPlayerButton);
		fightChosenPlayerButton.setVisible(false);

		comboBox = new JComboBox();
		comboBox.setBounds(371, 74, 160, 31);
		frame.getContentPane().add(comboBox);
		// Choose to fight against AI
		newGameButton = new JButton("MAKE NEW GAME");
		newGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (chooseName())
					try {
						createANewGame();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		});
		newGameButton.setBounds(543, 70, 163, 39);
		frame.getContentPane().add(newGameButton);

		makeMoveButton = new JButton("Make move");
		makeMoveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String move = JOptionPane.showInputDialog(null, "How much energy do you want to spend?", "1");
				int moveInt = Integer.parseInt(move);

				try {
					try {
						doAction(moveInt);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		makeMoveButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		makeMoveButton.setBounds(373, 0, 160, 73);
		makeMoveButton.setVisible(false);
		frame.getContentPane().add(makeMoveButton);

		yourEnergy = new TextField();
		yourEnergy.setEditable(false);
		yourEnergy.setBounds(58, 196, 119, 31);
		frame.getContentPane().add(yourEnergy);

		opponentEnergy = new TextField();
		opponentEnergy.setEditable(false);
		opponentEnergy.setBounds(58, 261, 119, 31);
		frame.getContentPane().add(opponentEnergy);

		JLabel lblNewJgoodiesLabel = DefaultComponentFactory.getInstance().createLabel("Your energy");
		lblNewJgoodiesLabel.setBounds(71, 178, 106, 14);
		frame.getContentPane().add(lblNewJgoodiesLabel);

		JLabel lblNewJgoodiesLabel_1 = DefaultComponentFactory.getInstance().createLabel("Opponents energy");
		lblNewJgoodiesLabel_1.setBounds(71, 241, 106, 14);
		frame.getContentPane().add(lblNewJgoodiesLabel_1);

		playVsAIButton = new JButton("Play vs AI");
		playVsAIButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		playVsAIButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fightChosenPlayerButton.setVisible(false);
				makeMoveButton.setVisible(false);
				newGameButton.setVisible(false);
				makeMoveVsAIButton.setVisible(true);
				if (chooseName()) {
					makeGameVsAI();
					comboBox.setVisible(false);
				} else {
					JOptionPane.showMessageDialog(frame, "You must choose a name", "Error", JOptionPane.ERROR_MESSAGE);
					addText("Make sure to choose a name first");
					fightChosenPlayerButton.setVisible(false);
					return;
				}
			}
		});
		playVsAIButton.setBounds(35, 563, 172, 66);
		frame.getContentPane().add(playVsAIButton);

		makeMoveVsAIButton = new JButton("Make move");
		makeMoveVsAIButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String move = JOptionPane.showInputDialog(null, "How much energy do you want to spend?", "1");
				int moveInt = Integer.parseInt(move);

				oneRoundVsAI(moveInt);
				if (debug()) {
					addDebugText("Is the game over? " + gamemaster.gameOver());

				}
				if (gamemaster.gameOver()) {
					currentPos = gamemaster.getHumanPlayer().getPosition();

					gamemaster.scoreGiverVsAI();
					gamemaster.updateRanking();
					if (debug()) {
						addDebugText("Player score: " + gamemaster.getHumanPlayer().getEarnedPoints());
						addDebugText("robot score: " + gamemaster.getRobot().getEarnedPoints());
					}
					addText("Game is over");
					addText("Submitting scores to the database.");
					makeMoveVsAIButton.setVisible(false);
				}

			}

		});
		makeMoveVsAIButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		makeMoveVsAIButton.setBounds(35, 479, 172, 73);
		makeMoveVsAIButton.setVisible(false);
		frame.getContentPane().add(makeMoveVsAIButton);

		debugArea = new TextArea();
		debugArea.setEditable(false);
		debugArea.setBounds(753, 115, 548, 595);
		frame.getContentPane().add(debugArea);

		Label label = new Label("Debugger");
		label.setBounds(1016, 79, 76, 22);
		frame.getContentPane().add(label);

		JButton debugButton = new JButton("ON / OFF \r\nDebugger");
		debugButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (debugArea.isVisible()) {
					debugArea.setVisible(false);
				} else {
					debugArea.setVisible(true);
				}
			}
		});
		debugButton.setBounds(896, 20, 302, 53);
		frame.getContentPane().add(debugButton);

		JButton clearDebuggerButton = new JButton("Clear debugger");
		clearDebuggerButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		clearDebuggerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clearDebugger();
			}
		});
		clearDebuggerButton.setBounds(838, 78, 172, 31);
		frame.getContentPane().add(clearDebuggerButton);
		comboBox.setVisible(false);
	}
}
