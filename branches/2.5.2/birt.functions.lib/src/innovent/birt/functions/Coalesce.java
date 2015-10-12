package innovent.birt.functions;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;

import innovent.birt.functions.factory.InnoventFunction;

public class Coalesce extends InnoventFunction {

	@Override
	public Object execute(Object[] arguments, IScriptFunctionContext context) throws BirtException {
		if (arguments.length < 1)
			return null;
		Object value = arguments[0];
		if (arguments.length < 2)
			return value;
		Object defaultValue = arguments[1];
		return value == null ? defaultValue : value;
	}

}
