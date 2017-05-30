package no.uib.info233.v2017.obe006.oblig4;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import no.uib.info233.v2017.obe006.oblig4.sql.Connector;

/**
 * The gamemaster assigns two players to play against each other and let the
 * players know when to make their move. It also evaluates the turns and can
 * update
 * 
 * @author obe006
 *
 */
public class Gamemaster {

	ArrayList<Integer> joinable = new ArrayList<Integer>();
	private HumanPlayer playerMe;
	private OpponentPlayer opponent;
	private RobotOne robot;
	ArrayList<String> p1NamesList;
	ArrayList<String> p1RandomList;
	ArrayList<String> p2NamesList;
	ArrayList<String> p2RandomList;
	public int rounds = 0; // current number of rounds, increment at end of a
							// turn
	private Connector connector;
	private String game_ID;
	private boolean host = false;
	private boolean CPUGame = false;

	/**
	 * Check if host is true
	 * 
	 * @return true if you are host
	 */
	public boolean isHost() {
		return host;
	}

	public void setHost(boolean host) {
		this.host = host;
	}

	/**
	 * Constructor without parameter, for testing purposes
	 */
	public Gamemaster() {
		connector = new Connector();
		playerMe = new HumanPlayer("testing");
		opponent = new OpponentPlayer("opponent");
	}

	/**
	 * constructor for gamemaster
	 */
	public Gamemaster(String me) {
		connector = new Connector();
		playerMe = new HumanPlayer(me);
		opponent = new OpponentPlayer("opponent");
	}

	/**
	 * Getter for the connector in the gamemaster
	 * 
	 * @return the connector
	 */
	public Connector getConnector() {
		return connector;
	}

	/**
	 * Players know who the gamemaster is.
	 */
	public void registerPlayers() {
		getHumanPlayer().registerGamemaster(this);
		if (isCPUGame()) {
			getRobot().registerGamemaster(this);
		} else {
			getOpponent().registerGamemaster(this);
		}
	}

	public void makeRobot() {
		RobotOne robot = new RobotOne("RobotOne");
		setRobot(robot);
		setCPUGame(true);
	}

	public void removeGameFromDB() {
		connector.deleteGameInProgress(getGameID());
	}

	/**
	 * Sets the moves to -1 to represent that their moves are cleared and not
	 * been done. Make sure to check for <0
	 */
	public void clearMoves() {
		playerMe.clearMoves();
		opponent.clearMoves();
	}

	/**
	 * The player makes a move and put it into db.
	 * 
	 * @param move
	 *            the amount of energy that the player choose to use.
	 * @throws SQLException
	 */
	public void makeHumanMove(int move) throws SQLException {
		if (move >= getHumanPlayer().getEnergy()) {
			move = getHumanPlayer().getEnergy();
		}
		playerMe.makeNextMove(move);
		if (isHost()) {
			connector.updatePlayerMove(getGameID(), getNumberOfMoves(getGameID()), move, "player_1_move");
		} else {
			connector.updatePlayerMove(getGameID(), getNumberOfMoves(getGameID()), move, "player_2_move");
		}
	}

	/**
	 * Hosts an open game. Removes the open_games entry.
	 * 
	 * @throws SQLException
	 */
	public void hostOpenGame() throws SQLException {
		setHost(true);
		playerMe.setHomePosition(-3);
		opponent.setHomePosition(3);
		createGameInProgress(playerMe, opponent);
		removeGameFromOpenGames(getHumanPlayer().getRandom());
	}

	/**
	 * Removes the games from open games where player_1_random matches the
	 * parameter.
	 * 
	 * @param player_1_random
	 */
	private void removeGameFromOpenGames(String player_1_random) {
		getConnector().deleteFromOpenGames(player_1_random);
	}

