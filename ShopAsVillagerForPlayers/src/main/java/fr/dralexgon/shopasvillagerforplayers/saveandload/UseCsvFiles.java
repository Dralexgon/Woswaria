package fr.dralexgon.shopasvillagerforplayers.saveandload;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class UseCsvFiles {
	
	public static void save(String nameFile,List<List<String>> listOfLines, String pathFile) {
		File folder = new File(pathFile);
		if (!folder.exists()) {
			folder.mkdir();
		}
		
		File saveFile = new File(pathFile+nameFile+".csv");
		if (!saveFile.exists()) {
			try {
				saveFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileWriter writer = new FileWriter(saveFile);
			BufferedWriter bw = new BufferedWriter(writer);
			for (List<String> line : listOfLines) {
				for (String cell : line) {
					bw.write(cell+";");
				}
				bw.newLine();
			}
			bw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<List<String>> load(String nameFile, String pathFile) {
		List<List<String>> listOfLines = new ArrayList<List<String>>();
		if (pathFile != "" && pathFile != null) {
			File folder = new File(pathFile);
			if (!folder.exists()) {
				return null;
			}
		}
		File saveFile = new File(pathFile+nameFile+".csv");
		if (!saveFile.exists()) {
			System.out.println("---------------------------------------------------------------------------------------------------------");
			System.out.println("[Error] \""+nameFile+"\" not found. (It's normal if it's the first time you start this plugin.)");
			System.out.println("---------------------------------------------------------------------------------------------------------");
			return null;
		}else {
			try {
				FileReader reader = new FileReader(saveFile);
				BufferedReader br = new BufferedReader(reader);
				String line2 = br.readLine();
				String line = br.readLine();
				while (line!=null) {
					List<String> elements = new ArrayList<String>();
					String[] elementsSplit = line.split(";");
					for (String string : elementsSplit) {
						elements.add(string);
					}
					listOfLines.add(elements);
					line = br.readLine();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				
			}
		}
		return listOfLines;
    }
}
