package cdolly.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import cdolly.configuration.Parameters;
import cdolly.executor.Executor;
import cdolly.experiments.Parameters2;
import cdolly.generator.test.TestManager;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;



public class RunMain {

	public static void main(String[] args) throws IOException {
		String alloyModelPath = Parameters.getString("Parameter.folder_alloy"); //$NON-NLS-1$
		Executor executor     = Executor.createExecutor(alloyModelPath);
		executor.run();
		TestManager runtest   = TestManager.createRunTest();
		
		String folder_programs = Parameters.getString("Parameter.folder_resource"); //$NON-NLS-1$
		String folder_test     = Parameters.getString("Parameter.folder_tests"); //$NON-NLS-1$
        String folder_compiled = Parameters.getString("Parameter.folder_compiled"); //$NON-NLS-1$
		
		File diretorio = new File(folder_test+"/");   //$NON-NLS-1$
		File[] contents = diretorio.listFiles(); 
		
		// compiles all programs
		runtest.compilePrograms(contents, folder_test);
		
		contents = diretorio.listFiles();
		
		// copies the compiled programs
		List<Integer> list_number = new ArrayList<Integer>(); 
		list_number = runtest.copiesProgramsCompiled(contents,folder_programs ,folder_compiled);
		
		Collections.sort(list_number);
		runtest.createFiles(folder_test); // files of tests
		runtest.runTests(list_number,  folder_test);
		

		
	}

}
