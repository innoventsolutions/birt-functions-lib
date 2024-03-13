package innovent.birt.test;

import java.math.BigDecimal;

import org.eclipse.birt.core.data.DataType;
import org.eclipse.birt.data.engine.api.aggregation.Accumulator;
import org.eclipse.birt.data.engine.api.aggregation.IAggrFunction;
import org.eclipse.birt.data.engine.core.DataException;
import org.junit.Assert;
import org.junit.Test;

import innovent.birt.aggregations.DecimalRunningNpv;

public class DecimalRunningNpvTest {
	private Accumulator prepare() {
		final IAggrFunction fn = new DecimalRunningNpv();
		Assert.assertEquals(IAggrFunction.RUNNING_AGGR, fn.getType());
		Assert.assertEquals(DataType.DECIMAL_TYPE, fn.getDataType());
		final var parameterDefns = fn.getParameterDefn();
		Assert.assertNotNull(parameterDefns);
		Assert.assertEquals(2, parameterDefns.length);
		final var accumulator = fn.newAccumulator();
		Assert.assertNotNull(accumulator);
		return accumulator;
	}

	@Test
	public void testAccumulateBigDecimalZero() {
		final var accumulator = prepare();
		try {
			accumulator.start();
			accumulator.onRow(new Object[] { new BigDecimal(0), Double.valueOf(1) });
			var value = accumulator.getValue();
			var expected = new BigDecimal("0E+1");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { new BigDecimal(0), Double.valueOf(1) });
			value = accumulator.getValue();
			expected = new BigDecimal("0E+1");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { new BigDecimal(0), Double.valueOf(1) });
			value = accumulator.getValue();
			expected = new BigDecimal("0E+1");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { new BigDecimal(0), Double.valueOf(1) });
			value = accumulator.getValue();
			expected = new BigDecimal("0E+1");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { new BigDecimal(0), Double.valueOf(1) });
			value = accumulator.getValue();
			expected = new BigDecimal("0E+1");
			Assert.assertEquals(expected, value);
			accumulator.finish();
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
			accumulator.onRow(new Object[] { new BigDecimal(1), Double.valueOf(1) });
			var value = accumulator.getValue();
			var expected = new BigDecimal("0.5");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { new BigDecimal(1), Double.valueOf(1) });
			value = accumulator.getValue();
			expected = new BigDecimal("0.75");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { new BigDecimal(2), Double.valueOf(1) });
			value = accumulator.getValue();
			expected = new BigDecimal("1.00");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { new BigDecimal(3), Double.valueOf(1) });
			value = accumulator.getValue();
			expected = new BigDecimal("1.1875");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { new BigDecimal(3), Double.valueOf(1) });
			value = accumulator.getValue();
			expected = new BigDecimal("1.28125");
			Assert.assertEquals(expected, value);
			accumulator.finish();
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
			accumulator.onRow(new Object[] { Integer.valueOf(1), Double.valueOf(1) });
			var value = accumulator.getValue();
			var expected = new BigDecimal("0.5");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { Integer.valueOf(1), Double.valueOf(1) });
			value = accumulator.getValue();
			expected = new BigDecimal("0.75");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { Integer.valueOf(2), Double.valueOf(1) });
			value = accumulator.getValue();
			expected = new BigDecimal("1.00");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { Integer.valueOf(3), Double.valueOf(1) });
			value = accumulator.getValue();
			expected = new BigDecimal("1.1875");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { Integer.valueOf(3), Double.valueOf(1) });
			value = accumulator.getValue();
			expected = new BigDecimal("1.28125");
			Assert.assertEquals(expected, value);
			accumulator.finish();
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
			accumulator.onRow(new Object[] { Double.valueOf(1), Double.valueOf(1) });
			var value = (BigDecimal) accumulator.getValue();
			var expected = new BigDecimal("0.5");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { Double.valueOf(1), Double.valueOf(1) });
			value = (BigDecimal) accumulator.getValue();
			expected = new BigDecimal("0.75");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { Double.valueOf(2), Double.valueOf(1) });
			value = (BigDecimal) accumulator.getValue();
			Assert.assertEquals(2, value.scale());
			Assert.assertEquals(3, value.precision());
			Assert.assertEquals(1, value.signum());
			expected = new BigDecimal("1.00");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { Double.valueOf(3), Double.valueOf(1) });
			value = (BigDecimal) accumulator.getValue();
			expected = new BigDecimal("1.1875");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { Double.valueOf(3), Double.valueOf(1) });
			value = (BigDecimal) accumulator.getValue();
			Assert.assertEquals(5, value.scale());
			Assert.assertEquals(6, value.precision());
			Assert.assertEquals(1, value.signum());
			expected = new BigDecimal("1.28125");
			Assert.assertEquals(expected, value);
			accumulator.finish();
		}
		catch (final DataException e) {
			Assert.fail(e.toString());
		}
	}
}
