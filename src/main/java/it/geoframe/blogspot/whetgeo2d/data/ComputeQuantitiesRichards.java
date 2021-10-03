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
import java.util.HashMap;
import java.util.List;

import it.geoframe.blogspot.closureequation.closureequation.ClosureEquation;
import it.geoframe.blogspot.closureequation.closureequation.Parameters;
import it.geoframe.blogspot.closureequation.closureequation.SoilWaterRetentionCurveFactory;
import it.geoframe.blogspot.closureequation.conductivitymodel.ConductivityEquation;
import it.geoframe.blogspot.closureequation.conductivitymodel.ConductivityEquationFactory;
import it.geoframe.blogspot.closureequation.conductivitymodel.UnsaturatedHydraulicConductivityTemperatureFactory;
import it.geoframe.blogspot.closureequation.equationstate.EquationState;
import it.geoframe.blogspot.closureequation.interfaceconductivity.InterfaceConductivity;
import it.geoframe.blogspot.closureequation.interfaceconductivity.SimpleInterfaceConductivityFactory;
import it.geoframe.blogspot.whetgeo2d.equationstate.EquationStateFactory;
import oms3.annotations.Author;
import oms3.annotations.Bibliography;
import oms3.annotations.Description;
import oms3.annotations.Documentation;
import oms3.annotations.Keywords;
import oms3.annotations.License;

@Description("This class compute all the quantities to solve the Richards' equation.")
@Documentation("")
@Author(name = "Niccolo' Tubini and Riccardo Rigon", contact = "tubini.niccolo@gmail.com")
@Keywords("Richards equation, numerical solver, finite volume ")
@Bibliography("")
//@Label()
//@Name()
//@Status()
@License("General Public License Version 3 (GPLv3)")
public class ComputeQuantitiesRichards {


	private ProblemQuantities variables;
	private Geometry geometry;
	private Parameters parameters;
	private Topology topology;


	@Description("List containing the closure equations")
	private List<ClosureEquation> closureEquation;

	@Description("Factory for the closure equations")
	private SoilWaterRetentionCurveFactory soilWaterRetentionCurveFactory;

	@Description("List containig the objects that describes the state equations of the problem")
	private List<EquationState> equationState;

	@Description("Factory for the equation state")
	private EquationStateFactory equationStateFactory;

	@Description("Object dealing with the hydraulic conductivity model")
	private List<ConductivityEquation> hydraulicConductivity;
	private ConductivityEquationFactory conductivityEquationFactory;
	private UnsaturatedHydraulicConductivityTemperatureFactory unsaturatedHydraulicConductivityTemperatureFactory;

	@Description("This object compute the interface hydraulic conductivity accordingly with the prescribed method.")
	private InterfaceConductivity interfaceConductivity;
	private SimpleInterfaceConductivityFactory interfaceConductivityFactory;


	private double sideFlux;
	private double sumBoundaryFlux;

	public ComputeQuantitiesRichards(String[] typeClosureEquation, String[] typeEquationState, String[] typeUHCModel, String typeUHCTemperatureModel,
			String interfaceHydraulicConductivityModel) {

		variables = ProblemQuantities.getInstance();
		geometry = Geometry.getInstance();
		parameters = Parameters.getInstance();
		topology = Topology.getInstance();

		soilWaterRetentionCurveFactory = new SoilWaterRetentionCurveFactory();
		closureEquation = new ArrayList<ClosureEquation>();
		for(int i=0; i<typeClosureEquation.length; i++) {

			closureEquation.add(soilWaterRetentionCurveFactory.create(typeClosureEquation[i]));

		}


		equationStateFactory = new EquationStateFactory();
		equationState = new ArrayList<EquationState>();
		for(int i=0; i<typeEquationState.length; i++) {

			equationState.add(equationStateFactory.create(typeEquationState[i], closureEquation.get(i)));
		}


		hydraulicConductivity = new ArrayList<ConductivityEquation>();
		conductivityEquationFactory = new ConductivityEquationFactory();
		for(int i=0; i<typeUHCModel.length; i++) {

			hydraulicConductivity.add(conductivityEquationFactory.create(typeUHCModel[i], closureEquation.get(i)));

		}


		unsaturatedHydraulicConductivityTemperatureFactory = new UnsaturatedHydraulicConductivityTemperatureFactory();
		for(int i=0; i<typeUHCModel.length; i++) {

			hydraulicConductivity.add(i, unsaturatedHydraulicConductivityTemperatureFactory.create(typeUHCTemperatureModel, closureEquation.get(i), hydraulicConductivity.get(i)) );

		}

		interfaceConductivityFactory = new SimpleInterfaceConductivityFactory();
		interfaceConductivity = interfaceConductivityFactory.createInterfaceConductivity(interfaceHydraulicConductivityModel);


	}


