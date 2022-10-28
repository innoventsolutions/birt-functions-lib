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

import innovent.birt.functions.BindParameters;

public class BindParametersTest {
	/**
	 * Test no arguments
	 */
	@Test
	public void testExecut0() {
		BindParameters bindParameters = new BindParameters();
		IScriptFunctionContext scriptContext = Mockito.mock(IScriptFunctionContext.class);
		try {
			bindParameters.execute(new Object[] {}, scriptContext);
		} catch (BirtException e) {
			Assert.assertEquals("No reportContext supplied to ResolveSQLParameters", e.getMessage());
		}
	}

	/**
	 * This test starts a report engine, runs a report, and then checks the output.
	 * 
	 * To run this test you will need to include the birt-runtime classes in the
	 * classpath and also build this plugin into a jar and put it in the classpath
	 * as well.
	 * 
	 * OR run an eclipse instance from within eclipse.
	 * 
	 * @author steve
	 * @throws FileNotFoundException
	 *
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testExecute() throws UnsupportedEncodingException, FileNotFoundException {
		try {
			final IReportEngine reportEngine = ReportEngine.getReportEngine();
			String rptDesignFileName = ReportEngine.RESOURCE_DIR + "/reports/test_bind_params.rptdesign";
			final InputStream is = new FileInputStream(rptDesignFileName);
			final IReportRunnable design = reportEngine.openReportDesign(is);
			final IGetParameterDefinitionTask paramTask = reportEngine.createGetParameterDefinitionTask(design);
			List<EngineException> errors = null;
			try {
				final IRunAndRenderTask rrTask = reportEngine.createRunAndRenderTask(design);
//				final Map<String, Object> appContext = rrTask.getAppContext();
//				final ClassLoader classLoader = getClass().getClassLoader();
//				System.out.println("BindParametersTest testExecute classLoader = " + classLoader);
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
					System.out.println("BindParametersTest output = " + output);
					Assert.assertTrue(output.indexOf("Australian Collectors, Co.") >= 0);
				} finally {
					rrTask.close();
				}
			} finally {
				paramTask.close();
			}
			if (errors != null) {
				Iterator<EngineException> iterator = errors.iterator();
				if (iterator.hasNext()) {
					EngineException error = iterator.next();
					Assert.fail("Engine exception: " + error.getMessage());
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Assert.fail(e.toString());
		} catch (BirtException e) {
			e.printStackTrace();
			Assert.fail(e.toString());
		}
	}
}
