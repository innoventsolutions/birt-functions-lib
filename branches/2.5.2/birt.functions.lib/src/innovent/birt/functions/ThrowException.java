/*
 * Copyright (c) 2008-Present  Innovent Solutions Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Blackboard, Inc.
 *  Scott Rosenbaum / Steve Schafer, Innovent Solutions, Inc.
 */
package innovent.birt.functions;

import innovent.birt.functions.factory.InnoventFunction;
import innovent.birt.functions.factory.InnoventFunctionFactory;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;
import org.eclipse.birt.report.engine.api.script.IReportContext;

/**
 * Throws an error that will show up in the report design and be accessible from
 * the runTask.getErrors() method
 *
 * Arguments:
 * <ol>
 * <li>Message</li>
 * <li>ReportContext [optional]</li>
 * </ol>
 * 
 * If the reportContext is not supplied, then an Unhandled JavaScript exception
 * will be thrown.
 * 
 * If reportContext is supplied then the passed message will show up in the
 * error message in the report.
 * 
 */
public class ThrowException extends InnoventFunction {
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(Object[] arguments, IScriptFunctionContext context) throws BirtException {
		String message = "Unknown thrown error: message is null";
		if (arguments == null || arguments.length == 0) {
			throw new BirtException(InnoventFunctionFactory.plugin_id, message, new Object[] { "" });
		}

		Object msgObject = arguments[0];
		if (msgObject != null) {
			message = msgObject.getClass().getName() + ": " + msgObject.toString();
		}
		if (arguments.length == 1) {
			throw new BirtException(InnoventFunctionFactory.plugin_id, message, new Object[] { "" });
		}

		if (arguments.length > 1) {
			IReportContext rc = getReportContext(arguments[1]);
			addBirtException(rc, message, BirtException.ERROR);
			return null;
		}
		return null;
	}
}
