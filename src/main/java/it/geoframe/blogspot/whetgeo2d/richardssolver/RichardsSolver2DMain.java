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

package it.geoframe.blogspot.whetgeo2d.richardssolver;

import static org.hortonmachine.gears.libs.modules.HMConstants.isNovalue;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

//import static org.jgrasstools.gears.libs.modules.JGTConstants.isNovalue;

import it.geoframe.blogspot.closureequation.closureequation.Parameters;
import it.geoframe.blogspot.closureequation.equationstate.EquationState;
import it.geoframe.blogspot.numerical.matop.Matop;
import it.geoframe.blogspot.whetgeo2d.data.ComputeQuantitiesRichards;
import it.geoframe.blogspot.whetgeo2d.data.Geometry;
import it.geoframe.blogspot.whetgeo2d.data.ProblemQuantities;
import it.geoframe.blogspot.whetgeo2d.data.Topology;
import it.geoframe.blogspot.whetgeo2d.matop.Matop2DRichards;
import it.geoframe.blogspot.whetgeo2d.pdefinitevolume.Richards2DFiniteVolumeSolver;
import oms3.annotations.In;
import oms3.annotations.Out;
import oms3.annotations.Unit;
import oms3.annotations.Execute;
import oms3.annotations.Description;
import oms3.annotations.Documentation;
import oms3.annotations.Author;
import oms3.annotations.Keywords;
import oms3.annotations.Bibliography;
import oms3.annotations.License;


@Description("Solve the Richards equation for the 2D domain.")
@Documentation("")
@Author(name = "Niccolo' Tubini, and Riccardo Rigon", contact = "tubini.niccolo@gmail.com")
@Keywords("Hydrology, Richards equation, Richardson-Richards equation, Infiltration, WHETGEO, WHETGEO-2D, GEOframe")
@Bibliography("Casulli (2010)")
//@Label()
//@Name()
//@Status()
@License("General Public License Version 3 (GPLv3)")
public class RichardsSolver2DMain {

	/*
	 * SOIL PARAMETERS
	 */
	@Description("The hydraulic conductivity at saturation")
	@In 
	@Unit ("m/s")
	public double[] ks;

	@Description("Saturated water content")
	@In 
	@Unit ("-")
	public double[] thetaS;

	@Description("Residual water content")
	@In 
	@Unit ("-")
	public double[] thetaR;

	@Description("First parameter of SWRC")
	@In 
	@Unit ("-")
	public double[] par1SWRC;

	@Description("Second parameter of SWRC")
	@In 
	@Unit ("-")
	public double[] par2SWRC;

	@Description("Third parameter of SWRC")
	@In 
	@Unit ("-")
	public double[] par3SWRC;

	@Description("Fourth parameter of SWRC")
	@In 
	@Unit ("-")
	public double[] par4SWRC;

	@Description("Fifth parameter of SWRC")
	@In 
	@Unit ("-")
	public double[] par5SWRC;

	@Description("Aquitard compressibility")
	@In 
	@Unit ("1/Pa")
	public double[] alphaSpecificStorage;

	@Description("Water compressibility")
	@In 
	@Unit ("1/Pa")
	public double[] betaSpecificStorage;

	@Description("Coefficient for water suction dependence on temperature")
	@In 
	@Unit ("K")
	public double beta0 = -776.45;

	@Description("Reference temperature for soil water content")
	@In 
	@Unit ("K")
	public double referenceTemperatureSWRC = 278.15;

	@Description("Control volume label defining the equation state")
	@In 
	@Unit("-")
	public List<Integer> elementEquationStateID;

	@Description("Control volume label defining the set of the paramters")
	@In 
	@Unit("-")
	public List<Integer> elementParameterID;

	/*
	 * MODELS
	 * - closure equation
	 * - conductivity model
	 * - interface conductivity model
	 */
	@Description("It is possibile to chose between 3 different models to compute "
			+ "the soil hydraulic properties: Van Genuchten; Brooks and Corey; Kosugi unimodal")
	@In 
	public String[] typeClosureEquation;

	@Description("It is possibile to chose between 3 different models to compute "
			+ "the soil hydraulic properties: Van Genuchten; Brooks and Corey; Kosugi unimodal")
	@In 
	public String[] typeEquationState;

	@Description("It is possible to choose among these models:"
			+ "Mualem Van Genuchten, Mualem Brooks Corey, ....")
	@In 
	public String[] typeUHCModel;

	@Description("It is possible to choose among these models:"
			+ "notemperature, ....")
	@In 
	public String typeUHCTemperatureModel;


	@Description("Hydraulic conductivity at control volume interface can be evaluated as"
			+ " the average of kappas[i] and kappas[i+1]"
			+ " the maximum between kappas[i] and kappas[i+1]"
			+ " the minimum between kappas[i] and kappas[i+1]"
			+ " a weighted average of kappas[i] and kappas[i+1] where weights are dx[i] and dx[i+1]")
	@In
	public String interfaceHydraulicConductivityModel;


