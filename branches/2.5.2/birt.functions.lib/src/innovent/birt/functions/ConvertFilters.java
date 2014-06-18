/**
 * (C) Copyright Blackboard Inc. 1998-2009 - All Rights Reserved
 * 
 * This program and the accompanying materials are made available 
 * under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * BLACKBOARD MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, 
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. 
 * BLACKBOARD SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, 
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * Contributors:
 *  Blackboard, Inc. 
 *  Innovent Solutions, Inc. - Steve Schafer / Scott Rosenbaum
 *
 */
package innovent.birt.functions;

import innovent.birt.functions.factory.InnoventFunction;
import innovent.birt.functions.factory.InnoventFunctionFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.Expression;
import org.eclipse.birt.report.model.api.OdaDataSetHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.ScalarParameterHandle;
import org.eclipse.birt.report.model.api.SlotHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.command.NameException;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.elements.structures.FilterCondition;
import org.eclipse.birt.report.model.api.elements.structures.ResultSetColumn;
import org.eclipse.birt.report.model.api.simpleapi.IResultSetColumn;

/**
 * Edits all the data sets in the report. If a filter is found on an ODA the
 * filter will be replaced with report level parameters that are bound to the
 * ODA query. This function should be called in the initialize event of the top
 * level report object. Arguments:
 * <ol>
 * <li>reportContext</li>
 * </ol>
 * <p>
 * The pattern looks like this:
 * <p>
 * <code>
 * All Filters on ODA DataSets will be replaced by parameters
 * </code>
 * <p>
 * Parameters will be created for each filter.
 * <p>
 */
public class ConvertFilters extends InnoventFunction {
	private static final Logger logger = Logger.getLogger(BindParameters.class.getName());

	public Object execute(Object[] arguments, IScriptFunctionContext context) throws BirtException {
		if (arguments.length < 1)
			throw new BirtException(InnoventFunctionFactory.plugin_id, "No reportContext supplied to ConvertFilters", new Object[] { "" });

		final IReportContext rptContext = getReportContext(arguments[0]);
		// do not run this code if in the dataSet editor
		if (isDataSetEditor(rptContext)) {
			return null;
		}

		//removeParameters(rptContext);

		Boolean debug = false;
		if (arguments.length == 2)
			debug = (Boolean) arguments[1];
		
		ReportDesignHandle designHandle = (ReportDesignHandle) rptContext.getReportRunnable().getDesignHandle();
		List<FilterConverter> foundFilterConverts = new ArrayList<FilterConverter>();
		@SuppressWarnings("unchecked")
		final List<DataSetHandle> dsAll = designHandle.getAllDataSets();
		for (DataSetHandle dataSetHandle : dsAll) {
			if (dataSetHandle instanceof OdaDataSetHandle) {
				try {
					foundFilterConverts.addAll(findFiltersToConvert(rptContext, (OdaDataSetHandle) dataSetHandle));
				} catch (SemanticException ex) {
					logger.warning("Failure while ConvertingFilter on DataSet " + dataSetHandle.getName() + " " + ex.getMessage());
					ex.printStackTrace();
				}
			}
		}

		/* 
		 * For each of the FilterConverts:
		 *  Check if any parameters are needed
		 *  Change parameter name if it is a duplicate paramName with different default values
		 *  
		 */
		ElementFactory ef = designHandle.getElementFactory();
		SlotHandle paramSlot = designHandle.getParameters();
		for (FilterConverter foundConvert : foundFilterConverts) {
			Map<String, String> paramsToAdd = foundConvert.testParameters(rptContext);
			for (Entry<String, String> createParam : paramsToAdd.entrySet()) {
				ScalarParameterHandle sph = createScalarParameter(ef, createParam.getKey(), createParam.getValue());
				paramSlot.add(sph);
				rptContext.setParameterValue(createParam.getKey(), createParam.getValue());
			}
		}

		/*
		 * Now walk through DataSets again and create binding for any 
		 * FilterConverts that match
		 */
		for (DataSetHandle dataSetHandle : dsAll) {
			List<FilterConverter> validConvert = new ArrayList<FilterConverter>();
			for (FilterConverter foundConvert : foundFilterConverts) {
				if (dataSetHandle.getName().equalsIgnoreCase(foundConvert.getDataSetName())) {
					validConvert.add(foundConvert);
				}
			}

			addWhereClauseBinding(dataSetHandle, validConvert);
		}

		if (debug) {
			try {

				String rptName = rptContext.getReportRunnable().getReportName();
				designHandle.saveAs(rptName + ".debug");
			} catch (IOException e) {
				//TODO better handling of saved file
				e.printStackTrace();
			}
		}

		/* END DEBUG */

		return null;
	}

