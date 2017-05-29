package no.uib.info233.v2017.obe006.oblig4;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Represents "you"
 * 
 * @author obe006
 *
 */
public class HumanPlayer extends Player {

	private String humanRandom;

	public HumanPlayer(String name) {
		super(name);
		setHomePosition(3);
		setRandom();
	}

	/**
	 * Makes a move.
	 * 
	 * @param move
	 */
	public void makeNextMove(int move) {
		setMove(move);
	}

	/**
	 * Makes a move vs the AI.
	 * 
	 * @param move
	 */
	public void makeNextMoveVsAI(int move) {
		makeNextMove(move);
		setEnergy(getEnergy() - move);

	}

	/**
	 * Getter for HumanPlayer random.
	 * 
	 * @return String humanRandom
	 */
	public String getRandom() {
		return humanRandom;
	}

	/**
	 * Setter for humanRandom Using RandomStringUtils from apache
	 */
	public void setRandom() {
		this.humanRandom = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
	}

}