	public List<EquationState> getRichardsStateEquation(){
		return equationState;
	}

	
	public void computeGravityGradient() {
		
		for(int edge=1; edge<topology.edgeRightNeighbour.size(); edge++) {
			
			if(topology.edgeRightNeighbour.get(edge)==0) {
				
				variables.gravityGradient.add( edge, (geometry.edgesCentroidsCoordinates.get(edge)[1] -
			 			geometry.elementsCentroidsCoordinates.get(topology.edgeLeftNeighbour.get(edge))[1])/geometry.delta_j.get(edge) );
				
			} else {
				
				variables.gravityGradient.add( edge, (geometry.elementsCentroidsCoordinates.get(topology.edgeRightNeighbour.get(edge))[1] -
									 			geometry.elementsCentroidsCoordinates.get(topology.edgeLeftNeighbour.get(edge))[1])/geometry.delta_j.get(edge) );
				
			}
		}
	}



	public void computeWaterVolume() {

		variables.waterVolume = 0.0;
		for(int element = 1; element<topology.elementEdgesSet.size(); element++) {
			variables.volumes.set(element, equationState.get(variables.elementEquationStateID.get(element)).equationState(variables.waterSuctions.get(element), variables.temperatures.get(element), variables.elementParameterID.get(element), element));
			variables.waterVolume += variables.volumes.get(element);
		}

	}



	public void computeWaterVolumeNew() {

		variables.waterVolumeNew = 0.0;
		for(int element = 1; element<topology.elementEdgesSet.size(); element++) {
			variables.volumesNew.set(element, equationState.get(variables.elementEquationStateID.get(element)).equationState(variables.waterSuctions.get(element), variables.temperatures.get(element), variables.elementParameterID.get(element), element));
			variables.waterVolumeNew += variables.volumesNew.get(element);
		}

	}



	public void computeThetas() {

		for(int element = 1; element<topology.elementEdgesSet.size(); element++) {
			variables.thetas.set(element, closureEquation.get(variables.elementEquationStateID.get(element)).f(variables.waterSuctions.get(element), variables.temperatures.get(element), variables.elementParameterID.get(element)));
		}

	}



	public void computeThetasNew() {

		for(int element = 1; element<topology.elementEdgesSet.size(); element++) {
			variables.thetasNew.set(element, closureEquation.get(variables.elementEquationStateID.get(element)).f(variables.waterSuctions.get(element), variables.temperatures.get(element), variables.elementParameterID.get(element)));
		}
	}

	
	
	public void computeSaturationDegreeNew() {

		for(int element = 1; element<topology.elementEdgesSet.size(); element++) {
			variables.saturationDegree.set(element, (variables.thetasNew.get(element)-parameters.thetaR[variables.elementParameterID.get(element)])
												/(parameters.thetaS[variables.elementParameterID.get(element)]-parameters.thetaR[variables.elementParameterID.get(element)]));
		}
	}

	
	
	public void computeXStar() {

		for(int element=1; element<topology.elementEdgesSet.size(); element++) {
			equationState.get(variables.elementEquationStateID.get(element)).computeXStar(variables.temperatures.get(element), variables.elementParameterID.get(element), element);
		}

	}



