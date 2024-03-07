/**
 * Copyright (c) 2008-Present  Innovent Solutions, Inc.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Scott Rosenbaum - Innovent Solutions
 *  Steve Schafer - Innovent Solutions
 *
 */
package innovent.birt.aggregations.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.birt.data.engine.api.aggregation.IAggrFunction;
import org.eclipse.birt.data.engine.api.aggregation.IAggregationFactory;

import innovent.birt.aggregations.ConcatenateUnique;
import innovent.birt.aggregations.CountUnique;
import innovent.birt.aggregations.DecimalAve;
import innovent.birt.aggregations.DecimalFirst;
import innovent.birt.aggregations.DecimalLast;
import innovent.birt.aggregations.DecimalMax;
import innovent.birt.aggregations.DecimalMedian;
import innovent.birt.aggregations.DecimalMin;
import innovent.birt.aggregations.DecimalMode;
import innovent.birt.aggregations.DecimalMovingAve;
import innovent.birt.aggregations.DecimalNpv;
import innovent.birt.aggregations.DecimalPercentRank;
import innovent.birt.aggregations.DecimalPercentile;
import innovent.birt.aggregations.DecimalQuartile;
import innovent.birt.aggregations.DecimalRunningSum;
import innovent.birt.aggregations.DecimalSum;
import innovent.birt.aggregations.DecimalVariance;
import innovent.birt.aggregations.DecimalWeightedAve;

/**
 * Create a list of all available aggregates and return to application
 *
 * @author Scott Rosenbaum
 *
 */
public class InnoventAggregateFactory implements IAggregationFactory {
	private final Map<String, IAggrFunction> aggrMap = new HashMap<>();
	private static final Logger logger = Logger.getLogger(InnoventAggregateFactory.class.getName());

	private void populateAggregation(final IAggrFunction function) {
		aggrMap.put(function.getName().toUpperCase(), function);
	}

	private void populateAggregations() {
		populateAggregation(new ConcatenateUnique());
		populateAggregation(new CountUnique());
		populateAggregation(new DecimalAve());
		populateAggregation(new DecimalFirst());
		populateAggregation(new DecimalLast());
		populateAggregation(new DecimalMax());
		populateAggregation(new DecimalMedian());
		populateAggregation(new DecimalMin());
		populateAggregation(new DecimalMode());
		populateAggregation(new DecimalMovingAve());
		populateAggregation(new DecimalNpv());
		populateAggregation(new DecimalPercentile());
		populateAggregation(new DecimalPercentRank());
		populateAggregation(new DecimalQuartile());
		populateAggregation(new DecimalRunningSum());
		populateAggregation(new DecimalSum());
		populateAggregation(new DecimalVariance());
		populateAggregation(new DecimalWeightedAve());
	}

	public InnoventAggregateFactory() {
		populateAggregations();
	}

	@Override
	public List<IAggrFunction> getAggregations() {
		return new ArrayList<>(aggrMap.values());
	}

	@Override
	public IAggrFunction getAggregation(final String name) {
		final var func = aggrMap.get(name.toUpperCase());
		if (func == null) {
			logger.warning(name + " is not a registered Aggregation");
		}
		return func;
	}
}
