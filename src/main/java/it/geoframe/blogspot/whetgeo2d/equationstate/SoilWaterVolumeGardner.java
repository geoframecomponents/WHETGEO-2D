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

/**
 * 
 */
package it.geoframe.blogspot.whetgeo2d.equationstate;

import it.geoframe.blogspot.closureequation.closureequation.ClosureEquation;
import it.geoframe.blogspot.closureequation.equationstate.EquationState;
import it.geoframe.blogspot.whetgeo2d.data.Geometry;
import it.geoframe.blogspot.whetgeo2d.data.ProblemQuantities;

/**
 * @author Niccolo` Tubini
 *
 */
public class SoilWaterVolumeGardner extends EquationState {

	private Geometry geometry;
	private ProblemQuantities variables;


	public SoilWaterVolumeGardner(ClosureEquation closureEquation) {
		super(closureEquation);
		this.geometry = Geometry.getInstance();
		this.variables = ProblemQuantities.getInstance();
	}



	@Override
	public double equationState(double x, double y, int id, int element) {
		
		return closureEquation.f(x, y, id)*geometry.elementsArea.get(element);

	}


	@Override
	public double dEquationState(double x, double y, int id, int element) {

		return closureEquation.df(x, y, id)*geometry.elementsArea.get(element);

	}


	@Override
	public double ddEquationState(double x, double y, int id, int element) {

		return closureEquation.ddf(x, y, id)*geometry.elementsArea.get(element);

	}


	@Override
	public double p(double x, double y, int id, int element) {

		return dEquationState(x, y, id, element);  

	}


	@Override
	public double pIntegral(double x, double y, int id, int element) {

		return equationState(x, y, id, element);  

	}



	@Override
	public void computeXStar(double y, int id, int element) {
		
		variables.waterSuctionStar1.add(element, -9999.0);
		variables.waterSuctionStar2.add(element, -9999.0);
		variables.waterSuctionStar3.add(element, -9999.0);
		
	}

	@Override
	public double initialGuess(double x, int id, int element) {
		
		return variables.waterSuctions.get(element);
		
	}


}
