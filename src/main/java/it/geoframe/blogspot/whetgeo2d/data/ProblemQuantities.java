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

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class ProblemQuantities {

	private static ProblemQuantities uniqueInstance;
	
	public static ProblemQuantities getInstance() {
		
		return uniqueInstance;
		
	}
	
	public static ProblemQuantities getInstance(List<Double> icWaterSuction, List<Double> icTemperature, List<Integer> elementEquationStateID, List<Integer> elementParameterID, int elementNumber, int edgeNumber) {
		
		if (uniqueInstance == null) {
			
			uniqueInstance = new ProblemQuantities(icWaterSuction, icTemperature, elementEquationStateID, elementParameterID, elementNumber, edgeNumber);
		}
		return uniqueInstance;
		
	}
	
	
	public List<Double> waterSuctions;
	public List<Double> temperatures;
	
	public List<Double> thetas;
	public List<Double> thetasNew;
	public List<Double> saturationDegree;
//	public Map<Integer, Double> dThetas;
//	public Map<Integer, Double> dThetas1;
//	public Map<Integer, Double> dThetas2;
//	public Map<Integer, Double> thetas1;
//	public Map<Integer, Double> thetas2;
	public List<Double> kappas;
	public List<Double> kappasInterface;
	public List<Double> gravityGradient;
	public List<Double> volumes;
	public List<Double> volumesNew;
	public List<Double> darcyVelocities;
	public List<Double> darcyVelocitiesX;
	public List<Double> darcyVelocitiesZ;
	public List<Double> darcyVelocitiesCapillary;
	public List<Double> darcyVelocitiesGravity;
	public List<Double> poreVelocities;
	public List<Double> celerities;        // Rasmussen et al. 2000
	public List<Double> kinematicRatio;  // Rasmussen et al. 2000
	public List<Double> waterSuctionStar1;
	public List<Double> waterSuctionStar2;
	public List<Double> waterSuctionStar3;
	
	public double waterVolume;
	public double waterVolumeNew;
	public double errorVolume;
	public double sumETs;
	public double timeDelta;
	public double sumBoundaryFlux;
	
	
	public List<Integer> elementEquationStateID;
	public List<Integer> elementParameterID;

	private ProblemQuantities(List<Double> icWaterSuction, List<Double> icTemperature, List<Integer> elementEquationStateID, List<Integer> elementParameterID, int elementNumber, int edgeNumber) {
		
		this.waterSuctions = new ArrayList<Double>(icWaterSuction);
		this.temperatures = new ArrayList<Double>(icTemperature);
		
		thetas = new ArrayList<Double>(Arrays.asList(new Double[elementNumber]));
		thetasNew = new ArrayList<Double>(Arrays.asList(new Double[elementNumber]));
		saturationDegree = new ArrayList<Double>(Arrays.asList(new Double[elementNumber]));
//	    dThetas = new HashMap<Integer, Double>();
//		ProblemQuantities.dThetas1 = new HashMap<Integer, Double>();
//		ProblemQuantities.dThetas2 = new HashMap<Integer, Double>();
//		ProblemQuantities.thetas1 = new HashMap<Integer, Double>();
//		ProblemQuantities.thetas2 = new HashMap<Integer, Double>();
		kappas = new ArrayList<Double>(Arrays.asList(new Double[elementNumber]));
		kappasInterface = new ArrayList<Double>(Arrays.asList(new Double[edgeNumber]));
		gravityGradient = new ArrayList<Double>(Arrays.asList(new Double[edgeNumber]));
		volumes = new ArrayList<Double>(Arrays.asList(new Double[elementNumber]));
		volumesNew = new ArrayList<Double>(Arrays.asList(new Double[elementNumber]));
		waterSuctionStar1 = new ArrayList<Double>(Arrays.asList(new Double[elementNumber]));
		waterSuctionStar2 = new ArrayList<Double>(Arrays.asList(new Double[elementNumber]));
		waterSuctionStar3 = new ArrayList<Double>(Arrays.asList(new Double[elementNumber])); 
		darcyVelocities = new ArrayList<Double>(Arrays.asList(new Double[edgeNumber]));
		darcyVelocitiesX = new ArrayList<Double>(Arrays.asList(new Double[edgeNumber]));
		darcyVelocitiesZ = new ArrayList<Double>(Arrays.asList(new Double[edgeNumber]));
		darcyVelocitiesCapillary = new ArrayList<Double>(Arrays.asList(new Double[edgeNumber]));
		darcyVelocitiesGravity = new ArrayList<Double>(Arrays.asList(new Double[edgeNumber]));
		poreVelocities = new ArrayList<Double>(Arrays.asList(new Double[edgeNumber]));
		celerities = new ArrayList<Double>(Arrays.asList(new Double[edgeNumber]));
		kinematicRatio = new ArrayList<Double>(Arrays.asList(new Double[edgeNumber]));
		
		this.elementEquationStateID = new ArrayList<Integer>(elementEquationStateID);
		this.elementParameterID = new ArrayList<Integer>(elementParameterID);

	}

}
