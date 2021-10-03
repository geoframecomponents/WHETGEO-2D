/*
 * GNU GPL v3 License
 *
 * Copyright 2019  Niccolo` Tubini
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

package it.geoframe.blogspot.whetgeo2d.matop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import it.geoframe.blogspot.numerical.matop.Matop;
import it.geoframe.blogspot.whetgeo2d.data.Geometry;
import it.geoframe.blogspot.whetgeo2d.data.ProblemQuantities;
import it.geoframe.blogspot.whetgeo2d.data.Topology;


public class Matop2DRichards extends Matop {

	private static Matop2DRichards uniqueInstance;
	private Geometry geometry;
	private Topology topology;
	private ProblemQuantities variables;
	private List<Double> Apsi;

	private double sideFlux;


	public static Matop2DRichards getInstance() {

		if (uniqueInstance == null) {

			uniqueInstance = new Matop2DRichards();

		}

		return uniqueInstance;
	}



	public Matop2DRichards() {

		geometry = Geometry.getInstance();
		topology = Topology.getInstance();
		variables = ProblemQuantities.getInstance();		
		Apsi = new ArrayList<Double>(Arrays.asList(new Double[topology.elementEdgesSet.size()]));


	}



	public List<Double> solve(List<Double> dis, List<Double> x) {

		//Map<Integer, Double> Apsi = new HashMap<Integer,Double>();

		//		Apsi.clear();
		for(int element=1; element<topology.elementEdgesSet.size(); element++) {

			Apsi.set(element, dis.get(element)*x.get(element));

		}


		for(int edge=1; edge<topology.edgeLeftNeighbour.size(); edge++) {
			/*
			 * FIXME: everywhere NO FLUX BOUNDARY CONDITION
			 */
			sideFlux = 0.0;

			if(topology.edgeRightNeighbour.get(edge)==0) {

				if(topology.edgesBoundaryBCType.get(edge)==2 || topology.edgesBoundaryBCType.get(edge)==4) {

//					System.out.println(edge+" "+variables.timeDelta +" "+variables.kappasInterface.get(edge)+" "+ -x.get(topology.edgeLeftNeighbour.get(edge))+" "+geometry.delta_j.get(edge));
					sideFlux = variables.timeDelta * variables.kappasInterface.get(edge) * ( -x.get(topology.edgeLeftNeighbour.get(edge)) )/geometry.delta_j.get(edge);
					Apsi.set( topology.edgeLeftNeighbour.get(edge), Apsi.get(topology.edgeLeftNeighbour.get(edge))-sideFlux );

				}

			} else {

				sideFlux = variables.timeDelta * variables.kappasInterface.get(edge) * ( x.get(topology.edgeRightNeighbour.get(edge))-x.get(topology.edgeLeftNeighbour.get(edge)) )/geometry.delta_j.get(edge);
//				System.out.println(edge+" "+variables.timeDelta +" "+variables.kappasInterface.get(edge)+" "+ -x.get(topology.edgeLeftNeighbour.get(edge))+" "+geometry.delta_j.get(edge));
				Apsi.set( topology.edgeLeftNeighbour.get(edge), Apsi.get(topology.edgeLeftNeighbour.get(edge))-sideFlux );
				Apsi.set( topology.edgeRightNeighbour.get(edge), Apsi.get(topology.edgeRightNeighbour.get(edge))+sideFlux );

			}
		}
		//			System.out.println("\n\nApsi:");
		//			for(Integer element : Topology.s_i.keySet()) {
		//				System.out.println("\t" + element + "\t" + Apsi.get(element));
		//			}

		//		for(int edge=1; edge<topology.edgesBoundaryBCType.size(); edge++) {
		//			/*
		//			 * FIXME: boundary conditions
		//			 * if 0 no flux
		//			 * if 1 neumann 
		//			 * if 2 dirichlet
		//			 */
		//			
		//			if(topology.edgesBoundaryBCType.get(edge)==2 || topology.edgesBoundaryBCType.get(edge)==4) {
		//				
		//				sideFlux = variables.timeDelta * variables.kappasInterface.get(edge) * ( -x.get(topology.edgeLeftNeighbour.get(edge)) )/geometry.delta_j.get(edge);
		//				
		//				Apsi.set( topology.edgeLeftNeighbour.get(edge), Apsi.get(topology.edgeLeftNeighbour.get(edge))-sideFlux );
		//				
		//			}
		//			
		//		}

		return Apsi;

	}

}
