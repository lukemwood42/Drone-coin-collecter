package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.imageio.IIOException;

public class StatefulDrone extends Drone{
	
	List<String> moves = new ArrayList<>();

	public StatefulDrone(double latitude, double longnitude) {
		super(latitude, longnitude);
	}
	
	public List<String> write2file(List<Station> stations) {
		List<Station> positiveStations = findPosStations(stations);
		List<Station> negativeStations = findNegStations(stations, positiveStations);
		List<Position> pastPositions = new ArrayList<>();
		pastPositions.add(super.position);
		while (!positiveStations.isEmpty() && super.moves < 250 && super.power >= 1.25) {
			HashMap<Station, Double> heuristicValues = new HashMap<>();
			for (Station s : positiveStations) {
				heuristicValues.put(s, calHeuristicValue(s, super.position, positiveStations, negativeStations));
			}
			Station best = null;
			double bestCost = Double.POSITIVE_INFINITY;
			for (Station s : heuristicValues.keySet()) {
				if (bestCost > heuristicValues.get(s)) {
					best = s;
					bestCost = heuristicValues.get(s);
				}
			}
//			System.out.println(best.toString());
			positiveStations.remove(best);
//			System.out.println(positiveStations.size());
			findBestRoute(best, super.position, negativeStations, pastPositions);
			//System.out.println(!positiveStations.isEmpty() && super.moves < 250 && super.power >= 1.25);
		}
		
		String temp = moves.get(moves.size() - 1);
		String[] coords = temp.split(",");
		double oldLat = Double.parseDouble(coords[0]);
		double oldLong = Double.parseDouble(coords[1]);
		Direction oppositeDirection = null;
		Direction lastDirection = null;
		List<Direction> dirs = new ArrayList<Direction>(Arrays.asList(super.directions));
		for (Direction dir : dirs) {
			Position p = super.position.nextPosition(dir);
			if (p.latitude == oldLat && p.longitude == oldLong) oppositeDirection = dir;
//			p = new Position(oldLat, oldLong);
//			p = p.nextPosition(d);
//			if (p == super.position) lastDirection = d;
		}
		double n = ((oppositeDirection.getRad() + Direction.S.getRad()) % (Math.PI * 2));
		for (Direction dir : dirs) {
			if (dir.getRad() == n) {
				lastDirection = dir;
			}
		}
		Direction d = null;
		while (super.moves <= 250 && super.power >= 1.25) {
			if (d == lastDirection) d = oppositeDirection;
			else d = lastDirection;
			Position p = new Position(oldLat, oldLong);
			oldLat = super.position.latitude;
			oldLong = super.position.longitude;
			super.makeMove(p);
			moves.add(oldLat + "," + oldLong + "," + d + "," + p.latitude + "," + p.longitude + "," + super.coins + "," + super.power);
			//System.out.println("[ " + super.position.longitude + "," + super.position.latitude + "],");
		}
		return moves;
	}

	public Double calHeuristicValue(Station s, Position p, List<Station> positiveStations, List<Station> negativeStations) {
		Double value = super.calDis(super.position.latitude, super.position.longitude, s);
		if (value <= 0.0025 && s.getCoins() != 0) {
			super.updateStation(s);
		}
		value += 0.0003 * calTravelCost(s, p, positiveStations, negativeStations);
		return value;
	}

