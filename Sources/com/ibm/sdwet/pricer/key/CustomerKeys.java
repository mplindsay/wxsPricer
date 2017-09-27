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

import java.util.Iterator;
import com.ibm.sdwet.pricer.object.Customer;

/**
 * This implementation of the Iterable class will generate a list of
 * <code>CustomerKeys</code> for the <code>Customer</code> passed as an argument
 * to the constructor.
 *
 * The class will be used as:
 * <pre>
 * {@code
 *	CustomerKeys keys = new CustomerKeys (customer);
 *	for (CustomerKey key: keys) . . . ;
 * }
 * </pre>
 * 
 * @see Customer
 * @see CustomerKey
 */


public class CustomerKeys implements Iterable<CustomerKey> {

	private Customer					customer;

	
	@SuppressWarnings("unused")
	private CustomerKeys ()
	{
		// don't let anyone do this
	}

	/**
	 * This constructor will copy theCustomer into a private variable to be used to
	 * to create an iterator object to be returned by the iterator() method
	 * 
	 * @param theCustomer is the Customer for which we will generate a list of keys
	 */	
	
	public CustomerKeys (
		Customer						theCustomer
		)
	{
		customer = theCustomer;
	}
	

	/**
	 * 
	 * The private nested CustomerIterator class implements a <code>CustomerKey</code> typed
	 * <code>java.util.Iterator</code> to produce de-normalized <code>Customer</code> keys
	 */
	
	private class CustomerIterator implements Iterator<CustomerKey> {
		
		private int							hashCode;
		private int							partitionBase;
		private int							column = 0;
		
		
		private CustomerIterator()
		{
			hashCode = CustomerKeys.this.customer.id.hashCode();
			partitionBase = GridGeometry.mod(hashCode, GridGeometry.rows) * GridGeometry.columns;
		}

		/**
		 * Are there more CustomerKeys in this column?
		 * 
		 * @return true of there are more <code>CustomerKey</code> objects to be had, otherwise false.
		 */
		@Override
		public boolean hasNext()
		{
			return column < GridGeometry.columns;
		}

		/**
		 * Generate and return the next <code>CustomerKey</code> and move on to the next, if there is one.
		 * 
		 * @return the next available <code>CustomerKey</code> of the column, otherwise null
		 */
		@Override
		public CustomerKey next()
		{
			if (!hasNext ()) return null;
			return new CustomerKey (CustomerKeys.this.customer, partitionBase + column++);
			
		}

		/**
		 * an unimplemented function
		 */
		@Override
		public void remove()
		{
			// no one should do this. should we throw an error or something
			System.out.println (
				"unexpected CustomerKeys.iterator().remove() call. you know this doesn't do anything, don't you?"
				);
		}

	}


	/**
	 *  Return an instance of the private inner class <code>CustomerIterator</code>
	 *  which will construct itself from our private customer member, ready to
	 *  generate a new set of de-normalized <code>CustomerKey</code> objects
	 *  for this <code>Customer</code>
	 * 
	 *  @return a <code>CustomerKey</code> typed <code>java.util.Iterator</code>
	 *  for this class's member customer
	 */
	
	@Override
	public Iterator<CustomerKey> iterator()
	{
			return new CustomerIterator ();
	}
}