	public void computeHydraulicConductivity() {

		for(int element=1; element<topology.elementEdgesSet.size(); element++) {
			variables.kappas.set(element, hydraulicConductivity.get(variables.elementEquationStateID.get(element)).k(variables.waterSuctions.get(element), variables.temperatures.get(element), variables.elementParameterID.get(element), element));
			variables.kappas.set(element, Math.max(variables.kappas.get(element), Math.ulp(1.0)) );
		}			

	}



	public void computeInterfaceHydraulicConductivity() {

		for(int edge=1; edge<topology.edgeRightNeighbour.size(); edge++) {

			if(topology.edgeRightNeighbour.get(edge)==0) {

				variables.kappasInterface.set(edge, variables.kappas.get(topology.edgeLeftNeighbour.get(edge)) *geometry.edgesLength.get(edge) );

			} else {

				variables.kappasInterface.set(edge, interfaceConductivity.compute(variables.kappas.get(topology.edgeRightNeighbour.get(edge)), variables.kappas.get(topology.edgeLeftNeighbour.get(edge)), 
						geometry.elementsArea.get(topology.edgeRightNeighbour.get(edge)), geometry.elementsArea.get(topology.edgeLeftNeighbour.get(edge))) * geometry.edgesLength.get(edge) );
//				variables.kappasInterface.set(edge, Math.pow(0.5*(1/variables.kappas.get(topology.edgeRightNeighbour.get(edge))+1/variables.kappas.get(topology.edgeLeftNeighbour.get(edge))),-1) * geometry.edgesLength.get(edge) );

			}

		}	

	}



	public void computeDarcyVelocities(HashMap<Integer, double[]> inHMBoundaryCondition) {
		
		sumBoundaryFlux = 0;

		for(int edge=1; edge<topology.edgeRightNeighbour.size(); edge++) {
			
			if(topology.edgeRightNeighbour.get(edge)==0) {

				if(topology.edgesBoundaryBCType.get(edge)==1) {

					sideFlux = geometry.edgesLength.get(edge)*inHMBoundaryCondition.get(topology.edgesBoundaryBCValue.get(edge))[0];

				} else if(topology.edgesBoundaryBCType.get(edge)==2) {

					sideFlux = variables.kappasInterface.get(edge)*( (inHMBoundaryCondition.get(topology.edgesBoundaryBCValue.get(edge))[0] - variables.waterSuctions.get(topology.edgeLeftNeighbour.get(edge)))/geometry.delta_j.get(edge) 
							+ variables.gravityGradient.get(edge) );

				} else if(topology.edgesBoundaryBCType.get(edge)==3) {

					sideFlux = variables.kappasInterface.get(edge)*geometry.edgesLength.get(edge)*Math.min(0.0, variables.gravityGradient.get(edge));

				} else if(topology.edgesBoundaryBCType.get(edge)==4) {

					sideFlux = variables.kappasInterface.get(edge)*( ((inHMBoundaryCondition.get(topology.edgesBoundaryBCValue.get(edge))[0]-geometry.edgesCentroidsCoordinates.get(edge)[1])-variables.waterSuctions.get(topology.edgeLeftNeighbour.get(edge)))/geometry.delta_j.get(edge) 
							+ variables.gravityGradient.get(edge) );

				}
				
				variables.darcyVelocities.set(edge, sideFlux);
				sumBoundaryFlux += sideFlux;

			} else { //internal domain

				sideFlux = variables.kappasInterface.get(edge)*( (variables.waterSuctions.get(topology.edgeRightNeighbour.get(edge)) - variables.waterSuctions.get(topology.edgeLeftNeighbour.get(edge)))/geometry.delta_j.get(edge) 
						+ variables.gravityGradient.get(edge) );
				variables.darcyVelocities.set(edge, sideFlux);

			}

		}
		
		variables.sumBoundaryFlux = sumBoundaryFlux;

	}
	