	/**
	 * Skeleton code to remove parameters that have been added.  
	 * Issue with parameterNames that are re-used
	 * 
	 * @param rptContext
	 * @throws SemanticException
	 */
	@SuppressWarnings("unused")
	private void removeParameters(IReportContext rptContext) throws SemanticException {
		List<ScalarParameterHandle> dropItems = new ArrayList<ScalarParameterHandle>();
		SlotHandle params = rptContext.getDesignHandle().getParameters();
		
		@SuppressWarnings("rawtypes")
		Iterator pIter = params.iterator();
		//find any parameters that are designated for removal
		while (pIter.hasNext()) {
			ScalarParameterHandle sph = (ScalarParameterHandle) pIter.next();
			Object pVal = rptContext.getParameterValue(sph.getName());
			if ("----".equalsIgnoreCase(pVal.toString())) {
				dropItems.add(sph);
			}
		}
		if (dropItems.size() == 0)
			return;

		for (ScalarParameterHandle sph : dropItems) {
			params.drop(sph);
		}
		
		// Once the parameter has been removed
		// need to clean up the beforeOpen method to remove code bindings
		@SuppressWarnings("unchecked")
		final List<DataSetHandle> dsAll = rptContext.getDesignHandle().getAllDataSets();
		for (DataSetHandle dsh : dsAll) {
			String b4open = dsh.getBeforeOpen();
			for (ScalarParameterHandle sph : dropItems) {
				System.out.println("");
				dsh.setBeforeOpen(removeParamBind(b4open, sph.getName()));
			}
		}

	}

	private String removeParamBind(String b4open, String paramName) {
		String[] modClause = b4open.split("\n");
		StringBuffer modString = new StringBuffer();
		for (int i = 0; i < modClause.length; i++) {
			String look4 = modClause[i];

			if (look4.length() == 0 || look4.indexOf(paramName + "'") > 0) {
				// don't add this back in
				// the space at the end is to handle io:fieldName and io:fieldName__1
				continue;
			}
			modString.append(modClause[i]).append("\n");
		}

		return modString.toString();
	}

	private void handleFilterBoundParams(IReportContext rptContext, FilterCondition fc) throws NameException {
		
		// Test to see if this is a parameterized FilterCondition
		String expVals = fc.getValue1ExpressionList().toString();
		if (expVals.indexOf("?") < 0) {
			// not parametrized
			return;
		}

		String paramName = expVals.substring(expVals.indexOf("?")+1,expVals.indexOf("]"));
		ScalarParameterHandle usedParam = null;
		SlotHandle paramHdls  = rptContext.getDesignHandle().getParameters();
		Iterator<ScalarParameterHandle> pIter = paramHdls.iterator();
		while (pIter.hasNext()) {
			usedParam = null;
			usedParam = pIter.next();
			if(usedParam != null && usedParam.getName().equalsIgnoreCase(paramName)){
				break;
			}
		}
		
		if (usedParam == null)
			return;
		
		// If we change the name to match our naming, we should match it
		String newParamName = fc.getExpr().substring(1, fc.getExpr().length() - 1);
		usedParam.setName(newParamName);
		
		// Also change default values on parameter, and expressions on filter to match
		List<Object> paramDefaultVals = usedParam.getDefaultValueList();
		List fcVals = fc.getValue1ExpressionList();
		List<String> newVals = new ArrayList<String>();
		//fcVals.clear();
		for (Object object : paramDefaultVals) {
			newVals.add(object.toString());
		}
		fc.setValue1(newVals);
		String vals = newVals.toString();
		rptContext.setParameterValue(newParamName, vals.substring(1, vals.length() - 1));
		
		return;
	}

