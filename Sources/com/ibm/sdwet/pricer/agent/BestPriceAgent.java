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

package com.ibm.sdwet.pricer.agent;

import java.util.Iterator;
import java.util.Map;

import com.ibm.sdwet.pricer.object.BestPrice;
import com.ibm.sdwet.pricer.object.Coupon;
import com.ibm.sdwet.pricer.object.Offer;
import com.ibm.sdwet.pricer.object.Rank;
import com.ibm.websphere.objectgrid.ObjectMap;
import com.ibm.websphere.objectgrid.Session;
import com.ibm.websphere.objectgrid.datagrid.MapGridAgent;
import com.ibm.websphere.objectgrid.query.NoResultException;
import com.ibm.websphere.objectgrid.query.ObjectQuery;

/***
 * The BestClassAgent will compute a customer's best price for a product
 * on a remote ObjectGrid.
 * <P>
 * At the client it will wrap up the
 * input terms for the best price computation, the <code>customerId</code>,
 * the <code>materialId</code> and the <code>quantity</code>. It will return
 * a computed <code>BestPrice</code>.
 * <P>
 * On the remote ObjectGrid the <code>process (...)</code> method will SELECT
 * the Customer and Product objects required for the computation,
 * will determine the best discount between them, will compute a discounted price, then
 * return the <code>BestPrice</code> to the client.
 * <P>
 * This class has a number of dependencies on the ObjectGrid xml.
 * That file must specify that the Customer and Material maps are filled with
 * <code>com.ibm.sdwet.pricer.object.Customer</code> and
 * <code>com.ibm.sdwet.pricer.object.Material</code> objects respectively.
 * It must also specify that <code>customerId</code> and <code>materialID</code>
 * fields are alternate keys to these tables and
 * <code>com.ibm.websphere.objectgrid.plugins.index.HashIndex</code> must
 * be plugged in.
 *  
 * @author mplindsay
 * @version 1.0
 * 
 * @see com.ibm.sdwet.pricer.object.BestPrice
 * @see com.ibm.sdwet.pricer.agent.BestPriceKey
 * @see com.ibm.sdwet.pricer.client.DoPrice
 * @see <a
 * 		href="http://www.ibm.com/support/knowledgecenter/SSTVLU_8.6.0/com.ibm.websphere.extremescale.javadoc.doc/topics/com/ibm/websphere/objectgrid/datagrid/AgentManager.html?lang=en"
 * 		>
 * 		<code>AgentManager API</code>
 * 		</a>
 * @see <a
 * 		href="http://www.ibm.com/support/knowledgecenter/SSTVLU_8.6.0/com.ibm.websphere.extremescale.doc/cxsobjquery.html?lang=en"
 * 		>
 * 		Using the ObjectQuery API
 * 		</a>
  * @see <a
 * 		href="http://www.ibm.com/support/knowledgecenter/SSTVLU_8.6.0/com.ibm.websphere.extremescale.doc/rxsquerylang.html?lang=en"
 * 		>
 * 		Reference for eXtremeScale Queries
 * 		</a>
 *
 */
