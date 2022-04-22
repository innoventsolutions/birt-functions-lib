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
 *  Scott Rosenbaum - Innovent Solutions
 *  Steve Schafer - Innovent Solutions
 * 				 
 */
package innovent.birt.aggregations;

import innovent.birt.aggregations.factory.InnoventParameterDefn;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.birt.core.data.DataType;
import org.eclipse.birt.core.data.DataTypeUtil;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.data.engine.api.aggregation.Accumulator;
import org.eclipse.birt.data.engine.api.aggregation.IAggrFunction;
import org.eclipse.birt.data.engine.api.aggregation.IParameterDefn;
import org.eclipse.birt.data.engine.core.DataException;

/**
 * Implements a concatenate function which only accumulates unique values.
 * 
 * Returns a single string which is the concatenation of all the unique values,
 * in an arbitrary order. Each value is separated with a single new-line. If the
 * length of any individual value, represented as a string, exceeds 1024
 * characters, it is truncated and " ..." is appended.
 * 
 * BIRT core now supports this feature, function retained as an example of HowTo do aggregates.
 * 
 */
public class ConcatenateUnique implements IAggrFunction {

	private static final InnoventParameterDefn dataValue = new InnoventParameterDefn("DataValue", "Data Value", false,
			true, InnoventParameterDefn.CALCULATABLE, "The value that will be counted uniquely");

	public String getName() {
		return "CONCATENATEUNIQUE";
	}

	public int getType() {
		return SUMMARY_AGGR;
	}

	public IParameterDefn[] getParameterDefn() {
		return new IParameterDefn[] { dataValue };
	}

	public int getDataType() {
		return DataType.STRING_TYPE;
	}

	public Object getDefaultValue() {
		return "";
	}

	public String getDescription() {
		return "Concatenates String values";
	}

	public String getDisplayName() {
		return "Concatenate Unique";
	}

	public int getNumberOfPasses() {
		return 1;
	}

	public boolean isDataOrderSensitive() {
		return true;
	}

	public Accumulator newAccumulator() {
		return new ConcatenateUniqueAccumulator();
	}

	private class ConcatenateUniqueAccumulator extends Accumulator {

		private boolean isFinished = false;
		private Set<String> values = new LinkedHashSet<String>();
		private final Integer maxVal = 1024;
		private Integer curCnt = 0;
		private Boolean isFull = false;

		public ConcatenateUniqueAccumulator() {
			super();
		}

		public void start() throws DataException {
			super.start();
			values = new LinkedHashSet<String>();
			isFinished = false;
		}

		public void finish() throws DataException {
			isFinished = true;
		}

		public void onRow(Object[] args) throws DataException {
			if (isFull)
				return;

			assert (args.length > 0);
			try {
				if (curCnt > maxVal) {
					values.add(" ...");
					isFull = true;
					return;
				}
				if (args[0] == null)
					return;

				String curVal = DataTypeUtil.toString(args[0]);
				values.add(curVal);
				curCnt = +curVal.length();

			} catch (BirtException e) {
				throw new DataException("1", e);
			}
		}

		public Object getValue() throws DataException {
			if (!isFinished) {
				throw new RuntimeException("Error! Call summary total function before finished the dataset");
			}
			StringBuffer cVal = new StringBuffer();
			for (Iterator<String> iterator = values.iterator(); iterator.hasNext();) {
				cVal.append((String) iterator.next());
				if (iterator.hasNext())
					cVal.append("\n");
			}
			return cVal.toString();
		}

	}

}
