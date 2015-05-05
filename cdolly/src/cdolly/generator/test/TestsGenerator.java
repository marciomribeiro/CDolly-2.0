package cdolly.generator.test;

import java.io.IOException;

import cdolly.generator.ifdef.IfdefGenerator;

public class TestsGenerator {
	
	
	private int program_number;
	private String program;
	private String include;
	private String folder;
	private TestsGenerator(int program,String folder) {
		this.program_number = program;
		this.folder = folder;
	}
	
	private TestsGenerator() {
	}

	public static TestsGenerator createTest(int program, String folder) {
		return new TestsGenerator(program,folder);
	}
	
	public String generator() {
		include = "\"../" + folder +"/f"+program_number+".c\"";
		program = "#include <stdbool.h>"
				+ "\n#include <string.h>"
				+ "\n#include <stdio.h>"
				+ "\n#include <stdlib.h>"
				+ "\n#define TAG"
				+ "\nmain(int argc, char *argv[ ]) {"
				+ "\n    FILE *fl;"
				+ "\n	 int c;"
				+ "\n	 if((fl = fopen(\"test_result.txt\", \"r+\")) == NULL)"
				+ "\n    {"
				+ "\n        perror(\"Erro: fopen\");"
				+ "\n        exit(EXIT_FAILURE);"
				+ "\n    }"
				+ "\n    while((c = fgetc(fl)) != EOF) {"
				+ "\n	 }"
				+ "\n"
				+ "\n    #include "+ include
				+ "\n    int param1 = (int)atoi(argv[1]);  "
				+ "\n	 int param2 = (int)atoi(argv[2]);"
				+ "\n	 int return1 = FunctionId_0(param1);"
				+ "\n	 int return2 = FunctionId_1(param2);"
				+ "\n    fprintf(fl,\""+program_number+";0;%d;%d;1;%d;%d\\n\", param1,return1,param2,return2);"
				+ "\n"
				+ "\n    if((c == EOF) && (feof(fl) == 0) && (ferror(fl) != 0))"
				+ "\n        perror(\"Erro: fgetc\");"
				+ "\n    fclose(fl);"
				+ "\n"
				+ "}";
				
				return program;
	}
}
