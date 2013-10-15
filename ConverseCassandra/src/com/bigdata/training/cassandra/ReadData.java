package com.bigdata.training.cassandra;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadData {

	public static void readFile(String file) throws FileNotFoundException{
		File filePath = new File(file);
		Scanner fileScanner = new Scanner(filePath);
		int i=1;
		while(fileScanner.hasNext()){
			String line = fileScanner.nextLine();
			// skip if blank line
			if (line.isEmpty()){
				continue;
			}
			i++;
			System.out.println(line);
			if (i>10){
				break;
			}
		}
	}
	public static ArrayList<String> readWholeFile(String file) throws FileNotFoundException{
		ArrayList<String> fileList = new ArrayList<String>();
		File filePath = new File(file);
		Scanner fileScanner = new Scanner(filePath);
		while (fileScanner.hasNext()){
			String line = fileScanner.nextLine();
			
			// skip if blank line
			if (line.isEmpty()){
				continue;
			}
			// remove double quotes from line
			line = line.replaceAll("\"", "");
			// add to array
			fileList.add(line);
		}
		return fileList;
	}
}
