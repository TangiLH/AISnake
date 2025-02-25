package strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import model.SnakeGame;
import utils.AgentAction;

public class TabularQLearning_duel extends Strategy {
	
	private static Random ran=new Random();
	HashMap<String, double[]> hashMap;
	public TabularQLearning_duel(int nbActions, double epsilon, double gamma, double alpha){
		super( nbActions,  epsilon,  gamma,  alpha);
		this.hashMap=new HashMap<>();
		
	}

	@Override
	public AgentAction chooseAction(int idxSnake, SnakeGame snakeGame) {
		String enState=super.encodeState(idxSnake, snakeGame);
		if(this.hashMap.get(enState)==null) {
			return AgentAction.values()[ran.nextInt(4)];
		}
		AgentAction action=AgentAction.values()[this.maxIndexFromMap(enState)];
		
			return action;
		
		
		
	}

	@Override
	public void update(int idx, SnakeGame state, AgentAction action, SnakeGame nextState, int reward,
			boolean isFinalState) {
		String enState=super.encodeState(idx, state);
		String enNextState=super.encodeState(idx, nextState);
		if(!this.hashMap.containsKey(enState)) {
			this.hashMap.put(enState, new double[4]);
		}
		if(!this.hashMap.containsKey(enNextState)) {
			this.hashMap.put(enNextState, new double[4]);
		}
		if(!this.hashMap.containsKey(enState)||!this.hashMap.containsKey(enNextState)) {
			System.out.println("ERRRRR");
		}
		if(isFinalState) {
			this.hashMap.get(enState)[action.ordinal()]= (1-this.alpha)*this.hashMap.get(enState)[action.ordinal()]
					+this.alpha*(reward);
		}
		else {
		this.hashMap.get(enState)[action.ordinal()]= (1-this.alpha)*this.hashMap.get(enState)[action.ordinal()]
				+this.alpha*(reward+this.maxDoubleFromMap(enNextState));
		}
	}
	
	private double maxDoubleFromMap(String state) {
		return Math.max(Math.max(this.hashMap.get(state)[0],this.hashMap.get(state)[1]),Math.max(this.hashMap.get(state)[2],this.hashMap.get(state)[3]));
	}
	
	private int maxIndexFromMap(String state) {
		ArrayList<Integer> listMax=new ArrayList<>();
		double[]tab=this.hashMap.get(state);

		double mem=tab[0];
		for (int i=0;i<4;i++) {
			if(tab[i]>mem) {
				listMax.clear();
				mem=tab[i];
				listMax.add(i);
			}
			else if(tab[i]==mem) {
				listMax.add(i);
			}
			else {
			}
		}
		if(listMax.size()==1) {
			return listMax.get(0);
		}
		if(listMax.size()==0) {
		}
		return listMax.get(ran.nextInt(listMax.size()));
	}

}