public class BestPriceAgent
	implements MapGridAgent
{
	private static final long serialVersionUID = 1L;
	
	private String						customerId;
	private String						productId;
	private int							quantity;
	
	public BestPriceAgent (
		String							theCustomerId,
		String							theProductId,
		int								theQuantity
		)
	{
		customerId = theCustomerId;
		productId = theProductId;
		quantity = theQuantity;
	}
	
	/**
	 * This method will construct the <code>BestPriceKey</code> naming the partition
	 * where objects named by each of <code>customerId</code> and <code>materialId</code>
	 * will be found. This key will be passed to the <code>callMapGrid (...)</code>
	 * method.
	 * 
	 * @return BestPriceKey 
	 */
	
	public BestPriceKey getBestPriceKey ()
	{
		return new BestPriceKey (customerId, productId);
	}

	
	/***
	 * This method will SELECT the <code>customerId</code> and <code>materialId</code>
	 * objects from the Customer and Material maps and compute a <code>BestPrice</code>.
	 * 
	 * @return a BestPrice to be returned to the client in a <code>java.util.Map</code>
	 */
	@Override
	public Object process(
		Session							theSession,
		ObjectMap						theMap,
		Object							theKey
		)
	{
		
		//
		// get the customer's rank, GOLD or SILVER or BRONZE, and the product's
		// list price
		//

		String selectCoupon
		= "SELECT c.rank, p.listPrice "
			+ "FROM Customer c, Product p "
			+ "WHERE c.id = '" + customerId + "' "
				+ "AND p.id = '" + productId + "'";
		

		Rank							customerRank;
		float							listPrice;

		try {
			
			ObjectQuery query = theSession.createObjectQuery (selectCoupon);
			Object[] singleResult = (Object[]) query.getSingleResult();

			customerRank = (Rank) singleResult[0];
			listPrice = ((Float) singleResult[1]).floatValue();
		}
		catch (Exception e) {
			System.out.println (
				"Could not get customerRank or listPrice. See System.err"
				);
			System.err.println (
				"Could not get customerRank or listPrice because: " + e
				);
			e.printStackTrace(System.err);
			
			throw new RuntimeException (
					"Could not get customerRank or listPrice because: " + e
					);
		}
		
		Coupon coupon = getCoupons (theSession, customerId, productId);
		Offer offer = getOffer (theSession, productId, customerRank);
		
		if (coupon == null && offer == null)
			return new BestPrice (listPrice, quantity);
		
		String							code;
		int								discount;

		if (coupon != null && offer != null)
		{
			code = coupon.id;
			discount = coupon.discount;
			if (offer.discount > coupon.discount)
			{
				code = offer.id;
				discount = offer.discount;
			}
		}
		if (coupon != null)
		{
			code = coupon.id;
			discount = coupon.discount;
			
		}
		else
		{
			code = offer.id;
			discount = offer.discount;
		}
		
		return new BestPrice (listPrice, discount, code, quantity);
	}
	

	/**
	 * The method returns a iterator that will walk through the list of coupons
	 * discovered for the identified customer and product. There will one or none
	 * 
	 * @param theSession is a WXS <code>Session</code> our <code>process</code> was passed
	 * and which we will use to get an object query from
	 * @param theCustomerId names the <code>Customer</code> for which we need <code>Coupons</code>
	 * @param theProductId names the <code>Product</code> for which we need <code>Coupons</code>
	 * @return the single <code>Coupon</code> we may have found
	 */
	
	private Coupon getCoupons (
		Session							theSession,
		String							theCustomerId,
		String							theProductId
		)
	{
		String selectCoupon
		= "SELECT c.id as code, c.discount as discount "
			+ "FROM Coupon c "
			+ "WHERE c.customerId = '" + theCustomerId + "' "
				+ "AND c.productId = '" + theProductId + "'";
		
		try
		{
			System.out.println (
					"At partition "
			          + theSession.getObjectGrid().getMap("Coupon").getPartitionId() +
					" " + selectCoupon);

			ObjectQuery query = theSession.createObjectQuery (selectCoupon);
			Object[] singleResult = (Object[]) query.getSingleResult();

			return new Coupon (
				(String) singleResult[0],
				theCustomerId,
				theProductId,
				((Integer) singleResult[1]).intValue()
				);
		}
		catch(NoResultException nre)
		{
			System.out.println ("Could not get Coupon because: " + nre);
			System.err.println ("Could not get Coupon because: " + nre);
			nre.printStackTrace(System.err);
			return null;			
		}
		catch (Throwable t)
		{
			System.out.println ("Could not get Coupon because: " + t);
			System.err.println ("Could not get Coupon because: " + t);
			t.printStackTrace(System.err);
			return null;			
		}

	}
	

	/**
	 * The method returns a iterator that will walk through the list of offers
	 * discovered for the identified product and customer's rank. There should
	 * be one
	 * 
	 * @param theSession is a WXS <code>Session</code> our <code>process</code> was passed
	 * and which we will use to get an object query from
	 * @param theProductId names the <code>Product</code> for which we need <code>Coupons</code>
	 * @param theRank is the <code>Customer.rank</code> for which we need an <code>Offer</code>
	 * @return the <code>Offer</code> which matches our <code>Rank</code> we may have found
	 */
	
	private Offer getOffer (
		Session							theSession,
		String							theProductId,
		Rank							theRank
		)
	{
		String selectOffer 
		= "SELECT o.id as code, o.discount as discount, o.rank as rank "
			+ "FROM Offer o "
			+ "WHERE o.productId = '" + productId + "' " ;
				//  + "AND o.rank = " + theRank;

		ObjectQuery query = theSession.createObjectQuery (selectOffer);
		@SuppressWarnings("unchecked")
		Iterator<Object[]> results = query.getResultIterator();
		
		while (results.hasNext())
		{
			Object[] fields = (Object[]) results.next();
			
			// why are we searching for the properly ranked
			// offer rather than selecting it? because WXS
			// can only select for familiar Java classes.
			// our Rank enumeration is not one of those
			
			if (((Rank) fields[2]) == theRank)
			{
				return new Offer (
						(String) fields[0],
						theProductId,
						theRank,
						((Integer) fields[1]).intValue()
						);
			}
		}
		
		return null;
	}
	

	/**
	 * This method is not implemented
	 * 
	 * @return null
	 */
	@Override
	public Map<Object, Object> processAllEntries(
		Session							theSession,
		ObjectMap						theMap
		)
	{
		return null;
	}

}
