/** 
 * Copyright (c) 2009-Present Innovent Solutions, Inc.
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

import java.util.ResourceBundle;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionExecutor;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionFactory;

public class InnoventFunctionFactory implements IScriptFunctionFactory {

	public static final String plugin_id = "innovent.birt.extensions";
	public static final String package_name = "innovent.birt.functions";

	/**
	 * Factory method to build a class that matches the function name. If you
	 * want to have a function that does not match you can look for other
	 * function names and instantiate the class as needed. Best to just name
	 * your Function classes like the function name.
	 * 
	 */
	public IScriptFunctionExecutor getFunctionExecutor(String functionName) throws BirtException {
		/*
		 * example of non-conforming class name Not required if FunctionName
		 * defined plugin.xml matches the class name
		 * 
		 * if (ModifySurveySql.class.getSimpleName().equals(functionName)){
		 * return new ModifySurveySql(); }
		 */

		String fullClassName = package_name + "." + functionName;
		try {
			Class<? extends IScriptFunctionExecutor> functionClass = Class.forName(fullClassName).asSubclass(
					IScriptFunctionExecutor.class);
			IScriptFunctionExecutor scriptFunction = functionClass.newInstance();
			return scriptFunction;

		} catch (Exception e) {
			e.printStackTrace();
			throw new BirtException(package_name, "Unable to find class: " + fullClassName, getResourceBundle(), e);
		}
	}

	public static ResourceBundle getResourceBundle() {
		return null;
	}

}
