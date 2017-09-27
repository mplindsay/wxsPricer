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

package com.ibm.sdwet.pricer.object;

/**
 * This class will represents the <code>listPrice</code> for a product and
 * the Customer's best <code>discount</code> and <code>discountPrice</code>.
 * It will be returned from the Grid by the BestPriceAgent.
 * 
 * @author mplindsay
 * @version 1.0
 * 
 * @see com.ibm.sdwet.pricer.agent.BestPriceAgent
 *
 */
public class BestPrice
{
	public float						listPrice;
	public float						totalPrice;
	public int							discount;
	public float						discountedPrice;
	public String						code;
	
	@SuppressWarnings("unused")
	private BestPrice ()
	{
		// don't let anyone do this
	}
	
	public BestPrice (
		float							theListPrice,
		int								theDiscount,
		String							theCode,
		float							theQuantity
		)
	{
		listPrice = theListPrice;
		totalPrice = listPrice * theQuantity;
		discount = theDiscount;
		discountedPrice = totalPrice * ((100 - discount) / 100.00f);
		code = theCode;
	}
	
	public BestPrice (
		float							theListPrice,
		int								theQuantity
		)
	{
		listPrice = theListPrice;
		totalPrice = listPrice * theQuantity;
		discount = 0;
		discountedPrice = totalPrice;
		code = "NoneSuch";
	}
}
