/** 
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
 * 	Steve Schafer - Innovent Solutions
 * 				 
 */
package innovent.birt.functions;

import innovent.birt.functions.factory.InnoventFunction;
import innovent.birt.functions.factory.InnoventFunctionFactory;

import java.util.List;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ModuleHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;

/**
 * Utility function that will add a ReportItem from a library into your report.
 * 
 * Use this to simplify programmatically modifying your reports.
 * 
 */
public class AppendLibraryContent extends InnoventFunction {

	private static final long serialVersionUID = 1L;
	private final String className = this.getClass().toString();

	/**
	 * Given the name of a Library ReportItem, Add it to the end of the report.
	 * 
	 * NOTE: Does not take care of any DataSources or DataSets that the item is
	 * dependent on.
	 * 
	 */
	public Object execute(Object[] args, IScriptFunctionContext scriptContext) throws BirtException {
		if (args.length < 1)
			throw new BirtException(InnoventFunctionFactory.plugin_id, "No reportContext supplied to " + className,
					new Object[] { "" });

		String reportItemName = String.valueOf(args[0]);
		if (reportItemName == null || reportItemName.trim().length() == 0) {
			throw new BirtException(InnoventFunctionFactory.plugin_id, "ReportItem Name is required for " + className,
					new Object[] { "" });
		}

		IReportContext rptContext = getReportContext(args[1]);
		// do not run this code if in the dataSet editor
		if (isDataSetEditor(rptContext)) {
			return null;
		}

		ReportDesignHandle designHandle = (ReportDesignHandle) rptContext.getReportRunnable().getDesignHandle();

		@SuppressWarnings("unchecked")
		List<ModuleHandle> libs = designHandle.getLibraries();
		DesignElementHandle libRptItemHandle = null;
		for (ModuleHandle libHandle : libs) {
			libRptItemHandle = libHandle.findElement(reportItemName);

			// create a new item from the library item.
			DesignElementHandle newLibItemHandle = designHandle.getElementFactory().newElementFrom(libRptItemHandle,
					libRptItemHandle.getName() + System.currentTimeMillis());
			designHandle.getBody().add(newLibItemHandle);
			return "success";
		}

		throw new BirtException(InnoventFunctionFactory.plugin_id,
				"failure to find " + reportItemName + " in libraries: " + this.className, new Object[] { "" });
	}

}
