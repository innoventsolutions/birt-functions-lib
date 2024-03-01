package innovent.birt.test;

import java.math.BigDecimal;

import org.eclipse.birt.core.data.DataType;
import org.eclipse.birt.data.engine.api.aggregation.Accumulator;
import org.eclipse.birt.data.engine.api.aggregation.IAggrFunction;
import org.eclipse.birt.data.engine.core.DataException;
import org.junit.Assert;
import org.junit.Test;

import innovent.birt.aggregations.DecimalMovingAve;

public class DecimalMovingAveTest {
	private Accumulator prepare() {
		final IAggrFunction fn = new DecimalMovingAve();
		Assert.assertEquals(IAggrFunction.RUNNING_AGGR, fn.getType());
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
			accumulator.onRow(new Object[] { new BigDecimal(0), Integer.valueOf(1) });
			accumulator.onRow(new Object[] { new BigDecimal(0), Integer.valueOf(1) });
			accumulator.onRow(new Object[] { new BigDecimal(0), Integer.valueOf(1) });
			accumulator.onRow(new Object[] { new BigDecimal(0), Integer.valueOf(1) });
			accumulator.onRow(new Object[] { new BigDecimal(0), Integer.valueOf(1) });
			accumulator.finish();
			final var value = accumulator.getValue();
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
			accumulator.onRow(new Object[] { new BigDecimal(1), Integer.valueOf(4) });
			accumulator.onRow(new Object[] { new BigDecimal(1), Integer.valueOf(0) });
			accumulator.onRow(new Object[] { new BigDecimal(2), Integer.valueOf(0) });
			accumulator.onRow(new Object[] { new BigDecimal(3), Integer.valueOf(0) });
			accumulator.onRow(new Object[] { new BigDecimal(3), Integer.valueOf(0) });
			accumulator.finish();
			final var value = accumulator.getValue();
			final var expected = new BigDecimal(2.25);
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
			accumulator.onRow(new Object[] { Integer.valueOf(1), Integer.valueOf(1) });
			accumulator.onRow(new Object[] { Integer.valueOf(1), Integer.valueOf(1) });
			accumulator.onRow(new Object[] { Integer.valueOf(2), Integer.valueOf(1) });
			accumulator.onRow(new Object[] { Integer.valueOf(3), Integer.valueOf(1) });
			accumulator.onRow(new Object[] { Integer.valueOf(3), Integer.valueOf(1) });
			accumulator.finish();
			final var value = accumulator.getValue();
			final var expected = new BigDecimal(3);
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
			accumulator.onRow(new Object[] { Double.valueOf(1), Integer.valueOf(1) });
			accumulator.onRow(new Object[] { Double.valueOf(1), Integer.valueOf(1) });
			accumulator.onRow(new Object[] { Double.valueOf(2), Integer.valueOf(1) });
			accumulator.onRow(new Object[] { Double.valueOf(3), Integer.valueOf(1) });
			accumulator.onRow(new Object[] { Double.valueOf(3), Integer.valueOf(1) });
			accumulator.finish();
			final var value = accumulator.getValue();
			final var expected = new BigDecimal("3.0");
			Assert.assertEquals(expected, value);
		}
		catch (final DataException e) {
			Assert.fail(e.toString());
		}
	}
}
