package innovent.birt.test;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionExecutor;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.engine.executor.ExecutionContext;
import org.eclipse.birt.report.engine.script.internal.ReportContextImpl;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import innovent.birt.functions.ThrowException;

public class ThrowExceptionTest {
	// Note: Mockito cannot mock fields. Context is accessed via reflection.
	private static class MockReportContext extends ReportContextImpl {
		@SuppressWarnings("unused")
		public final ExecutionContext context;

		public MockReportContext(ExecutionContext context) {
			super(context);
			this.context = context;
		}
	}

	@Test
	public void testExecute1() {
		IScriptFunctionContext scriptContext = Mockito.mock(IScriptFunctionContext.class);
		IScriptFunctionExecutor sfe = new ThrowException();
		Object result = null;
		try {
			result = sfe.execute(new Object[] { "test" }, scriptContext);
		} catch (BirtException e) {
			Assert.assertEquals("java.lang.String: test", e.getMessage());
		}
		Assert.assertNull(result);
	}

	@Test
	public void testExecute2() {
		IScriptFunctionContext scriptContext = Mockito.mock(IScriptFunctionContext.class);
		ExecutionContext executionContext = Mockito.mock(ExecutionContext.class);
		IReportContext reportContext = new MockReportContext(executionContext);
		IScriptFunctionExecutor sfe = new ThrowException();
		Object result = null;
		try {
			result = sfe.execute(new Object[] { "test", reportContext }, scriptContext);
		} catch (BirtException e) {
			Assert.fail(e.toString());
		}
		Assert.assertNull(result);
	}
}
