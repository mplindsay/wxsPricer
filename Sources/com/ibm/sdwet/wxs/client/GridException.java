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

package com.ibm.sdwet.wxs.client;

public class GridException
	extends Exception
{
	private static final long serialVersionUID = 1L;

	public GridException(
		String							theMessage,
		Throwable						theCause
		)
	{
		super(theMessage + " because " + theCause);
		System.err.println (theMessage + " because " + theCause);
		theCause.printStackTrace(System.err);
	}

	public GridException(String theMessage)
	{
		super(theMessage);
		System.err.println (theMessage);
	}

	
}
