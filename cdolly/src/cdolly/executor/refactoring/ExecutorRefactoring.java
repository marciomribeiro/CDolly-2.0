package cdolly.executor.refactoring;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import cdolly.configuration.Parameters;
import cdolly.generator.test.TestsGenerator;
import cdolly.utils.GeneralFunctions;



public class ExecutorRefactoring {
	private static Logger logger = Logger.getLogger(ExecutorRefactoring.class);
	private String folder_project;
	private boolean saveInFileSystem = true;

	private ExecutorRefactoring(String folder_project) {
		this.folder_project = folder_project;
	}

	private ExecutorRefactoring() {
	}

	public static ExecutorRefactoring createExecutor(String folder_project) {
		return new ExecutorRefactoring(folder_project);
	}

	public void run() throws IOException {
		
		String folder_refactoring          = Parameters.getString("Parameter.folder_refactoring"); //$NON-NLS-1$
		String folder_test_refactoring     = Parameters.getString("Parameter.folder_tests_refactoring"); //$NON-NLS-1$
		String folder_compiled             = Parameters.getString("Parameter.folder_compiled"); //$NON-NLS-1$
        String folder_compiled_refactoring = Parameters.getString("Parameter.folder_compiled_refactoring"); //$NON-NLS-1$
		
		FileWriter fw = new FileWriter(folder_project +"/script_refectoring.sh");
		
		String content = "";
		
		content += "#!/bin/bash\n"
				+ "cd /home/rafael/workspace/cdolly\n"
				+ "program=$1\n"
				+ "java -cp './bin/:./libs/*' cdolly.main.refactoring.RunTestRefactoring $program";

		fw.write(content);
	    fw.close();
	    
		GeneralFunctions.executaComando("chmod 777 "+ folder_project +"/script_refectoring.sh", null);
		

		
		List<Integer> list_name = new ArrayList<Integer>(); 
		File diretorio = new File(folder_compiled);  
		File[] contents = diretorio.listFiles(); 
		for (int i=0; i<contents.length; i++) {  
			if (contents[i].toString().contains(".c") && !contents[i].toString().contains("~")) {
				System.out.println(contents[i].toString().split("/")[6].replace("f","").replace(".c",""));
				list_name.add(Integer.parseInt(contents[i].toString().split("/")[6].replace("f","").replace(".c","")));
			}
		}
		Collections.sort(list_name);
		
		File fOutputFolder = new File(folder_refactoring);
		File programOutputFolder = new File(fOutputFolder.getAbsolutePath());
		if (!programOutputFolder.exists()){
			programOutputFolder.mkdir();
		}
		
		File fOutputFolderTest = new File(folder_test_refactoring);
		File programOutputFolderTest = new File(fOutputFolderTest.getAbsolutePath());
		if (!programOutputFolderTest.exists()){
			programOutputFolderTest.mkdir();
		}

		for(Integer name : list_name){
			System.out.println("refatorando: " + name.toString());
			GeneralFunctions.executaComando(folder_project + "/script_refectoring.sh "+name.toString(), null);
			
			FileWriter fw_test = new FileWriter(programOutputFolderTest.getAbsolutePath() + "/test" + name.toString() + ".c");
			TestsGenerator program_test = TestsGenerator.createTest(name,"refactoring");
			fw_test.write(program_test.generator());
			fw_test.close();
		
		}
		
	}
}
