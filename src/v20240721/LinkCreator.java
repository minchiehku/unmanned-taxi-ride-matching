package v20240721;

import java.util.ArrayList;
import java.util.Random;

public class LinkCreator {
	
	/**
	 * �إߨ��I���s�ulink
	 * �إߨC�I�F�~
	 * 1. ��l�Ƥ@��link�p�⾹ linkCounter
	 * 2. �p�⦳�h��nodes, �H�۾F�x�}�Ӫ�ܨ��I���O�_��link 
	 * 3. �}�l�إ�link, �p����I���Z��
	 * 4. forloop�`���p��, �ëإߨC�I���F�~(��link��)����
	 * 
	 */
	
	public static ArrayList<Link> forLinkList(ArrayList<Node> nodeList) {
        ArrayList<Link> linkList = new ArrayList<>();
        int linkCounter = 1; //�p��linkID
        
        //�}�l�إ�link, �p��C��node��O��node���Z��
        for (int i = 0; i < nodeList.size(); i++) {
        	
        	//��l��
        	ArrayList<Node> neighborNodeList = new ArrayList<>(); //�����F�~, ���Ǭ��s���p-�j
            Node startNode = nodeList.get(i); //�}�l�I
            int currentSpeed = 0; //��l�ƹD���t��
            Node minEndNode = null; //�̵u�����I
            double shortestDistance = Double.MAX_VALUE; //���double����������̤j���ƭȡC���O�@�ӫD�`�j���ƭȡA����1.7976931348623157 x 10^308�C
            
            //�M��s���쪺�`�I(EndNode)
            for (int j = 0; j < nodeList.size(); j++) {
            	//�ۦPnode���L
                if (i == j) {
                    continue;
                }
                
                Node endNode = nodeList.get(j); //�����I
                double distance = Calculate.twoNodesDistance(startNode, endNode);
                
                //"""�]�w�Z��""" ���d�򤺵��������إ�link
                if (distance <= 1) {
                    int linkNum = linkCounter; //��link�s��
//                    currentSpeed = random.nextInt(41) + 20; //�����H�����p20-60km/hr
                    currentSpeed = 60;
                    Link link = new Link(linkNum, startNode, endNode, currentSpeed, distance); //�إ�link
                    linkList.add(link);  //�[�Jlink���X
                    neighborNodeList.add(endNode); //�x�s�F�~  
                    
                    //�����̤p���Z��
                    if (distance < shortestDistance) {
                    	shortestDistance = distance;
                    }
                    
                    linkCounter++;
                    continue;
                }
                
                //�p�G���j��1, �h�����̤p���Z��
                if (distance < shortestDistance) {
                	minEndNode = endNode;
                	shortestDistance = distance;
                }                
            }
            
            //"""�]�w�Z��""", �p�G���S���p��1��, �h���̵u��
            if (!(shortestDistance <= 1)) {
            	int linkNum = linkCounter;            	
//            	currentSpeed = random.nextInt(41) + 20; //�����H�����p20-60km/hr
            	currentSpeed = 60; 
            	Link link = new Link(linkNum, startNode, minEndNode, currentSpeed, shortestDistance);
                linkList.add(link);
                neighborNodeList.add(minEndNode);
                         
                linkCounter++;
            }
            
            //�]�w�F�~
            startNode.setNeighborNodes(neighborNodeList);
                        
        }

        return linkList;
    }
	
	public static int[][] forMatrix(ArrayList<Link> linkList, ArrayList<Node> nodeList) {
	    int numNodes = nodeList.size();  
    	int[][] arraylinks = new int[numNodes][numNodes]; //�۾F�x�}����link [0-299][0-299]
    	
    	//��l�ƨC�qlink, �w�]�S���s��
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                arraylinks[i][j] = -1;
            }
        }
	        // 
        for (Link link : linkList) {
            Node startNode = link.getStartNode();
            Node endNode = link.getEndNode();
            int startIndex = startNode.getNodeID() - 1;
            int endIndex = endNode.getNodeID() - 1;
	        arraylinks[startIndex][endIndex] = link.getLinkID();
	        }
        return arraylinks;
    }
}
