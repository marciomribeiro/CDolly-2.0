package cdolly.generator.ifdef;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cdolly.executor.Executor;

public class IfdefGenerator {
	
	private String genericProgram;
	String[] line_code;
	String   line_if;
	String[] list_line_if;
	String   operator;
	
	private IfdefGenerator(String program) {
		this.genericProgram = program;
	}
	
	private IfdefGenerator() {
	}

	public static IfdefGenerator createIfdef(String program) {
		return new IfdefGenerator(program);
	}
	
	public String addIfdef() throws IOException {
		
		
		line_code = this.genericProgram.toString().split("\\n",-1);
		String list_binary_expression[] = {" * "," / "," % "," + "," - "," << "," >> "," < "," > "," <= "," >= "," && "," || "," & "," ^ " ," | "," = "," *= "," /= "," %= "," -= "," <<= "," >>= "," &= "," ^= "," |= "," == "," != ",};
		for (int i = 0; i < line_code.length; i++) {
            if (line_code[i].contains("if")) {
            	line_if = line_code[i].toString();
            	break;
            }
        }
		for (int i = 0; i < list_binary_expression.length; i++) {
            if (line_if.contains(list_binary_expression[i].toString())) {
            	operator     = list_binary_expression[i].toString();
        		break;
            }
        }
		int indice_cod_if1 = line_if.indexOf(operator);
        int indice_cod_if2 = indice_cod_if1 + operator.length();
        
		String cod_if0 = line_if.substring(0, indice_cod_if1 );
		String cod_if1 = line_if.substring(indice_cod_if2, line_if.length() -2);
		
		
		String ifdef_tag  = "        #ifdef TAG";
		String endif      = "        #endif";
		String new_line   = cod_if0.toString()+ "\n" + ifdef_tag + "\n" + "         " + operator.toString() + cod_if1 + "\n" + endif + "\n" + "    ){" + "\n"; 
		String new_code   = "";
		
		for (int i = 0; i < line_code.length; i++) {
			if (line_code[i].contains("if")) {
				new_code = new_code + "\n" + new_line;
			}
			else {
				new_code = new_code + "\n" + line_code[i];
			}
				
        }
		
		return new_code;
		
		
		
	}
}
