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

/**
 * This class contains the tokens of a legal statement, as parsed by the
 * <code>StatementIterator</code>
 * and to be executed by the <code>PricerClient</code> application
 * 
 * @author mplindsay
 * @version 1.0
 *
 *@see PricerClient
 *@see StatementIterator
 */
public class Statement
{
	public enum Type
	{
		load,
		price,
		list,
		sizes,
		clear,
		help,
		exit,
		error
	}
	
	public Statement (
		Type							theType
		)
	{
		type = theType;
	}
	
	public Statement (
		Type							theType,
		String							theCustomerId,
		String							theProductId,
		int								theQuantity
		)
	{
		type = theType;
		customerId = theCustomerId;
		productId = theProductId;
		quantity = theQuantity;
	}
	
	public Statement (
			Type							theType,
			String							theMap,
			String							theKey
			)
		{
			type = theType;
			map = theMap;
			key = theKey;
		}
	
	public Type							type;
	
	public String						customerId;
	public String						productId;
	public int							quantity;
	
	public String						map;
	public String						key;

}
