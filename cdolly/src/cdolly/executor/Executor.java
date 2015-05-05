package cdolly.executor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.internal.core.dom.rewrite.astwriter.ASTWriter;

import com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile;

import cdolly.configuration.Parameters;
import cdolly.experiments.Parameters2;
import cdolly.generator.ProgramGenerator;
import cdolly.generator.UnexistingModelException;
import cdolly.generator.ifdef.IfdefGenerator;
import cdolly.generator.test.TestsGenerator;
import cdolly.utils.LogInitializer;

public class Executor {

	private static Logger logger = Logger.getLogger(Executor.class);
	private String modelPath;
	private boolean saveInFileSystem = true;

	private Executor(String modelPath) {
		this.modelPath = modelPath;
	}

	private Executor() {
	}

	public static Executor createExecutor(String alloyModelPath) {
		return new Executor(alloyModelPath);
	}

	public void run() {
		try {
			LogInitializer.setup();

		} catch (IOException e1) {
			System.err.println(e1.getMessage());
			System.err.println("Aborting...");
			System.exit(1);
		}
		String alloyModelPath = this.modelPath;
		try {
			long initialTime = (new Date()).getTime();
			ProgramGenerator generator = new ProgramGenerator(alloyModelPath);
			int programCounter = 1;
			
			File fOutputFolder = null;
			if (saveInFileSystem){
				fOutputFolder = new File(Parameters.getString("Parameter.folder_resource"));
				if (!fOutputFolder.exists()){
					fOutputFolder.mkdir();
				}
			}
			File fOutputFolderTest = null;
			if (saveInFileSystem){
				fOutputFolderTest = new File(Parameters.getString("Parameter.folder_tests"));
				if (!fOutputFolderTest.exists()){
					fOutputFolderTest.mkdir();
				}
			}
			
			String program = null;
			int fileCounter = 1;
			int notCompiled = 0;
			long totalCompilationTime = 0;
			StringBuffer compilationErrorPrograms = new StringBuffer();
			for (List<IASTTranslationUnit> programs : generator) {
				logger.info("Program #" + (programCounter) + "\n");
				for (IASTTranslationUnit translationUnit : programs) {
					ASTWriter astW = new ASTWriter();
					program = astW.write(translationUnit);
					logger.info("TranslationUnit:\n" + program);
				}
				if (saveInFileSystem){
					long initialCompilationTime = (new Date()).getTime();
					File programOutputFolder = new File(fOutputFolder.getAbsolutePath());
					if (!programOutputFolder.exists()){
						programOutputFolder.mkdir();
					}
					File programOutputFolderTest = new File(fOutputFolderTest.getAbsolutePath());
					if (!programOutputFolderTest.exists()){
						programOutputFolderTest.mkdir();
					}
					
					try {
						FileWriter fw = new FileWriter(programOutputFolder.getAbsolutePath() + "/f" + fileCounter + ".c");
						IfdefGenerator ifdef = IfdefGenerator.createIfdef(program);
						fw.write(ifdef.addIfdef());
						fw.close();
						
						FileWriter fw_test = new FileWriter(programOutputFolderTest.getAbsolutePath() + "/test" + fileCounter + ".c");
						TestsGenerator program_test = TestsGenerator.createTest(fileCounter,"resources");
						fw_test.write(program_test.generator());
						fw_test.close();
						fileCounter++;
						
						//compiling
//						boolean compiled = cdolly.utils.Compile.run(programOutputFolder.getAbsolutePath());
//						if (!compiled){
//							notCompiled++;
//							compilationErrorPrograms.append("," + programCounter);
//						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					long finalCompilationTime = (new Date()).getTime();
					totalCompilationTime += finalCompilationTime - initialCompilationTime;
				}
			}
			
//			long finalTime = (new Date()).getTime();
//			long totalTime  = finalTime - initialTime;
//			logger.info("Total execution time (ms): " + totalTime);
//			logger.info("Total execution time without compilation (ms): " + (totalTime - totalCompilationTime));
//			logger.info("Total #programs: " + programCounter);
//			logger.info("Total #programs not compiled: " + notCompiled);
//			logger.info("Programs with compilation error: " + compilationErrorPrograms.toString());

		} catch (UnexistingModelException e) {
			e.printStackTrace();
		}
	}

}