	private int calTravelCost(Station s, Position p, List<Station> positiveStations, List<Station> negativeStations) {
		List<Direction> dirs = new ArrayList<Direction>(Arrays.asList(super.directions));
		Direction closestDirection = null;
		double disFromStation = Double.POSITIVE_INFINITY;
		for (Direction d : dirs) {
			Position tempP = p.nextPosition(d);
			if (tempP.inPlayArea()) {
				if (calDis(tempP.latitude, tempP.longitude, s) < disFromStation) {
					if (super.calDis(tempP.latitude, tempP.longitude, s) <= 0.00025) {
						return 1;
					}
					else {
						boolean avoids = true;
						for (Station ns : negativeStations) {
							if (super.calDis(tempP.latitude, tempP.longitude, ns) <= 0.00025) {
								avoids = false;
							}
						}
						if (avoids == true) {
							closestDirection = d;
							disFromStation = calDis(tempP.latitude, tempP.longitude, s);
						}
					}
				}		
			} 
		}
		try {
			return 1 + calTravelCost(s, p.nextPosition(closestDirection), positiveStations, negativeStations);
		}
		catch (StackOverflowError e){
            return 1;
		}
		
	}
	
	
	private void findBestRoute(Station target, Position curP, List<Station> negativeStations, List<Position> pastPositions) {
		Direction closestDirection = null;
		double disFromStation = Double.POSITIVE_INFINITY;
		List<Direction> dirs = new ArrayList<Direction>(Arrays.asList(super.directions));
		for (Direction d : dirs) {
			Position tempP = curP.nextPosition(d);
			if (tempP.inPlayArea() && !pastPositions.contains(tempP)) {
				if (calDis(tempP.latitude, tempP.longitude, target) < disFromStation) {
						boolean avoids = true;
						for (Station ns : negativeStations) {
							if (super.calDis(tempP.latitude, tempP.longitude, ns) <= 0.00025) {
								avoids = false;
							}
						}
						if (avoids == true) {
							closestDirection = d;
							disFromStation = calDis(tempP.latitude, tempP.longitude, target);
						}
					
				}		
			} 
		}
		
		if (closestDirection == null) {
			closestDirection = negativemoveOnly(negativeStations);
		}
		Position newP = curP.nextPosition(closestDirection);
		super.makeMove(newP);
		pastPositions.add(newP);
		if (super.calDis(super.position.latitude, super.position.longitude, target) <= 0.00025) {
			super.updateStation(target);
			//System.out.println("[ " + curP.longitude + "," + curP.latitude + "],");
			moves.add(curP.latitude + "," + curP.longitude + "," + closestDirection + "," + newP.latitude + "," + newP.longitude + "," + super.coins + "," + super.power);
		}
		else if (super.power >= 1.25 && super.moves < 250){
			moves.add(curP.latitude + "," + curP.longitude + "," + closestDirection + "," + newP.latitude + "," + newP.longitude + "," + super.coins + "," + super.power);
			//System.out.println("[ " + curP.longitude + "," + curP.latitude + "],");
			findBestRoute(target, curP.nextPosition(closestDirection), negativeStations, pastPositions);	
		}
		
		//System.out.println(curP.latitude + "," + curP.longitude + "," + closestDirection + "," + newP.latitude + "," + newP.longitude + "," + super.coins + "," + super.power);
		
	}

	public ArrayList<Station> findPosStations(List<Station> stations) {
		ArrayList<Station> posStations = new ArrayList<>();
		for (Station s : stations) {
			if (s.getCoins() >= 0) {
				posStations.add(s);
			}
		}
		return posStations;
	}
	
	public ArrayList<Station> findNegStations(List<Station> stations, List<Station> positiveStations) {
		ArrayList<Station> negStations = new ArrayList<>();
		for (Station s : stations) {
			if (!positiveStations.contains(s)) {
				negStations.add(s);
			}
		}
		return negStations;
	}
	
	
	public Direction negativemoveOnly(List<Station> negativeStations) {
		List<Station> reachableStations = super.stationsInRange(negativeStations);
		Direction highestDir = null;
		Station highestStation = null;
		double highestCoins = Double.NEGATIVE_INFINITY;
		for (Direction d : super.directions) {
			Position p = super.position.nextPosition(d);
			if (p.inPlayArea()) {
				for (Station s : reachableStations) {
					if (super.calDis(p.latitude, p.longitude, s) <= 0.00025) {
						if (highestCoins < s.getCoins()) {
							highestDir = d;
							highestStation = s;
						}
					}
				}
			}
		}
		super.updateStation(highestStation);
		return highestDir;
	}


	

	
}
