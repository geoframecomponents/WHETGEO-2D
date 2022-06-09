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

package it.geoframe.blogspot.whetgeo2d.pdefinitevolume;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import it.geoframe.blogspot.whetgeo2d.data.*;
import it.geoframe.blogspot.closureequation.closureequation.Parameters;
import it.geoframe.blogspot.closureequation.equationstate.EquationState;
import it.geoframe.blogspot.numerical.newtonalgorithm.NestedNewtonCG;
import it.geoframe.blogspot.numerical.matop.Matop;

import oms3.annotations.*;

@Description("This code solve the mixed form of Richards equation."
		+ "A semi-implicit finite volume method is used to discretize the equation, and the non-linear system is solved using the nested Newton algorithm.")
@Documentation("")
@Author(name = "Niccolo' Tubini and Riccardo Rigon", contact = "tubini.niccolo@gmail.com")
@Keywords("Richards equation, numerical solver, finite volume ")
@Bibliography("")
//@Label()
//@Name()
//@Status()
@License("General Public License Version 3 (GPLv3)")

public class Richards2DFiniteVolumeSolver {

	@Description("Time step of integration")
	@Unit ("s")
	private double timeDelta;

	@Description("Maximun number of Newton iterations")
	final int MAXITER_NEWT = 50;


	@Description("Right hand side vector of the scalar equation to solve")
	@Unit ("-")
	private List<Double> rhs;


	@Description("Main diagonal of the matrix for the Jacobi preconditioner")
	@Unit ("-")
	private List<Double> mainDiagonal;
	
	@Description("")
	@Unit ("-")
	private List<Integer> elementParameterID;
	
	@Description("")
	@Unit ("-")
	private List<Integer> elementEquationStateID;

	private boolean checkData = false;
	
	private ProblemQuantities variables;
	
	private Geometry geometry;
	
	private Topology topology;
	
	private double sideFlux;
	
	private double mainDiagonalEntry;
	

//	@Description("Object to perform the nested Newton algortithm")
	private NestedNewtonCG nestedNewtonAlg;

    //////////////////////////////



	public Richards2DFiniteVolumeSolver( double newtonTolerance, double cdgTolerance,
			int MAXITER_NEWT, List<EquationState> equationState, Matop matop, List<Integer> elementParameterID, List<Integer> elementEquationStateID) {

		geometry = Geometry.getInstance();
		topology = Topology.getInstance();
		
		rhs = new ArrayList<Double>(Arrays.asList(new Double[topology.elementEdgesSet.size()]));
		mainDiagonal = new ArrayList<Double>(Arrays.asList(new Double[topology.elementEdgesSet.size()]));
		this.elementParameterID = new ArrayList<Integer>(elementParameterID);
		this.elementEquationStateID = new ArrayList<Integer>(elementEquationStateID);
		
		nestedNewtonAlg = new NestedNewtonCG(newtonTolerance, MAXITER_NEWT, equationState, matop, cdgTolerance,
				elementParameterID, elementEquationStateID);

	}


	