	/*
	 * INITIAL CONDITION
	 */
	@Description("Initial condition for water suction")
	@In
	@Unit("m")
	public List<Double> icWaterSuction;

	@Description("Initial condition for temperature")
	@In
	@Unit("K")
	public List<Double> temperature;

	/*
	 * GEOMETRY
	 */
	@Description("z coordinate read from grid NetCDF file")
	@In
	@Unit("m")
	public List<Double[]> elementsCentroidsCoordinates;

	@Description("Space delta to compute gradients read from grid NetCDF file")
	@In 
	@Unit("m")
	public List<Double[]> edgesCentroidsCoordinates;

	@Description("Length of control volumes read from grid NetCDF file")
	@In 
	@Unit("m")
	public List<Double> elementsArea;

	@Description("")
	@In 
	@Unit("m")
	public List<Double> edgesLenght;

	@Description("")
	@In 
	@Unit("m")
	public List<Double> delta_j;

	@Description("")
	@In 
	@Unit("m")
	public List<Double[]> edgeNormalVector;

	/*
	 * TOPOLOGY
	 */

	@Description("")
	@In 
	@Unit("m")
	public List<Integer> edgeLeftNeighbour;

	@Description("")
	@In 
	@Unit("m")
	public List<Integer> edgeRightNeighbour;

	@Description("")
	@In 
	@Unit("m")
	public List<ArrayList<Integer>> elementEdgesSet;


	/*
	 * TIME STEP
	 */
	@Description("Time amount at every time-loop")
	@In
	@Unit ("s")
	public double tTimeStep;

	@Description("Time step of integration")
	@In
	@Unit ("s")
	public double timeDelta;

	/*
	 * ITERATION PARAMETERS
	 */
	@Description("Tolerance for Newton iteration")
	@In
	public double newtonTolerance = 1e-9; 

	@Description("Conjugate gradient tolerance")
	@In
	public double cgTolerance = 1e-9; 

	@Description("Number of Picard iteration to update the diffusive flux matrix")
	@In
	public int picardIteration=1;

	/*
	 *  BOUNDARY CONDITIONS
	 */
	@Description("")
	@In 
	@Unit("m")
	public List<Integer> edgesBoundaryBCType;

	@Description("")
	@In 
	@Unit("m")
	public List<Integer> edgesBoundaryBCValue;

	@Description("The HashMap with the time series of the boundary condition")
	@In
	@Unit ("")
	public HashMap<Integer, double[]> inHMBoundaryCondition;

	@Description("")
	@In
	@Unit ("")
	public HashMap<Integer, double[]> inSaveDate;

	@Description("The current date of the simulation.")
	@In
	@Out
	public String inCurrentDate;


	/*
	 * OUTPUT
	 */

	@Description("ArrayList of variable to be stored in the buffer writer")
	@Out
	public ArrayList<ArrayList<Double>> outputToBuffer;


	@Description("Control variable")
	@Out
	public boolean doProcessBuffer;

	//////////////////////////////////////////
	//////////////////////////////////////////


	@Description("Maximun number of Newton iterations")
	private final int MAXITER_NEWT = 50;

	@Description("Number of control volume for domain discetrization")
	@Unit (" ")
	private int KMAX; 

	@Description("It is needed to iterate on the date")
	private int step;

	@Description("Control value to save output:"
			+ "- 1 save the current time step output"
			+ "- 0 do not save")
	private double saveDate;

	@Description("Temporary variable to read boundary conditions")
	private double tmpBCValue;

	private Richards2DFiniteVolumeSolver richardsSolver;
	private ProblemQuantities variables;
	private Geometry geometry;
	private Topology topology;
	private Parameters parameters;
	private ComputeQuantitiesRichards computeQuantitiesRichards;
	private Matop matop2DRichards;


