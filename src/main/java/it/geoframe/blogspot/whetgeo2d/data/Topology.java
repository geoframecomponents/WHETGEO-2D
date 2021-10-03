/*
 * GNU GPL v3 License
 *
 * Copyright 2019 Niccolo` Tubini
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.geoframe.blogspot.whetgeo2d.data;

import java.util.ArrayList;
import java.util.List;

public class Topology {

private static Topology uniqueInstance;
	
	public static Topology getInstance() {

		return uniqueInstance;
	}
	
	public static Topology getInstance(List<Integer> edgeLeftNeighbour, List<Integer> edgeRightNeighbour, List<Integer> edgesBoundaryBCType,
			 							List<Integer> edgesBoundaryBCValue, List<ArrayList<Integer>> elementEdgesSet) {
		if (uniqueInstance == null) {
			uniqueInstance = new Topology(edgeLeftNeighbour, edgeRightNeighbour, edgesBoundaryBCType, edgesBoundaryBCValue, elementEdgesSet);
		}
		return uniqueInstance;
	}
	
	public List<Integer> edgeLeftNeighbour;
	public List<Integer> edgeRightNeighbour;
	public List<Integer> edgesBoundaryBCType;
	public List<Integer> edgesBoundaryBCValue;
	public List<ArrayList<Integer>> elementEdgesSet;

	
	private Topology(List<Integer> edgeLeftNeighbour, List<Integer> edgeRightNeighbour, List<Integer> edgesBoundaryBCType,
				List<Integer> edgesBoundaryBCValue, List<ArrayList<Integer>> elementEdgesSet) {
		
		this.edgeLeftNeighbour = new ArrayList<Integer>(edgeLeftNeighbour);
		this.edgeRightNeighbour = new ArrayList<Integer>(edgeRightNeighbour);
		this.edgesBoundaryBCType = new ArrayList<Integer>(edgesBoundaryBCType);
		this.edgesBoundaryBCValue = new ArrayList<Integer>(edgesBoundaryBCValue);
		this.elementEdgesSet = new ArrayList<ArrayList<Integer>>(elementEdgesSet);
				
	}

}
