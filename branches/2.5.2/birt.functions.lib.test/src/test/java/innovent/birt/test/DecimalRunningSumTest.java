package innovent.birt.test;

import java.math.BigDecimal;

import org.eclipse.birt.core.data.DataType;
import org.eclipse.birt.data.engine.api.aggregation.Accumulator;
import org.eclipse.birt.data.engine.api.aggregation.IAggrFunction;
import org.eclipse.birt.data.engine.core.DataException;
import org.junit.Assert;
import org.junit.Test;

import innovent.birt.aggregations.DecimalRunningSum;

public class DecimalRunningSumTest {
	private Accumulator prepare() {
		final IAggrFunction fn = new DecimalRunningSum();
		Assert.assertEquals(IAggrFunction.RUNNING_AGGR, fn.getType());
		Assert.assertEquals(DataType.DECIMAL_TYPE, fn.getDataType());
		final var parameterDefns = fn.getParameterDefn();
		Assert.assertNotNull(parameterDefns);
		Assert.assertEquals(parameterDefns.length, 1);
		final var accumulator = fn.newAccumulator();
		Assert.assertNotNull(accumulator);
		return accumulator;
	}

	@Test
	public void testAccumulateBigDecimalZero() {
		final var accumulator = prepare();
		try {
			accumulator.start();
			accumulator.onRow(new Object[] { new BigDecimal(0) });
			var value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			var expected = new BigDecimal(0);
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { new BigDecimal(0) });
			value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			expected = new BigDecimal(0);
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { new BigDecimal(0) });
			value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			expected = new BigDecimal(0);
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { new BigDecimal(0) });
			value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			expected = new BigDecimal(0);
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { new BigDecimal(0) });
			value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			expected = new BigDecimal(0);
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
			accumulator.onRow(new Object[] { new BigDecimal(1) });
			var value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			var expected = new BigDecimal(1);
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { new BigDecimal(1) });
			value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			expected = new BigDecimal(2);
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { new BigDecimal(2) });
			value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			expected = new BigDecimal(4);
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { new BigDecimal(3) });
			value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			expected = new BigDecimal(7);
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { new BigDecimal(3) });
			value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			expected = new BigDecimal(10);
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
			accumulator.onRow(new Object[] { Integer.valueOf(1) });
			var value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			var expected = new BigDecimal(1);
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { Integer.valueOf(1) });
			value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			expected = new BigDecimal(2);
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { Integer.valueOf(2) });
			value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			expected = new BigDecimal(4);
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { Integer.valueOf(3) });
			value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			expected = new BigDecimal(7);
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { Integer.valueOf(3) });
			value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			expected = new BigDecimal(10);
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
			accumulator.onRow(new Object[] { Double.valueOf(1) });
			var value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			var expected = new BigDecimal("1.0");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { Double.valueOf(1) });
			value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			expected = new BigDecimal("2.0");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { Double.valueOf(2) });
			value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			expected = new BigDecimal("4.0");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { Double.valueOf(3) });
			value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			expected = new BigDecimal("7.0");
			Assert.assertEquals(expected, value);
			accumulator.onRow(new Object[] { Double.valueOf(3) });
			value = accumulator.getValue();
			System.out.println("scale = " + ((BigDecimal) value).scale());
			System.out.println("precision = " + ((BigDecimal) value).precision());
			System.out.println("signum = " + ((BigDecimal) value).signum());
			expected = new BigDecimal("10.0");
			Assert.assertEquals(expected, value);
			accumulator.finish();
		}
		catch (final DataException e) {
			Assert.fail(e.toString());
		}
	}
}
