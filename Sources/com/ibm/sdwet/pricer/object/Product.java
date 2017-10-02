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

import java.io.Serializable;

/**
 * The class keeps the <code>materialId</code> and the <code>listPrice</code>
 * 
 * @author mplindsay
 * @version 1.0
 *
 */

public class Product
	implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public String						id;
	public float						listPrice;
	
	@SuppressWarnings("unused")
	private Product ()
	{
		// don't let anyone do this
	}
	
	public Product (
		String							theProductId,
		float							theListPrice
		)
	{
		id = theProductId;
		listPrice = theListPrice;
	}
	
	public String toString ()
	{
		return "{"+ id + "," + listPrice + "}" ;		

	}
}
