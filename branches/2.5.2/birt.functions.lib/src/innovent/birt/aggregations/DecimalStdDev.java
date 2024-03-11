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
import org.eclipse.birt.data.aggregation.impl.SupportedDataTypes;
import org.eclipse.birt.data.engine.api.aggregation.Accumulator;
import org.eclipse.birt.data.engine.api.aggregation.IAggrFunction;
import org.eclipse.birt.data.engine.api.aggregation.IParameterDefn;
import org.eclipse.birt.data.engine.core.DataException;

/**
 * Implements a decimal sum.
 *
 */
@SuppressWarnings("restriction")
public class DecimalStdDev extends AggrFunction implements IAggrFunction {
	@Override
	public String getName() {
		return "DECIMALSTDDEV";
	}

	@Override
	public String getDisplayName() {
		return "Decimal StdDev"; //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return "Standard Deviation of Decimal Values";
	}

	@Override
	public int getType() {
		return SUMMARY_AGGR;
	}

	@Override
	public int getDataType() {
		return DataType.DECIMAL_TYPE;
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

	private static class MyAccumulator extends Accumulator {
		private BigDecimal sum = new BigDecimal(0);
		private BigDecimal squareSum = new BigDecimal(0);
		private int count = 0;

		@Override
		public void start() {
			sum = new BigDecimal(0);
			squareSum = new BigDecimal(0);
			count = 0;
		}

		@Override
		public void onRow(final Object[] args) throws DataException {
			assert args.length > 0;
			final var arg = args[0];
			if (args != null) {
				var obj = new BigDecimal(0);
				try {
					obj = DataTypeUtil.toBigDecimal(arg);
				}
				catch (final BirtException e) {
					throw DataException.wrap(e);
				}
				if (obj == null) {
					obj = new BigDecimal(0);
				}
				sum = sum.add(obj);
				squareSum = squareSum.add(obj.multiply(obj));
				count++;
			}
		}

		@Override
		public Object getValue() throws DataException {
			if (count <= 1) {
				return null;
			}
			final var countBd = new BigDecimal(count);
			final var ret = countBd.multiply(squareSum).subtract(sum.multiply(sum)).divide(
				countBd.multiply(countBd.subtract(new BigDecimal(1))), MathContext.DECIMAL128);
			return new BigDecimal(Math.sqrt(ret.doubleValue()));
		}
	}
}
