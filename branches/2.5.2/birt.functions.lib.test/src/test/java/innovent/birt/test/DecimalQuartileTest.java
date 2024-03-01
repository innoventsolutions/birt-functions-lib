package innovent.birt.test;

import java.math.BigDecimal;

import org.eclipse.birt.core.data.DataType;
import org.eclipse.birt.data.engine.api.aggregation.Accumulator;
import org.eclipse.birt.data.engine.api.aggregation.IAggrFunction;
import org.eclipse.birt.data.engine.core.DataException;
import org.junit.Assert;
import org.junit.Test;

import innovent.birt.aggregations.DecimalPercentile;

public class DecimalQuartileTest {
	private Accumulator prepare() {
		final IAggrFunction fn = new DecimalPercentile();
		Assert.assertEquals(IAggrFunction.SUMMARY_AGGR, fn.getType());
		Assert.assertEquals(DataType.DECIMAL_TYPE, fn.getDataType());
		final var parameterDefns = fn.getParameterDefn();
		Assert.assertNotNull(parameterDefns);
		Assert.assertEquals(parameterDefns.length, 2);
		final var accumulator = fn.newAccumulator();
		Assert.assertNotNull(accumulator);
		return accumulator;
	}

	@Test
	public void testAccumulateBigDecimalZero() {
		final var accumulator = prepare();
		try {
			accumulator.start();
			accumulator.onRow(new Object[] { new BigDecimal(0), Double.valueOf(0.5) });
			accumulator.onRow(new Object[] { new BigDecimal(0), Double.valueOf(0.5) });
			accumulator.onRow(new Object[] { new BigDecimal(0), Double.valueOf(0.5) });
			accumulator.onRow(new Object[] { new BigDecimal(0), Double.valueOf(0.5) });
			accumulator.onRow(new Object[] { new BigDecimal(0), Double.valueOf(0.5) });
			accumulator.finish();
			final var value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			final var expected = new BigDecimal(0);
			Assert.assertEquals(expected, value);
		}
		catch (final DataException e) {
			Assert.fail(e.toString());
		}
	}

	@Test
	public void testAccumulateBigDecimal() {
		final var accumulator = prepare();
		try {
			accumulator.start();
			accumulator.onRow(new Object[] { new BigDecimal(1), Double.valueOf(0.5) });
			accumulator.onRow(new Object[] { new BigDecimal(1), Double.valueOf(0) });
			accumulator.onRow(new Object[] { new BigDecimal(2), Double.valueOf(0) });
			accumulator.onRow(new Object[] { new BigDecimal(3), Double.valueOf(0) });
			accumulator.onRow(new Object[] { new BigDecimal(3), Double.valueOf(0) });
			accumulator.finish();
			final var value = accumulator.getValue();
			final var expected = new BigDecimal(2);
			Assert.assertEquals(expected, value);
		}
		catch (final DataException e) {
			Assert.fail(e.toString());
		}
	}

	@Test
	public void testAccumulateIntegers() {
		final var accumulator = prepare();
		try {
			accumulator.start();
			accumulator.onRow(new Object[] { Integer.valueOf(1), Double.valueOf(0.5) });
			accumulator.onRow(new Object[] { Integer.valueOf(1), Double.valueOf(0.5) });
			accumulator.onRow(new Object[] { Integer.valueOf(2), Double.valueOf(0.5) });
			accumulator.onRow(new Object[] { Integer.valueOf(3), Double.valueOf(0.5) });
			accumulator.onRow(new Object[] { Integer.valueOf(3), Double.valueOf(0.5) });
			accumulator.finish();
			final var value = accumulator.getValue();
			final var expected = new BigDecimal(2);
			Assert.assertEquals(expected, value);
		}
		catch (final DataException e) {
			Assert.fail(e.toString());
		}
	}

	@Test
	public void testAccumulateDoubles() {
		final var accumulator = prepare();
		try {
			accumulator.start();
			accumulator.onRow(new Object[] { Double.valueOf(1), Double.valueOf(0.5) });
			accumulator.onRow(new Object[] { Double.valueOf(1), Double.valueOf(0.5) });
			accumulator.onRow(new Object[] { Double.valueOf(2), Double.valueOf(0.5) });
			accumulator.onRow(new Object[] { Double.valueOf(3), Double.valueOf(0.5) });
			accumulator.onRow(new Object[] { Double.valueOf(3), Double.valueOf(0.5) });
			accumulator.finish();
			final var value = accumulator.getValue();
			final var expected = new BigDecimal("2.0");
			Assert.assertEquals(expected, value);
		}
		catch (final DataException e) {
			Assert.fail(e.toString());
		}
	}
}