	/**
	 * Returns the move that the opponent has made.
	 * 
	 * @param game_ID
	 *            String
	 * @param move_number
	 *            int
	 * @return opponents move as an int
	 * @throws SQLException
	 */
	public int getOpponentMoveDB(String game_ID, int move_number) throws SQLException {
		int move = 0;
		String opponentField = "player_1_move";
		if (isHost()) {
			opponentField = "player_2_move";
		}
		ResultSet sql = connector.getOpponentMove(game_ID, move_number, opponentField);
		if (sql.next()) {
			move = sql.getInt(1);
			if (sql.wasNull() == false) {
				getOpponent().setMove(move);
			} else {
				move = -1;
			}
		}
		return move;
	}

	/**
	 * Checks whether or not the opponent has made a move.
	 * 
	 * @return true if the opponent has made a move.
	 * @throws SQLException
	 */
	public boolean waitForOpponent() throws SQLException {
		boolean madeMove = false;
		int move = getOpponentMoveDB(getGameID(), rounds);
		if (move >= 0) {
			madeMove = true;
		}
		return madeMove;
	}

	public boolean didOpponentStartTheGame() {
		// The Bot_Basic starts on round 1
		if (opponent.getName().contains("Bot_Basic")) {
			rounds = 1;
		}
		ResultSet theGame = connector.returnGameInProgress(getGameID(), rounds);
		boolean result = false;
		try {
			if (theGame.next()) {
				result = true;
			} else {
				result = false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * getter for gameID
	 * 
	 * @return String with the gameID
	 */
	public String getGameID() {
		return game_ID;
	}

	/**
	 * Joins an open game
	 * 
	 * @param playerRandomArrayID
	 */
	public void joinOpenGame(int playerRandomArrayID) {
		try {
			if (canJoinOpenGame()) {
				opponent.setOpponentRandom(p1RandomList.get(playerRandomArrayID));
				opponent.setName(p1NamesList.get(playerRandomArrayID));
				connector.updateOpenGames(opponent.getName(), opponent.getRandom(), playerMe.getName(),
						playerMe.getRandom());
				setGameID();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the game_ID and returns what it has set it as.
	 * 
	 * @return the game id
	 */
	public String setGameID() {
		if (!isHost()) {
			game_ID = opponent.getRandom() + getHumanPlayer().getRandom();
		} else {
			game_ID = getHumanPlayer().getRandom() + opponent.getRandom();
		}
		return game_ID;
	}

	/**
	 * Used to get the latest number of moves done from db.
	 * 
	 * @param game_id
	 * @return int moves
	 * @throws SQLException
	 */
	public int getNumberOfMoves(String game_id) throws SQLException {
		int moves = 0;
		ResultSet sql = connector.getLastMoveNumber(game_id);
		if (sql.next()) {
			moves = sql.getInt(1);
		}
		return moves;
	}

	/**
	 * If you are the host you will insert a new line into the db with the
	 * current game state and new null fields for players to insert their move
	 * 
	 * @throws SQLException
	 */
	public void makeNewGameInProgressInsertion() throws SQLException {
		if (isHost()) {
			int numberOfMoves = getNumberOfMoves(getGameID()) + 1;
			connector.insertGamesInProgressNewLine(getGameID(), getHumanPlayer().getPosition(),
					getHumanPlayer().getName(), getOpponent().getName(), getHumanPlayer().getEnergy(),
					getOpponent().getEnergy(), numberOfMoves);
		}
	}

	/**
	 * Updates the players with their new state
	 * 
	 * @throws SQLException
	 */
	public void updateGamemasterState() throws SQLException {
		ResultSet sql = connector.returnGameInProgress(getGameID(), getNumberOfMoves(getGameID()));
		if (sql.next()) {
			if (isHost()) {
				getHumanPlayer().setEnergy(sql.getInt(2) - getHumanPlayer().getMove());
				getOpponent().setEnergy(sql.getInt(3) - getOpponent().getMove());
			} else {
				getHumanPlayer().setEnergy(sql.getInt(3));
				getOpponent().setEnergy(sql.getInt(2));
			}
			getOpponent().setPosition(sql.getInt(1));
			getHumanPlayer().setPosition(sql.getInt(1));
		}
	}

	/**
	 * Makes request to the connector to create a new game in progress
	 * 
	 * @param playerMe
	 * @param opponent
	 */
	public void createGameInProgress(HumanPlayer playerMe, OpponentPlayer opponent) {
		setGameID();
		connector.insertGamesInProgress(game_ID, playerMe.getName(), opponent.getName());
	}

	/**
	 * Checks if it is possible to join a game. Also creates arraylists with the
	 * info from the DB about open games.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public boolean canJoinOpenGame() throws SQLException {
		boolean canJoin = false;
		ResultSet listGames = returnOpenGames();
		p1NamesList = new ArrayList<String>();
		p1RandomList = new ArrayList<String>();
		p2NamesList = new ArrayList<String>();
		p2RandomList = new ArrayList<String>();
		joinable = new ArrayList<Integer>();

		while (listGames.next()) {
			p1NamesList.add(listGames.getString("player_1"));
			p1RandomList.add(listGames.getString("player_1_random"));
			p2NamesList.add(listGames.getString("player_2"));
			p2RandomList.add(listGames.getString("player_2_random"));
		}
		int i = 0;
		while (i < p1NamesList.size()) {
			if (p1NamesList.get(i) != null && p2NamesList.get(i) == null) {
				joinable.add(i);
			}
			i++;
		}
		i = 0;
		if (joinable.get(i) != null) {
			int x = joinable.get(i);
			opponent.setName(p1NamesList.get(x));
			opponent.setOpponentRandom(p1RandomList.get(x));
			canJoin = true;
		}
		return canJoin;
	}

	/**
	 * Check if i have an opponent
	 * 
	 * @param myRandom
	 * @return boolean true if opponent is there
	 * @throws SQLException
	 */
	public boolean checkForOpponent(String myRandom) throws SQLException {
		ResultSet myGame = connector.returnMyOpenGame(myRandom);
		myGame.next();
		if (myGame.getString("player_2") != null) {
			opponent.setName(myGame.getString("player_2"));
			opponent.setOpponentRandom(myGame.getString("player_2_random"));
			return true;
		} else {
			return false;
		}
	}

	public ResultSet returnOpenGames() {
		return connector.returnOpenGames();
	}

	/**
	 * Save your game in teh database.
	 * 
	 * @param game_ID
	 *            the name you choose for your game.
	 * @return true if it saved.
	 */
	public boolean saveGame(String game_ID) {
		boolean success = false;
		if (connector.conn != null) {
			connector.saveGame(game_ID, getHumanPlayer().getName(), getRobot().getName(),
					getHumanPlayer().getPosition(), getHumanPlayer().getEnergy(), getRobot().getEnergy());
			success = true;
		} else {
			return false;
		}
		return success;
	}

	/**
	 * Loads the game from the db.
	 * 
	 * @param game_ID
	 * @throws SQLException
	 */
	public void loadGame(String game_ID) throws SQLException {
		ResultSet rs = connector.loadGame(game_ID);
		if (rs.next()) {
			robot = new RobotOne(rs.getString("player_2"));
			playerMe = new HumanPlayer(rs.getString("player_1"));
			playerMe.setPosition(rs.getInt("game_position"));
			playerMe.setEnergy(rs.getInt("player_1_energy"));
			robot.setPosition(rs.getInt("game_position"));
			robot.setEnergy(rs.getInt("player_2_energy"));
		}
	}

	/**
	 * Creates a new game.
	 * 
	 * @param player
	 *            (you)
	 */
	public void makeNewGame(Player player) {
		playerMe.setRandom();
		connector.insertNewGame(player.getName(), playerMe.getRandom());
	}

	/**
	 * Makes their moves and evaluates the round.
	 * 
	 * @param move
	 */
	public void playAIRound(int move) {
		if (move >= getHumanPlayer().getEnergy()) {
			move = getHumanPlayer().getEnergy();
		}
		getHumanPlayer().makeNextMoveVsAI(move);
		getRobot().makeNextMove();
		evaluatePositions();
	}

	/**
	 * Gives the player and the AI a score
	 */
	public void scoreGiverVsAI() {
		switch (getHumanPlayer().getPosition()) {
		case -3:
			playerMe.gameOver(-1);
			robot.gameOver(2);
			break;

		case -2:
			playerMe.gameOver(0);
			robot.gameOver(1);
			break;

		case -1:
			playerMe.gameOver((float) 0.25);
			robot.gameOver((float) 0.75);
			break;

		case 0:
			playerMe.gameOver((float) 0.5);
			robot.gameOver((float) 0.5);
			break;

		case 1:
			playerMe.gameOver((float) 0.75);
			robot.gameOver((float) 0.25);
			break;

		case 2:
			playerMe.gameOver(1);
			robot.gameOver(0);
			break;

		case 3:
			playerMe.gameOver(2);
			robot.gameOver(-1);
			break;

		default:
			break;
		}
	}

	/**
	 * Gives the players a score based on the ending position.
	 */
	public void scoreGiver() {
		if (isHost()) {
			switch (opponent.getPosition()) {
			case -3:
				playerMe.gameOver(-1);
				opponent.gameOver(2);
				break;

			case -2:
				playerMe.gameOver(0);
				opponent.gameOver(1);
				break;

			case -1:
				playerMe.gameOver((float) 0.25);
				opponent.gameOver((float) 0.75);
				break;

			case 0:
				playerMe.gameOver((float) 0.5);
				opponent.gameOver((float) 0.5);
				break;

			case 1:
				playerMe.gameOver((float) 0.75);
				opponent.gameOver((float) 0.25);
				break;

			case 2:
				playerMe.gameOver(1);
				opponent.gameOver(0);
				break;

			case 3:
				playerMe.gameOver(2);
				opponent.gameOver(-1);
				break;

			default:
				break;
			}
		} else {
			switch (opponent.getPosition()) {
			case -3:
				playerMe.gameOver(2);
				opponent.gameOver(-1);
				break;

			case -2:
				playerMe.gameOver(1);
				opponent.gameOver(0);
				break;

			case -1:
				playerMe.gameOver((float) 0.75);
				opponent.gameOver((float) 0.25);
				break;

			case 0:
				playerMe.gameOver((float) 0.5);
				opponent.gameOver((float) 0.5);
				break;

			case 1:
				playerMe.gameOver((float) 0.25);
				opponent.gameOver((float) 0.75);
				break;

			case 2:
				playerMe.gameOver(0);
				opponent.gameOver(1);
				break;

			case 3:
				playerMe.gameOver(-1);
				opponent.gameOver(2);
				break;

			default:
				break;
			}
		}
	}

	/**
	 * Checks if the game is over
	 * 
	 * @return true if the game is over.
	 */
	public boolean gameOver() {
		boolean gameover = false;

		if (isCPUGame()) {
			if (getHumanPlayer().getPosition() == getHumanPlayer().getHomePosition()
					|| getRobot().getPosition() == getRobot().getHomePosition()
					|| getHumanPlayer().getEnergy() == 0 && getRobot().getEnergy() == 0)
				gameover = true;
		}

		if (!isCPUGame()) {
			if (getHumanPlayer().getPosition() == getHumanPlayer().getHomePosition()
					|| getOpponent().getPosition() == getOpponent().getHomePosition()
					|| getHumanPlayer().getEnergy() == 0 && getOpponent().getEnergy() == 0) {
				gameover = true;
			} else {
				gameover = false;
			}
		}
		return gameover;
	}

	/**
	 * Checks who won the round and updates the positions.
	 */
	public void evaluatePositions() {

		if (!isCPUGame()) {
			if (getHumanPlayer().getMove() == getOpponent().getMove()) {
				return;
			} else if (getHumanPlayer().getMove() > getOpponent().getMove()) {// youwin
				if (isHost()) {
					getHumanPlayer().setPosition(getHumanPlayer().getPosition() + 1);
					getOpponent().setPosition(getOpponent().getPosition() + 1);
				} else {
					getHumanPlayer().setPosition(getHumanPlayer().getPosition() - 1);
					getOpponent().setPosition(getOpponent().getPosition() - 1);
				}
			} else if (getHumanPlayer().getMove() < getOpponent().getMove()) {// opponentwins
				if (isHost()) {
					getHumanPlayer().setPosition(getHumanPlayer().getPosition() - 1);
					getOpponent().setPosition(getOpponent().getPosition() - 1);
				} else {
					getHumanPlayer().setPosition(getHumanPlayer().getPosition() + 1);
					getOpponent().setPosition(getOpponent().getPosition() + 1);
				}
			}
		} else { // cpugame
			if (getHumanPlayer().getMove() == getRobot().getMove()) {
				return;
			} else if (getHumanPlayer().getMove() > getRobot().getMove()) {// youwin

				getHumanPlayer().setPosition(getHumanPlayer().getPosition() + 1);
				getRobot().setPosition(getRobot().getPosition() + 1);

			} else if (getHumanPlayer().getMove() < getRobot().getMove()) {// robotwins

				getHumanPlayer().setPosition(getHumanPlayer().getPosition() - 1);
				getRobot().setPosition(getRobot().getPosition() - 1);
			}
		}
	}

	/**
	 * Checks who won the round
	 * 
	 * @return a string with who won the round.
	 */
	public String whoWonRound() {
		String whoWon = null;
		if (!isCPUGame()) {
			if (getHumanPlayer().getMove() == getOpponent().getMove()) {
				whoWon = "Round ended in tie.";

			} else if (getHumanPlayer().getMove() > getOpponent().getMove()) {

				whoWon = "You won the round!";
			} else {

				whoWon = "The opponent won the round.";
			}

		} else { // cpugame
			if (getHumanPlayer().getMove() == getRobot().getMove()) {
				whoWon = "Round ended in tie.";

			} else if (getHumanPlayer().getMove() > getRobot().getMove()) {

				whoWon = "You won the round!";
			} else {

				whoWon = "The opponent won the round.";
			}
		}
		return whoWon;
	}

	/**
	 * Attempts to connect to a database to insert the rankings
	 */
	public void updateRanking() {
		connector.insertRanking(playerMe.getName(), playerMe.getEarnedPoints());
		if (!isCPUGame()) {
			connector.insertRanking(opponent.getName(), opponent.getEarnedPoints());

		} else {
			connector.insertRanking(robot.getName(), robot.getEarnedPoints());
		}
	}

	/**
	 * Getter for player1
	 * 
	 * @return player1
	 */
	public OpponentPlayer getOpponent() {
		return opponent;
	}

	/**
	 * getter for player2
	 * 
	 * @return player2
	 */
	public HumanPlayer getHumanPlayer() {
		return playerMe;
	}

	/**
	 * Getter for robot
	 * 
	 * @return RobotoOne the robot.
	 */
	public RobotOne getRobot() {
		return robot;
	}

	/**
	 * Setter for robot
	 * 
	 * @param robot
	 */
	public void setRobot(RobotOne robot) {
		this.robot = robot;
	}

	/**
	 * Checking if it is a CPUGAME
	 * 
	 * @return true if it is a cpugame.
	 */
	public boolean isCPUGame() {
		return CPUGame;
	}

	/**
	 * Setter for cpugame, set to true if it is a game vs ai.
	 * 
	 * @param cPUGame
	 */
	public void setCPUGame(boolean cPUGame) {
		CPUGame = cPUGame;
	}
}
