/** 
 * Copyright (c) 2009 Innovent Solutions, Inc.
 * 
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms 
 * of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * 	Scott Rosenbaum - Innovent Solutions
 * 	Steve Schafer - Innovent Solutions
 * 				 
 */
package innovent.birt.functions;

import innovent.birt.functions.factory.InnoventFunction;
import innovent.birt.functions.factory.InnoventFunctionFactory;

import java.io.File;
import java.util.List;

import org.eclipse.birt.chart.model.attribute.ColorDefinition;
import org.eclipse.birt.chart.model.attribute.Fill;
import org.eclipse.birt.chart.model.attribute.FontDefinition;
import org.eclipse.birt.chart.model.attribute.Palette;
import org.eclipse.birt.chart.model.attribute.Text;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.ImageImpl;
import org.eclipse.birt.chart.model.attribute.impl.PaletteImpl;
import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.script.IChartScriptContext;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.script.functionservice.IScriptFunctionContext;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.model.api.DimensionHandle;
import org.eclipse.birt.report.model.api.SharedStyleHandle;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;

public class SetChartPalette extends InnoventFunction {
	private final String className = this.getClass().toString();

	@Override
	public Object execute(Object[] args, IScriptFunctionContext context)
			throws BirtException {
		Object tmpObj = args[0];
		if (!(tmpObj instanceof Series)) {
			throw new BirtException(InnoventFunctionFactory.plugin_id,
					"First argument has to be Series object " + className,
					new Object[] { "" });
		}
		final Series series = (Series) tmpObj;

		tmpObj = args[1];
		if (!(tmpObj instanceof IChartScriptContext)) {
			throw new BirtException(InnoventFunctionFactory.plugin_id,
					"Second argument has to be IChartScriptContext object "
							+ className, new Object[] { "" });

		}
		final IChartScriptContext chartScriptCtx = (IChartScriptContext) tmpObj;

		final IReportContext reportContext = (IReportContext) chartScriptCtx
				.getExternalContext().getObject();

		@SuppressWarnings("unchecked")
		final List<SharedStyleHandle> lstStyles = reportContext
				.getDesignHandle().getAllStyles();
		File resourceFolder = null;
		try {
			final String resFolderName = reportContext.getReportRunnable()
					.getReportEngine().getConfig().getResourcePath();
			if (resFolderName != null) {
				resourceFolder = new File(resFolderName);
			}
		} catch (Exception e) {
			throw new BirtException(InnoventFunctionFactory.plugin_id,
					"Unable to find RESOURCE HOME in " + className,
					new Object[] { "" });
		}

		final SharedStyleHandle styleHdl = getSeriesStyle(series, lstStyles);
		final Palette seriesPalette = getSeriesPalette(resourceFolder, styleHdl);
		SeriesDefinitionImpl seriesDefn = (SeriesDefinitionImpl) series
				.eContainer();

		// The palette is the fill for the series
		if (seriesPalette != null && seriesDefn != null) {
			seriesDefn.setSeriesPalette(seriesPalette);
		}

		// Also handle fonts for series
		Label seriesLabel = series.getLabel();
		if (seriesLabel != null && seriesLabel.isVisible()) {
			setSeriesFont(seriesLabel.getCaption(), styleHdl);
		}

		return "SUCCESS";
	}

	private void setSeriesFont(Text curText, SharedStyleHandle styleHdl) {
		// curText is a reference to original
		ColorDefinition cd = curText.getColor();
		String cssColor = styleHdl.getColor().getDisplayValue();
		if (cssColor != null && cssColor.length() > 0
				&& cssColor.startsWith("RGB(")) {
			// Expecting a string in form RGB(red, green, blue)
			cssColor = cssColor.substring(4, cssColor.length() - 1);
			String[] cS = cssColor.split(",");
			cd.set(Integer.valueOf(cS[0]), Integer.valueOf(cS[1]), Integer
					.valueOf(cS[2]));
		}

		FontDefinition fontDef = curText.getFont();

		if (styleHdl.getFontFamilyHandle() != null) {
			fontDef.setName(styleHdl.getFontFamilyHandle().getStringValue());
		}
		if (styleHdl.getFontSize() != null) {
			DimensionHandle cssSize = styleHdl.getFontSize();
			if ("px".equalsIgnoreCase(cssSize.getAbsoluteValue().getUnits())) {
				fontDef
						.setSize((float) cssSize.getAbsoluteValue()
								.getMeasure());
			}
		}
		String cssStyle = styleHdl.getFontStyle();
		if (cssStyle.equals(DesignChoiceConstants.FONT_STYLE_ITALIC)) {
			fontDef.setItalic(true);
		}
		if (cssStyle.equals(DesignChoiceConstants.FONT_STYLE_NORMAL)) {
			fontDef.setBold(false);
			fontDef.setItalic(false);
		}
		String cssWeight = styleHdl.getFontWeight();
		if (cssWeight.equalsIgnoreCase(DesignChoiceConstants.FONT_WEIGHT_BOLD)
				|| cssWeight
						.equalsIgnoreCase(DesignChoiceConstants.FONT_WEIGHT_BOLDER)) {
			fontDef.setBold(true);
		}

	}

	private SharedStyleHandle getSeriesStyle(Series series,
			List<SharedStyleHandle> lstStyles) {
		String seriesId = (String) series.getSeriesIdentifier();
		if (seriesId.indexOf("=") > 0) {
			// The seriesId has been externalized. 
			// Series in form resource.bundle.name=Label
			// need to get value
			seriesId = seriesId.substring(0, seriesId.indexOf("="));
		}
		if (seriesId == null || seriesId.trim().length() == 0) {
			return null;
		}

		seriesId = seriesId.replace(' ', '-').toLowerCase();

		for (SharedStyleHandle styleHandle : lstStyles) {
			if (seriesId.equals(styleHandle.getName())) {
				return styleHandle;
			}
		}

		// did not find style
		return null;
	}

	private Palette getSeriesPalette(File resourceFolder,
			SharedStyleHandle styleHandle) {
		if (styleHandle == null)
			return null;

		Fill fill = null;
		String imageFileName = styleHandle.getBackgroundImage();
		if (imageFileName != null) {
			File imageFile = null;
			if (resourceFolder == null) {
				imageFile = new File(imageFileName);
			} else {
				imageFile = new File(resourceFolder, imageFileName);
			}

			if (imageFile.exists()) {
				// try to use a background image if it exists
				fill = ImageImpl.create(imageFile.toURI().toString());
			}
		}
		if (fill == null) {
			/* No background image use color based image */
			String colorString = styleHandle.getBackgroundColor()
					.getDisplayValue();
			if (colorString != null && colorString.length() > 0
					&& colorString.startsWith("RGB(")) {
				// Expecting a string in form RGB(red, green, blue)
				colorString = colorString
						.substring(4, colorString.length() - 1);
				String[] cS = colorString.split(",");
				fill = ColorDefinitionImpl.create(Integer.valueOf(cS[0]),
						Integer.valueOf(cS[1]), Integer.valueOf(cS[2]));
			}
		}
		if (fill == null)
			return null;

		Palette newPalette = PaletteImpl.create(fill);
		return newPalette;
	}

}
