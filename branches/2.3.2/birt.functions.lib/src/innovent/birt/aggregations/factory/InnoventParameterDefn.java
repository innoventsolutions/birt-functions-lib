/** 
 * Copyright (c) 2009-Present Innovent Solutions, Inc.
 * 
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms 
 * of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * 	Scott Rosenbaum - Innovent Solutions
 *    Steve Schafer - Innovent Solutions
 * 				 
 */
package innovent.birt.aggregations.factory;

import org.eclipse.birt.core.data.DataType;
import org.eclipse.birt.data.engine.api.aggregation.IParameterDefn;

/**
 * Parameter Definitions define the parameter that is passed into an
 * aggregate function.  Copied from core, due to visibility issues 
 * 
 * Visibility Issues should have been fixed in 2.5
 * 
 */
public class InnoventParameterDefn implements IParameterDefn {

	private String name;
	private boolean isOptional = false;
	private boolean isDataField = false;
	private String displayName;
	private String description;
	private int[] supportedDataTypes;

	public static final int[] CALCULATABLE = new int[] { DataType.BOOLEAN_TYPE,
			DataType.INTEGER_TYPE, DataType.DOUBLE_TYPE, DataType.DECIMAL_TYPE,
			DataType.STRING_TYPE, DataType.DATE_TYPE, DataType.SQL_DATE_TYPE,
			DataType.SQL_TIME_TYPE };

	/**
	 * 
	 * @param name
	 * @param displayName
	 * @param isOptional
	 * @param isDataField
	 * @param supportedDataTypes
	 * @param description
	 */
	public InnoventParameterDefn(String name, String displayName,
			boolean isOptional, boolean isDataField, int[] supportedDataTypes,
			String description) {
		assert name != null;
		assert supportedDataTypes != null;

		this.name = name;
		this.isOptional = isOptional;
		this.isDataField = isDataField;
		this.displayName = displayName;
		this.supportedDataTypes = supportedDataTypes;
		this.description = description;
	}

	/**
	 * @param isOptional
	 *            the isOptional to set
	 */
	public void setOptional(boolean isOptional) {

		this.isOptional = isOptional;
	}

	/**
	 * @param isDataField
	 *            the isDataField to set
	 */
	public void setDataField(boolean isDataField) {
		this.isDataField = isDataField;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.birt.data.engine.api.aggregation.IParameterDefn#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.birt.data.engine.api.aggregation.IParameterDefn#getDescription
	 * ()
	 */
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.birt.data.engine.api.aggregation.IParameterDefn#getDisplayName
	 * ()
	 */
	public String getDisplayName() {
		return displayName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.birt.data.engine.api.aggregation.IParameterDefn#isDataField()
	 */
	public boolean isDataField() {
		return isDataField;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.birt.data.engine.api.aggregation.IParameterDefn#isOptional()
	 */
	public boolean isOptional() {
		return isOptional;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.birt.data.engine.api.aggregation.IParameterDefn#supportDataType
	 * (int)
	 */
	public boolean supportDataType(int dataType) {
		if (dataType == DataType.UNKNOWN_TYPE)
			return true;

		for (int i = 0; i < supportedDataTypes.length; i++) {
			if (supportedDataTypes[i] == DataType.ANY_TYPE
					|| supportedDataTypes[i] == dataType) {
				return true;
			}
		}
		return false;
	}
}
