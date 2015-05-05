package cdolly.main.refactoring;

import java.io.File;
import java.io.IOException;

import cdolly.experiments.Parameters2;
import de.fosd.typechef.lexer.LexerException;
import de.fosd.typechef.lexer.options.OptionException;
import test.ColligensTool;

public class RunTestRefactoring {

	public static void main(String[] args) throws IOException, LexerException, OptionException {
		System.out.print("Entrou");
		ColligensTool.UNCRUSTIFY_PATH = Parameters2.ABSOLUTE_SRC_FOLDER_UNCRUSTIFY;
		ColligensTool.CFG = "defaults.cfg";
		ColligensTool.refactorIncompleteIfConditions(new File("compiled/f"+args[0]+".c"), new File("refactoring/f"+args[0]+".c"));
		//ColligensTool.refactorIncompleteIfConditions(new File("compiled/f72.c"), new File("refactoring/f72.c"));
	}

}
