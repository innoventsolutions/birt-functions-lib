package innovent.birt.test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;
import org.eclipse.birt.engine.ReportEngine;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import innovent.birt.functions.WhereClauseBinding;

/**
 * @author steve
 *
 */
public class WhereClauseBindingTest {
	/**
	 * Test passing no arguments
	 */
	@Test
	public void testExecute0() {
		IScriptFunctionContext scriptContext = Mockito
				.mock(IScriptFunctionContext.class);
		WhereClauseBinding whereClauseBinding = new WhereClauseBinding();
		try {
			whereClauseBinding.execute(new Object[] {}, scriptContext);
		}
		catch (BirtException e) {
			Assert.assertEquals(
					"No reportContext supplied to WhereClauseBinding",
					e.getMessage());
		}
	}

	/**
	 * Test passing reportContext
	 * 
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testExecute1() throws FileNotFoundException {
		try {
			final IReportEngine reportEngine = ReportEngine.getReportEngine();
			final String rptDesignFileName = ReportEngine.RESOURCE_DIR + "/reports/test_where_clause_binding.rptdesign";
			final InputStream is = new FileInputStream(rptDesignFileName);
			final IReportRunnable design = reportEngine.openReportDesign(is);
			final IGetParameterDefinitionTask paramTask = reportEngine
					.createGetParameterDefinitionTask(design);
			List<EngineException> errors = null;
			try {
				final IRunAndRenderTask rrTask = reportEngine
						.createRunAndRenderTask(design);
//				final Map<String, Object> appContext = rrTask.getAppContext();
//				final ClassLoader classLoader = getClass().getClassLoader();
//				System.out.println("WhereClauseBindingTest testExecute1 classLoader = " + classLoader);
//				appContext.put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY,
//						classLoader);
				// rrTask.setAppContext(appContext);
				try {
					final ByteArrayOutputStream os = new ByteArrayOutputStream();
					final RenderOption options = new HTMLRenderOption();
					options.setOutputFormat("HTML");
					options.setOutputStream(os);
					rrTask.setRenderOption(options);
					rrTask.run();
					errors = rrTask.getErrors();
					String output = os.toString("utf-8");
					System.out.println("WhereClauseBindingTest output = " + output);
					Assert.assertTrue(
							output.indexOf("Australian Collectors, Co.") >= 0);
					Assert.assertFalse(
							output.indexOf("Baane Mini Imports") >= 0);
				}
				finally {
					rrTask.close();
				}
			}
			finally {
				paramTask.close();
			}
			if (errors != null) {
				Iterator<EngineException> iterator = errors.iterator();
				if (iterator.hasNext()) {
					EngineException error = iterator.next();
					Assert.fail("Engine exception: " + error.getMessage());
				}
			}
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Assert.fail(e.toString());
		}
		catch (BirtException e) {
			e.printStackTrace();
			Assert.fail(e.toString());
		}
	}
}
