package innovent.birt.test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.engine.ReportEngine;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.junit.Assert;
import org.junit.Test;

/**
 * To run this test you will need to include the birt-runtime classes in the
 * classpath and also build this plugin into a jar and put it in the classpath
 * as well.
 * 
 * @author steve
 *
 */
public class BindParametersTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testExecute() throws UnsupportedEncodingException {
		try {
			final IReportEngine reportEngine = ReportEngine.getReportEngine();
			final InputStream is = this.getClass().getResourceAsStream("/reports/test_bind_params.rptdesign");
			final IReportRunnable design = reportEngine.openReportDesign(is);
			final IGetParameterDefinitionTask paramTask = reportEngine.createGetParameterDefinitionTask(design);
			List<EngineException> errors = null;
			try {
				final IRunAndRenderTask rrTask = reportEngine.createRunAndRenderTask(design);
				final Map<String, Object> appContext = rrTask.getAppContext();
				final ClassLoader classLoader = getClass().getClassLoader();
				appContext.put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, classLoader);
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
					System.out.println(output);
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
		} catch (BirtException e) {
			Assert.fail(e.toString());
		}
	}
}
