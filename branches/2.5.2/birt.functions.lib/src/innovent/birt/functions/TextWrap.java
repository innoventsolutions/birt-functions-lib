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

	/**
	 * Inserts line-endings to word-wrap a string into multiple lines.
	 * 
	 * @param string
	 * @param length
	 * @return
	 */
	public Object execute(Object[] args, IScriptFunctionContext context)
			throws BirtException {
		String inputString = String.valueOf(args[0]);
		if (inputString == null)
			return "";

		String p = String.valueOf(args[1]);
		if (p == null)
			return inputString;

		// FIX, handle positions passed as numbers
		// BIRT automatically converts a number to a floating point
		Integer position = 0;
		if (p.indexOf(".") >= 0){
			Float f = Float.valueOf(p);
			position = Integer.valueOf(Math.round(f));
		} else {
			position = Integer.valueOf(p);
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
