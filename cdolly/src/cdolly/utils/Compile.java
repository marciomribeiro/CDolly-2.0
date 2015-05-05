package cdolly.utils;
import java.io.File;

import org.apache.log4j.Logger;


public class Compile {

	private static Logger logger = Logger.getLogger(Compile.class);
	


	public static boolean run(String programPath) {

		//FuncoesGerais.executaComando("cp /Users/gugawag/Documents/mestrado/workspace/cdolly-v2/resources/script_seq.sh "
		GeneralFunctions.executaComando("cp /home/rafael/Alloy/gugawag-cdolly-c7de1822b5a8/resources/script_seq.sh "
							+ programPath, null);
		//copying main.c to be possible compile a program
		//FuncoesGerais.executaComando("cp /Users/gugawag/Documents/mestrado/workspace/cdolly-v2/resources/main.c "
		GeneralFunctions.executaComando("cp /home/rafael/Alloy/gugawag-cdolly-c7de1822b5a8/resources/main.c "
		
				+ programPath, null);

		
		File scriptPath = new File(programPath + "/script_seq.sh");
		boolean executouComandoCorretamente = GeneralFunctions.executaComando(scriptPath.getAbsolutePath()
				+ " a.out " + programPath + " " + "c99" + " " + "clang" + " " + " ", null);

		//removing files
		GeneralFunctions.executaComando("rm " + programPath + "/script_seq.sh ", null);
		GeneralFunctions.executaComando("rm " + programPath + "/main.c ", null);
		if (executouComandoCorretamente){
			GeneralFunctions.executaComando("rm " + programPath + "/a.out ", null);
			return true;
		}

        return false;        
	}

}
