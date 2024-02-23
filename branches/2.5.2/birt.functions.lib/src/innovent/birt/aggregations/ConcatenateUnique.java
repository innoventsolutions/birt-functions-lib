/**
 * Copyright (c) 2008-Present  Innovent Solutions, Inc.
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

import innovent.birt.aggregations.factory.InnoventParameterDefn;

/**
 * Implements a concatenate function which only accumulates unique values.
 *
 * Returns a single string which is the concatenation of all the unique values,
 * in an arbitrary order. Each value is separated with a single new-line. If the
 * length of any individual value, represented as a string, exceeds 1024
 * characters, it is truncated and " ..." is appended.
 *
 * BIRT core now supports this feature, function retained as an example of HowTo
 * do aggregates.
 *
 */
public class ConcatenateUnique implements IAggrFunction {
	private static final InnoventParameterDefn dataValue = new InnoventParameterDefn("DataValue",
			"Data Value", false, true, InnoventParameterDefn.CALCULATABLE,
			"The value that will be counted uniquely");

	@Override
	public String getName() {
		return "INNOVENTCONCATUNIQUE";
	}

	@Override
	public int getType() {
		return SUMMARY_AGGR;
	}

	@Override
	public IParameterDefn[] getParameterDefn() {
		return new IParameterDefn[] { dataValue };
	}

	@Override
	public int getDataType() {
		return DataType.STRING_TYPE;
	}

	@Override
	public Object getDefaultValue() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Concatenates String values";
	}

	@Override
	public String getDisplayName() {
		return "Concatenate Unique";
	}

	@Override
	public int getNumberOfPasses() {
		return 1;
	}

	@Override
	public boolean isDataOrderSensitive() {
		return true;
	}

	@Override
	public Accumulator newAccumulator() {
		return new ConcatenateUniqueAccumulator();
	}

	private class ConcatenateUniqueAccumulator extends Accumulator {
		private boolean isFinished = false;
		private Set<String> values = new LinkedHashSet<>();
		private final Integer maxVal = 1024;
		private Integer curCnt = 0;
		private Boolean isFull = false;

		public ConcatenateUniqueAccumulator() {
		}

		@Override
		public void start() throws DataException {
			super.start();
			values = new LinkedHashSet<>();
			isFinished = false;
		}

		@Override
		public void finish() throws DataException {
			isFinished = true;
		}

		@Override
		public void onRow(final Object[] args) throws DataException {
			if (isFull)
				return;
			assert args.length > 0;
			try {
				if (curCnt > maxVal) {
					values.add(" ...");
					isFull = true;
					return;
				}
				if (args[0] == null)
					return;
				final var curVal = DataTypeUtil.toString(args[0]);
				values.add(curVal);
				curCnt = +curVal.length();
			}
			catch (final BirtException e) {
				throw new DataException("1", e);
			}
		}

		@Override
		public Object getValue() throws DataException {
			if (!isFinished)
				throw new RuntimeException(
						"Error! Call summary total function before finished the dataset");
			final var cVal = new StringBuilder();
			for (final var iterator = values.iterator(); iterator.hasNext();) {
				cVal.append(iterator.next());
				if (iterator.hasNext()) {
					cVal.append("\n");
				}
			}
			return cVal.toString();
		}
	}
}
