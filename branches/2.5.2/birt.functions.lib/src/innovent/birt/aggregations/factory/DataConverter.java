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
 *  Steve Schafer - Innovent Solutions
 * 				 
 */
package innovent.birt.aggregations.factory;

import java.math.BigDecimal;
import java.util.Date;

public class DataConverter {

	/**
	 * Boolean	-> Double 
	 * true		-> 1 
	 * others	-> 0 
	 * Date 		-> Double Date.getTime();
	 * String 	-> Double Double.valueOf(String);
	 * 
	 * @param source
	 * @return
	 * @throws RuntimeException
	 */
	public static Double toDouble(Object source) throws RuntimeException {
		if (source == null)
			return null;

		if (source instanceof Integer) {
			double doubleValue = ((Integer) source).doubleValue();
			return new Double(doubleValue);
		} else if (source instanceof BigDecimal) {
			double doubleValue = ((BigDecimal) source).doubleValue();
			return new Double(doubleValue);
		} else if (source instanceof Boolean) {
			if (true == ((Boolean) source).booleanValue())
				return new Double(1d);
			return new Double(0d);
		} else if (source instanceof Date) {
			double doubleValue = ((Date) source).getTime();
			return new Double(doubleValue);
		} else if (source instanceof Double) {
			return (Double) source;
		} else if (source instanceof String) {
			try {
				return Double.valueOf((String) source);
			} catch (NumberFormatException e) {
				throw new RuntimeException(
						"Failure to convert to double, bad number format", e);
			}
		} else {
			throw new RuntimeException(
					"Failure to convert to double, unconvertible data type: "
							+ source.getClass().toString());
		}
	}

	/**
	 * Boolean -> Integer true -> 1 others -> 0 String -> Integer
	 * Integer.valueOf(String);
	 * 
	 * @param source
	 * @return
	 * @throws RuntimeException
	 */
	public static Integer toInteger(Object source) throws RuntimeException {
		if (source == null)
			return null;

		if (source instanceof Integer) {
			return (Integer) source;
		} else if (source instanceof BigDecimal) {
			int intValue = ((BigDecimal) source).intValue();
			return new Integer(intValue);
		} else if (source instanceof Boolean) {
			if (true == ((Boolean) source).booleanValue())
				return Integer.valueOf(1);
			return Integer.valueOf(0);
		} else if (source instanceof Double) {
			return ((Double) source).intValue();
		} else if (source instanceof String) {
			try {
				return Integer.valueOf((String) source);
			} catch (NumberFormatException e) {
				throw new RuntimeException(
						"Failure to convert to double, bad number format", e);
			}
		} else {
			throw new RuntimeException(
					"Failure to convert to double, unconvertible data type: "
							+ source.getClass().toString());
		}
	}

}
