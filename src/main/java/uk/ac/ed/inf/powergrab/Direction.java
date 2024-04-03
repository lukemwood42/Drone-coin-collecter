package uk.ac.ed.inf.powergrab;

public enum Direction {
	N(0.0 * (Math.PI/180)), 
	NNE(22.5 * (Math.PI/180)), 
	NE(45.0 * (Math.PI/180)),
	ENE(67.5 * (Math.PI/180)), 
	E(90.0 * (Math.PI/180)), 
	ESE(112.5 * (Math.PI/180)), 
	SE(135 * (Math.PI/180)), 
	SSE(157.5 * (Math.PI/180)), 
	S(180.0 * (Math.PI/180)), 
	SSW(202.5 * (Math.PI/180)), 
	SW(225.0 * (Math.PI/180)), 
	WSW(247.5 * (Math.PI/180)), 
	W(270.0 * (Math.PI/180)), 
	WNW(292.5 * (Math.PI/180)), 
	NW(315.0 * (Math.PI/180)), 
	NNW(337.5 * (Math.PI/180))
	;
		
	private final double rad;

	Direction(double rad) {
	    this.rad = rad;
	}
	    
	public double getRad() {
	    return this.rad;
	}

}
