package strategy;

import java.util.ArrayList;

import agent.Snake;
import item.Item;
import model.SnakeGame;

import utils.AgentAction;
import utils.Position;


public abstract class Strategy {

	private boolean modeTrain;
	
	protected int nbActions;
	protected double epsilon;
	protected double base_epsilon;
	protected double gamma;
	protected double alpha;

	public Strategy() {
	}
	
	public Strategy(int nbActions, double epsilon, double gamma, double alpha) {
		
		this.nbActions = nbActions;
		this.epsilon = epsilon;
		this.base_epsilon = epsilon;
		this.gamma = gamma;
		this.alpha = alpha;
	}
	
	public abstract AgentAction chooseAction(int idxSnake, SnakeGame snakeGame);

	
	public abstract void update(int idx, SnakeGame state,  AgentAction action, SnakeGame nextState, int reward, boolean isFinalState);
	
	
	public boolean isModeTrain() {
		return modeTrain;
	}


	public void learn() {
		
	}

	
	public void setModeTrain(boolean modeTrain) {
		
		this.modeTrain = modeTrain;
		
		if(this.modeTrain) {
			this.epsilon = this.base_epsilon;
		} else {
			this.epsilon = 0;
			
		}
	}
	
public String encodeState(int idxSnake,SnakeGame snakeGame) {
		
		ArrayList<Snake>listSnakes=snakeGame.getSnakes();
		StringBuilder sb = new StringBuilder();
		for(Item i : snakeGame.getItems()) {
			sb.append(i.getX());
			sb.append(i.getY());
			sb.append("|");
		}
		
		for(Snake s : snakeGame.getSnakes()) {
			if(!s.isDead()) {
				int i =0;
				if(s.getId()==idxSnake) {
					sb.append("Y");
				}
				for(Position p : s.getPositions()) {
					sb.append(p.getX());
					sb.append(p.getY());
				}
				sb.append("|");
			}
		}
		return sb.toString();
		
	}


	

	
}
