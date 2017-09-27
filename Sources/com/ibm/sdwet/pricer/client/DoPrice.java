//
//	This sample program is provided AS IS and may be used, executed, copied and
//	modified without royalty payment by customer (a) for its own instruction and 
//	study, (b) in order to develop applications designed to run with an IBM 
//	WebSphere product, either for customer's own internal use or for redistribution 
//	by customer, as part of such an application, in customer's own products. "
//
//	(C) COPYRIGHT International Business Machines Corp. 2017
//	All Rights Reserved * Licensed Materials - Property of IBM
//

package com.ibm.sdwet.pricer.client;

import java.util.Map;
import java.util.Vector;

import com.ibm.sdwet.pricer.agent.BestPriceAgent;
import com.ibm.sdwet.pricer.agent.BestPriceKey;
import com.ibm.sdwet.pricer.object.BestPrice;
import com.ibm.sdwet.wxs.client.Grid;
import com.ibm.sdwet.wxs.client.GridException;
import com.ibm.websphere.objectgrid.datagrid.AgentManager;

/**
 * This command handling class will compute a <code>BestPrice</code> for the
 * <code>Customer</code> and <code>Product</code> named by the passed <code>Statement</code>.
 *
 * @author mplindsay
 * @version 1.0
 * 
 * @see com.ibm.sdwet.pricer.object.BestPrice
 * @see com.ibm.sdwet.pricer.agent.BestPriceAgent
 * @see com.ibm.sdwet.wxs.client.Grid
 */

public class DoPrice
{
	/**
	 * The method will construct a <code>BestPriceAgent</code> and hand it
	 * to <code>AgentManager.callMapAgent(...)</code> to invoke the agent on
	 * the grid.
	 * <P>
	 * We can fetch the manager through any map of the mapset containing our
	 * data. In fact we will use the <code>Customer</code> map.
	 * <P>
	 * The Map returned by the <code>callAgentManager</code> should contain
	 * a single <code>BestPrice</code> but may contain error results or other
	 * debris. The method will inspect the Map and display to successfully
	 * computed price or the error message.
	 * 
	 * @param theStatement	provides the <code>customerId</code> and
	 * 						<code>materialId</code> and <code>quantity</code> for
	 * 						which the caller wants a best price.
	 */
	public static void please (
		Statement						theStatement
		)
	{
		try
		{			
			BestPriceAgent agent = new BestPriceAgent (
										theStatement.customerId,
										theStatement.productId,
										theStatement.quantity
										);

			Vector<BestPriceKey> keys = new Vector<BestPriceKey> ();
			keys.add(agent.getBestPriceKey());
			
			System.out.println ("go fetch " + theStatement.customerId + "'s best price " 
					+ " for " + theStatement.quantity + " " + theStatement.productId + "(s)"
					);
			
			AgentManager am = Grid.getMap("Customer").getAgentManager();
			
			@SuppressWarnings("unchecked")
			Map<BestPriceKey, BestPrice> responseMap
											= (Map<BestPriceKey, BestPrice>) (am.callMapAgent(agent, keys));
			
			for (BestPrice best: responseMap.values())
			{
				System.out.printf (
					"The list price for the " + theStatement.productId + " is $%,.2f%n", best.listPrice
					);
				System.out.printf (
					"The %d%% discounted price is $%,.2f (%s)%n",
						best.discount, best.discountedPrice, best.code
					);
			}
		}
		catch (GridException ge)
		{
			// no action required
		}
		catch (Exception e)
		{
			new GridException ("Could not compute best price", e);
		}
	}
}