	//	
	//	public void computeDarcyVelocitiesCapillary(int KMAX) {
	//		
	//		for(int k = 1; k < KMAX; k++) {
	//			variables.darcyVelocitiesCapillary[k] = -variables.kappasInterface[k] * (variables.waterSuctions[k]-variables.waterSuctions[k-1])/geometry.spaceDeltaZ[k];
	//		}
	//		
	//		// element == 0
	//		if(this.bottomBCType.equalsIgnoreCase("Bottom Free Drainage") || this.bottomBCType.equalsIgnoreCase("BottomFreeDrainage")){
	//			variables.darcyVelocitiesCapillary[0] = + 0.0;
	//		} else if (this.bottomBCType.equalsIgnoreCase("Bottom Impervious") || this.bottomBCType.equalsIgnoreCase("BottomImpervious")) {
	//			variables.darcyVelocitiesCapillary[0] = + 0.0;
	//		} else if (this.bottomBCType.equalsIgnoreCase("Bottom Dirichlet") || this.bottomBCType.equalsIgnoreCase("BottomDirichlet")) {
	//			variables.darcyVelocitiesCapillary[0] = -variables.kappasInterface[0] * (variables.waterSuctions[0]-variables.richardsBottomBCValue)/geometry.spaceDeltaZ[0];
	//		}
	//		
	//		// element == KMAX-1
	//		if(this.topBCType.equalsIgnoreCase("Top Dirichlet") || this.topBCType.equalsIgnoreCase("TopDirichlet")){
	////			variables.darcyVelocitiesCapillary[KMAX-1] = -variables.kappasInterface[KMAX-1] * (variables.waterSuctions[KMAX-1]-variables.waterSuctions[KMAX-2])/geometry.spaceDeltaZ[KMAX-1];
	//			variables.darcyVelocitiesCapillary[KMAX] = -variables.kappasInterface[KMAX] * (variables.richardsTopBCValue-variables.waterSuctions[KMAX-1])/geometry.spaceDeltaZ[KMAX];
	//		} else if (this.topBCType.equalsIgnoreCase("Top Neumann") || this.topBCType.equalsIgnoreCase("TopNeumann")){
	////			variables.darcyVelocitiesCapillary[KMAX-1] = -variables.kappasInterface[KMAX-1] * (variables.waterSuctions[KMAX-1]-variables.waterSuctions[KMAX-2])/geometry.spaceDeltaZ[KMAX-1];
	//			variables.darcyVelocitiesCapillary[KMAX] = -9999.0;
	//		} else if (this.topBCType.equalsIgnoreCase("Top coupled") || this.topBCType.equalsIgnoreCase("TopCoupled")){
	////			variables.darcyVelocitiesCapillary[KMAX-1] = -variables.kappasInterface[KMAX-1] * (variables.waterSuctions[KMAX-1]-variables.waterSuctions[KMAX-2])/geometry.spaceDeltaZ[KMAX-1];
	//			variables.darcyVelocitiesCapillary[KMAX] = -9999.0;
	//		}
	//		
	//	}
	//	
	//	public void computeDarcyVelocitiesGravity(int KMAX) {
	//		
	//		for(int k = 1; k < KMAX; k++) {
	//			variables.darcyVelocitiesGravity[k] = -variables.kappasInterface[k];
	//		}
	//		
	//		// element == 0
	//		if(this.bottomBCType.equalsIgnoreCase("Bottom Free Drainage") || this.bottomBCType.equalsIgnoreCase("BottomFreeDrainage")){
	//			variables.darcyVelocitiesGravity[0] = -variables.kappasInterface[0];
	//		} else if (this.bottomBCType.equalsIgnoreCase("Bottom Impervious") || this.bottomBCType.equalsIgnoreCase("BottomImpervious")) {
	//			variables.darcyVelocitiesGravity[0] = + 0.0;
	//		} else if (this.bottomBCType.equalsIgnoreCase("Bottom Dirichlet") || this.bottomBCType.equalsIgnoreCase("BottomDirichlet")) {
	//			variables.darcyVelocitiesGravity[0] = -variables.kappasInterface[0];
	//		}
	//		
	//		// element == KMAX-1
	//		if(this.topBCType.equalsIgnoreCase("Top Dirichlet") || this.topBCType.equalsIgnoreCase("TopDirichlet")){
	////			variables.darcyVelocitiesGravity[KMAX-1] = -variables.kappasInterface[KMAX-1];
	//			variables.darcyVelocitiesGravity[KMAX] = -variables.kappasInterface[KMAX];
	//		} else if (this.topBCType.equalsIgnoreCase("Top Neumann") || this.topBCType.equalsIgnoreCase("TopNeumann")){
	////			variables.darcyVelocitiesGravity[KMAX-1] = -variables.kappasInterface[KMAX-1];
	//			variables.darcyVelocitiesGravity[KMAX] = -9999.0;
	//		} else if (this.topBCType.equalsIgnoreCase("Top coupled") || this.topBCType.equalsIgnoreCase("TopCoupled")){
	////			variables.darcyVelocitiesGravity[KMAX-1] = -variables.kappasInterface[KMAX-1];
	//			variables.darcyVelocitiesGravity[KMAX] = -9999.0;
	//		}	
	//		
	//	}
	//	
	//	public void computePoreVelocities(int KMAX) {
	//		
	//		for(int k = 1; k < KMAX-1; k++) {
	//			variables.poreVelocities[k] = variables.darcyVelocities[k]/interfaceConductivity.compute(variables.thetas[k-1]-parameters.thetaR[variables.parameterID[k-1]],variables.thetas[k]-parameters.thetaR[variables.parameterID[k]]
	//					,geometry.controlVolume[k-1], geometry.controlVolume[k]);
	//		}
	//		
	//		// element == 0
	//		if(this.bottomBCType.equalsIgnoreCase("Bottom Free Drainage") || this.bottomBCType.equalsIgnoreCase("BottomFreeDrainage")){
	//			variables.poreVelocities[0] = variables.darcyVelocities[0]/(variables.thetas[0]-parameters.thetaR[variables.parameterID[0]]);
	//		} else if (this.bottomBCType.equalsIgnoreCase("Bottom Impervious") || this.bottomBCType.equalsIgnoreCase("BottomImpervious")) {
	//			variables.poreVelocities[0] = + 0.0;
	//		} else if (this.bottomBCType.equalsIgnoreCase("Bottom Dirichlet") || this.bottomBCType.equalsIgnoreCase("BottomDirichlet"))  {
	//			variables.poreVelocities[0] = variables.darcyVelocities[0]/interfaceConductivity.compute(closureEquation.get(variables.equationStateID[0]).f(variables.richardsBottomBCValue, variables.temperatures[0], variables.parameterID[0])-parameters.thetaR[variables.parameterID[0]], variables.thetas[0]-parameters.thetaR[variables.parameterID[0]]
	//					,geometry.controlVolume[0], geometry.controlVolume[0]);
	//		} else if (this.bottomBCType.equalsIgnoreCase("Bottom Dirichlet") || this.bottomBCType.equalsIgnoreCase("BottomDirichlet"))  {
	//			variables.poreVelocities[0] = variables.darcyVelocities[0]/(variables.thetas[0]-parameters.thetaR[variables.parameterID[0]]);
	//		}
	//		
	//		// element == KMAX-1
	//		if(this.topBCType.equalsIgnoreCase("Top Dirichlet") || this.topBCType.equalsIgnoreCase("TopDirichlet")){
	//			variables.poreVelocities[KMAX-1] = variables.darcyVelocities[KMAX-1]/interfaceConductivity.compute(variables.thetas[KMAX-2]-parameters.thetaR[variables.parameterID[KMAX-2]],variables.thetas[KMAX-1]-parameters.thetaR[variables.parameterID[KMAX-1]]
	//					,geometry.controlVolume[KMAX-2], geometry.controlVolume[KMAX-1]);
	//			variables.poreVelocities[KMAX] = variables.darcyVelocities[KMAX]/interfaceConductivity.compute(closureEquation.get(variables.equationStateID[KMAX-1]).f(variables.richardsTopBCValue, variables.temperatures[KMAX-1], variables.parameterID[KMAX-1])-parameters.thetaR[variables.parameterID[KMAX-1]], variables.thetas[KMAX-1]-parameters.thetaR[variables.parameterID[KMAX-1]]
	//					,geometry.controlVolume[KMAX-1], geometry.controlVolume[KMAX-1]);
	//		} else if (this.topBCType.equalsIgnoreCase("Top Neumann") || this.topBCType.equalsIgnoreCase("TopNeumann")){
	//			variables.poreVelocities[KMAX-1] = variables.darcyVelocities[KMAX-1]/interfaceConductivity.compute(variables.thetas[KMAX-2]-parameters.thetaR[variables.parameterID[KMAX-2]],variables.thetas[KMAX-1]-parameters.thetaR[variables.parameterID[KMAX-1]]
	//					,geometry.controlVolume[KMAX-2], geometry.controlVolume[KMAX-1]);
	//			variables.poreVelocities[KMAX] = variables.darcyVelocities[KMAX-1]/(variables.thetas[KMAX-1]-parameters.thetaR[variables.parameterID[KMAX-1]]);
	//		} else if (this.topBCType.equalsIgnoreCase("Top coupled") || this.topBCType.equalsIgnoreCase("TopCoupled")){
	//			variables.poreVelocities[KMAX-1] = variables.darcyVelocities[KMAX-1]/interfaceConductivity.compute(closureEquation.get(variables.equationStateID[KMAX-1]).f(variables.waterSuctions[KMAX-1], variables.temperatures[KMAX-1], variables.parameterID[KMAX-1])-parameters.thetaR[variables.parameterID[KMAX-1]], variables.thetas[KMAX-2]-parameters.thetaR[variables.parameterID[KMAX-2]]
	//					,geometry.controlVolume[KMAX-2], geometry.controlVolume[KMAX-2]);
	//			variables.poreVelocities[KMAX] = -9999.0;
	//		}	
	//		
	//	}
	//	
	//	public void computeCelerities(int KMAX) {
	//		
	//		for(int k = 1; k < KMAX-1; k++) {
	//			variables.celerities[k] = -9999.0;
	//		}
	//		
	//		// element == 0
	//		if(this.bottomBCType.equalsIgnoreCase("Bottom Free Drainage") || this.bottomBCType.equalsIgnoreCase("BottomFreeDrainage")){
	//			variables.celerities[0] = -9999.0;
	//		} else if (this.bottomBCType.equalsIgnoreCase("Bottom Impervious") || this.bottomBCType.equalsIgnoreCase("BottomImpervious")) {
	//			variables.celerities[0] = + 0.0;
	//		} else if (this.bottomBCType.equalsIgnoreCase("Bottom Dirichlet") || this.bottomBCType.equalsIgnoreCase("BottomDirichlet"))  {
	//			variables.celerities[0] = -9999.0;
	//		} else if (this.bottomBCType.equalsIgnoreCase("Bottom Dirichlet") || this.bottomBCType.equalsIgnoreCase("BottomDirichlet"))  {
	//			variables.celerities[0] = -9999.0;
	//		}
	//		
	//		// element == KMAX-1
	//		if(this.topBCType.equalsIgnoreCase("Top Dirichlet") || this.topBCType.equalsIgnoreCase("TopDirichlet")){
	//			variables.celerities[KMAX-1] = -9999.0;
	//			variables.celerities[KMAX] = -9999.0;
	//		} else {
	//			variables.celerities[KMAX-1] = -9999.0;
	//			variables.celerities[KMAX] = -9999.0;
	//		}	
	//		
	//	}
	//
	//	public void computeKinematicRatio(int KMAX) {
	//		
	//		for(int k = 0; k < KMAX+1; k++) {
	//			variables.kinematicRatio[k] = variables.celerities[k]/variables.poreVelocities[k];
	//		}
	//
	//	}
	//	
	
		public void computeError(double timeDelta) {
			
//			System.out.println(variables.waterVolumeNew);
//			System.out.println(variables.waterVolume);
//			System.out.println(timeDelta*variables.sumBoundaryFlux);
			variables.errorVolume = variables.waterVolumeNew - variables.waterVolume - timeDelta*variables.sumBoundaryFlux;
	
		}
	
}
