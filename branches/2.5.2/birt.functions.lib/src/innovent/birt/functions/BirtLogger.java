/*
 * Copyright (c) 2008-2015  Innovent Solutions Inc.
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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;

/**
 * Arguments:
 * <ol>
 * <li>message (String) - the message to log</li>
 * <li>priority (String) - one of "ALL", "FINE", "FINER", "FINEST", "INFO",
 * "SEVERE", "WARNING", or "OFF"
 * </ol>
 * 
 * @author Steve Schafer / Innovent Solutions
 */
public class BirtLogger extends InnoventFunction {
	private static final Logger logger = Logger.getLogger(BirtLogger.class
			.getName());

	@Override
	public Object execute(final Object[] arguments,
			final IScriptFunctionContext context) throws BirtException {
		String message = "message is null";
		if (arguments.length >= 1) {
			final Object object = arguments[0];
			if (object != null)
				message = object.getClass().getName() + ": "
						+ object.toString();
		}
		Level level = Level.INFO;
		if (arguments.length >= 2) {
			final Object object = arguments[1];
			if (object != null) {
				final String levelName = object.toString().toUpperCase();
				try {
					level = Level.parse(levelName);
				} catch (IllegalArgumentException e) {
					message = "Unrecognized logging level: " + levelName
							+ ", original message = " + message;
				}
			}
		}
		logger.log(level, message);
		return null;
	}

}
