package Players;

import GameMechanics.AdjacencyMatrix;
import GameMechanics.Node;
import GameMechanics.RandomFill;

import java.util.ArrayList;

/**
 * Created by Gleb on 13/04/2019.
 */
public class EvaluationPlayers implements PlayerInterface {

    private static final int million = 1000000;
    private int size;
    private AdjacencyMatrix ourAdjacencyMatrix, theirAdjacencyMatrix;
    private int playerNumber;
    private int theirNumber;
    private int depth;
    private boolean allNodes;
    private int fillCount;

    public EvaluationPlayers(int size, int playerNumber, int depth, int reducedNodes,int fillCount) {
        this.size = size;
        this.playerNumber = playerNumber;
        this.depth = depth;
        this.fillCount = fillCount;
        if(reducedNodes == 1){
            this.allNodes = false;
        } else {
            this.allNodes = true;
        }
        switch (this.playerNumber) {
            case 1:
                this.theirNumber = 2;
                break;
            default:
                this.theirNumber = 1;
        }
        System.out.println("there number "  + theirNumber);
        this.ourAdjacencyMatrix = new AdjacencyMatrix(this.size, this.playerNumber);
        this.theirAdjacencyMatrix = new AdjacencyMatrix(this.size, this.theirNumber);

    }

    public int getMove() {
    Node rootNode = new Node(0,0,this.depth,this.allNodes,this.ourAdjacencyMatrix,this.theirAdjacencyMatrix);
    int score = miniMax(rootNode,Integer.MIN_VALUE,Integer.MAX_VALUE);
//        System.out.println("score is " + score);
//        Node node = rootNode;
//        for(int j=0;j<depth;j++){
//            int store;
//            ArrayList<Node> listTest = node.getChildren();
//            if(listTest != null) {
//                for (int i = 0; i < listTest.size(); i++) {
//                    if (score == listTest.get(i).getMiniMaxValue()) {
//                        System.out.println("j=" + j + "move = " + listTest.get(i).getLastMove());
//                        store= i;
//                        node = listTest.get(store);
//
//                        break;
//                    }
//                }
//            }
//        }
    if(rootNode.getChildren() == null){
        System.out.println("problem in lookahead path");
        return -1;
    }
    ArrayList<Node> rootChildren = rootNode.getChildren();
    int move = -1;
    for(int i=0;i<rootChildren.size();i++){
        if(score == rootChildren.get(i).getMiniMaxValue()){
            move = rootChildren.get(i).getLastMove();
            break;
        }
    }
    if(move == -1){
        System.out.println("problem in lookahead pathDiff");
        System.out.println("root node children size = " + rootChildren.size() + "freelistsize " + ourAdjacencyMatrix.getFreeNodesList().size());
        if(rootNode.isTerminal()){
            System.out.println("terminal");
        } else{
            System.out.println("not terminal");
//            rootNode.getHeroAm().displayThisMatrix();
        }
        move =  (Integer)this.ourAdjacencyMatrix.getFreeNodesList().get(0);

    }
    this.ourAdjacencyMatrix.nodeWon(move);
    this.theirAdjacencyMatrix.nodeLost(move);
    return move;
    }


    public boolean getHasWon() {
        return ourAdjacencyMatrix.existsEdge(size * size, size * size + 1);
    }

    public void updateOpponentsMove(int theirMove) {
        theirAdjacencyMatrix.nodeWon(theirMove);
        ourAdjacencyMatrix.nodeLost(theirMove);
    }

    public int miniMax(Node node,int alpha, int beta){      // minimax with alpha-beta prune
        if(node.isTerminal()){
            int val;
            if(this.fillCount > 0){
                val = this.fillHeuristic(node.getHeroAm(),node.getVillainAm());
            } else {
                val = this.pathHeuristic(node.getHeroAm(),node.getVillainAm(),node.getCurrentDepth());
            }
            node.setMiniMaxValue(val);
            return val;
        }if(node.isMaximiser()){
            int bestVal = Integer.MIN_VALUE;
            ArrayList<Node> nodeChildren = node.getChildren();
            for(int i=0;i<nodeChildren.size();i++){
                int val = miniMax(nodeChildren.get(i),alpha,beta);
                bestVal = Math.max(bestVal,val);
                alpha = Math.max(bestVal,alpha);
                if (beta<=alpha){
                    break;
                }
            }
            node.setMiniMaxValue(bestVal);
            return bestVal;
        }else{
            int bestVal = Integer.MAX_VALUE;
            ArrayList<Node> nodeChildren = node.getChildren();
            for(int i=0;i<nodeChildren.size();i++){
                int val = miniMax(nodeChildren.get(i),alpha,beta);
                bestVal = Math.min(bestVal,val);
                beta = Math.min(bestVal,beta);
                if(beta<=alpha){
                    break;
                }
            }
            node.setMiniMaxValue(bestVal);
            return bestVal;
        }


    }

    public int pathHeuristic(AdjacencyMatrix heroAm, AdjacencyMatrix villAm, int depth){
        System.out.println("here");

        if(heroAm.existsEdge(size*size,size*size+1)){
            return million*(100-depth);        }
        if(villAm.existsEdge(size*size,size*size+1)){
            return million*(depth-100);        }
        int heroLength = heroAm.shortestPathBetween(size*size,size*size+1)[0];
        int vilLength = villAm.shortestPathBetween(size*size,size*size+1)[0];
        if(heroLength == vilLength){
            int heroShortestPaths = heroAm.pathsOfLengthBetween(heroLength,size*size,size*size+1);
            int vilShortestPaths = villAm.pathsOfLengthBetween(heroLength,size*size,size*size+1);
            return heroShortestPaths - vilShortestPaths;
        } else{
            return (vilLength-heroLength)*million;
        }
    }

    public int fillHeuristic(AdjacencyMatrix heroAm, AdjacencyMatrix villAm){
        System.out.println("here");
        if(heroAm.existsEdge(size*size,size*size+1)){
            return million*(100-depth);
        }
        if(villAm.existsEdge(size*size,size*size+1)){
            return million*(depth-100);
        }
        RandomFill rf = new RandomFill(heroAm,this.fillCount);
        return rf.heuristicFillCount();
    }
}



