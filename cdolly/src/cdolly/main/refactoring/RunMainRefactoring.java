package cdolly.main.refactoring;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cdolly.configuration.Parameters;
import cdolly.executor.Executor;
import cdolly.executor.refactoring.ExecutorRefactoring;
import cdolly.experiments.Parameters2;
import cdolly.generator.ifdef.IfdefGenerator;
import cdolly.generator.test.TestManager;
import cdolly.generator.test.TestsGenerator;
import cdolly.utils.GeneralFunctions;
import test.ColligensTool;
import de.fosd.typechef.lexer.LexerException;
//import de.fosd.typechef.lexer.LexerException;
import de.fosd.typechef.lexer.options.OptionException;


public class RunMainRefactoring  {

	
	public static void main(String[] args) throws IOException {
		
		
		String folder_project     = Parameters.getString("Parameter.folder_project"); //$NON-NLS-1$
		ExecutorRefactoring executor = ExecutorRefactoring.createExecutor(folder_project);
		executor.run();
		
		
		String folder_compiled_refactoring = Parameters.getString("Parameter.folder_compiled_refactoring"); //$NON-NLS-1$
		String folder_test_refactoring     = Parameters.getString("Parameter.folder_tests_refactoring"); //$NON-NLS-1$
		String folder_refactoring          = Parameters.getString("Parameter.folder_refactoring"); //$NON-NLS-1$
		TestManager runtest = TestManager.createRunTest();
		
		File[] contents;
		
		File diretorio = new File(folder_test_refactoring+"/");  
		contents = diretorio.listFiles(); 
		
		// compiles all programs
		runtest.compilePrograms(contents, folder_test_refactoring);
		contents = diretorio.listFiles();
		
		// copies the compiled programs
		List<Integer> list_number = new ArrayList<Integer>(); 
		list_number = runtest.copiesProgramsCompiled(contents,folder_refactoring ,folder_compiled_refactoring);
	
		Collections.sort(list_number);
		runtest.createFiles(folder_test_refactoring);
		runtest.runTestsRefactored(list_number,  folder_test_refactoring);
		runtest.joinResults();
		
		
		
	}
	
}

