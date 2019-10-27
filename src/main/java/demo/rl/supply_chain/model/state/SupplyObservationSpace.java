package demo.rl.supply_chain.model.state;

import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.nd4j.linalg.api.ndarray.INDArray;


/**
 * Meta definition class of Observation Space
 * 
 * @author gamita
 *
 * @param <O>
 */
public class SupplyObservationSpace<O> implements ObservationSpace<O> {


	/**
	 * (non-Javadoc)
	 * 
	 * @see org.deeplearning4j.rl4j.space.ObservationSpace#getName()
	 */
	@Override
	public String getName() {
		return "Supply Chain Observation Space";
	}


	/**
	 * in the case of this demo:
	 * 
	 * Shape of vector = [ This time order qty , Next order qty , Retailer inventory qty, Factory inventory qty]
	 * 
	 * i.e. [4]
	 * 
	 * @see org.deeplearning4j.rl4j.space.ObservationSpace#getShape()
	 */
	@Override
	public int[] getShape() {

		return new int[] { 4 };

	}


	/**
	 * (non-Javadoc)
	 * 
	 * @see org.deeplearning4j.rl4j.space.ObservationSpace#getLow()
	 */
	@Override
	public INDArray getLow() {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * (non-Javadoc)
	 * 
	 * @see org.deeplearning4j.rl4j.space.ObservationSpace#getHigh()
	 */
	@Override
	public INDArray getHigh() {
		// TODO Auto-generated method stub
		return null;
	}

}
