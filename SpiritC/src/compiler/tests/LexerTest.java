package compiler.tests;

import compiler.Lexer;
import compiler.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author david
 * @date 4/12/17.
 */
class LexerTest
{
	private void testForTokens(String testString, String[] tokens)
	{
		InputStream inputStream = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
		PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream, 3);

		Lexer lexer = new Lexer(pushbackInputStream, "Test.spirit");

		for (String token : tokens)
		{
			Token tok = lexer.getToken();
			Assertions.assertEquals(token, tok.value);
		}

		// Make sure all test cases end with an EOF token. //
		Assertions.assertEquals(Token.TokenType.EOF, lexer.getToken().tokenType);

	}


	@Test
	void getToken()
	{
		testForTokens("a := 123", new String[]{"a", ":", "=", "123"});
		testForTokens("var a = 7123", new String[]{"var", "a", "=", "7123"});
		testForTokens("func b():", new String[]{"func", "b", "(", ")", ":"});
		testForTokens("\tvar a = 7123", new String[]{"", "var", "a", "=", "7123"});
		testForTokens("func b(a : int, b : int) -> int:", new String[]{
				"func", "b", "(", "a", ":", "int", ",", "b", ":", "int", ")", "->", "int", ":"});
		testForTokens("func b():\nvar a = 1", new String[]{
				"func", "b", "(", ")", ":", "\n", "var", "a", "=", "1"});
		testForTokens("a	b", new String[]{"a", "b"});
		testForTokens("a		b", new String[]{"a", "b"});
		testForTokens("a\n b", new String[]{"a", "\n", "", "b"});
		testForTokens("a(\n)b", new String[]{"a", "(", ")", "b"});
		testForTokens("a \"Kawaii desu ne?\"\n", new String[]{"a", "Kawaii desu ne?", "\n"});
		testForTokens("a \"Kawaii desu ne?\" ", new String[]{"a", "Kawaii desu ne?", ""});

		testForTokens("a is b", new String[]{"a", "==", "b"});
		testForTokens("[T]", new String[]{"[", "T", "]"});
		System.out.println("Test/s successful.");




	}

}