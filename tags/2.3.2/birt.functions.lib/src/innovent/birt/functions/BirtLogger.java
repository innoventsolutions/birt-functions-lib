/**
 * (C) Copyright Blackboard Inc. 1998-2009 - All Rights Reserved
 * 
 * This program and the accompanying materials are made available 
 * under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * BLACKBOARD MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, 
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. 
 * BLACKBOARD SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, 
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * Contributors:
 *  Blackboard, Inc. 
 *  Innovent Solutions, Inc. - Steve Schafer / Scott Rosenbaum
 *
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
	private static final Logger logger = Logger.getLogger(BirtLogger.class.getName());

	@Override
	public Object execute(final Object[] arguments, final IScriptFunctionContext context) throws BirtException {
		String message = "message is null";
		if (arguments.length >= 1) {
			final Object object = arguments[0];
			if (object != null)
				message = object.getClass().getName() + ": " + object.toString();
		}
		Level level = Level.INFO;
		if (arguments.length >= 2) {
			final Object object = arguments[1];
			if (object != null) {
				final String levelName = object.toString().toUpperCase();
				try {
					level = Level.parse(levelName);
				} catch (IllegalArgumentException e) {
					message = "Unrecognized logging level: " + levelName + ", original message = " + message;
				}
			}
		}
		logger.log(level, message);
		return null;
	}

}
