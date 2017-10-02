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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.ibm.sdwet.pricer.key.CouponKey;
import com.ibm.sdwet.pricer.key.CustomerKey;
import com.ibm.sdwet.pricer.key.CustomerKeys;
import com.ibm.sdwet.pricer.key.OfferKey;
import com.ibm.sdwet.pricer.key.ProductKey;
import com.ibm.sdwet.pricer.key.ProductKeys;
import com.ibm.sdwet.pricer.object.Coupon;
import com.ibm.sdwet.pricer.object.Customer;
import com.ibm.sdwet.pricer.object.Offer;
import com.ibm.sdwet.pricer.object.Product;
import com.ibm.sdwet.pricer.object.Rank;
import com.ibm.sdwet.wxs.client.Grid;
import com.ibm.sdwet.wxs.client.GridException;

/**
 * This command handling class will load a small list of <code>Customer</code>
 * and <code>Product</code> entries into the Grid.
 * <P>
 * The <code>Customer</code> and <code>Product</code> to described in the
 * static arrays <code>customer</code> and <code>product</code> at the top of the class.
 * <P>
 * The list is sufficiently small, six customers and six materials, that we have not
 * bothered with the better practice, sorting the puts by destination partition
 * and performing batch puts in each partition. We remind the reader this is probably
 * a good thing to do in almost any other case and move on.
 *
 * @author mplindsay
 * @version 1.0
 * 
 * @see com.ibm.sdwet.pricer.key.CustomerKeys
 * @see com.ibm.sdwet.pricer.key.ProductKeys
 * @see com.ibm.sdwet.wxs.client.Grid
 */

public class DoLoad
{
	/**
	 * A list of Account entries to be entered into the grid.
	 */
	private static Customer customers[] = {
			new Customer ("Alice", Rank.GOLD),
			new Customer ("Blain", Rank.SILVER),
			new Customer ("Cathy", Rank.BRONZE),
			new Customer ("David", Rank.GOLD),
			new Customer ("Elain", Rank.SILVER),
			new Customer ("Frank", Rank.BRONZE)
			};

	/**
	 * A list of Product entries to be entered into the grid.
	 */
	private static Product products[] = {
			//	new Product ("Autogyro",  Mode.PRIVATE,   8000.00f, 0),
			//	new Product ("Batmobile", Mode.PRIVATE, 130000.00f,  5),
			//	new Product ("Catamaran", Mode.PRIVATE,  30000.00f, 10),
			//	new Product ("Dirigible", Mode.COMMON,  600000.00f, 15),
			//	new Product ("Elevator",  Mode.COMMON,    5000.00f, 20),
			//	new Product ("Ferryboat", Mode.COMMON, 1000000.00f, 25)

			new Product ("Autogyro",     8000.00f),
			new Product ("Batmobile",  130000.00f),
			new Product ("Catamaran",   30000.00f),
			new Product ("Dirigible",  600000.00f),
			new Product ("Elevator",     5000.00f),
			new Product ("Ferryboat", 1000000.00f)
			};
		


	/**
	 * This method will oversee the put of Account and Product objects
	 * through the loadAccount and loadMaterial methods.
	 * 
	 * This method will report exceptions to the console and consume them.
	 * A failed command will not break the application.
	 * 
	 * @param theStatement is not referenced in this command handler
	 * 
	 */
	public static void please (
		Statement						theStatement
		)
	{
		try
		{
			loadCustomers ();
			loadProducts ();
		}
		catch (GridException ge)
		{
			System.out.println ("Could not load Customers and Products because " + ge);
			ge.printStackTrace();
		}

	}

