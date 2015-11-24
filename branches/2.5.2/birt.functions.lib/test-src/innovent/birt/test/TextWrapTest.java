package innovent.birt.test;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionExecutor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import innovent.birt.functions.TextWrap;

public class TextWrapTest {

	@Test
	public void testExecute1() {
		IScriptFunctionContext scriptContext = Mockito.mock(IScriptFunctionContext.class);
		IScriptFunctionExecutor sfe = new TextWrap();
		Object result = null;
		try {
			result = sfe.execute(new Object[] { "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", "20" }, scriptContext);
		} catch (BirtException e) {
			Assert.fail(e.toString());
		}
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof String);
		String[] parts = ((String) result).split("\n");
		Assert.assertEquals(26,  parts.length);
	}
}
