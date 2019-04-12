package GameMechanics;

import com.sun.tools.classfile.Opcode;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by Gleb on 12/04/2019.
 */
public class RandomFill {
    private int[][] startAdjMat;
    private AdjacencyMatrix originalAm;
    private int fills;
    private int size;


    public RandomFill( AdjacencyMatrix am, int howManyFills){
        this.fills = howManyFills;
        this.originalAm = am;
        this.startAdjMat = am.copyMatrix();
        this.size = am.getSize();
        System.out.println("fills " + fills);
    }


    public int bestMove(List nodesToTry){
        int bestWinCount = 0;
        int bestNodeIndex = 0;
        for(int i=0; i<nodesToTry.size();i++){
            int winCount = 0;
            for(int j=0; j<this.fills;j++){
                if(wonAfterFill(this.startAdjMat,i)){
                    winCount++;
                }
            }
            if(winCount>bestWinCount){
                bestWinCount = winCount;
                bestNodeIndex = i;
            }
        }
        return (Integer)nodesToTry.get(bestNodeIndex);
    }

    public boolean wonAfterFill(int [][] theMatrix, int played){
        AdjacencyMatrix copiedMat = new AdjacencyMatrix(this.size,originalAm.getPlayerNumber());
        copiedMat.setAdjMat(theMatrix);
        copiedMat.nodeWon(played);
        List listOfNodes = copiedMat.getFreeNodesList();
        Collections.shuffle(listOfNodes);
        for(int i =0;i<listOfNodes.size();i++){
            if(i%2 == 0){
                copiedMat.nodeLost((Integer)listOfNodes.get(i));
            } else {
                copiedMat.nodeWon((Integer)listOfNodes.get(i));
            }
        }
        return copiedMat.existsEdge(size*size,size*size+1);

    }




}