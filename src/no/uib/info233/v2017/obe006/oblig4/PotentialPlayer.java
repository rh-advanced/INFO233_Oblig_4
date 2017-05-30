package no.uib.info233.v2017.obe006.oblig4;

public class PotentialPlayer {

	int id;
	String player_1;
	String player_1_random;

	/**
	 * This is a representation of a player that has an open game Could
	 * potentially be used instead of the arraylists and hashmap to find and
	 * display open games.
	 * 
	 * @param id
	 * @param player_1
	 * @param player_1_random
	 */
	public PotentialPlayer(int id, String player_1, String player_1_random) {
		this.id = id;
		this.player_1 = player_1;
		this.player_1_random = player_1_random;
	}

}