	public List<Double> solve(double timeDelta, HashMap<Integer, double[]> inBC, List<Double> kappasInterface, List<Double> volumes, List<Double> ets,
			List<Double> waterSuctions, List<Double> temperatures, List<Double> gravityGradient) {


		this.timeDelta = timeDelta;


		/* 
		 * Right-hand-side of the algebraic system:
		 */
		for(int element=1; element<topology.elementEdgesSet.size(); element++) {
			rhs.set(element, volumes.get(element));
			mainDiagonal.set(element, 0.0);
		}
				
//		System.out.println("\n\n\tSIDE FLUX");
		for(int edge=1; edge<topology.edgeRightNeighbour.size(); edge++) {
			
			/*
			 * Add boundary condition
			 * 	- 1 neumann
			 * 	- 2 dirichlet with water pressure
			 * 	- 3 free drainage
			 * 	- 4 dirichlet with total head
			 */
			if(topology.edgeRightNeighbour.get(edge)==0) {
				
				if(topology.edgesBoundaryBCType.get(edge)==1) {
					/*
					 * FIXME: scalar product between edge and flux
					 */
					sideFlux = this.timeDelta*geometry.edgesLength.get(edge)*inBC.get(topology.edgesBoundaryBCValue.get(edge))[0];
//					rhs.set(topology.edgeLeftNeighbour.get(edge), rhs.get(topology.edgeLeftNeighbour.get(edge))+sideFlux);
					mainDiagonalEntry = 0.0;
					
				} else if(topology.edgesBoundaryBCType.get(edge)==2) {
					
					sideFlux = this.timeDelta*kappasInterface.get(edge)*( inBC.get(topology.edgesBoundaryBCValue.get(edge))[0]/geometry.delta_j.get(edge) 
																					+ gravityGradient.get(edge) );
//					rhs.set(topology.edgeLeftNeighbour.get(edge), rhs.get(topology.edgeLeftNeighbour.get(edge))+sideFlux);
					mainDiagonalEntry = this.timeDelta*kappasInterface.get(edge)/geometry.delta_j.get(edge);

				} else if(topology.edgesBoundaryBCType.get(edge)==3) {
					
//					sideFlux = this.timeDelta*kappasInterface.get(edge)*geometry.edgesLength.get(edge)*Math.min(0.0, geometry.edgeNormalVector.get(edge)[1]);
					sideFlux = this.timeDelta*kappasInterface.get(edge)*geometry.edgesLength.get(edge)*Math.min(0.0, gravityGradient.get(edge));

//					System.out.println(edge+" " +this.timeDelta*kappasInterface.get(edge)*geometry.edgesLength.get(edge) +" "+gravityGradient.get(edge)+" "+sideFlux);
					mainDiagonalEntry = 0.0;

				} else if(topology.edgesBoundaryBCType.get(edge)==4) {
				
					sideFlux = this.timeDelta*kappasInterface.get(edge)*( (inBC.get(topology.edgesBoundaryBCValue.get(edge))[0]-geometry.edgesCentroidsCoordinates.get(edge)[1])/geometry.delta_j.get(edge) 
																					+ gravityGradient.get(edge) );
					mainDiagonalEntry = this.timeDelta*kappasInterface.get(edge)/geometry.delta_j.get(edge);

				}
				
//				System.out.println(edge + " " + sideFlux);
				rhs.set(topology.edgeLeftNeighbour.get(edge), rhs.get(topology.edgeLeftNeighbour.get(edge))+sideFlux);
				mainDiagonal.set(topology.edgeLeftNeighbour.get(edge), mainDiagonal.get(topology.edgeLeftNeighbour.get(edge))+mainDiagonalEntry);
				
			} else {
				
				sideFlux = this.timeDelta*kappasInterface.get(edge)*gravityGradient.get(edge);
//				System.out.println(edge + " " + sideFlux);
				rhs.set(topology.edgeRightNeighbour.get(edge), rhs.get(topology.edgeRightNeighbour.get(edge)) - sideFlux);
				rhs.set(topology.edgeLeftNeighbour.get(edge), rhs.get(topology.edgeLeftNeighbour.get(edge)) + sideFlux);
				
				mainDiagonalEntry = this.timeDelta*kappasInterface.get(edge)/geometry.delta_j.get(edge);
				mainDiagonal.set(topology.edgeRightNeighbour.get(edge), mainDiagonal.get(topology.edgeRightNeighbour.get(edge)) + mainDiagonalEntry);
				mainDiagonal.set(topology.edgeLeftNeighbour.get(edge), mainDiagonal.get(topology.edgeLeftNeighbour.get(edge)) + mainDiagonalEntry);
				
			}
			
		}
		
		
//		for(int element=1; element<topology.elementEdgesSet.size(); element++) {
//			System.out.println("\t"+element+"\t"+mainDiagonal.get(element));
//		}
//		
//		System.out.println("RHS");
//		for(int element=1; element<topology.elementEdgesSet.size(); element++) {
//			System.out.println("\t"+element+"\t"+rhs.get(element));
//		}
				

		
		/* 
		 * NESTED NEWTON ALGORITHM /
		 */

		nestedNewtonAlg.set(waterSuctions, temperatures, rhs, mainDiagonal);
		return waterSuctions = nestedNewtonAlg.solver();



	} //// MAIN CYCLE END ////


} 



