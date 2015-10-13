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

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.Expression;
import org.eclipse.birt.report.model.api.ExpressionType;
import org.eclipse.birt.report.model.api.OdaDataSetHandle;
import org.eclipse.birt.report.model.api.OdaDataSetParameterHandle;
import org.eclipse.birt.report.model.api.PropertyHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.StructureFactory;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.elements.structures.OdaDataSetParameter;

/**
 * Edits all the data sets in the report. If special comment patterns are found,
 * they may be replaced with new strings and new parameters are created and
 * bound to the query. This function should be called in the beforeFactory event
 * of the top level report object. Arguments:
 * <ol>
 * <li>reportContext</li>
 * </ol>
 * <p>
 * The pattern looks like this:
 * <p>
 * <code>
 * &#47;* BIND prefix $paramname suffix *&#47;
 * </code>
 * <p>
 * where prefix and suffix are any strings and paramname is the name of one of
 * the report parameters. The dollar-sign and "BIND" are required. Normally this
 * pattern is placed in a where clause. Here is an example:
 * <p>
 * <code>
 * where 0=0 <br>
 * &#47;* BIND and c.pk1 = $param#course_id *&#47;
 * </code>
 * </p>
 * When the report runs, if param#course_id exists and contains a non-null value
 * and that value is not "null", the entire comment will be replaced with
 * <p>
 * <code>
 * and c.pk1 = ?
 * </code>
 * </p>
 * and a new parameter will be created in the dataset to correspond with this
 * "?" marker and with its default value set to the value of the param#course_id
 * report parameter.
 * <p>
 * If the report parameter is a multi-value parameter, then multiple
 * comma-separated question-marks are inserted. In this case you would want to
 * use SQL syntax that can handle multiple question marks, like this:
 * <p>
 * <code>
 * &#47;* BIND and ls.layer in ($param#course_id) *&#47;
 * </code>
 * </p>
 * which will be translated to something like this
 * <p>
 * <code>
 * and ls.layer in (?, ?, ?)
 * </code>
 * </p>
 * If there are no values in the multi-value parameter or if all the values
 * happen to be null, or are equal to the string "null", the comment will not be
 * translated.
 */
public class BindParameters extends InnoventFunction {
	private static final Logger logger = Logger.getLogger(BindParameters.class.getName());
	private static final String PARAM_TOKEN = "?";
	private final Pattern pattern = Pattern.compile("/\\* BIND (.*?)\\$([A-Za-z0-9-_#]+)(.*?)\\*/",
			Pattern.MULTILINE | Pattern.DOTALL);
	private int paramCount = 0;

