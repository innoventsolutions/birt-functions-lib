package innovent.birt.test;

import org.eclipse.birt.core.data.DataType;
import org.eclipse.birt.data.engine.api.aggregation.Accumulator;
import org.eclipse.birt.data.engine.api.aggregation.IAggrFunction;
import org.eclipse.birt.data.engine.core.DataException;
import org.junit.Assert;
import org.junit.Test;

import innovent.birt.aggregations.ConcatenateUnique;

public class ConcatenateUniqueTest {
	private Accumulator prepare() {
		final IAggrFunction fn = new ConcatenateUnique();
		Assert.assertEquals("getType() must return " + IAggrFunction.SUMMARY_AGGR, fn.getType(),
			IAggrFunction.SUMMARY_AGGR);
		Assert.assertEquals("getDataType() must return " + DataType.STRING_TYPE, fn.getDataType(),
			DataType.STRING_TYPE);
		final var parameterDefns = fn.getParameterDefn();
		Assert.assertNotNull(parameterDefns);
		Assert.assertEquals(parameterDefns.length, 1);
		final var accumulator = fn.newAccumulator();
		Assert.assertNotNull(accumulator);
		return accumulator;
	}

	@Test
	public void testAccumulateStrings() {
		final var accumulator = prepare();
		try {
			accumulator.start();
			accumulator.onRow(new Object[] { "a" });
			accumulator.onRow(new Object[] { "a" });
			accumulator.onRow(new Object[] { "b" });
			accumulator.onRow(new Object[] { "c" });
			accumulator.onRow(new Object[] { "c" });
			accumulator.finish();
			final var value = accumulator.getValue();
			final var expected = "a\nb\nc";
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
			accumulator.onRow(new Object[] { Integer.valueOf(1) });
			accumulator.onRow(new Object[] { Integer.valueOf(1) });
			accumulator.onRow(new Object[] { Integer.valueOf(2) });
			accumulator.onRow(new Object[] { Integer.valueOf(3) });
			accumulator.onRow(new Object[] { Integer.valueOf(3) });
			accumulator.finish();
			final var value = accumulator.getValue();
			final var expected = "1\n2\n3";
			Assert.assertEquals(expected, value);
		}
		catch (final DataException e) {
			Assert.fail(e.toString());
		}
	}
}
