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

import it.geoframe.blogspot.numerical.matop.Matop;

public class MatopFactory {

	public Matop createMatop(String type) {
		
		Matop matop = null;
		
		if(type.equalsIgnoreCase("2DRichards") || type.equalsIgnoreCase("2D Richards")) {
			matop = Matop2DRichards.getInstance();
		} else {
			System.out.println("ERROR MatopFactory.createMatop");
		}
		
		return matop;
	}
}
