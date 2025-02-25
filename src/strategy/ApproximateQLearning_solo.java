package strategy;

import java.util.ArrayList;
import java.util.Random;

import agent.Snake;
import item.Item;
import model.SnakeGame;
import utils.AgentAction;
import utils.Position;

public class ApproximateQLearning_solo extends Strategy{

	int d = 3;
	int n;
	double epsilon;
	double alpha;
	double gamma;
	
	boolean debug = false;
	
	double[] w;
	
	Random random = new Random();
	
	double[] current_f;

	
	
	public ApproximateQLearning_solo(int n, double epsilon, double alpha, double gamma) {
		
		this.n = n;
		this.epsilon = epsilon;
		this.alpha = alpha;
		this.gamma = gamma;
		
		this.current_f=new double[d+1];
		
		this.w = new double[d+1];
		
		
		
		for(int i=0;i<d+1;i++) {
			this.w[i] = random.nextDouble(1);
		}
		
		afficherParams();
	}
	
	
	public double[] extractFeatures(SnakeGame nextState, int idxSnake, AgentAction agentAction) {
		
		double[] f = new double[d+1];
		f[0] = 1;
		Snake snake=nextState.getSnakes().get(idxSnake);
		
		
		
		
		
		
		Snake snake_copy=new Snake(snake.getPositions().getFirst(), snake.getLastMove(), idxSnake, snake.getColorSnake());
		
		//printDebug(snake_copy.getPositions().get(0).getX()+" "+snake_copy.getPositions().get(0).getY());
		
		snake_copy=this.copySnake(snake);
		snake_copy.move(agentAction, nextState);
		
		boolean reverse=false;
		
		//f[]=0;//snake.getSize();
		//f[2]=0;//this.closestItem(nextState,snake_copy,nextState.getItems());
		if(!isLegalMove(snake,agentAction)) {
			printDebug(agentAction+" illegalReverse");
			f[3]=0;
			reverse=true;
		}
		else {
			f[3]=0.5;
		}
		double alive=this.snakeAlive(snake_copy,nextState);
		f[1]=alive/10;
		if(alive==0||reverse) {
			f[2]=0;
		}
		else {
			f[2]=this.closestItem(nextState,snake_copy,nextState.getItems())*0.5;
		}
		
		printDebug(agentAction+" alive "+f[1]);
		return f;
		
	}
	
	


public boolean isLegalMove(Snake snake, AgentAction action) {
		
		if(snake.getSize() > 1) {
			if(snake.getLastMove() == AgentAction.MOVE_DOWN && action == AgentAction.MOVE_UP) {
				
				return false;
				
			} else if(snake.getLastMove() == AgentAction.MOVE_UP && action == AgentAction.MOVE_DOWN) {
				
				return false;
				
			} else if(snake.getLastMove() == AgentAction.MOVE_LEFT && action == AgentAction.MOVE_RIGHT) {
				
				return false;
				
			} else if(snake.getLastMove() == AgentAction.MOVE_RIGHT && action == AgentAction.MOVE_LEFT) {
				
				return false;
				
			}
		}
		return true;
		
	}


	private double snakeAlive(Snake snake_copy, SnakeGame nextState) {
		boolean[][]walls = nextState.getWalls();
		//ArrayList<Item>listItems=nextState.getItems();
		ArrayList<Snake>listSnakes=nextState.getSnakes();
		int idsnake=snake_copy.getId();
		for(Position p:snake_copy.getPositions()) {
			if(walls[p.getX()][p.getY()]) {
				return 0;
			}
			for(Position p2:snake_copy.getPositions()) {
				if(!p2.equals(p)) {
					if(p.getX()==p2.getX()&&p.getY()==p2.getY()) {
						return 0;
					}
				}
			}
		}
		return 1;
	}

	/**
	 * retourne l'inverse de la distance de l'item le plus proche. (0 si le snake est le plus loin possible de l'item)
	 * @param snakeGame
	 * @param snake
	 * @param items
	 * @return
	 */
	private double closestItem(SnakeGame snakeGame,Snake snake, ArrayList<Item> items) {
		double distance;
		Position position=snake.getPositions().get(0);
        double minDistance=(double)(snakeGame.getSizeX()*snakeGame.getSizeY());
        Boolean walls=snakeGame.getWalls()[0][0];
        Item featuresItem=null;
        int sizeX=snakeGame.getSizeX();
        int sizeY=snakeGame.getSizeY();
        for(Item item:items){
            distance=distance(position,new Position(item.getX(),item.getY()), sizeX, sizeY, walls);
            if(minDistance>distance){
                minDistance=distance;
                featuresItem=item;
            }

        }
        printDebug("mindDist "+1/(minDistance+1));
        
       // double maxDistance=Math.sqrt(sizeX*sizeX+sizeY*sizeY);
        return 1/(minDistance+1);
	}


	public double scalarProduct(double[] w, double[] f) {
		
		//double[] w2 = {1.0,2.0};
		
		double q = 0;
		//double test;
		for(int i = 0; i < w.length; i++) {
			q += w[i]*f[i];
		}
		
		return q;
		
	}
	
	
	
