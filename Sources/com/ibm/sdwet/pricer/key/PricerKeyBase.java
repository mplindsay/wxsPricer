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

import com.ibm.websphere.objectgrid.plugins.PartitionableKey;

/**
 * This class implements the bit of common code we will use in all the derived keys
 * to manage the hashCode and partition numbers we will be managing
 */


public class PricerKeyBase implements PartitionableKey {

	private int							hashCode;
	private Integer						partition;
	
	/**
	 * The null constructor generates a key. No one should do this.
	 */
	protected PricerKeyBase ()
	{
		hashCode = 0;
		partition = null;
	}
	
	/**
	 * The derived classes will be compute a hashCode and partition id.
	 * This base class will keep hang on to them and provide them to the WXS as needed.
	 * 
	 * @param theHashCode is the int value to be returned as this keys <code>hashCode()</code> value
	 * @param thePartition is the Integer value to be returned as this keys <code>ibmGetPartition()</code> value
	 */
	protected PricerKeyBase (
		int								theHashCode,
		Integer							thePartition
		)
	{
		hashCode = theHashCode;
		partition = thePartition;
	}

	
	@Override
	/**
	 * WXS needs our int hashCode
	 */
	public int hashCode (){
		return hashCode;
	}
	
	@Override
	/**
	 * WXS needs the Object which hashCode() is the partition number
	 */
	public Object ibmGetPartition() {
		return partition;
	}

}
