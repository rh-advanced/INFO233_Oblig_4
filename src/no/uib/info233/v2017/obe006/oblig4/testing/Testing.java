package no.uib.info233.v2017.obe006.oblig4.testing;

import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import no.uib.info233.v2017.obe006.oblig4.Gamemaster;
import no.uib.info233.v2017.obe006.oblig4.Player;
import no.uib.info233.v2017.obe006.oblig4.RobotOne;

/**
 * Testing class, lots of outdated things here that should be removed or
 * changed.
 * 
 * @author obe006
 *
 */
public class Testing {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testInsertGameInProgress() throws SQLException {
		Gamemaster gamemaster = new Gamemaster();
		gamemaster.joinOpenGame(0);
		gamemaster.getConnector().conn.close();
	}

	/**
	 * Tests if it is possible to join an open game, provided that there is one
	 * with a spot open
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testJoinOpenGame() throws SQLException {
		Gamemaster gamemaster = new Gamemaster();
		gamemaster.joinOpenGame(0);
		gamemaster.getConnector().conn.close();
	}

	@Test
	public void testReturnOpenGames() throws SQLException {

		Gamemaster gamemaster = new Gamemaster();
		ResultSet openGames = gamemaster.returnOpenGames();
		gamemaster.getConnector().conn.close();
	}

	@Test
	public void testCanJoinOpenGame() throws SQLException {
		Gamemaster gamemaster = new Gamemaster();
		gamemaster.canJoinOpenGame();
		gamemaster.getConnector().conn.close();
	}

	@Test
	public void testCreateGame() {
		Player player1 = new RobotOne("player1");
		Gamemaster gamemaster = new Gamemaster();
		gamemaster.makeNewGame(player1);
		try {
			gamemaster.getConnector().conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRandomStringLength() {
		String test = null;
		test = RandomStringUtils.randomAlphanumeric(10);
		assertEquals(10, test.length());

	}

	@Test
	public void testLastMoveNumber() throws SQLException {
		Gamemaster gamemaster = new Gamemaster();
		System.out.println(gamemaster.getNumberOfMoves("z6ux79sjnqfez1meejrf"));
		gamemaster.getConnector().conn.close();
	}
}
