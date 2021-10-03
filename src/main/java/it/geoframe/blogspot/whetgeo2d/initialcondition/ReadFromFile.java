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
public class ReadFromFile {
	
	@In
	public String fileName;
	
	
	@Out
	public List<Double> initialCondition;
	
	private List<Double> values;
		
	
	@Execute
	public void process() throws NumberFormatException, IOException {
		

		initialCondition = readInitialConditionFile(fileName);
		

		
//		System.out.println("\nIC");
//		System.out.println("x\ty\tvalue");
//		for(int element=1; element<initialCondition.size(); element++) {
//			
//			System.out.println(initialCondition.get(element));
//			
//		}
		
	}

	
	
	
	private List<Double> readInitialConditionFile(String fileName) throws NumberFormatException, IOException{
		
		List<Double> values = new ArrayList<Double>();
		
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
		String[] lineContent;

		while((line = bufferReader.readLine()) != null){
			
			lineContent = line.split(",");
			values.add(Double.valueOf(lineContent[0]));
			
		}

		return values;
		
	}
	
	
}
