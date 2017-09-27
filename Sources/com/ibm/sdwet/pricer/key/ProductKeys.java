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

import com.ibm.sdwet.pricer.object.Product;


/**
 * This implementation of the Iterable class will generate a list of
 * <code>ProductKeys</code> for the <code>Product</code> passed as an argument
 * to the constructor.
 *
 * The class will be used as:
 * <pre>
 * {@code
 *	ProductKeys keys = new ProductKeys (product);
 *	for (ProductKey key: keys) . . . ;
 *}
 * </pre>
 * 
 * @see Product
 * @see ProductKey
 * 
 */
public class ProductKeys implements Iterable <ProductKey>
{

	private Product						product;

		
	@SuppressWarnings("unused")
	private ProductKeys ()
	{
		// don't let anyone do this
	}
		
	/**
	 * This constructor will copy theProduct into a private variable to be used to
	 * to create an iterator object to be returned by the iterator() method
	 * 
	 * @param theProduct - the <code>Product</code>for which we will generate
	 * a de-normalized list of keys
	 */	
	
	public ProductKeys (
		Product						theProduct
		)
	{
		product = theProduct;
	}
	

	/**
	 * 
	 * The private nested ProductIterator class implements a <code>ProductKey</code> typed
	 * <code>java.util.Iterator</code> to produce de-normalized <code>Product</code> keys
	 */
	
	private class ProductIterator implements Iterator<ProductKey> {
		
		private int							hashCode;
		private int							partitionBase;
		private int							row = 0;
		
		
		private ProductIterator()
		{
			hashCode = ProductKeys.this.product.id.hashCode();
			partitionBase = GridGeometry.mod(hashCode, GridGeometry.columns);
		}

		/**
		 * Are there more ProductKeys in this column?
		 * 
		 * @return true of there are more <code>ProductKey</code> objects to be had, otherwise false.
		 */
		@Override
		public boolean hasNext()
		{
			return row < GridGeometry.rows;
		}

		/**
		 * Generate and return the next <code>ProductKey</code> and move on to the next, if there is one.
		 * 
		 * @return the next available <code>ProdcutKey</code> of the row, otherwise null
		 */
		@Override
		public ProductKey next()
		{
			if (!hasNext ()) return null;
			
			return new ProductKey (product, partitionBase + (GridGeometry.columns * row++));
			
		}

		/**
		 * an unimplemented function
		 */
		@Override
		public void remove()
		{
			// no one should do this. should we throw an error instead
			System.out.println (
				"unexpected ProductKeys.iterator().remove() call. you know this doesn't do anything, don't you?"
				);
		}

	}

		
	/**
	 *  Return an instance of the private inner class <code>ProductIterator</code>
	 *  which will construct itself from our private product member, ready to
	 *  generate a new set of de-normalized <code>ProductKey</code> objects
	 *  for that product
	 * 
	 *  @return a <code>ProductKey</code> typed <code>java.util.Iterator</code>
	 *  for this classes <code>Product</code> object
	 */
	@Override
	public Iterator<ProductKey> iterator()
	{
			return new ProductIterator ();
	}
}
