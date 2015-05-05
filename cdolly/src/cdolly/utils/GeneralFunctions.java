package cdolly.utils;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class GeneralFunctions {

	private static Logger logger = Logger.getLogger(GeneralFunctions.class);
	
	public static boolean executaComando(String comando, String dir) {

		String cmd = comando;
		Runtime run = Runtime.getRuntime();
		Process pr = null;
		try {
//			logger.info("Vai executar comando: " + cmd + " na pasta: " + dir);
			if (dir == null){
				pr = run.exec(cmd);
			} else{
				pr = run.exec(cmd, null, new File(dir));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			 // any error message?
            StreamGobbler errorGobbler = new 
                StreamGobbler(pr.getErrorStream(), "ERROR");            
            
            // any output?
            StreamGobbler outputGobbler = new 
                StreamGobbler(pr.getInputStream(), "OUTPUT");
                
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
                                    
            // any error???
            int exitVal = pr.waitFor();
//            logger.info("ExitValue (script_seq.sh): " + exitVal);             
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        Scanner scanner = new Scanner(pr.getInputStream());
        String retorno = null;

        //Tem retorno     
        if( scanner.hasNextLine() ){
       	 retorno = scanner.nextLine();
        }
        if ( retorno != null ){
       	 //Sucesso
       	 if ( Integer.parseInt( retorno.trim() ) == 0 ){
       		logger.debug("Executou corretamente!");
       	 } else{
       		logger.error("Deu problema na execu��o do comando. C�digo de retorno: " + retorno);
       		return false;
       	 }
        }
        return true;
	}

}
