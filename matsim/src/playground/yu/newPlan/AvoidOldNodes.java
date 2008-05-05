/* *********************************************************************** *
 * project: org.matsim.*
 * AvoidOldNodes.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

/**
 * 
 */
package playground.yu.newPlan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.matsim.basic.v01.BasicLeg;
import org.matsim.basic.v01.BasicPlanImpl.LegIterator;
import org.matsim.config.Config;
import org.matsim.gbl.Gbl;
import org.matsim.network.Link;
import org.matsim.network.MatsimNetworkReader;
import org.matsim.network.NetworkLayer;
import org.matsim.network.Node;
import org.matsim.plans.MatsimPlansReader;
import org.matsim.plans.Person;
import org.matsim.plans.Plan;
import org.matsim.plans.Plans;
import org.matsim.world.World;

/**
 * @author yu
 * 
 */
public class AvoidOldNodes extends NewPlan {
	private boolean nullRoute = false;
	private Set<String> nodeIds;

	/**
	 * 
	 */
	public AvoidOldNodes(NetworkLayer network, Plans plans) {
		super(network, plans);
		nodeIds = new HashSet<String>();
	}

	public void addNode(String nodeId) {
		nodeIds.add(nodeId);
	}

	public void addLink(String linkId) {
		Link l = net.getLink(linkId);
		nodeIds.add(l.getFromNode().getId().toString());
		nodeIds.add(l.getToNode().getId().toString());
	}

	@Override
	public void run(Person person) {
		for (Plan p : person.getPlans()) {
			for (LegIterator i = p.getIteratorLeg(); i.hasNext();) {
				BasicLeg bl = i.next();
				tag: for (final Node n : (ArrayList<Node>) (bl.getRoute()
						.getRoute())) {
					final String nId = n.getId().toString();
					for (String nodeId : nodeIds) {
						if (nId.equals(nodeId)) {
							nullRoute = true;
							break tag;
						}
					}
				}

				if (nullRoute)
					bl.setRoute(null);
				nullRoute = false;
			}
		}
		this.pw.writePerson(person);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		World world = Gbl.getWorld();
		Config config = Gbl.createConfig(args);

		NetworkLayer network = new NetworkLayer();
		new MatsimNetworkReader(network).readFile(config.network()
				.getInputFile());
		world.setNetworkLayer(network);

		Plans population = new Plans();
		AvoidOldNodes aon = new AvoidOldNodes(network, population);
		aon.addNode("100000");
		aon.addLink("3000000");
		aon.addLink("3000001");
		for (int i = 3000022; i <= 3000025; i++) {
			aon.addLink(Integer.toString(i));
		}
		population.addAlgorithm(aon);
		new MatsimPlansReader(population).readFile(config.plans()
				.getInputFile());
		world.setPopulation(population);
		population.runAlgorithms();
		aon.writeEndPlans();
	}

}
