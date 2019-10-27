package demo.rl.supply_chain.model.environment;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;



/**
 * Environment Class for Supply Chain
 * 
 * @author gamita
 *
 */
public class SupplyEnvironment {


	public static final double PURCHASE_QUANTITY_PER_TIME = 30;


	public static final double PURODUCTION_QUANTITY_PER_TIME = 80;


	public static final int MAX_ORDER_QUANTITY = 15;


	private Random random = new Random(1234);


	private Queue<Double> customerOrderState = new ArrayDeque<>();


	private double retailerInventoryQuantity;


	private double factoryInventoryQuantity;


	public static final int ACTION_NOTHING_OPERATION = 0;


	public static final int ACTION_PURCHASE = 1;


	public static final int ACTION_PRODUCE = 2;


	public static final int ACTION_PURCHASE_AND_PRODUCE = 3;



	/**
	 * Constructor:
	 * 
	 * initialize environment
	 * 
	 */
	public SupplyEnvironment() {

		super();

		// initialize this time order quantity
		this.customerOrderState.add(generateOrderQuantity());

		// initialize next time order quantity
		this.customerOrderState.add(generateOrderQuantity());

		// initialize retailer inventory quantity at start
		this.retailerInventoryQuantity = PURCHASE_QUANTITY_PER_TIME;

		// initialize factory inventory quantity at start
		this.factoryInventoryQuantity = PURODUCTION_QUANTITY_PER_TIME;

	}




	/**
	 * current state of this environment is changed according to a behavior of given action.
	 * 
	 * @param action
	 * @return action result
	 */
	public ActionResult action(Integer action) {

		ActionResult actionResult = null;

		// execute a given action.
		switch (action) {

		case ACTION_NOTHING_OPERATION:
			actionResult = this.nothingOperation();
			break;

		case ACTION_PURCHASE:
			actionResult = this.purchase();
			break;

		case ACTION_PRODUCE:
			actionResult = this.produce();
			break;

		case ACTION_PURCHASE_AND_PRODUCE:
			actionResult = this.purchaseAndProduce();
			break;

		}

		return actionResult;

	}



	/**
	 * Action #0 : No Purchase and No Produce
	 * 
	 * @return
	 */
	private ActionResult nothingOperation() {

		double thisTimeOrderQty = getThisTimeOrderQty();

		double chanceLossQuantity = operateSales();

		double nextTimeOrderQty = slideOrderQty();

		return createActionResult(ACTION_NOTHING_OPERATION, thisTimeOrderQty, nextTimeOrderQty, chanceLossQuantity, 0);

	}



	/**
	 * Action #1 : only Purchase
	 * 
	 * @return
	 */
	private ActionResult purchase() {

		double thisTimeOrderQty = getThisTimeOrderQty();

		double chanceLossQty = operateSales();

		double shipLossQty = operatePurchase();

		double nextTimeOrderQty = slideOrderQty();

		return createActionResult(ACTION_PURCHASE, thisTimeOrderQty, nextTimeOrderQty, chanceLossQty, shipLossQty);

	}



	/**
	 * Action #2 : only Produce
	 * 
	 * @return
	 */
	private ActionResult produce() {

		double thisTimeOrderQty = getThisTimeOrderQty();

		double chanceLossQty = operateSales();

		operateProduction();

		double nextTimeOrderQty = slideOrderQty();

		return createActionResult(ACTION_PRODUCE, thisTimeOrderQty, nextTimeOrderQty, chanceLossQty, 0);

	}



	/**
	 * Action #3 : Purchase and Produce
	 * 
	 * @return
	 */
	private ActionResult purchaseAndProduce() {

		double thisTimeOrderQty = getThisTimeOrderQty();

		double chanceLossQty = operateSales();

		double shipLossQty = operatePurchase();

		operateProduction();

		double nextTimeOrderQty = slideOrderQty();

		return createActionResult(ACTION_PURCHASE_AND_PRODUCE, thisTimeOrderQty, nextTimeOrderQty, chanceLossQty, shipLossQty);

	}



	private double operateSales() {

		double chanceLossQuantity = Math.abs(Math.min(this.retailerInventoryQuantity - getThisTimeOrderQty(), 0));
		this.retailerInventoryQuantity = Math.max(this.retailerInventoryQuantity - getThisTimeOrderQty(), 0);

		return chanceLossQuantity;

	}



	private double operatePurchase() {

		double shipLossQuantity = Math.abs(Math.min(this.factoryInventoryQuantity - PURCHASE_QUANTITY_PER_TIME, 0));

		this.retailerInventoryQuantity += Math.min(PURCHASE_QUANTITY_PER_TIME, this.factoryInventoryQuantity);
		this.factoryInventoryQuantity -= Math.min(PURCHASE_QUANTITY_PER_TIME, this.factoryInventoryQuantity);

		return shipLossQuantity;

	}



	private void operateProduction() {
		this.factoryInventoryQuantity += PURODUCTION_QUANTITY_PER_TIME;
	}



	private double getThisTimeOrderQty() {

		return customerOrderState.peek();

	}



	/**
	 * For next step,
	 * 
	 * current "next order qty" moves to "this time order qty" position.
	 * 
	 * next "next order qty" is generated.
	 * 
	 * @return next time order quantity
	 */
	private double slideOrderQty() {

		this.customerOrderState.poll();
		this.customerOrderState.add(generateOrderQuantity());

		return customerOrderState.peek();

	}



	/**
	 * 
	 * @param action
	 * @param thisTimeOrderQuantity
	 * @param nextTimeOrderQuantity
	 * @param chanceLossQuantity
	 * @param shipLossQuantity
	 * @return
	 * 
	 */
	private ActionResult createActionResult(int action, double thisTimeOrderQuantity, double nextTimeOrderQuantity, double chanceLossQuantity, double shipLossQuantity) {

		return new ActionResult(action, this.customerOrderState, this.retailerInventoryQuantity, this.factoryInventoryQuantity, thisTimeOrderQuantity, nextTimeOrderQuantity, chanceLossQuantity,
			shipLossQuantity);

	}




	/**
	 * Generate customer's order quantity
	 * 
	 * @return
	 */
	private double generateOrderQuantity() {

		return (double) random.nextInt(MAX_ORDER_QUANTITY) + 1;

	}



	public Queue<Double> getCustomerOrderState() {
		return customerOrderState;
	}



	public double getRetailerInventoryQuantity() {
		return retailerInventoryQuantity;
	}



	public double getFactoryInventoryQuantity() {
		return factoryInventoryQuantity;
	}


}
