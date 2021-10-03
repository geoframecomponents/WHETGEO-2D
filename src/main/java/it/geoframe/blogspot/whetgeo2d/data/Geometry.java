/*
 * GNU GPL v3 License
 *
 * Copyright 2021 Niccolo` Tubini
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

public class Geometry {
	
	private static Geometry uniqueInstance;
	
	public static Geometry getInstance() {

		return uniqueInstance;

	}
	
	public static Geometry getInstance(List<Double> elementsArea, List<Double> edgesLenght, List<Double> delta_j,
			List<Double[]> edgeNormalVector, List<Double[]> elementsCentroidsCoordinates, List<Double[]> edgesCentroidsCoordinates) {
	
		if (uniqueInstance == null) {
		
			uniqueInstance = new Geometry(elementsArea, edgesLenght, delta_j, edgeNormalVector, elementsCentroidsCoordinates, edgesCentroidsCoordinates);
		}
		
		return uniqueInstance;

	}
	
	
	public List<Double> elementsArea;
	public List<Double> edgesLength;
	public List<Double> delta_j;
	public List<Double[]> edgeNormalVector;
	public List<Double[]> elementsCentroidsCoordinates;
	public List<Double[]> edgesCentroidsCoordinates;
	
	private Geometry(List<Double> elementsArea, List<Double> edgesLenght, List<Double> delta_j,
			List<Double[]> edgeNormalVector, List<Double[]> elementsCentroidsCoordinates, List<Double[]> edgesCentroidsCoordinates) {
		
		this.elementsArea = new ArrayList<Double>(elementsArea);
		this.edgesLength = new ArrayList<Double>(edgesLenght);
		this.delta_j = new ArrayList<Double>(delta_j);
		this.edgeNormalVector = new ArrayList<Double[]>(edgeNormalVector);
		this.elementsCentroidsCoordinates = new ArrayList<Double[]>(elementsCentroidsCoordinates);
		this.edgesCentroidsCoordinates = new ArrayList<Double[]>(edgesCentroidsCoordinates);
		
	}


}
