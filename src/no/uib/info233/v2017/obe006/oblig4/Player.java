package no.uib.info233.v2017.obe006.oblig4;

/**
 * The class responsible for player information and behavior.
 * 
 * @author obe006
 *
 */
public class Player {
	private static final int MIN_ENERGY = 0;
	private int energy;
	private String name;
	private int currentPosition;
	private float earnedPoints;
	protected Gamemaster gamemaster;
	private int homePosition;
	private int move; // energyUsed

	/**
	 * Constructor for player
	 * 
	 * @param name
	 *            of the player
	 */
	public Player(String name) {
		setEnergy(100);
		setName(name);
		registerGamemaster(gamemaster);
	}

	/**
	 * getter
	 * 
	 * @return your current energylevel
	 */
	public int getEnergy() {
		return energy;
	}

	public void clearMoves() {
		setMove(-1);
	}

	/**
	 * Makes a move based on position
	 * 
	 * @param currentPosition
	 * @param yourEnergy
	 * @param opponentEnergy
	 * @throws SQLException
	 */

	/**
	 * Registers the gamemaster for to the player.
	 * 
	 * @param gamemaster
	 *            the gamemaster to be registered.
	 */
	public void registerGamemaster(Gamemaster gamemaster) {
		setGamemaster(gamemaster);
	}

	/**
	 * Setter for energy, can not be less than 0
	 * 
	 * @param energy
	 */
	public void setEnergy(int energy) {
		if (energy >= MIN_ENERGY)
			this.energy = energy;
		else {
			energy = 0;
		}
	}

	/**
	 * Lets the player know how many points he earned.
	 * 
	 * @param earnedPoints
	 */
	public void gameOver(float earnedPoints) {
		this.earnedPoints = earnedPoints;
	}

	/**
	 * getter
	 * 
	 * @return name of player
	 */
	public String getName() {
		return name;
	}

	/**
	 * setter
	 * 
	 * @param name
	 *            of player
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * getter for position
	 * 
	 * @return position of player
	 */
	public int getPosition() {
		return currentPosition;
	}

	/**
	 * setter for position
	 * 
	 * @param position
	 *            of player
	 */
	public void setPosition(int position) {
		this.currentPosition = position;
	}

	/**
	 * getter for earnedpoints
	 * 
	 * @return the amount of points the player earned, represented as a float.
	 */
	public float getEarnedPoints() {
		return earnedPoints;
	}

	/**
	 * getter for gamemaster
	 * 
	 * @return the gamemaster
	 */
	public Gamemaster getGamemaster() {
		return gamemaster;
	}

	/**
	 * setter for gamemaster
	 * 
	 * @param gamemaster
	 */
	public void setGamemaster(Gamemaster gamemaster) {
		this.gamemaster = gamemaster;
	}

	/**
	 * getter for homeposition, which is the position you recieve the lowest
	 * amount of points on
	 * 
	 * @return homeposition
	 */
	public int getHomePosition() {
		return homePosition;
	}

	/**
	 * setter for homeposition, which is the position you recieve the lowest
	 * amount of points on
	 * 
	 * @param homePosition
	 */
	public void setHomePosition(int homePosition) {
		this.homePosition = homePosition;
	}

	public int getMove() {
		return move;
	}

	public void setMove(int move) {
		this.move = move;
	}
}
