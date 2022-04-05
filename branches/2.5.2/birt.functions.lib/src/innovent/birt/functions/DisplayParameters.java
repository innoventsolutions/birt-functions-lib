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

import java.util.Collection;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.impl.ParameterDefn;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.model.api.CellHandle;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.GridHandle;
import org.eclipse.birt.report.model.api.LabelHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.RowHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.command.ContentException;
import org.eclipse.birt.report.model.api.command.NameException;
import org.eclipse.birt.report.model.elements.interfaces.IStyleModel;

/**
 * Utility method that will insert a grid at the head of your report displaying
 * the values of the parameters that the user passed to the report.
 * 
 */
public class DisplayParameters extends InnoventFunction {

	private static final long serialVersionUID = 1L;

	/**
	 * Find and replace all of the Multi-Selection Strings in the sql code
	 */
	@SuppressWarnings("unchecked")
	public Object execute(Object[] args, IScriptFunctionContext scriptContext) throws BirtException {
		IReportContext rptContext = getReportContext(args[0]);
		// do not run this code if in the dataSet editor
		if (isDataSetEditor(rptContext)) {
			return null;
		}
		IReportRunnable runnable = rptContext.getReportRunnable();
		IGetParameterDefinitionTask parameterTask = runnable.getReportEngine()
				.createGetParameterDefinitionTask(runnable);

		ReportDesignHandle designHandle = (ReportDesignHandle) rptContext.getReportRunnable().getDesignHandle();
		Collection<ParameterDefn> paramRefs = parameterTask.getParameterDefns(false);
		if (paramRefs == null || paramRefs.size() == 0) {
			LabelHandle lh = designHandle.getElementFactory().newLabel("paramters");
			lh.setText("No Parameters Defined");
			designHandle.getBody().add(lh, 0);
		}

		GridHandle newGrid = designHandle.getElementFactory().newGridItem("parameterGrid", 3, paramRefs.size() + 1);
		// newGrid.setWidth("100%");
		DesignElementHandle deh = designHandle.getBody().get(0);
		if (newGrid.getName().equalsIgnoreCase(deh.getName()))
			deh.drop();

		newGrid.setProperty(IStyleModel.BACKGROUND_COLOR_PROP, "#99B3CC");
		DesignElementHandle hdrRow = newGrid.getRows().get(0);
		hdrRow.setProperty(IStyleModel.BACKGROUND_COLOR_PROP, "#0529B");
		hdrRow.setProperty(IStyleModel.COLOR_PROP, "#FFFFFF");
		designHandle.getBody().add(newGrid, 0);

		// Add Labels to the first row cells
		AddLabelToCell(newGrid, 0, 0, "Name");
		AddLabelToCell(newGrid, 0, 1, "Prompt");
		AddLabelToCell(newGrid, 0, 2, "Value");

		// for each parameter name, get the parameter value
		// add the name and value to a hashmap
		int i = 0;
		for (ParameterDefn parameterDefn : paramRefs) {
			String pName = parameterDefn.getName();
			Object values = rptContext.getParameterValue(pName);
			i++;
			AddLabelToCell(newGrid, i, 0, pName);
			AddLabelToCell(newGrid, i, 1, parameterDefn.getPromptText());
			// Need to handle multi-select parameters
			if (values instanceof Object[]) {
				StringBuffer sb = new StringBuffer();
				Object[] pvals = (Object[]) values;
				for (int j = 0; j < pvals.length; j++) {
					sb.append(pvals[j]);
					if (j < pvals.length - 1) {
						sb.append("\n");
					}
				}
				AddLabelToCell(newGrid, i, 2, sb);
			} else {
				AddLabelToCell(newGrid, i, 2, values);
			}
		}

		return "success";
	}

	private void setBorder(DesignElementHandle elementHdl) throws SemanticException {
		elementHdl.setProperty(IStyleModel.BORDER_BOTTOM_COLOR_PROP, "#0529B");
		elementHdl.setProperty(IStyleModel.BORDER_BOTTOM_WIDTH_PROP, "thin");
		elementHdl.setProperty(IStyleModel.BORDER_BOTTOM_STYLE_PROP, "solid");

		elementHdl.setProperty(IStyleModel.BORDER_TOP_COLOR_PROP, "#0529B");
		elementHdl.setProperty(IStyleModel.BORDER_TOP_WIDTH_PROP, "thin");
		elementHdl.setProperty(IStyleModel.BORDER_TOP_STYLE_PROP, "solid");

		elementHdl.setProperty(IStyleModel.BORDER_LEFT_COLOR_PROP, "#0529B");
		elementHdl.setProperty(IStyleModel.BORDER_LEFT_WIDTH_PROP, "thin");
		elementHdl.setProperty(IStyleModel.BORDER_LEFT_STYLE_PROP, "solid");

		elementHdl.setProperty(IStyleModel.BORDER_RIGHT_COLOR_PROP, "#0529B");
		elementHdl.setProperty(IStyleModel.BORDER_RIGHT_WIDTH_PROP, "thin");
		elementHdl.setProperty(IStyleModel.BORDER_RIGHT_STYLE_PROP, "solid");
	}

	private void AddLabelToCell(GridHandle newGrid, int row, int col, Object obj)
			throws SemanticException, ContentException, NameException {
		String str = obj == null ? "NULL" : obj.toString();
		LabelHandle lh = newGrid.getElementFactory().newLabel(str);
		lh.setText(str);
		RowHandle rowHandle = (RowHandle) newGrid.getRows().get(row);
		CellHandle cellHandle = (CellHandle) rowHandle.getCells().get(col);
		this.setBorder(cellHandle);
		cellHandle.getContent().add(lh);
	}
}
