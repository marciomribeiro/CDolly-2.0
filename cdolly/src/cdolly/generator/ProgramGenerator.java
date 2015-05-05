package cdolly.generator;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import cdolly.generator.core.Generator;
import cdolly.synthesis.SynthesisUnit;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.CommandScope;
import edu.mit.csail.sdg.alloy4compiler.ast.Module;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;

/**
 * @author Jeanderson Candido<br>
 *         <a href="http://jeandersonbc.github.io"
 *         target="_blank">http://jeandersonbc.github.io</a>
 */
public class ProgramGenerator extends Generator<List<IASTTranslationUnit>> {

	private static Logger logger = Logger.getLogger(ProgramGenerator.class);

	/**
	 * The current alloy instance.
	 */
	private A4Solution currentProgram;

	/**
	 * It is true if this is the first time attempting to successfully generate
	 * programs.
	 */
	private boolean isFirstTime;

	/**
	 * File containing the alloy model.
	 */
	private File model;

	public ProgramGenerator(String alloyModelPath)	throws UnexistingModelException {

		this.isFirstTime = true;

		this.model = new File(alloyModelPath);
		if (!this.model.exists())
			throw new UnexistingModelException(alloyModelPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cdolly.generator.core.Generator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (this.isFirstTime)
			initializeAlloyAnalyzer();
		if (this.currentProgram.satisfiable() && this.isFirstTime) {
			isFirstTime = false;
			return true;
		}
		boolean hasMoreInstances = true;
		try {
			if (!hasValidInstances() || hasExausted())
				hasMoreInstances = false;
			// chama o método next ForLoopIterator
			this.currentProgram = this.currentProgram.next();

		} catch (Err e) {
			hasMoreInstances = false;
		}
		return hasMoreInstances;

	}

	private boolean hasExausted() throws Err {
		if (this.currentProgram == null)
			return false;

		return this.currentProgram.equals(this.currentProgram.next());
	}

	private boolean hasValidInstances() throws Err {
		return this.currentProgram.next().satisfiable()
				&& !this.currentProgram.equals(this.currentProgram.next());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cdolly.generator.core.Generator#generateNext()
	 */
	@Override
	protected List<IASTTranslationUnit> generateNext() {
		return SynthesisUnit.extractCode(currentProgram);
	}

	private void initializeAlloyAnalyzer() {
		A4Reporter rep = createA4Reporter();
		Map<String, String> loaded = null;
		Module cMetamodel = null;
		System.out.print("1\n");
		try {
			cMetamodel = CompUtil.parseEverything_fromFile(rep, loaded,
					this.model.getAbsolutePath());
			System.out.print("2\n");
			for (Command command : cMetamodel.getAllCommands()) {
				System.out.print("3\n");
				ConstList<CommandScope> constList = null;
				command = command.change(constList);

				A4Options options = new A4Options();
				options.solver = A4Options.SatSolver.SAT4J;
				//retorna a representação do programa em c especificado em alloy 	
				System.out.print("4\n");
				this.currentProgram = TranslateAlloyToKodkod
						.execute_command(rep, cMetamodel.getAllReachableSigs(),
								command, options);
				System.out.print(this.currentProgram+"\n");
			}

		} catch (Err e) {
			logger.error(e.getMessage());
		}

	}

	private A4Reporter createA4Reporter() {
		return new A4Reporter() {
			@Override
			public void warning(ErrorWarning msg) {
				System.out.print("Relevance Warning:\n"
						+ (msg.toString().trim()) + "\n\n");
				System.out.flush();
			}
		};
	}

}
