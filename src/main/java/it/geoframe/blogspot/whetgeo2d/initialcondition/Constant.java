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

package it.geoframe.blogspot.whetgeo2d.initialcondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oms3.annotations.*;



/**
 * @author Niccolo` Tubini
 *
 */
public class Constant {

	@In
	public List<Double[]> elementsCentroidsCoordinates;
	
	@In
	public double value;
	
	@Out
	public List<Double> initialCondition;
	
	@Execute
	public void process() {
		
		
		initialCondition = new ArrayList<Double>(Arrays.asList(new Double[elementsCentroidsCoordinates.size()]));
		
		for(int element=1; element<elementsCentroidsCoordinates.size(); element++) {
			
			initialCondition.set(element, value);
			
		}
	}
	
}
