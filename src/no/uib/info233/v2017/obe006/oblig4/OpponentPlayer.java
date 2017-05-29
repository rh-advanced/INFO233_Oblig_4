package no.uib.info233.v2017.obe006.oblig4;

/**
 * Represents the opponent
 * 
 * @author obe006
 *
 */
public class OpponentPlayer extends Player {

	private String opponentRandom;

	public OpponentPlayer(String name) {
		super(name);
		setHomePosition(-3);
	}

	public String getRandom() {
		return opponentRandom;
	}

	public void setOpponentRandom(String player_2_random) {
		this.opponentRandom = player_2_random;
	}

}
