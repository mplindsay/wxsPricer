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

package com.ibm.sdwet.pricer.key;

import java.io.Serializable;

import com.ibm.sdwet.pricer.object.Offer;

/**
 * The class allows us to specify a <code>ProductKey</code> which identifies a partition
 * where this of <code>Offer</code> must be co-located
 */

public class OfferKey
	extends PricerKeyBase implements Serializable
{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 * @param theOffer will provide the keys <code>hashCode()</code> value
	 * @param theProductKey will provide this keys <code>ibmGetPartition()</code> value
	 */

	public OfferKey (
		Offer							theOffer,
		ProductKey						theProductKey
		)
	{
		super (
			theOffer.id,
			(Integer) theProductKey.ibmGetPartition()
			);
	}
	

	@Override
	public String toString()
	{
		return super.toString();
	}
	
}
