package compiler.tests;

import compiler.LangCompiler;
import compiler.Lexer;
import compiler.Parser;
import compiler.ast.ASTClass;
import compiler.backends.CompilerCPP;
import compiler.lib.IndentPrinter;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Tyrerexus
 * @date 28/04/17.
 */
class CompilerCPPTest
{

	private String print = 	"prints : (what : string)\n\t#inline\n\tcout << what << endl;\n\t#end\n" +
							"printi : (what : int)\n\t#inline\n\tcout << what << endl;\n\t#end\n";

	private void testCompiler(String testName, String testString)
	{
		System.out.println("=== " + testName + " ===");

		InputStream inputStream = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
		PushbackInputStream i = new PushbackInputStream(inputStream, 3);

		LangCompiler c = new CompilerCPP(new IndentPrinter(System.out), new IndentPrinter(System.err));



		Lexer l = new Lexer(i, testName);
		Parser p = new Parser(l);

		ASTClass cl = new ASTClass(testName, null);
		cl.ignoreImports = true;

		p.parseFile(cl);

		c.compileClass(cl);

		System.out.println('\n');


	}

	@Test
	void singleTest()
	{
		testCompiler("ClassGenerics", "generic [A B C]");
	}

	@Test
	void testAll()
	{
		testCompiler("Assignment", "helloWorld := 42");
		testCompiler("Expressions", "B:=2\nA:= B + 2");
		testCompiler("IfAndElse", "B:=2\nif B == 1\n\tB = 42\nelse\n\tB=32");
		//testCompiler("Loops", "B := 0\nloop A:=1, A < 10, A = A + 1\n\tB = B + 1");
		testCompiler("Function_Declaration0", "a : () int = 5");
		testCompiler("Function_Declaration1", "a : (x, y : int)\n\tb := 5\na 5 10\na 10 10");
		testCompiler("ReturnVal", "a : () = 42");
		testCompiler("Call", "a : (x, y : int)\nb : ()\n\ta 1 2");

		//testCompiler("Imports", "import Hello.World");
		testCompiler("Inline", "a := 10\n#inline\ncout << a;\n#end\na := 15");

		testCompiler("Overloading", "+ : () int = 3");

		/*
		try
		{
			String fileContents = new String(Files.readAllBytes(Paths.get("/home/alex/TestArea/Print.spirit")));
			System.out.println(fileContents);
			testCompiler("Inline2", fileContents);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		*/
	}
}