package innovent.birt.test;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionExecutor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import innovent.birt.functions.BirtLogger;

public class BirtLoggerTest {

	@Test
	public void testExecute() {
		IScriptFunctionContext scriptContext = Mockito.mock(IScriptFunctionContext.class);
		IScriptFunctionExecutor sfe = new BirtLogger();
		try {
			sfe.execute(new Object[] { "test message", "FINEST" }, scriptContext);
		} catch (BirtException e) {
			Assert.fail(e.toString());
		}
	}
}