	@Execute
	public void solve() {

		if (step==0){


			variables = ProblemQuantities.getInstance(icWaterSuction, temperature, elementEquationStateID, elementParameterID, elementEdgesSet.size(), edgeRightNeighbour.size());
			topology = Topology.getInstance(edgeLeftNeighbour, edgeRightNeighbour, edgesBoundaryBCType, edgesBoundaryBCValue, elementEdgesSet);
			geometry = Geometry.getInstance(elementsArea, edgesLenght, delta_j, edgeNormalVector, elementsCentroidsCoordinates, edgesCentroidsCoordinates);

			parameters = Parameters.getInstance(referenceTemperatureSWRC, beta0, thetaS, thetaR, par1SWRC, par2SWRC, par3SWRC, par4SWRC, par5SWRC, ks, alphaSpecificStorage, betaSpecificStorage);

			computeQuantitiesRichards = new ComputeQuantitiesRichards(typeClosureEquation, typeEquationState, typeUHCModel, typeUHCTemperatureModel, interfaceHydraulicConductivityModel);

			computeQuantitiesRichards.computeXStar();

			computeQuantitiesRichards.computeGravityGradient();

			outputToBuffer = new ArrayList<ArrayList<Double>>();

			List<EquationState> equationState = computeQuantitiesRichards.getRichardsStateEquation();

			matop2DRichards = new Matop2DRichards();

			richardsSolver = new Richards2DFiniteVolumeSolver(newtonTolerance, cgTolerance, MAXITER_NEWT, equationState, matop2DRichards,
					variables.elementParameterID, variables.elementEquationStateID);


		}

		doProcessBuffer = false;

		//		System.out.println(inCurrentDate);
		saveDate = 1.0;
		if(inSaveDate != null) {
			saveDate = inSaveDate.get(1)[0];
		}

		outputToBuffer.clear();

		double sumTimeDelta = 0;


		while(sumTimeDelta < tTimeStep) {

			if(sumTimeDelta + timeDelta>tTimeStep) {
				timeDelta = tTimeStep - sumTimeDelta;
			}
			sumTimeDelta = sumTimeDelta + timeDelta;

			variables.timeDelta = timeDelta;


			/*
			 * Compute water volumes
			 */
			computeQuantitiesRichards.computeThetas();


			//						for(int element=1; element<topology.elementEdgesSet.size(); element++) {
			//							System.out.println(element + " psi " + variables.waterSuctions.get(element) + " theta " + variables.thetas.get(element));
			//						}

			computeQuantitiesRichards.computeWaterVolume();

			//						for(int element=1; element<topology.elementEdgesSet.size(); element++) {
			//							System.out.println(element + " " + variables.volumes.get(element));
			//						}


			/*
			 * Solve PDE
			 */
			for(int picard=0; picard<picardIteration; picard++) {

				/*
				 * Compute hydraulic conductivity
				 * 
				 */	
				computeQuantitiesRichards.computeHydraulicConductivity();
//												for(int element=1; element<topology.elementEdgesSet.size(); element++) {
//													System.out.println(element + " " + variables.kappas.get(element));
//												}
				
				
				computeQuantitiesRichards.computeInterfaceHydraulicConductivity();
//												for(int edge=1; edge<topology.edgeRightNeighbour.size(); edge++) {
//													System.out.println("\t" + edge + " psi " + variables.kappasInterface.get(edge));
//												}


				/*
				 * Solve PDE
				 */
				//				System.out.println("\n\n\n\t\tSOLVER");
				variables.waterSuctions = richardsSolver.solve(timeDelta, inHMBoundaryCondition, variables.kappasInterface, variables.volumes, null,
						variables.waterSuctions, variables.temperatures, variables.gravityGradient);

			} // close Picard iteration


			/*
			 * Compute 
			 * - water volume and total water volume
			 * - water content
			 */
			computeQuantitiesRichards.computeWaterVolumeNew();
			computeQuantitiesRichards.computeThetasNew();
			computeQuantitiesRichards.computeSaturationDegreeNew();


			/*
			 * Fluxes
			 */
			computeQuantitiesRichards.computeDarcyVelocities(inHMBoundaryCondition);
			//			computeQuantitiesRichards.computeDarcyVelocitiesCapillary(KMAX);
			//			computeQuantitiesRichards.computeDarcyVelocitiesGravity(KMAX);
			//			computeQuantitiesRichards.computePoreVelocities(KMAX);
			//			computeQuantitiesRichards.computeCelerities(KMAX);
			//			computeQuantitiesRichards.computeKinematicRatio(KMAX);
			//
			//			
			/*
			 * compute error
			 */
			computeQuantitiesRichards.computeError(timeDelta);
//			System.out.println("\n\n\tERROR VOLUME "+variables.errorVolume);

		}


		if(saveDate == 1) {

			outputToBuffer.add((ArrayList<Double>) variables.waterSuctions);
			outputToBuffer.add((ArrayList<Double>) variables.thetasNew);
			outputToBuffer.add((ArrayList<Double>) variables.volumesNew);
			outputToBuffer.add((ArrayList<Double>) variables.saturationDegree);
			outputToBuffer.add((ArrayList<Double>) variables.darcyVelocities);
			outputToBuffer.add((ArrayList<Double>) variables.darcyVelocities);
			outputToBuffer.add((ArrayList<Double>) variables.darcyVelocities);
			outputToBuffer.add(new ArrayList<Double>(Arrays.asList(variables.errorVolume)));
			doProcessBuffer = true;

		} else {

		}
		step++;
		//
	} //// MAIN CYCLE END ////



}  /// CLOSE Richards2d ///
