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
 *  Steve Schafer - Innovent Solutions
 * 				 
 */
package innovent.birt.aggregations.factory;

import innovent.birt.aggregations.ConcatenateUnique;
import innovent.birt.aggregations.CountUnique;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.birt.data.engine.api.aggregation.IAggrFunction;
import org.eclipse.birt.data.engine.api.aggregation.IAggregationFactory;

/**
 * Create a list of all available aggregates and return to application
 * 
 * @author Scott Rosenbaum
 *
 */
public class InnoventAggregateFactory implements IAggregationFactory {

	private Map<String, IAggrFunction> aggrMap = new HashMap<String, IAggrFunction>();
	private static final Logger logger = Logger
			.getLogger(InnoventAggregateFactory.class.getName());

	private void populateAggregations() {

		final ConcatenateUnique concatenateUnique = new ConcatenateUnique();
		aggrMap.put(concatenateUnique.getName().toUpperCase(),
				concatenateUnique);

		final CountUnique countUnique = new CountUnique();
		aggrMap.put(countUnique.getName().toUpperCase(), countUnique);
	}

	public InnoventAggregateFactory() {
		populateAggregations();
	}

	public List<IAggrFunction> getAggregations() {
		return new ArrayList<IAggrFunction>(aggrMap.values());
	}

	public IAggrFunction getAggregation(String name) {
		IAggrFunction func = aggrMap.get(name.toUpperCase());
		if (func == null) {
			logger.warning(name + " is not a registered Aggregation");
		}
		return func;
	}
}