	/**
	 * For each OdaDataSet walk through looking for filters that we will transform
	 * For each filter that we transform return a FilterConverter
	 * 
	 * @param rptContext
	 * @param dataSetHdl
	 * @throws SemanticException
	 */
	private List<FilterConverter> findFiltersToConvert(IReportContext rptContext, OdaDataSetHandle dataSetHdl) throws SemanticException {

		@SuppressWarnings("unchecked")
		List<FilterCondition> filterConditions = (List<FilterCondition>) dataSetHdl.getProperty("filter");
		List<FilterConverter> requiredConverters = new ArrayList<FilterConverter>();
		if (filterConditions == null || filterConditions.size() < 1)
			return requiredConverters;

		List<FilterCondition> keepFilters = new ArrayList<FilterCondition>();
		for (FilterCondition fc : filterConditions) {
			if (!supportedCondition(fc)) {
				// These are filters that will not be converted
				// build a new list that will be kept un-modified
				keepFilters.add(fc);
				continue;
			}
			// If user bound a parameter need to handle this situation
			// we will create a list of parameters that are named using our scheme
			// with the right default parameters
			handleFilterBoundParams(rptContext, fc);
			//Supported condition so continue with processing
			requiredConverters.add(new FilterConverter(fc, dataSetHdl));
			logger.fine("Converter processed: " + fc.getExpr() + " : " + fc.getOperator());
		}

		if (requiredConverters.size() == 0) {
			// No filters so we are done
			return requiredConverters;
		}

		// Need to remove filters that become parameters
		// and keep parameters that are not supported
		dataSetHdl.setProperty("filter", new ArrayList<FilterCondition>());
		for (FilterCondition fc : keepFilters) {
			dataSetHdl.addFilter(fc);
		}

		return requiredConverters;

	}

	/**
	 * Modify a DataSet so that a where clause is added to filter
	 * based on the parameter values
	 * 
	 * @param odaDataSetHandle
	 * @param newConverter
	 * @throws SemanticException
	 */
	private void addWhereClauseBinding(DataSetHandle odaDataSetHandle, List<FilterConverter> newConverter) throws SemanticException {
		StringBuffer paramBinds = new StringBuffer();
		for (FilterConverter convert : newConverter) {
			// each converter will add one bind to the where clause
			paramBinds.append("this.queryText += CustomFunctions.WhereClauseBinding(reportContext, this, '");
			paramBinds.append(convert.getOperator()).append("', '");
			paramBinds.append(convert.getDataType()).append("', '");
			paramBinds.append(convert.getParameterNames()).append("');");
			paramBinds.append("\n");
		}

		// Have to be careful with the b4open method
		// someone may add a filter later on, so we don't want to lose any code in b4open
		StringBuffer b4open = new StringBuffer();
		b4open.append(odaDataSetHandle.getBeforeOpen() == null ? "" : odaDataSetHandle.getBeforeOpen());
		if (b4open.length() > 0) {
			b4open.append("\n");
		}
		b4open.append(paramBinds.toString());
		odaDataSetHandle.setBeforeOpen(b4open.toString());
	}

	/**
	 * Not all FilterConditions are supported
	 * 
	 * @param fc
	 * @return
	 */
	private Boolean supportedCondition(FilterCondition fc) {

		if (fc.getOperator().equalsIgnoreCase(DesignChoiceConstants.MAP_OPERATOR_IN))
			return true;

		if (fc.getOperator().equalsIgnoreCase(DesignChoiceConstants.MAP_OPERATOR_LIKE))
			return true;

		if (fc.getOperator().equalsIgnoreCase(DesignChoiceConstants.MAP_OPERATOR_NOT_IN))
			return true;

		if (fc.getOperator().equalsIgnoreCase(DesignChoiceConstants.MAP_OPERATOR_NOT_LIKE))
			return true;

		//TODO Does not support composite condition filters.
		//e.g.  (IN([io_sample:custState], "CA", "NJ")) AND  (IN([io_sample:officeCode], "1", "2")) is True

		logger.info("Unsupported condition: " + fc.getOperator() + " expr: " + fc.getExpr());
		return false;
	}

