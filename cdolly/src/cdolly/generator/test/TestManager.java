package cdolly.generator.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import scala.xml.Null;
import cdolly.configuration.Parameters;
import cdolly.executor.Executor;
import cdolly.experiments.Parameters2;
import cdolly.generator.ifdef.IfdefGenerator;
import cdolly.utils.GeneralFunctions;

public class TestManager {
	


	private TestManager() {
	}



	public static TestManager createRunTest() {
		return new TestManager();
	}

	// compiles all programs
	public static void compilePrograms(File[] contents, String folder_test) {

		
		for (int i=0; i<contents.length; i++) {
			String program_name = contents[i].toString().split("/")[6];
			if(!program_name.contains(".txt") && !program_name.contains(".sh")) {
				String comand = "gcc "+ folder_test + "/" + program_name +" -o "+ folder_test + "/"+program_name.replace(".c",""); 
				System.out.println("Compilando o programa " + program_name);
				GeneralFunctions.executaComando(comand, null);
			}
		}
	}
	
	public static List<Integer> copiesProgramsCompiled(File[] contents,String folder_programs, String folder_compiled) {
		File fOutputFolder = new File(folder_compiled);
		File programOutputFolder = new File(fOutputFolder.getAbsolutePath());
		if (!programOutputFolder.exists()){
			programOutputFolder.mkdir();
		}
	
		System.out.println("Copiando todos os programas compilados!");
		List<Integer> list_number = new ArrayList<Integer>(); 
		for (int i=0; i<contents.length; i++) {  
			String program_name = contents[i].toString().split("/")[6];
			if(!program_name.contains(".c") && !program_name.contains(".txt") && !program_name.contains(".sh")) {
				program_name = program_name.replace("test", "");
				String comand = "cp " + folder_programs + "/f"+program_name + ".c " + folder_compiled; 
				GeneralFunctions.executaComando(comand, null);
				list_number.add(Integer.parseInt(program_name));
				
			}
		}
		
		
		return list_number;
	}
	
	public static void createFiles(String folder_test) throws IOException {
		FileWriter fw = new FileWriter(folder_test+"/test_result.txt");
		String content = "";
		content += "Program;Function 0;Input 0;Output 0;Function 1;Input 1;Output 1\n";
		content += "0;0;10;10;1;20;20\n";
		
		fw.write(content);
		fw.close();
		
		GeneralFunctions.executaComando("chmod 777 "+folder_test +"/test_result.txt", null);
		fw = new FileWriter(folder_test+"/script_seq.sh");
		content = "";
		content += "#!/bin/bash"
				+ "\nprogram=$1"
				+ "\nparam1=$2"
				+ "\nparam2=$3"
				+ "\ndiretory=$4"
				+ "\ncd $diretory"
				+ "\n./$program $param1 $param2";
		
		fw.write(content);
		fw.close();
		GeneralFunctions.executaComando("chmod 777 "+ folder_test +"/script_seq.sh", null);
		
	}
	
	
	public static void runTests(List<Integer> list_number, String folder_test) throws IOException {
		
		for(Integer number : list_number){
			System.out.println("Testando o programa: "+ number.toString());
			String program_name = "test" + number.toString();   //contents[i].toString().split("/")[6];
			FileReader arq = new FileReader(folder_test+"/test_result.txt");  
			BufferedReader readArq = new BufferedReader(arq); 
			String line = readArq.readLine(); 
			String last_line = "";
			while (line != null) { 
				last_line = line;
				line = readArq.readLine(); 			
			} 
			String param1 = last_line.split(";")[3];
			String param2 = last_line.split(";")[6];
			arq.close();
			readArq.close();
			GeneralFunctions.executaComando(folder_test + "/script_seq.sh " + program_name + " "+ param1 + " " + param2 + " " + folder_test + "/", null);
				
			
		}	
		
	}
	
public static void runTestsRefactored(List<Integer> list_number, String folder_test_refactoring) throws IOException {
	String folder_tests     = Parameters.getString("Parameter.folder_tests"); //$NON-NLS-1$
		for(Integer number : list_number){
			String program_name = "test" + number.toString();   //contents[i].toString().split("/")[6];
			FileReader arq = new FileReader(folder_tests+"/test_result.txt");  
			BufferedReader readArq = new BufferedReader(arq); 
			String line = readArq.readLine();
			String line2 = "";
			while (line != null) { 
				if (line.split(";")[0].contains(number.toString())) {
					line2 = line;
					break;
				}
				line = readArq.readLine(); 	
				
			}
			System.out.println(line2);
			String param1 = line2.split(";")[2];
			String param2 = line2.split(";")[5];
			arq.close();
			readArq.close();
			GeneralFunctions.executaComando(folder_test_refactoring + "/script_seq.sh " + program_name + " "+ param1 + " " + param2 + " " + folder_test_refactoring + "/", null);
		}	
	}
	
	public static void joinResults() throws IOException {
		String folder_test = Parameters.getString("Parameter.folder_tests");
		String folder_test_refactoring = Parameters.getString("Parameter.folder_tests_refactoring");
		String folder_project = Parameters.getString("Parameter.folder_project");
		
		String result = "";
		FileReader arq = new FileReader(folder_test+"/test_result.txt");
		BufferedReader readArq1 = new BufferedReader(arq); 
		String line1 = readArq1.readLine();
		
		FileReader arq2 = new FileReader(folder_test_refactoring+"/test_result.txt");  
		BufferedReader readArq2 = new BufferedReader(arq2); 
		String line2 = readArq2.readLine();
		Integer cont = 2;
		
		result = result + line1 + "; ;" + line2 + "\n";
		line1 = readArq1.readLine();
		line2 = readArq2.readLine();
		
		while (line1 != null) { 
			if ( line2 != null && line1.split(";")[0].equals(line2.split(";")[0] ) ) {
				String formula1 ="=SE(D"+ cont.toString() + "=L"+ cont.toString() + ")";
				String formula2 ="=SE(G"+ cont.toString() + "=O"+ cont.toString() + ")";
				result = result + line1 + "; ;" + line2 + ";" + formula1 + ";"+ formula2 +"\n";
				line1 = readArq1.readLine();
				line2 = readArq2.readLine(); 
			}
			else {
				result = result + line1 + "; ; Não Refatorou ou Não compilou! \n";
				line1 = readArq1.readLine();
				
			}
			cont++;
		} 

		
		FileWriter fw = new FileWriter(folder_project+"/full_test_result.txt");
		fw.write(result);
		fw.close();
	}
	

}
