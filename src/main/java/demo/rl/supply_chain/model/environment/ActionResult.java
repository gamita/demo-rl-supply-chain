package demo.rl.supply_chain.model.environment;

import java.util.Queue;

import com.fasterxml.jackson.annotation.JsonIgnore;

import demo.rl.supply_chain.model.state.SupplyState;


/**
 * Action Result class
 * 
 * This class includes some info about executed actions, inventory qty, reward calculation, next state after executed action etc,
 * as a result of executed action.
 * 
 * @author gamita
 *
 */
public class ActionResult {


	private int action;


	private SupplyState nextState;


	private double retailerInventoryQuantity;


	private double factoryInventoryQuantity;


	private double thisTimeOrderQuantity;


	private double nextTimeOrderQuantity;


	private double chanceLossQuantity;


	private double shipmentLossQuantity;




	/**
	 * Constructor:
	 * 
	 * @param action
	 * @param customerOrderState
	 * @param retailernventoryQuantity
	 * @param factoryInventoryQuantity
	 * @param thisTimeOrderQuantity
	 * @param nextTimeOrderQuantity
	 * @param chanceLossQuantity
	 * @param shipmentLossQuantity
	 * 
	 */
	public ActionResult(
			int action,
			Queue<Double> customerOrderState,
			double retailernventoryQuantity,
			double factoryInventoryQuantity,
			double thisTimeOrderQuantity,
			double nextTimeOrderQuantity,
			double chanceLossQuantity,
			double shipmentLossQuantity) {

		super();

		this.action = action;

		this.nextState = new SupplyState(customerOrderState, retailernventoryQuantity, factoryInventoryQuantity);

		this.retailerInventoryQuantity = retailernventoryQuantity;

		this.factoryInventoryQuantity = factoryInventoryQuantity;

		this.thisTimeOrderQuantity = thisTimeOrderQuantity;

		this.nextTimeOrderQuantity = nextTimeOrderQuantity;

		this.chanceLossQuantity = chanceLossQuantity;

		this.shipmentLossQuantity = shipmentLossQuantity;

	}




	/**
	 * Calculate reward
	 * 
	 * @return reward
	 * 
	 */
	public double getReward() {

		// set base point
		double reward = 1;

		if (isChanceLoss()) {
			// deduct 2 points when a chance loss happened.
			reward -= 2;
		}

		if (isShipmentLoss()) {
			// deduct 2 points when a shipment loss happened.
			reward -= 2;
		}

		if (isDoubleOverRetailerInventory()) {
			// deduct 1 point when the retailer inventory is too much.
			reward -= 1;
		}

		if (isTripleOverRetailerInventory()) {
			// deduct more 1 point.
			reward -= 1;
		}

		if (isDoubleOverFactoryInventory()) {
			// deduct 1 point when the factory inventory is too much.
			reward -= 1;
		}

		if (isTripleOverFactoryInventory()) {
			// deduct more 1 point.
			reward -= 1;
		}


		return reward;

	}




	/**
	 * Chance loss happened or not.
	 * 
	 * @return flag whether chance loss happened or not
	 */
	public boolean isChanceLoss() {

		return this.getChanceLossQuantity() > 0;
	}



	/**
	 * Shipment loss happened or not.
	 * 
	 * @return flag whether shipment loss happened or not
	 */
	public boolean isShipmentLoss() {

		return this.getShipmentLossQuantity() > 0;
	}



	/**
	 * Over-stock at factory happened or not.
	 * 
	 * @return flag whether over-stock at factory loss happened or not
	 */
	public boolean isOverFactoryInventory() {

		return isDoubleOverFactoryInventory() || isTripleOverFactoryInventory();
	}



	/**
	 * Double over-stock at factory happened or not.
	 * 
	 * @return flag whether double over-stock at factory loss happened or not
	 */
	public boolean isDoubleOverFactoryInventory() {

		return this.getFactoryInventoryQuantity() > SupplyEnvironment.PURODUCTION_QUANTITY_PER_TIME * 2;
	}



	/**
	 * Triple over-stock at factory happened or not.
	 * 
	 * @return flag whether triple over-stock at factory loss happened or not
	 */
	public boolean isTripleOverFactoryInventory() {

		return this.getFactoryInventoryQuantity() > SupplyEnvironment.PURODUCTION_QUANTITY_PER_TIME * 3;
	}



	/**
	 * Over-stock at retailer happened or not.
	 * 
	 * @return flag whether over-stock at retailer loss happened or not
	 */
	public boolean isOverRetailerInventory() {

		return isDoubleOverRetailerInventory() || isTripleOverRetailerInventory();
	}



	/**
	 * Double over-stock at retailer happened or not.
	 * 
	 * @return flag whether double over-stock at retailer loss happened or not
	 */
	public boolean isDoubleOverRetailerInventory() {

		return this.getRetailerInventoryQuantity() > SupplyEnvironment.PURCHASE_QUANTITY_PER_TIME * 2;
	}



	/**
	 * Triple over-stock at retailer happened or not.
	 * 
	 * @return flag whether triple over-stock at retailer loss happened or not
	 */
	public boolean isTripleOverRetailerInventory() {

		return this.getRetailerInventoryQuantity() > SupplyEnvironment.PURCHASE_QUANTITY_PER_TIME * 3;
	}



	/**
	 * Return a executed action in this step.
	 * 
	 * @return executed action
	 */
	public int getAction() {

		return action;
	}



	/**
	 * An executed action includes purchase operation or not.
	 * 
	 * @return flag whether executed action includes purchase operation or not
	 */
	public boolean isIncludePurchaseAction() {

		return this.action == SupplyEnvironment.ACTION_PURCHASE || this.action == SupplyEnvironment.ACTION_PURCHASE_AND_PRODUCE;
	}



	/**
	 * An executed action includes production operation or not.
	 * 
	 * @return flag whether executed action includes production operation or not
	 */
	public boolean isIncludeProduceAction() {

		return this.action == SupplyEnvironment.ACTION_PRODUCE || this.action == SupplyEnvironment.ACTION_PURCHASE_AND_PRODUCE;
	}



	/**
	 * Return next state after action
	 * 
	 * @return next state
	 */
	@JsonIgnore
	public SupplyState getNextState() {

		return this.nextState;
	}



	public double getRetailerInventoryQuantity() {

		return retailerInventoryQuantity;
	}



	public double getFactoryInventoryQuantity() {

		return factoryInventoryQuantity;
	}



	public double getThisTimeOrderQuantity() {

		return thisTimeOrderQuantity;
	}



	public double getNextTimeOrderQuantity() {

		return nextTimeOrderQuantity;
	}



	public double getChanceLossQuantity() {

		return chanceLossQuantity;
	}



	public double getShipmentLossQuantity() {

		return shipmentLossQuantity;
	}



	/**
	 * Return purchase quantity in this step
	 * 
	 * @return purchase quantity in this step
	 */
	public double getPurchaseQuantity() {

		return isIncludePurchaseAction() ? SupplyEnvironment.PURCHASE_QUANTITY_PER_TIME : 0;
	}



	/**
	 * Return production quantity in this step
	 * 
	 * @return production quantity in this step
	 */
	public double getProducionQuantity() {

		return isIncludeProduceAction() ? SupplyEnvironment.PURODUCTION_QUANTITY_PER_TIME : 0;
	}



	@Override
	public String toString() {

		return String.format(
				"    Reward: %s    Chance Loss: %s    Shipment Loss: %s    Next State: %s ",
				this.getReward(),
				this.getChanceLossQuantity(),
				this.getShipmentLossQuantity(),
				this.nextState.getData());

	}



}