	@Override
	public Object execute(Object[] arguments, IScriptFunctionContext context) throws BirtException {
		if (arguments.length < 1)
			throw new BirtException(InnoventFunctionFactory.plugin_id,
					"No reportContext supplied to ResolveSQLParameters", new Object[] { "" });

		final IReportContext rptContext = getReportContext(arguments[0]);
		// do not run this code if in the dataSet editor
		if (isDataSetEditor(rptContext)) {
			return null;
		}

		ReportDesignHandle designHandle = (ReportDesignHandle) rptContext.getReportRunnable().getDesignHandle();
		@SuppressWarnings("unchecked")
		final List<DataSetHandle> dsAll = designHandle.getAllDataSets();
		for (DataSetHandle dataSetHandle : dsAll) {
			if (dataSetHandle instanceof OdaDataSetHandle) {
				bindParameters(rptContext, (OdaDataSetHandle) dataSetHandle);
			}
		}

		/*
		 * DEBUG modified design to a file
		 */
		try {
			designHandle.saveAs("fred.xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* END DEBUG */

		return null;
	}

	/**
	 * For each OdaDataSet walk through looking for appropriate SQL pattern and
	 * change query text to use ? where appropriate and re-order parameters.
	 * 
	 * @param rptContext
	 * @param odaDataSetHandle
	 * @throws SemanticException
	 */
	private void bindParameters(IReportContext rptContext, OdaDataSetHandle odaDataSetHandle) throws SemanticException {
		String sqlText = odaDataSetHandle.getQueryText();
		// NOTE: if "?" is the last character in the string, the last empty
		// part
		// is excluded
		if (sqlText.endsWith("?"))
			sqlText += "\n";

		DynamicQueryText qryTextObject = new DynamicQueryText(rptContext, odaDataSetHandle, pattern);
		String newSql = qryTextObject.processQueryText(sqlText);

		logger.info("ResolveSQLParameters, translated query = " + newSql);
		odaDataSetHandle.setQueryText(newSql);
		reOrderParameters(odaDataSetHandle);
	}

	/**
	 * As parameter bindings are added, the position is accurately tracked, but
	 * the ODA does not pay attention to the position when running.
	 * <p>
	 * This routine re-orders the parameters in the list to match the position
	 * variable.
	 * 
	 * @param curDataSet
	 */
	private void reOrderParameters(OdaDataSetHandle curDataSet) {
		@SuppressWarnings("unchecked")
		Iterator<OdaDataSetParameterHandle> params = curDataSet.parametersIterator();
		int pos = 0;
		while (params.hasNext()) {
			pos++;
			Object obj = params.next();
			if (obj instanceof OdaDataSetParameterHandle) {
				OdaDataSetParameterHandle handle = (OdaDataSetParameterHandle) obj;
				handle.setPosition(pos);
				logger.finest(pos + " " + handle.getName() + " = " + handle.getDefaultValue());
			}
		}
	}

	/**
	 * Inner class that represents a SQL string that may contain both standard
	 * stanadard parameters (?) and dynamic parameters in the form
	 * <p>
	 * <code>
	 * &#47;* BIND prefix $paramname suffix *&#47;
	 * </code>
	 * <p>
	 * This class will handle the string substitutions required to change all of
	 * the dynamic parameters into standard JDBC parameters.
	 * <p>
	 * In addition, this class will add parameter bindings in the appropriate
	 * locations for all of the dynamic parameters. The dynamic parameters will
	 * be hard-coded to the actual value of the passed parameter.
	 * 
	 */
	private final class DynamicQueryText {
		int originalParamIndex = 0;
		int addedParamCount = 0;
		StringBuffer sb = new StringBuffer();
		String paramToken = "";
		final IReportContext rptContext;
		final OdaDataSetHandle odaDataSetHandle;
		final Pattern pattern;

		public DynamicQueryText(final IReportContext rptContext, final OdaDataSetHandle odaDataSetHandle,
				final Pattern pattern) {
			this.rptContext = rptContext;
			this.odaDataSetHandle = odaDataSetHandle;
			this.pattern = pattern;
		}

		public String processQueryText(final String sqlText) throws SemanticException {
			originalParamIndex = 0;
			addedParamCount = 0;
			sb = new StringBuffer();
			paramToken = "";
			String[] sqlParts = sqlText.split("\\?");
			for (String part : sqlParts) {
				processSqlPart(part);
			}
			return sb.toString();
		}

		private final void processSqlPart(String part) throws SemanticException {
			sb.append(paramToken);
			paramToken = PARAM_TOKEN;
			final Matcher matcher = pattern.matcher(part);
			while (matcher.find()) {
				final String prefix = matcher.group(1);
				final String paramName = matcher.group(2);
				final String suffix = matcher.group(3);
				Object paramObject = rptContext.getParameterValue(paramName);
				int newParamIndex = originalParamIndex + addedParamCount;
				if (paramObject != null) {
					if (paramObject instanceof Object[]) {
						Object[] paramObjectArray = (Object[]) paramObject;
						StringBuffer subsb = new StringBuffer();
						String sep = "";
						int subParamCount = 0;
						for (Object subParamObject : paramObjectArray) {
							if (addParameterBinding(odaDataSetHandle, newParamIndex, subParamObject)) {
								subsb.append(sep);
								sep = ", ";
								subsb.append(PARAM_TOKEN);
								subParamCount++;
							}
						}
						if (subParamCount > 0) {
							addedParamCount += subParamCount;
							matcher.appendReplacement(sb, prefix + subsb + suffix);
						}
					} else if (addParameterBinding(odaDataSetHandle, newParamIndex, paramObject)) {
						matcher.appendReplacement(sb, prefix + PARAM_TOKEN + suffix);
						addedParamCount++;
					}
				}
			}
			matcher.appendTail(sb);
			originalParamIndex++;
		}

		/**
		 * For a given DataSet add a parameter binding to the static value that
		 * is passed to the report.
		 * <p>
		 * Use the position variable to determine the appropriate location for
		 * the parameter (as it appears in the query),
		 * 
		 * @param odaDataSetHandle
		 * @param newParamIndex
		 * @param paramObject
		 * @return
		 * @throws SemanticException
		 */
		private boolean addParameterBinding(OdaDataSetHandle odaDataSetHandle, int newParamIndex, Object paramObject)
				throws SemanticException {
			if (paramObject instanceof Double) {
				addParameterBinding(odaDataSetHandle, newParamIndex, "float", paramObject.toString());
				return true;
			}
			if (paramObject instanceof Date) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(((Date) paramObject).getTime());
				if (calendar.get(Calendar.YEAR) == 0 && calendar.get(Calendar.MONTH) == 0
						&& calendar.get(Calendar.DAY_OF_MONTH) == 0) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
					addParameterBinding(odaDataSetHandle, newParamIndex, "time", dateFormat.format((Date) paramObject));
				} else if (calendar.get(Calendar.HOUR) == 0 && calendar.get(Calendar.MINUTE) == 0
						&& calendar.get(Calendar.SECOND) == 0 && calendar.get(Calendar.MILLISECOND) == 0) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					addParameterBinding(odaDataSetHandle, newParamIndex, "date",
							quote(dateFormat.format((Date) paramObject)));
				} else {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					addParameterBinding(odaDataSetHandle, newParamIndex, "datetime",
							quote(dateFormat.format((Date) paramObject)));
				}
				return true;
			}
			if (paramObject instanceof Boolean) {
				addParameterBinding(odaDataSetHandle, newParamIndex, "boolean", paramObject.toString());
				return true;
			}
			if (paramObject instanceof BigDecimal) {
				addParameterBinding(odaDataSetHandle, newParamIndex, "decimal", paramObject.toString());
				return true;
			}
			if (paramObject instanceof Integer) {
				addParameterBinding(odaDataSetHandle, newParamIndex, "integer", paramObject.toString());
				return true;
			}
			if (paramObject instanceof String) {
				String string = (String) paramObject;
				if ("null".equals(string))
					return false;
				addParameterBinding(odaDataSetHandle, newParamIndex, "string", quote(string));
				return true;
			}
			if (paramObject != null) {
				addParameterBinding(odaDataSetHandle, newParamIndex, "string", quote(paramObject.toString()));
				return true;
			}
			return false;
		}

		private String quote(String string) {
			return "\"" + string.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
		}

		private void addParameterBinding(OdaDataSetHandle odaDataSetHandle, int paramIndex, String dataType,
				String dataValue) throws SemanticException {
			OdaDataSetParameter parameter = StructureFactory.createOdaDataSetParameter();
			parameter.setName("rsp_param_" + paramCount++);
			parameter.setPosition(paramIndex);
			parameter.setDataType(dataType);

			// no longer works. Need to use new format to add as an expression
			// parameter.setDefaultValue(dataValue);
			Expression expr = new Expression(dataValue, ExpressionType.JAVASCRIPT);
			parameter.setExpressionProperty(OdaDataSetParameter.DEFAULT_VALUE_MEMBER, expr);

			parameter.setIsInput(true);
			parameter.setIsOutput(false);
			PropertyHandle parameterHandle = odaDataSetHandle.getPropertyHandle(DataSetHandle.PARAMETERS_PROP);
			parameterHandle.insertItem(parameter, paramIndex);
		}

	}

}
