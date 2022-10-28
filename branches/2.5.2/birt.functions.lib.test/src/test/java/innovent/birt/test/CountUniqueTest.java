package innovent.birt.test;

import org.eclipse.birt.core.data.DataType;
import org.eclipse.birt.data.engine.api.aggregation.Accumulator;
import org.eclipse.birt.data.engine.api.aggregation.IAggrFunction;
import org.eclipse.birt.data.engine.api.aggregation.IParameterDefn;
import org.eclipse.birt.data.engine.core.DataException;
import org.junit.Assert;
import org.junit.Test;

import innovent.birt.aggregations.CountUnique;

public class CountUniqueTest {
	private Accumulator prepare(int type) {
		IAggrFunction fn = new CountUnique();
		Assert.assertEquals("getType() must return " + IAggrFunction.SUMMARY_AGGR, fn.getType(),
				IAggrFunction.SUMMARY_AGGR);
		Assert.assertEquals("getDataType() must return " + DataType.STRING_TYPE, fn.getDataType(),
				DataType.INTEGER_TYPE);
		IParameterDefn[] parameterDefns = fn.getParameterDefn();
		Assert.assertNotNull(parameterDefns);
		Assert.assertEquals(parameterDefns.length, 1);
		Accumulator accumulator = fn.newAccumulator();
		IParameterDefn parameterDefn = parameterDefns[0];
		Assert.assertTrue(parameterDefn.isDataField());
		Assert.assertFalse(parameterDefn.isOptional());
		Assert.assertTrue(parameterDefn.supportDataType(DataType.STRING_TYPE));
		Assert.assertNotNull(accumulator);
		return accumulator;
	}

	@Test
	public void testAccumulateStrings() {
		Accumulator accumulator = prepare(DataType.STRING_TYPE);
		try {
			accumulator.start();
			accumulator.onRow(new Object[] { "a" });
			accumulator.onRow(new Object[] { "a" });
			accumulator.onRow(new Object[] { "b" });
			accumulator.onRow(new Object[] { "c" });
			accumulator.onRow(new Object[] { "c" });
			accumulator.finish();
			Object value = accumulator.getValue();
			Assert.assertEquals(value, Integer.valueOf(3));
		} catch (DataException e) {
			Assert.fail(e.toString());
		}
	}

	@Test
	public void testAccumulateIntegers() {
		Accumulator accumulator = prepare(DataType.INTEGER_TYPE);
		try {
			accumulator.start();
			accumulator.onRow(new Object[] { Integer.valueOf(1) });
			accumulator.onRow(new Object[] { Integer.valueOf(1) });
			accumulator.onRow(new Object[] { Integer.valueOf(2) });
			accumulator.onRow(new Object[] { Integer.valueOf(3) });
			accumulator.onRow(new Object[] { Integer.valueOf(3) });
			accumulator.finish();
			Object value = accumulator.getValue();
			Assert.assertEquals(value, Integer.valueOf(3));
		} catch (DataException e) {
			Assert.fail(e.toString());
		}
	}
}
