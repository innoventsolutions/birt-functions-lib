/*
 * Copyright (c) 2008-2015  Innovent Solutions Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Scott Rosenbaum / Steve Schafer, Innovent Solutions, Inc.
 */
package innovent.birt.functions;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;

import innovent.birt.functions.factory.InnoventFunction;

public class Coalesce extends InnoventFunction {

	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(Object[] arguments, IScriptFunctionContext context) throws BirtException {
		if (arguments.length < 1)
			return null;
		Object value = arguments[0];
		if (arguments.length < 2)
			return value;
		Object defaultValue = arguments[1];
		return value == null ? defaultValue : value;
	}

}
