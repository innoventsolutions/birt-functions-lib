/*
 * Copyright (c) 2008-2015  Innovent Solutions Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Blackboard, Inc.
 *  Scott Rosenbaum / Steve Schafer, Innovent Solutions, Inc.
 */
package innovent.birt.functions;

import innovent.birt.functions.factory.InnoventFunction;
import innovent.birt.functions.factory.InnoventFunctionFactory;

import java.util.Iterator;
import java.util.logging.Logger;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.engine.api.script.ScriptException;
import org.eclipse.birt.report.engine.script.internal.instance.DataSetInstance;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.ScalarParameterHandle;
import org.eclipse.birt.report.model.api.SlotHandle;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;

/**
 * Placed in a DataSets before method, this functions will add to the where
 * clause to place a SQL snippet into the expression which will be evaluated at
 * runtime binding a parameter and its values to the where clause
 * 
 * NOTE: the parameter name is the name of the field that is being filtered.
 * 
 * @author highcroft
 *
 */
public class WhereClauseBinding extends InnoventFunction {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BindParameters.class.getName());

	@SuppressWarnings("rawtypes")
	public Object execute(Object[] arguments, IScriptFunctionContext context) throws BirtException {
		if (arguments.length < 1)
			throw new BirtException(InnoventFunctionFactory.plugin_id, "No reportContext supplied to ConvertFilters",
					new Object[] { "" });

		final IReportContext rptContext = getReportContext(arguments[0]);
		final DataSetInstance dataSet = (DataSetInstance) arguments[1];
		final String oper = (String) arguments[2];
		final String dataType = (String) arguments[3];
		final String paramNames = (String) arguments[4];
		logger.fine("Extend Where Clause: " + dataSet.getName() + " " + oper + " " + dataType + " " + paramNames);
		final String[] paramArray = paramNames.split(",");
		final String paramOne = paramArray[0];
		// final String paramTwo = paramArray.length > 1 ? paramArray[1] : "";

		ReportDesignHandle designHandle = (ReportDesignHandle) rptContext.getReportRunnable().getDesignHandle();
		SlotHandle paramSlot = designHandle.getParameters();
		ScalarParameterHandle sph = null;
		for (Iterator pIter = paramSlot.iterator(); pIter.hasNext();) {
			Object obj = (Object) pIter.next();
			if (obj instanceof ScalarParameterHandle) {
				ScalarParameterHandle aSph = (ScalarParameterHandle) obj;
				if (aSph.getName().equals(paramOne)) {
					sph = aSph;
				}
			}
		}

		// TODO Multi-Parameter Support
		String paramName = sph.getName().replace(':', '.');
		String paramValues = (String) rptContext.getParameterValue(sph.getName());
		if (paramValues == null || paramValues.trim().length() == 0)
			return "";

		// Name may have been extended
		int repPoint = paramName.indexOf("__");
		if (repPoint > 0) {
			paramName = paramName.substring(0, repPoint);
		}

		StringBuffer sqlScript = new StringBuffer();
		sqlScript.append(checkWhereClause(dataSet));
		sqlScript.append("\nAND ").append(paramName);

		sqlScript.append(" ").append(oper).append(" ");

		if (DesignChoiceConstants.MAP_OPERATOR_IN.equalsIgnoreCase(oper)) {
			sqlScript.append(getInClause(dataType, paramValues.trim()));
			return sqlScript.toString();
		}

		if (DesignChoiceConstants.MAP_OPERATOR_LIKE.equalsIgnoreCase(oper)) {
			sqlScript.append(getLikeClause(dataType, paramValues.trim()));
			return sqlScript.toString();
		}

		return "";
	}

	// Like clause just returns the value with wild card appended to end
	private StringBuffer getLikeClause(final String dataType, String paramValue) {
		StringBuffer sb = new StringBuffer();
		sb.append(addParamValue(dataType, paramValue));
		return sb;
	}

	private StringBuffer getInClause(final String dataType, String paramValue) {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		String[] pVals = paramValue.split(";");
		for (int i = 0; i < pVals.length; i++) {
			sb.append(addParamValue(dataType, pVals[i].trim()));
			if (i < pVals.length - 1) {
				sb.append(",");
			}
		}
		sb.append(")");
		return sb;
	}

	private StringBuffer addParamValue(final String dataType, String paramValue) {

		StringBuffer sb = new StringBuffer();
		if (DesignChoiceConstants.PARAM_TYPE_STRING.equalsIgnoreCase(dataType)) {
			sb.append("'");
			sb.append(paramValue.replace('*', '%'));
			sb.append("'");
		} else {
			sb.append(paramValue);
		}

		return sb;
	}

	private String checkWhereClause(DataSetInstance dataSet) throws ScriptException {
		String qry = dataSet.getQueryText();
		if (qry != null && !qry.toLowerCase().contains("where")) {
			// TODO Handle clauses after Where
			return "\nWHERE 0=0\n";
		}
		return "\n";
	}

}
