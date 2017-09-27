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

/**
 * This class keeps the configuration of our grid, the count of rows and
 * columns, and properly computes the unsigned partition number from
 * signed java integers.
 * <P>
 * The row and column counts of the grid are asserted here, not computed.
 * The application then has a dependency on the correct count of partitions,
 * that is rows * columns partitions, being asserted in the 
 * <code>ObjectGridDeployment</code> file.
 * 
 * @author mplindsay
 * @version 1.0
 *
 * @see <a
 * 		href="http://www.ibm.com/support/knowledgecenter/SSTVLU_8.6.0/com.ibm.websphere.extremescale.doc/rxsdplcyref.html?lang=en"
 * 		>
 * 		<code>ObjectGridDeployment</code>
 * 		</a>
 */
public class GridGeometry
{
	public static final int partitions = 6;
	public static final int rows = 2;
	public static final int columns = 3;

	public static int mod (
		int				theDividend,
		int				theDivisor
		)
	{
		return (int) Math.abs(theDividend % theDivisor);
	}

}
