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

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oms3.annotations.*;



/**
 * @author Niccolo` Tubini
 *
 */
public class Interpolate {

	@In
	public List<Double[]> elementsCentroidsCoordinates;
	
	@In
	public String fileName;
	
	@In
	public double power = 1.0;
	
	@Out
	public List<Double> initialCondition;
	
	private List<double[]> coordinatesAndValue;
	
	private double distance;
	private double weight;
	private double weight_i;
	private double interp;
	
	
	@Execute
	public void process() throws NumberFormatException, IOException {
		
		initialCondition = new ArrayList<Double>(Arrays.asList(new Double[elementsCentroidsCoordinates.size()]));
		
		coordinatesAndValue = readInitialConditionFile(fileName);
		
		System.out.println("Sample points");
		System.out.println("x\ty\tvalue");
		for(int row=0; row<coordinatesAndValue.size(); row++) {
			
			System.out.println(coordinatesAndValue.get(row)[0]+"\t"+coordinatesAndValue.get(row)[1]+"\t"+coordinatesAndValue.get(row)[2]);
			
		}
		
		
		
		for(int element=1; element<elementsCentroidsCoordinates.size(); element++) {
			
			weight = 0.0;
			interp = 0.0;
			System.out.println("\t" + element + " " + weight + " " + interp);
			for(int point=0; point<coordinatesAndValue.size(); point++) {
				
				distance = Math.sqrt( Math.pow(elementsCentroidsCoordinates.get(element)[0]-coordinatesAndValue.get(point)[0],2) +
											Math.pow(elementsCentroidsCoordinates.get(element)[1]-coordinatesAndValue.get(point)[1],2) );
				
				if(distance==0.0) {
					
					interp = coordinatesAndValue.get(point)[2];
					weight = 1.0;
					break;
					
				} else {
					
					weight_i = 1/Math.pow(distance, power);
					weight += weight_i;
					interp += coordinatesAndValue.get(point)[2]*weight_i;
					
				}
				
			}
			
			interp = interp/weight;
			
			initialCondition.set(element, interp);
			
		}
		
		
		System.out.println("\nInterpolate points");
		System.out.println("x\ty\tvalue");
		for(int element=1; element<elementsCentroidsCoordinates.size(); element++) {
			
			System.out.println(elementsCentroidsCoordinates.get(element)[0]+"\t"+elementsCentroidsCoordinates.get(element)[1]+"\t"+initialCondition.get(element));
			
		}
		
	}

	
	
	
	private List<double[]> readInitialConditionFile(String fileName) throws NumberFormatException, IOException{
		
		double[] tmp = new double[3]; // x, z, value
		List<double[]> coordinatesAndValue = new ArrayList<double[]>();
		
		File file = new File(fileName);
		FileInputStream fileInputStream = null;
	
		try {
			
			fileInputStream = new FileInputStream(file);
			
		} catch (FileNotFoundException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		InputStreamReader inputStreamerReader = new InputStreamReader(fileInputStream);
		BufferedReader bufferReader = new BufferedReader(inputStreamerReader);
		String line;
		
		while((line = bufferReader.readLine()) != null){
			
			String[] lineContent = line.split(",");
			tmp[0] = Double.valueOf(lineContent[0]);
			tmp[1] = Double.valueOf(lineContent[1]);
			tmp[2] = Double.valueOf(lineContent[2]);
			coordinatesAndValue.add(tmp.clone());
			
		}

		return coordinatesAndValue;
		
	}
	
	
}
