package demo.rl.supply_chain.model.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.deeplearning4j.rl4j.space.Encodable;

/**
 * A class preserving state info
 * 
 * @author gamita
 *
 */
public class SupplyState implements Encodable {


	/** a vector of this supply state */
	private double[] state;



	/**
	 * Constructor:
	 * 
	 * @param state
	 */
	public SupplyState(double[] state) {

		super();
		this.state = state;

	}



	/**
	 * Constructor:
	 * 
	 * @param customerOrderState
	 * @param retailerInventoryQuantity
	 * @param factoryInventoryQuantity
	 * 
	 */
	public SupplyState(Queue<Double> customerOrderState, double retailerInventoryQuantity, double factoryInventoryQuantity) {

		super();

		List<Double> stateList = new ArrayList<>(customerOrderState);
		stateList.add(retailerInventoryQuantity);
		stateList.add(factoryInventoryQuantity);

		this.state = stateList.stream().mapToDouble(d -> d).toArray();

	}




	/**
	 * return a flat vector of this state
	 * 
	 * @see org.deeplearning4j.rl4j.space.Encodable#toArray()
	 * 
	 */
	@Override
	public double[] toArray() {

		return this.state;

	}



}
