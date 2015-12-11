package innovent.birt.test;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionExecutor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import innovent.birt.functions.TextWrap;

public class TextWrapTest {
	private static final Object LOREM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

	/**
	 * Test passing no arguments
	 */
	@Test
	public void testExecute0() {
		IScriptFunctionContext scriptContext = Mockito
				.mock(IScriptFunctionContext.class);
		IScriptFunctionExecutor sfe = new TextWrap();
		try {
			sfe.execute(new Object[] {}, scriptContext);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			Assert.assertEquals("0", e.getMessage());
		}
		catch (BirtException e) {
			Assert.fail(e.toString());
		}
	}

	/**
	 * Test passing only one argument
	 */
	@Test
	public void testExecute1() {
		IScriptFunctionContext scriptContext = Mockito
				.mock(IScriptFunctionContext.class);
		IScriptFunctionExecutor sfe = new TextWrap();
		Object result = null;
		try {
			result = sfe.execute(new Object[] { "abc" }, scriptContext);
		}
		catch (Exception e) {
			Assert.fail(e.toString());
		}
		Assert.assertNotNull(result);
		Assert.assertEquals("abc", result);
	}

	/**
	 * Test a short string and passing an integer
	 */
	@Test
	public void testExecute2() {
		IScriptFunctionContext scriptContext = Mockito
				.mock(IScriptFunctionContext.class);
		IScriptFunctionExecutor sfe = new TextWrap();
		Object result = null;
		try {
			result = sfe.execute(new Object[] { "abc", 20 }, scriptContext);
		}
		catch (BirtException e) {
			Assert.fail(e.toString());
		}
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof String);
		String[] parts = ((String) result).split("\n");
		Assert.assertEquals(1, parts.length);
	}

	/**
	 * Test a long string
	 */
	@Test
	public void testExecute3() {
		IScriptFunctionContext scriptContext = Mockito
				.mock(IScriptFunctionContext.class);
		IScriptFunctionExecutor sfe = new TextWrap();
		Object result = null;
		try {
			result = sfe.execute(new Object[] { LOREM, "20" }, scriptContext);
		}
		catch (BirtException e) {
			Assert.fail(e.toString());
		}
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof String);
		String[] parts = ((String) result).split("\n");
		Assert.assertEquals(26, parts.length);
	}
}