	@Override
	public AgentAction chooseAction(int idxSnake, SnakeGame snakeGame) {
		printDebug("choose");
		AgentAction agentAction=AgentAction.values()[0];
		double[][]featuresList=new double[n][d+1];
		double[] features =extractFeatures(snakeGame,idxSnake,agentAction);
		double maxQNewState=scalarProduct(this.w,features);
		double newQState;
		
		int max_i =0;
		ArrayList<Integer>listeIndiceMax=new ArrayList<>();
		listeIndiceMax.add(0);
		for(int i=1;i<n;i++) {
			agentAction=AgentAction.values()[i];
			features=extractFeatures(snakeGame,idxSnake,agentAction);
			
			featuresList[i]=features;
			newQState=scalarProduct(this.w,features);//choix du nouveau max
			printDebug("scal"+newQState+" "+maxQNewState);
			if(newQState>maxQNewState) {
				max_i=i;
				maxQNewState=newQState;
				listeIndiceMax.clear();
				listeIndiceMax.add(i);
			}
			else if (newQState==maxQNewState) {
				listeIndiceMax.add(i);
			}
			
			
		}
		printDebug(listeIndiceMax.toString());
		if(random.nextDouble() < this.epsilon) {
			
			int i=random.nextInt(n);
			this.current_f=featuresList[i];
			return AgentAction.values()[i];
			
		} else {
			max_i=listeIndiceMax.get(random.nextInt(listeIndiceMax.size()));
			this.current_f=featuresList[max_i];
			
			printDebug(AgentAction.values()[max_i].toString());
			return AgentAction.values()[max_i];
		
		}
	}

	@Override
	public void update(int idx, SnakeGame state,  AgentAction action, SnakeGame nextState, int reward, boolean isFinalState) {
		afficherParams();
		if(isFinalState) {
			printDebug("FINALL");
		}
		//System.out.println("update");
		//System.out.println("nextstate"+nextState.getSnakes().get(idx).getPositions().get(0).getX()+" "+nextState.getSnakes().get(idx).getPositions().get(0).getY());
		AgentAction agentAction=AgentAction.values()[0];
		double[] features =extractFeatures(nextState,idx,agentAction);
		double maxQNewState=scalarProduct(this.w,features);
		double newQState=maxQNewState;
		//System.out.println(features[3]+" "+this.w[3]);
		for(int i=1;i<n;i++) {
			printDebug("scal"+newQState+" "+maxQNewState);
			
			agentAction=AgentAction.values()[i];
			features=extractFeatures(nextState,idx,agentAction);
			//System.out.println(features[3]+" "+this.w[3]);
			newQState=scalarProduct(this.w,features);
			maxQNewState=maxQNewState>newQState?maxQNewState:newQState;
		}
		maxQNewState=maxQNewState*10;
		printDebug("MaxQ "+maxQNewState);
		//System.out.println("");
		double target = reward + this.gamma*maxQNewState;
		
		if(isFinalState) {
			int walls=state.getWalls()[0][0]?2:0;
			int maxPossibleSize=(state.getSizeX()-walls)*(state.getSizeY()-walls);
			if(state.getSnakes().get(idx).getSize()>=maxPossibleSize*0.2) {
				target=10;
			}
			else {
				target=reward;//+1.5*state.getSnakes().get(idx).getSize();
			}
			printDebug("target "+target);
		}
		
		double Qstate = scalarProduct(this.w,  this.current_f);
		
		
		for(int i = 0; i < d+1; i++) {
			
			this.w[i] = this.w[i] - this.alpha*this.current_f[i]*(Qstate - target);
			
		}
		
		afficherParams();
		printDebug("-----------\n");
	}
	
	public void afficherParams() {
		if(debug) {
		for(int i=0;i<this.w.length;i++) {
			System.out.println("w"+i+" " + this.w[i]);
		}
		}
		
	}
	
	public Snake copySnake(Snake sourceSnake) {
		ArrayList<Position>newPositions=new ArrayList<>();
		for(Position p : sourceSnake.getPositions()) {
			newPositions.add(new Position(p.getX(),p.getY()));
		}
		
		Snake newSnake=new Snake(null, null, sourceSnake.getId(), sourceSnake.getColorSnake());
		newSnake.setPositions(newPositions);
		
		return newSnake;
	}

	public void printDebug(String s) {
		if(debug) {

			System.out.println(s);
		}
	}
	
	
	/**
	 * calcule la distance entre deux points. S'il n'y a pas de murs, calcule en prenant en compte le retour par l'autre coté de la carte.
	 * @param positionB le deuxième points
	 * @param sizeX la taille de la carte en abcisse
	 * @param sizeY la taille de la carte en ordonnée
	 * @param murs vrai s'il y a des murs faux sinon. 
	 * @return
	 */
	public Double distance(Position positionA, Position positionB,int sizeX,int sizeY,boolean murs){
		double deltaX=positionA.getX()-positionB.getX();
		double deltaY=positionA.getY()-positionB.getY();
		Double retour;
		if(!murs){
			printDebug("nowalls");
			double deltaXalt=Math.abs(sizeX-Math.max(positionA.getX(),positionB.getX())+Math.abs(Math.min(positionA.getX(),positionB.getX())));
			double deltaYalt=Math.abs(sizeY-Math.max(positionA.getY(),positionB.getY())+Math.abs(Math.min(positionA.getY(),positionB.getY())));
			Double minX=Math.min(deltaX, deltaXalt);
			Double minY=Math.min(deltaY, deltaYalt);
			retour=Math.sqrt(minX*minX+minY*minY);
		}
		else{
			retour =Math.sqrt(deltaX*deltaX+deltaY*deltaY);
		}
		return retour;
	}
}
