package com.example.ilpcoursework.Controller;

import com.example.ilpcoursework.Data.*;
import com.example.ilpcoursework.RestClientData.CentralArea;
import com.example.ilpcoursework.RestClientData.noFlyZones;
import jakarta.websocket.RemoteEndpoint;

import java.util.*;

public class DronePathFinding {
    private static final double [] ANGLES = {0,22.5,45,67.5,90,112.5, 135, 157.5, 180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5};
    private static final LngLat endPoint = new LngLat(-3.186874, 55.944494);
    private static BasicController controller = new BasicController();
    private static double DRONE_MOVE_DISTANCE = 0.00015;

    public List<LngLat> pathFinder (LngLat startPoint, noFlyZones [] noFly, CentralArea cent, Order order){
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(currNode -> currNode.getGScore() +
                currNode.getHScore()));

        Map<LngLat, Node> nodeMap = new HashMap<>();

        Set<LngLat> visited = new HashSet<>();

        boolean insideCentral = false;

        Node startNode = new Node(startPoint,null,999.0,0.0,distanceTo(startPoint,endPoint));
        nodeMap.put(startPoint, startNode);

        openSet.add(startNode);

int i =0;
        while(!openSet.isEmpty()){
            Node currentNode = openSet.poll();
            visited.add(currentNode.getPoint());
            LngLatPairRequest npr = new LngLatPairRequest(currentNode.getPoint(),endPoint);

            if(controller.checkCloseness(npr)){
                return reconstructPath(currentNode,endPoint,startPoint,order);
            }


            Region reg = new Region(cent.getName(),cent.vertices);
            IsInRegionRequest is = new IsInRegionRequest(currentNode.getPoint(),reg);


            if (controller.checkRegion(is)) {
                insideCentral = true;

            }

            List<Node> nextPositions = generateNextPositions(currentNode.getPoint(),noFly,insideCentral,cent);


            for(Node nextNode : nextPositions){


                if(visited.contains(nextNode.getPoint())){
                continue;
                }

                double calcGScore = currentNode.getGScore()+DRONE_MOVE_DISTANCE;

                LngLat pos = nextNode.getPoint();
                nextNode.setParent(currentNode);
                nextNode.setGScore(calcGScore);
                nextNode.setHScore(distanceTo(nextNode.getPoint(),endPoint));

                if(nodeMap.containsKey(pos)){
                    Node node = nodeMap.get(pos);


                    if(node.getGScore()>calcGScore){

                        nodeMap.put(pos, nextNode);
                        openSet.remove(node);
                        openSet.add(nextNode);

                    }
                }
                else{
                    nodeMap.put(pos, nextNode);
                    openSet.add(nextNode);

                }
            }




        }
        return null;
    }

    public List<LngLat> reconstructPath(Node currentNode, LngLat endPoint, LngLat startPoint, Order order){
        List<LngLat> path = new ArrayList<>();
        while(currentNode.getParent() != null){
            path.add(currentNode.getPoint());

            currentNode = currentNode.getParent();
        }

        path.add(startPoint);
        Collections.reverse(path);

        return path;


    }

    private List<Node> generateNextPositions(LngLat curr, noFlyZones [] noFly, boolean flag, CentralArea cent){
        List<Node> nextPositions = new ArrayList<>();
        for(double angle: ANGLES){
            boolean valid = true;
            NextPositionRequest npr = new NextPositionRequest();
            npr.setAngle(angle);
            npr.setStart(curr);
            LngLat nextPosition = controller.getNextPosition(npr);
            for(noFlyZones flyZone : noFly){


                Region zoneReg = new Region(flyZone.getName(),flyZone.getVertices());
                IsInRegionRequest checkZone = new IsInRegionRequest(nextPosition,zoneReg);
                if(controller.checkRegion(checkZone)){
                    valid = false;
                    break;
                }


            }
            Region centralRegion = new Region(cent.getName(),cent.vertices);
            if (flag && !controller.checkRegion(new IsInRegionRequest(nextPosition, centralRegion))) {
                valid = false;
            }
            if(valid){
                nextPositions.add(new Node(nextPosition,null,angle,0.0,0.0));
            }
        }
    return nextPositions;

    }



    private double distanceTo(LngLat first, LngLat second){
        return Math.sqrt(Math.pow(first.getLng()- second.getLng(),2 )+Math.pow(first.getLat()- second.getLat(),2));
    }



















}