	private ScalarParameterHandle createScalarParameter(ElementFactory ef, String paramName, String paramValues) throws SemanticException {
		// This is the design element that will show up in future reports.
		ScalarParameterHandle sph = ef.newScalarParameter(paramName);

		//TODO DataType support
		sph.setDataType(DesignChoiceConstants.PARAM_TYPE_STRING);

		// add existing parameters, parameterValues are always a list of values
		// we are breaking this by adding all items in same list
		List<String> pVals = new ArrayList<String>();
		pVals.add(paramValues);
		sph.setDefaultValueList(pVals);
		sph.setIsRequired(false);

		sph.setValueType("static");
		sph.setDistinct(true);
		sph.setParamType("simple");
		sph.setControlType("text-box");
		sph.setCategory("Unformatted");

		// Automatically will use the ColumnNames Defined in IOB
		// So these should be relatively business friendly.
		sph.setPromptText("Enter " + paramName.substring(paramName.indexOf(":") + 1));

		return sph;

	}

	/**
	 * This inner class gathers up the info for each Filter Condition
	 * FilterConditions may have more than one parameter
	 * or
	 * Reuse a parameter that is already in report
	 * This thing takes care of that situation
	 * 
	 * TODO: Currently only supports a single parameterName per expression
	 * 
	 * @author Scott Rosenbaum
	 *
	 */
	private class FilterConverter {
		private final FilterCondition fc;
		private final String dataSetName;
		private String dataType = DesignChoiceConstants.PARAM_TYPE_STRING;
		private Map<String, String> params = new HashMap<String, String>();

		FilterConverter(FilterCondition fc, DataSetHandle dataSetHdl) {
			this.fc = fc;
			this.dataSetName = dataSetHdl.getName();

			// TODO handle multi parameter expressions
			// currently only handle simple one value parameters
			List<ResultSetColumn> columnList = (List<ResultSetColumn>) dataSetHdl.getProperty(DataSetHandle.RESULT_SET_PROP);
			String paramName = fc.getExpr().substring(1, fc.getExpr().length() - 1);
			for (ResultSetColumn col : columnList) {
				if (paramName.equalsIgnoreCase(col.getColumnName())) {
					dataType = col.getDataType();
					break;
				}
			}

			StringBuffer paramValues = new StringBuffer();
			List<Expression> fcValues = fc.getValue1ExpressionList();

			for (Expression fcExpr : fcValues) {
				//Strip off the double quotes replace with single quotes for strings
				//TODO DataType Support
				paramValues.append(fcExpr.getStringExpression().replace('"', ' ')).append(";");

			}
			// remove last trailing semi-colon
			paramValues.deleteCharAt(paramValues.length() - 1);

			// TODO only one parameter
			params.put(paramName, paramValues.toString());

		}

		public String toString() {
			if (fc == null)
				return "No Filter Condition";

			return fc.getExpr() + " " + fc.getOperator() + " " + fc.getValue1ExpressionList().toString();
		}

		Map<String, String> testParameters(IReportContext rptContext) {
			Map<String, String> paramsRequired = new HashMap<String, String>();

			for (Entry<String, String> aParam : params.entrySet()) {
				Object usedValObj = rptContext.getParameterValue(aParam.getKey());
				if (usedValObj == null) {
					paramsRequired.put(aParam.getKey(), aParam.getValue());
					continue;
				}
				String usedValue = usedValObj.toString();
				if (usedValue.equalsIgnoreCase(aParam.getValue())) {
					// parameter value is the same as existing parameter
					// don't create a new one, we will re-use
					continue;
				}
				// we have a parameter that matches existing parameterName with a new value
				// we need to mask in a new name, which will be removed at bind time
				for (int i = 1; i < 20; i++) {
					String newName = aParam.getKey() + "__" + i;
					Object chkExists = rptContext.getParameterValue(newName);
					if (chkExists != null) {
						// look for a new parameter name
						continue;
					}
					// return that this new param needs to be added
					paramsRequired.put(newName, aParam.getValue());
					// change the namme of the parameter used here
					params.remove(aParam.getKey());
					params.put(newName, aParam.getValue());
					break;
				}

			}
			return paramsRequired;
		}

		Map<String, String> getParams() {
			return params;
		}

		String getOperator() {
			return fc.getOperator();
		}

		// TODO multiple parameterNames per condition
		String getParameterNames() {
			StringBuffer sb = new StringBuffer();
			Set<String> keys = params.keySet();
			for (String paramName : keys) {
				sb.append(paramName).append(",");
			}
			// Trim that last comma
			return sb.substring(0, sb.length() - 1);
		}

		public String getDataSetName() {
			return dataSetName;
		}

		public String getDataType() {
			return dataType;
		}

	}

}
