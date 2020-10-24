package demo.rl.supply_chain.model.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * A class for state info
 * 
 * @author gamita
 *
 */
public class SupplyState implements Encodable {


	/**
	 * A vector of the below supply state:
	 *
	 * [ This time order qty , Next order qty , Retailer inventory qty, Factory inventory qty]
	 *
	 **/
	private INDArray state;




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

		this.state = Nd4j.create(stateList);

	}



	@Override
	public INDArray getData() {

		return this.state;
	}



	@Override
	public boolean isSkipped() {

		return false;
	}



	@Override
	public Encodable dup() {

		// TODO Auto-generated method stub
		return null;
	}



	@Override
	@Deprecated
	public double[] toArray() {

		return this.state.toDoubleVector();

	}


}
