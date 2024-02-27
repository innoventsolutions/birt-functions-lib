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
 * 	Scott Rosenbaum - Innovent Solutions
 *    Steve Schafer - Innovent Solutions
 *
 */
package innovent.birt.aggregations;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.birt.core.data.DataType;
import org.eclipse.birt.data.engine.api.aggregation.Accumulator;
import org.eclipse.birt.data.engine.api.aggregation.IAggrFunction;
import org.eclipse.birt.data.engine.api.aggregation.IParameterDefn;
import org.eclipse.birt.data.engine.core.DataException;

import innovent.birt.aggregations.factory.InnoventParameterDefn;

/**
 * Implements a custom Optimistic Sum Aggregation Function Provides a distinct
 * count based on a single column value.
 *
 * BIRT now supports function core. Retained as an example of HowTo
 */
public class CountUnique implements IAggrFunction {
	private static final InnoventParameterDefn dataValue = new InnoventParameterDefn("DataValue",
			"Data Value", false, true, InnoventParameterDefn.CALCULATABLE,
			"The value that will be counted uniquely");

	@Override
	public int getDataType() {
		return DataType.INTEGER_TYPE;
	}

	@Override
	public Object getDefaultValue() {
		return 0;
	}

	@Override
	public int getNumberOfPasses() {
		return 1;
	}

	@Override
	public boolean isDataOrderSensitive() {
		return false;
	}

	@Override
	public String getName() {
		return "INNOVENTCOUNTUNIQUE";
	}

	@Override
	public int getType() {
		return SUMMARY_AGGR;
	}

	@Override
	public String getDescription() {
		return "Count Unique Values";
	}

	@Override
	public String getDisplayName() {
		return "Innovent Count Unique"; //$NON-NLS-1$
	}

	@Override
	public IParameterDefn[] getParameterDefn() {
		return new IParameterDefn[] { dataValue };
	}

	@Override
	public Accumulator newAccumulator() {
		return new CountUniqueAccumulator();
	}

	/**
	 * Accumulate class does actual work of figuring out the number of unique
	 * values.
	 *
	 */
	private class CountUniqueAccumulator extends Accumulator {
		private boolean isFinished = false;
		private Set<Object> values = new HashSet<>();

		public CountUniqueAccumulator() {
		}

		@Override
		public void start() throws DataException {
			super.start();
			values = new HashSet<>();
			isFinished = false;
		}

		@Override
		public void finish() throws DataException {
			isFinished = true;
		}

		@Override
		public void onRow(final Object[] args) throws DataException {
			assert args.length > 0;
			if (args[0] == null)
				return;
			values.add(args[0]);
		}

		@Override
		public Object getValue() throws DataException {
			if (!isFinished)
				throw new RuntimeException(
						"Error! Call summary total function before finished the dataset");
			return Integer.valueOf(values.size());
		}
	}
}
