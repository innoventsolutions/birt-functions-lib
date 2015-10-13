/*
 * Copyright (c) 2008-2015  Innovent Solutions, Inc.
 * 
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms 
 * of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * 	Scott Rosenbaum - Innovent Solutions
 *  Steve Schafer - Innovent Solutions
 * 				 
 */
package innovent.birt.functions.factory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionExecutor;
import org.eclipse.birt.report.engine.api.IEngineTask;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.engine.executor.ExecutionContext;

/**
 * Common super-class used to create convenience methods for use in other
 * functions.
 * 
 */
public abstract class InnoventFunction implements IScriptFunctionExecutor {
	private static final Logger logger = Logger
			.getLogger(InnoventFunction.class.getName());

	public abstract Object execute(Object[] arguments,
			IScriptFunctionContext context) throws BirtException;

	/**
	 * When the DataSet editor runs, modifications to the queryText as done in the BindParameter function
	 * will get written to the queryText.  To avoid this, we test if the query is being run by the DataSet
	 * editor.
	 * 
	 * BIRT 2.5 has added a new Task type that identifies the DataSet editor so that this code is no longer 
	 * required. 
	 *  
	 * @param reportContext
	 * @return
	 */
	public static boolean isDataSetEditor(IReportContext reportContext) {
		try {

			// get the protect field 'context' from reportContext
			@SuppressWarnings({ "rawtypes" })
			Class rciClass = reportContext.getClass();
			Field fieldFromScript = rciClass.getDeclaredField("context");
			if (fieldFromScript == null) {
				throw new NoSuchFieldException(
						"Reporting Access to context from IReportContext");
			}
			// instantiate the ExecutionContext object that
			// populates the context field
			fieldFromScript.setAccessible(true);
			Object execContext = fieldFromScript.get(reportContext);
			ExecutionContext ectx = (ExecutionContext) execContext;

			IEngineTask et = ectx.getEngineTask();
			if ("DummyEngineTask".equalsIgnoreCase(et.getClass()
					.getSimpleName())) {
				logger.info("Is Data Set Editor");
				return true;
			}

		} catch (SecurityException e) {
			logger.warning("BIRT Functions, check for DataSet Editor: " + e.getMessage());
		} catch (NoSuchFieldException e) {
			logger.warning("BIRT Functions, check for DataSet Editor: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.warning("BIRT Functions, check for DataSet Editor: " + e.getMessage());
		} catch (IllegalAccessException e) {
			logger.warning("BIRT Functions, check for DataSet Editor: " + e.getMessage());
		}
		logger.finest("Is Not a Data Set Editor");

		return false;
	}

	/**
	 * Get a handle to the reportContext. This is a common error and trickier
	 * then one may think, best to wrap off with a good implementation
	 * 
	 * @param rcArgument
	 * @param sqlText
	 * @return
	 * @throws BirtException
	 */
	protected IReportContext getReportContext(final Object rcArgument)
			throws BirtException {
		if (rcArgument == null) {
			throw new BirtException(InnoventFunctionFactory.plugin_id,
					"ReportContext object is null in "
							+ this.getClass().getSimpleName(),
					InnoventFunctionFactory.getResourceBundle());
		}
		if ((rcArgument instanceof IReportContext) != true) {
			throw new BirtException(InnoventFunctionFactory.plugin_id,
					"ReportContext is not an instance of IReportContext in "
							+ this.getClass().getSimpleName(),
					InnoventFunctionFactory.getResourceBundle());
		}
		logger.finest("Found report context");
		return (IReportContext) rcArgument;
	}

	@SuppressWarnings("unchecked")
	public static void addBirtException(IReportContext reportContext,
			String errorMessage, Integer severity) {
		BirtException be = new BirtException("org.eclipse.birt.report.engine",
				errorMessage, new Object[] { "" });
		be.setSeverity(severity);

		try {
			// get the protect field 'context' from reportContext
			@SuppressWarnings("rawtypes")
			Class rciClass = reportContext.getClass();
			Field fieldFromScript = rciClass.getDeclaredField("context");
			if (fieldFromScript == null) {
				return;
			}

			// instantiate the ExecutionContext object that
			// populates the context field
			fieldFromScript.setAccessible(true);
			Object execContext = fieldFromScript.get(reportContext);

			// now get a handle to the addException method on ExecutionObject
			@SuppressWarnings("rawtypes")
			Class execClass = execContext.getClass();
			Method addMethod = execClass.getMethod("addException",
					new Class[] { BirtException.class });

			// finally invoke the method which will add the BirtException 
			// to the report
			addMethod.setAccessible(true);
			addMethod.invoke(execContext, new Object[] { be });

			// Lots of ways for this to break...
		} catch (Exception e) {
			logger.warning(e.getMessage());
			e.printStackTrace();
		}
		return;
	}
}
