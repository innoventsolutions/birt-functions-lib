package innovent.birt.test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;
import org.eclipse.birt.engine.ReportEngine;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import innovent.birt.functions.DisplayParameters;

/**
 * @author steve
 *
 */
public class DisplayParametersTest {
	/**
	 * Test passing no arguments
	 */
	@Test
	public void testExecute0() {
		final var scriptContext = Mockito.mock(IScriptFunctionContext.class);
		final var displayParameters = new DisplayParameters();
		try {
			displayParameters.execute(new Object[] {}, scriptContext);
		}
		catch (final ArrayIndexOutOfBoundsException e) {
		}
		catch (final BirtException e) {
			Assert.fail("Failed to execute: " + e);
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
			final var reportEngine = ReportEngine.getReportEngine();
			final var rptDesignFileName = ReportEngine.RESOURCE_DIR
				+ "/reports/test_display_parameters.rptdesign";
			final InputStream is = new FileInputStream(rptDesignFileName);
			final var design = reportEngine.openReportDesign(is);
			final var paramTask = reportEngine.createGetParameterDefinitionTask(design);
			List<EngineException> errors = null;
			try {
				final var rrTask = reportEngine.createRunAndRenderTask(design);
				final Map<String, Object> appContext = rrTask.getAppContext();
				final var classLoader = /* getClass().getClassLoader() */ Thread.currentThread().getContextClassLoader();
				System.out.println(
					"DisplayParametersTest testExecute1 classLoader = " + classLoader);
				appContext.put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, classLoader);
				// rrTask.setAppContext(appContext);
				try {
					final var os = new ByteArrayOutputStream();
					final RenderOption options = new HTMLRenderOption();
					options.setOutputFormat("HTML");
					options.setOutputStream(os);
					rrTask.setRenderOption(options);
					rrTask.run();
					errors = rrTask.getErrors();
					final var output = os.toString("utf-8");
					System.out.println("DisplayParametersTest output = " + output);
					Assert.assertTrue(output.indexOf("Australian Collectors, Co.") >= 0);
					Assert.assertTrue(output.indexOf("NewParameter") >= 0);
					Assert.assertTrue(output.indexOf("abc") >= 0);
				}
				finally {
					rrTask.close();
				}
			}
			finally {
				paramTask.close();
			}
			if (errors != null) {
				final var iterator = errors.iterator();
				if (iterator.hasNext()) {
					final var error = iterator.next();
					Assert.fail("Engine exception: " + error.getMessage());
				}
			}
		}
		catch (final UnsupportedEncodingException | BirtException e) {
			e.printStackTrace();
			Assert.fail(e.toString());
		}
	}
}
