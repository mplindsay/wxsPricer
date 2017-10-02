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

import com.ibm.sdwet.pricer.object.Product;

/**
 * The class allows us to specify a partition key of our choosing to the WXS runtime,
 * so that we can copy a <code>Product</code> object to multiple partitions. The
 * <code>Product</code> object will be indexed at the partition by the
 * <code>productId</code>.
 * <p>
 * The range of partitions will be computed by the <code>ProductKeys</code> class.
 * 
 * @see Product
 * @see ProductKeys
 */

public class ProductKey
	extends PricerKeyBase implements Serializable
{

	private static final long serialVersionUID = 1L;

	/**
	 * The constructor will build a key from
	 * the <code>Product.id</code>'s <code>hashCode()</code> value and
	 * <code>thePartition</code> value
	 * 
	 * @param theProduct provides the <code>String id</code> from which our <code>hashCode()</code>
	 * will be derived
	 * @param thePartition is the int value from which an <code>Integer</code> will be constructed
	 * to represent our partition value
	 */
	public ProductKey (
		Product			theProduct,
		int				thePartition
		)
	{
		super (
			theProduct.id,
			new Integer(thePartition)
			);
	}


	@Override
	public String toString()
	{
		return super.toString();
	}

}
