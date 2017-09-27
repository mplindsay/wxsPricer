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

import com.ibm.sdwet.pricer.key.GridGeometry;
import com.ibm.websphere.objectgrid.plugins.PartitionableKey;

/**
 * The <code>BestPriceKey</code> will direct a <code>BestPriceAgent</code>
 * to the unique partition of the remote object grid.
 * <P>
 * The class has no public constructor.
 * The <code>BestPriceAgent</code> <code>getKey()</code> method will construct it.
 * 
 * @author mplindsay
 * @version 1.0
 *
 * @see GridGeometry
 */
public class BestPriceKey
	implements PartitionableKey
{
	private int							partition;

	/**
	 * The method will compute a column for <code>theCustomerId</code>
	 * and a row for <code>theMaterialId</code>, and compute a partition
	 * number for [column, row] based on the geometry defined by the
	 * <code>GridGeometry</code> class.
	 * 
	 * @param theCustomerId is the <code>String</code> used to name the Customer in the cache
	 * @param theProductId is the <code>String</code> used to name the Product in the cache
	 */
	public BestPriceKey (
		String							theCustomerId,
		String	 						theProductId
		)
	{
		int column= GridGeometry.mod (
				theCustomerId.hashCode(), GridGeometry.columns
				);

		int row = GridGeometry.mod (
				theProductId.hashCode(), GridGeometry.rows
				);

		int p = (row * GridGeometry.columns) + column;

		partition = new Integer (p);
	}

	/**
	 * The method will return the <code>Integer</code> object which will
	 * steer the WXS runtime to the partition of our choosing.
	 * 
	 */
	
	@Override
	public Object ibmGetPartition ()
	{
		return new Integer (partition);
	}

}
