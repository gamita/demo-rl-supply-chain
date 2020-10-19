package demo.rl.supply_chain.training;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.configuration.DQNDenseNetworkConfiguration;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.nd4j.linalg.learning.config.Adam;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import demo.rl.supply_chain.model.agent.SupplyAgent;
import demo.rl.supply_chain.model.environment.ActionResult;
import demo.rl.supply_chain.model.state.SupplyState;



/**
 * Trainer class (Singleton)
 * 
 * This class kicks DQN training with configurations.
 * 
 * 
 * @author gamita
 *
 */
@Service
public class Trainer {


	/** Agent */
	private SupplyAgent agent = null;


	public static final int MAX_STEP_BY_EPOCH = 100;



	/**
	 * Start DQN
	 *
	 * As reference:
	 * 
	 * https://github.com/eclipse/deeplearning4j-examples/tree/master/rl4j-examples (thanks the examples.)
	 * 
	 * if you want to save (and play) your model after a training, please see above example.
	 * 
	 */
	@Async
	public void startDQNTraining() {


		// create an agent
		this.agent = new SupplyAgent();


		// initialize q-learning configuration
		QLearningConfiguration qlConfig =

				QLearningConfiguration.builder()

						.seed(1234L)

						.maxEpochStep(MAX_STEP_BY_EPOCH)

						.maxStep(20000)

						.expRepMaxSize(5000)

						.batchSize(32)

						.targetDqnUpdateFreq(100)

						.updateStart(0)

						.rewardFactor(1)

						.gamma(0.95)

						.errorClamp(3.0)

						.minEpsilon(0.01f)

						.epsilonNbStep(10000)

						.doubleDQN(true)

						.build();




		// initialize neural network configuration for DQN
		DQNFactoryStdDense qlNet =

				new DQNFactoryStdDense(

						DQNDenseNetworkConfiguration.builder()

								.numLayers(3)

								.numHiddenNodes(16)

								.l2(0.0001)

								.updater(new Adam())

								.build()

				);


		QLearningDiscreteDense<SupplyState> dqn = new QLearningDiscreteDense<SupplyState>(this.agent, qlNet, qlConfig);

		dqn.train();

		this.agent.close();



	}





	/**
	 * Return accumulated step transitions
	 * 
	 * @param reportSize
	 *                   report size indicates to report last n-size steps
	 * 
	 * @return list of action-result(including order qty, inventory qty, reward) in each step
	 * 
	 */
	public List<ActionResult> getStepHistory(int reportSize) {


		if (this.agent == null || agent.getStepHistory().size() < reportSize) {

			return new ArrayList<>();

		}

		int steps = agent.getStepHistory().size();

		return this.agent.getStepHistory().subList(steps - reportSize, steps - 1);

	}





	/**
	 * Return accumulated reward history
	 * 
	 * @return list of sum reward in each episode
	 * 
	 */
	public List<Double> getRewardHistory() {


		if (this.agent == null || agent.getStepHistory().isEmpty()) {

			return new ArrayList<>();

		}

		List<Double> rewardHistory = new ArrayList<>();
		double sumOfRewardsInEpisode = 0;

		for (int i = 0; i < agent.getStepHistory().size(); i++) {

			sumOfRewardsInEpisode += agent.getStepHistory().get(i).getReward();

			// refresh sum value by each episode
			if (i % MAX_STEP_BY_EPOCH == MAX_STEP_BY_EPOCH - 1) {

				rewardHistory.add(sumOfRewardsInEpisode);
				sumOfRewardsInEpisode = 0;

			}

		}

		return rewardHistory;

	}


}
