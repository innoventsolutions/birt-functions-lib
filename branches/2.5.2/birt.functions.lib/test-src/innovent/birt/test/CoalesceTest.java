package innovent.birt.test;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionExecutor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import innovent.birt.functions.Coalesce;

public class CoalesceTest {

	@Test
	public void testExecute1() {
		IScriptFunctionContext scriptContext = Mockito.mock(IScriptFunctionContext.class);
		IScriptFunctionExecutor sfe = new Coalesce();
		Object result = null;
		try {
			result = sfe.execute(new Object[] { null, "test" }, scriptContext);
		} catch (BirtException e) {
			Assert.fail(e.toString());
		}
		Assert.assertNotNull(result);
		Assert.assertEquals("test", result);
	}

	@Test
	public void testExecute2() {
		IScriptFunctionContext scriptContext = Mockito.mock(IScriptFunctionContext.class);
		IScriptFunctionExecutor sfe = new Coalesce();
		Object result = null;
		try {
			result = sfe.execute(new Object[] { "test1", "test2" }, scriptContext);
		} catch (BirtException e) {
			Assert.fail(e.toString());
		}
		Assert.assertNotNull(result);
		Assert.assertEquals("test1", result);
	}
}