	/**
	 * This method will put Customers from a literal list of customers into
	 * the grid. It will iteratively invoke the CustomerKeyIterator to perform
	 * the required partitioning and denormalization.
	 * 
	 * @throws GridException is any <code>GridException</code> returned by a <code>Grid</code> call
	 * or an encapsulation of any exception returned by a WXS operation
	 */
	private static void loadCustomers () throws GridException
	{

		for (Customer customer: customers)
		try
		{
			CustomerKeys customerKeys = new CustomerKeys(customer);
			for (CustomerKey customerKey: customerKeys)
			{
				Grid.getMap("Customer").put(customerKey, customer);

				List<Coupon> coupons = createCoupons (customer);
				Hashtable<CouponKey, Coupon> keyAndCoupon = new Hashtable<CouponKey, Coupon> ();
				
				for (Coupon coupon: coupons)
					keyAndCoupon.put(
							new CouponKey (coupon, customerKey), coupon
							);
				
				Grid.getMap("Coupon").putAll (keyAndCoupon);
			}
		}
		catch (GridException ge)
		{
			throw ge;
		}
		catch (Throwable t)
		{
			throw new GridException ("Could not put Customer", t);
		}
		
		System.out.println ("Customers loaded");
		
	}
	
	/**
	 * This method will generate three Coupons for each Customers for different
	 * Products and discounts. it will use the CustomerKeyIteratorfrom a literal list of customers into
	 * the grid. It will iteratively invoke the CustomerKeyIterator to perform
	 * the required partitioning and denormalization.
	 * 
	 */
	
	private static int cindex = 0;
	private static int cid = 0;
	
	private static List<Coupon> createCoupons (
		Customer						theCustomer
		) throws GridException
	{
		ArrayList<Coupon> coupons = new ArrayList<Coupon> ();

		coupons.add (new Coupon (
							"cp" + cid++,
							theCustomer.id,
							products[cindex].id,
							3 + cindex
							)
						);
		
		coupons.add (new Coupon (
							"cp" + cid++,
							theCustomer.id,
							products[cindex + 1].id,
							3 + cindex + 1
							)
						);

		cindex++;
		if (cindex == (customers.length - 1)) cindex = 0;
		
		return coupons;
	}
	
	/**
	 * This method will put Products from a literal list of Products into
	 * the grid. It will iteratively invoke the ProductKeyIterator to perform
	 * the required partitioning and denormalization.
	 * 
	 * @throws GridException is any <code>GridException</code> returned by a <code>Grid</code> call
	 * or an encapsulation of any exception returned by a WXS operation
	 */
	private static void loadProducts () throws GridException
	{
		for (Product product: products)
		try
		{
			ProductKeys productKeys = new ProductKeys(product);
			for (ProductKey productKey: productKeys)
			{
				Grid.getMap("Product").put(productKey, product);

				List<Offer> offers = createOffers (product);
				Hashtable<OfferKey, Offer> keyAndOffer = new Hashtable<OfferKey, Offer> ();
				
				for (Offer offer: offers)
					keyAndOffer.put(
							new OfferKey (offer, productKey), offer
							);
				
				Grid.getMap("Offer").putAll (keyAndOffer);
			}
		}
		catch (GridException ge)
		{
			throw ge;
		}
		catch (Exception e)
		{
			throw new GridException ("could not put product", e);
		}
		
		System.out.println ("Products loaded");
	}

	private static int oid = 0;
	private static int ocount = 1;
	
	/**
	 * This method will generate one to three <code>Offer</code>s
	 * for each <code>Product</code> for one or three
	 * <code>Customer.rank</code>s and discounts
	 * 
	 * @param theProduct for which we must generate <code>Offers</code>s
	 * 
	 * @return java.util.List the list of generated <code>Offer</code> objects
	 * 
	 * @throws GridException forwarding any exception encountered
	 */
	private static List<Offer> createOffers(
		Product							theProduct
		) throws GridException
	{
		ArrayList<Offer> offers = new ArrayList<Offer> ();
		
		for (int i = 0; i < ocount; i++)
		{
			Rank customerRank;
			switch (i)
			{
			case 2: customerRank = Rank.BRONZE; break;
			case 1: customerRank = Rank.SILVER; break;
			default: customerRank = Rank.GOLD; break;
			}
			
			offers.add (
				new Offer(
					"of" + oid++, theProduct.id, customerRank,
					5 - i
				)
			);			
		}

		ocount= (ocount < 3) ? ++ocount : 1;
		
		return offers;
	}

}
