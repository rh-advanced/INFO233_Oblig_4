package no.uib.info233.v2017.obe006.oblig4;

import java.util.Random;

/**
 * One robot with some behavior
 * 
 * @author obe006
 *
 */
public class RobotOne extends Player {

	/**
	 * Constructor for RobotOne
	 * 
	 * @param name
	 *            of robot
	 */
	public RobotOne(String name) {
		super(name);
		setHomePosition(-3);
	}

	/**
	 * Makes the move
	 * 
	 * @return
	 */
	private int makeAMove() {
		if (getEnergy() == 1) {
			setMove(1);
			return 1;
		} else if (getEnergy() == 0) {
			setMove(0);
			return 0;
		}
		Random randomNumber = new Random();
		int useEnergy = randomNumber.nextInt(getEnergy());
		setMove(useEnergy);
		return useEnergy;
	}

	/**
	 * Gets a random move from private method that generates the move then
	 * updates move.
	 * 
	 */
	public void makeNextMove() {
		setEnergy(getEnergy() - this.makeAMove());
	}

}
