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

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;

public class TextWrap extends InnoventFunction {

	private static final long serialVersionUID = 1L;

	/**
	 * Inserts line-endings to word-wrap a string into multiple lines.
	 * 
	 * @param string
	 * @param length
	 * @return
	 */
	public Object execute(Object[] args, IScriptFunctionContext context) throws BirtException {
		String inputString = String.valueOf(args[0]);
		if (inputString == null)
			return "";
		if (args.length == 1)
			return inputString;

		Object arg1 = args[1];
		Integer position = null;
		if (arg1 instanceof String) {
			String p = (String) arg1;
			// BIRT automatically converts a number to a floating point
			if (p.indexOf(".") >= 0) {
				Float f = Float.valueOf(p);
				position = Integer.valueOf(Math.round(f));
			} else {
				position = Integer.valueOf(p);
			}
		} else if (arg1 instanceof Number) {
			Number number = (Number) arg1;
			position = number.intValue();
		}

		if (position == null)
			return inputString;
		final String[] words = inputString.split(" ");
		{
			int maxWordLength = 0;
			for (final String word : words)
				if (maxWordLength < word.length())
					maxWordLength = word.length();
			if (position < maxWordLength)
				position = maxWordLength;
		}
		final StringBuilder sb = new StringBuilder();
		String separator = "";
		@SuppressWarnings("unused")
		boolean first = true;
		int currentLength = 0;
		for (final String word : words) {
			if (currentLength + separator.length() + word.length() > position) {
				while (currentLength < position) {
					sb.append(" ");
					currentLength++;
				}
				sb.append("\n");
				currentLength = 0;
				separator = "";
			}
			sb.append(separator);
			sb.append(word);
			currentLength += separator.length() + word.length();
			separator = " ";
			first = false;
		}
		return sb.toString();
	}
}
