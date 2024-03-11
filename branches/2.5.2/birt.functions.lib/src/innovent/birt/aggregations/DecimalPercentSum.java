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

import java.math.BigDecimal;
import java.math.MathContext;

import org.eclipse.birt.core.data.DataType;
import org.eclipse.birt.core.data.DataTypeUtil;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.data.aggregation.impl.AggrFunction;
import org.eclipse.birt.data.aggregation.impl.Constants;
import org.eclipse.birt.data.aggregation.impl.ParameterDefn;
import org.eclipse.birt.data.aggregation.impl.RunningAccumulator;
import org.eclipse.birt.data.aggregation.impl.SupportedDataTypes;
import org.eclipse.birt.data.engine.api.aggregation.Accumulator;
import org.eclipse.birt.data.engine.api.aggregation.IParameterDefn;
import org.eclipse.birt.data.engine.core.DataException;

/**
 * Implements a decimal sum.
 *
 */
@SuppressWarnings("restriction")
public class DecimalPercentSum extends AggrFunction {
	@Override
	public String getName() {
		return "DECIMALPERCENTSUM";
	}

	@Override
	public String getDisplayName() {
		return "Decimal Percent Sum"; //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return "Percent Sum of Decimal Values";
	}

	@Override
	public int getDataType() {
		return DataType.DECIMAL_TYPE;
	}

	@Override
	public int getType() {
		return RUNNING_AGGR;
	}

	@Override
	public int getNumberOfPasses() {
		return 2;
	}

	@Override
	public IParameterDefn[] getParameterDefn() {
		return new IParameterDefn[] { new ParameterDefn(Constants.EXPRESSION_NAME,
				Constants.EXPRESSION_DISPLAY_NAME, false, true, SupportedDataTypes.CALCULATABLE, "") //$NON-NLS-1$
		};
	}

	@Override
	public Accumulator newAccumulator() {
		return new MyAccumulator();
	}

	private static class MyAccumulator extends RunningAccumulator {
		private BigDecimal sum = new BigDecimal(0);
		private int passNo = 0;
		private Object value;

		MyAccumulator() {
		}

		@Override
		public void start() throws DataException {
			super.start();
			passNo++;
		}

		@Override
		public void onRow(final Object[] args) throws DataException {
			assert args.length > 0;
			BigDecimal arg;
			try {
				arg = DataTypeUtil.toBigDecimal(args[0]);
			}
			catch (final BirtException e) {
				throw DataException.wrap(e);
			}
			if (passNo == 1) {
				if (arg != null) {
					sum = sum.add(arg);
				}
			}
			else if (arg == null || sum.equals(new BigDecimal(0))) {
				value = Integer.valueOf(0);
			}
			else {
				value = arg.divide(sum, MathContext.DECIMAL128);
			}
		}

		@Override
		public Object getValue() {
			return value;
		}
	}
}
