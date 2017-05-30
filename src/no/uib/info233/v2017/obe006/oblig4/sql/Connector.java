package no.uib.info233.v2017.obe006.oblig4.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Responsible for communication with database.
 * 
 * @author obe006
 *
 */
public class Connector {

	static final String HOST = "wildboy.uib.no";
	static final String DBNAME = "oblig4";
	static final int PORT = 3306;
	static final String USRNM = "Memphur";
	static final String URSPW = "8J<?zxu4.5$[6%NU";
	public Connection conn = null;
	Statement stmt = null;
	PreparedStatement preparedStatement = null;
	ResultSet rs = null;

	/**
	 * Establishes connection with database
	 */
	public Connector() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}

		catch (ClassNotFoundException cnfe) {
			System.out.println("Feil i lasting av jdbc-driver " + cnfe);
		}

		String mySqlUrl = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DBNAME;
		System.out.println("Connecting to database...");

		conn = null;
		try {
			conn = DriverManager.getConnection(mySqlUrl, USRNM, URSPW);
		} catch (SQLException e) {
			System.out.println("Error in connection, check console.");
			e.printStackTrace();
			return;
		}
		if (conn != null) {
			System.out.println("Connected.");

		} else {
			System.out.println("Could not connect to database.");
		}
	}

	/**
	 * Inserts playername and score in to database.
	 * 
	 * @param name
	 *            of player
	 * @param points
	 *            players points
	 */
	public void insertRanking(String name, Float points) {
		String insertTableSQL = "INSERT INTO ranking" + "(player, score) VALUES" + "(?, ?);";

		try {
			preparedStatement = conn.prepareStatement(insertTableSQL);
			preparedStatement.setString(1, name);
			preparedStatement.setFloat(2, points);
			System.out.println("Inserting score to database.");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inserts the game into the DB
	 * 
	 * @param game_ID
	 * @param player1
	 * @param player2
	 */
	public void insertGamesInProgress(String game_ID, String player1, String player2) {
		String insertTableSQL = "INSERT INTO game_in_progress"
				+ "(game_id, player_1, player_2, game_position, player_1_energy, player_2_energy, move_number  ) VALUES"
				+ "(?, ?, ?, ?, ?, ?, ?);";

		try {
			preparedStatement = conn.prepareStatement(insertTableSQL);
			preparedStatement.setString(1, game_ID);
			preparedStatement.setString(2, player1);
			preparedStatement.setString(3, player2);
			preparedStatement.setInt(4, 0);
			preparedStatement.setInt(5, 100);
			preparedStatement.setInt(6, 100);
			preparedStatement.setInt(7, 0);
			System.out.println("Inserting into games_in_progress");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertGamesInProgressNewLine(String game_ID, int gamePosition, String player1, String player2,
			int player_1_energy, int player_2_energy, int numberOfMoves) {
		String insertTableSQL = "INSERT INTO game_in_progress"
				+ "(game_id, player_1, player_2, game_position, player_1_energy, player_2_energy, move_number  ) VALUES"
				+ "(?, ?, ?, ?, ?, ?, ?);";

		try {
			preparedStatement = conn.prepareStatement(insertTableSQL);
			preparedStatement.setString(1, game_ID);
			preparedStatement.setString(2, player1);
			preparedStatement.setString(3, player2);
			preparedStatement.setInt(4, gamePosition);
			preparedStatement.setInt(5, player_1_energy);
			preparedStatement.setInt(6, player_2_energy);
			preparedStatement.setInt(7, numberOfMoves);
			System.out.println("Inserting into games_in_progress");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet loadGame(String game_ID) {
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT player_1, player_2, game_position, player_1_energy, player_2_energy FROM saved_games WHERE game_id='"
					+ game_ID + "';";
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * Makes a save in the DB.
	 * 
	 * @param game_ID
	 * @param player1
	 * @param player2
	 * @param game_position
	 * @param player_1_energy
	 *            -player
	 * @param player_2_energy
	 *            - thebot
	 */
	public void saveGame(String game_ID, String player1, String player2, int game_position, int player_1_energy,
			int player_2_energy) {
		String insertTableSQL = "INSERT INTO saved_games"
				+ "(game_id, player_1, player_2, game_position, player_1_energy, player_2_energy ) VALUES"
				+ "(?, ?, ?, ?, ?, ?);";
		try {
			preparedStatement = conn.prepareStatement(insertTableSQL);
			preparedStatement.setString(1, game_ID);
			preparedStatement.setString(2, player1);
			preparedStatement.setString(3, player2);
			preparedStatement.setInt(4, game_position);
			preparedStatement.setInt(5, player_1_energy);
			preparedStatement.setInt(6, player_2_energy);
			System.out.println("Saving game");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update the fields in opengame
	 * 
	 * @param player1
	 *            is the field player1 in the DB
	 * @param player1random
	 *            is the field player_1_random in the DB
	 * @param player2
	 *            is "you"
	 * @param player2random
	 *            is "your random"
	 */
	public void updateOpenGames(String player1, String player1random, String player2, String player2random) {

		System.out.println("Updating open games...");
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql = "UPDATE open_games " + "SET player_2 = '" + player2 + "' WHERE player_1_random = '" + player1random
				+ "';";
		String sql2 = "UPDATE open_games " + "SET player_2_random = '" + player2random + "' WHERE player_1_random = '"
				+ player1random + "';";
		try {
			stmt.executeUpdate(sql);
			stmt.executeUpdate(sql2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ResultSet returnGameInProgress(String game_ID, int move_number) {
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT game_position, player_1_energy, player_2_energy, player_1_move, player_2_move, move_number FROM game_in_progress WHERE game_id='"
					+ game_ID + "' AND move_number='" + move_number + "';";
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * Returns a resultset with the highest move in the db with that game_ID
	 * 
	 * @param game_ID
	 * @return
	 */
	public ResultSet getLastMoveNumber(String game_ID) {
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT MAX(move_number) FROM game_in_progress WHERE game_id = '" + game_ID + "';";
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * If you are the host you are player_1_move if you are not the host you are
	 * player_2_move
	 * 
	 * @param game_ID
	 * @param move_number
	 * @param humanMove
	 */
	public void updatePlayerMove(String game_ID, int move_number, int humanMove, String player_x_move) {
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String sql = "UPDATE game_in_progress " + "SET " + player_x_move + " = '" + humanMove + "' WHERE game_id = '"
				+ game_ID + "' AND move_number ='" + move_number + "';";
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the move as an int
	 * 
	 * @param game_ID
	 * @param move_number
	 *            the amount of moves that has taken place in the game
	 * @return int move
	 */
	public ResultSet getOpponentMove(String game_ID, int move_number, String player_X_move) {
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT " + player_X_move + " FROM game_in_progress WHERE game_id = '" + game_ID
					+ "' AND move_number = '" + move_number + "';";
			rs = stmt.executeQuery(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}

	/**
	 * Delete the game in progress colums where game_ID matches
	 * 
	 * @param game_ID
	 */
	public void deleteGameInProgress(String game_ID) {

		try {
			stmt = conn.createStatement();
			String sql = "DELETE FROM game_in_progress  WHERE game_id='" + game_ID + "';";
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns "my" open game from the DB
	 * 
	 * @param myRandom
	 *            my random key
	 * @return a resultset of my game
	 */
	public ResultSet returnMyOpenGame(String myRandom) {
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT player_2, player_2_random FROM open_games WHERE player_1_random='" + myRandom + "';";
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * Gets all the open games from the DB
	 * 
	 * @return all open games as a resultset
	 */
	public ResultSet returnOpenGames() {
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT * FROM open_games";
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;

	}

	/**
	 * Inserts a new game to the DB
	 * 
	 * @param name
	 *            your name
	 * @param random
	 *            your random key
	 */
	public void insertNewGame(String name, String random) {
		String insertTableSQL = "INSERT INTO open_games" + "(player_1, player_1_random) VALUES" + "(?, ?);";
		System.out.println("Inserting new game into DB...");
		try {
			preparedStatement = conn.prepareStatement(insertTableSQL);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, random);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}