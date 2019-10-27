package demo.rl.supply_chain.training;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.util.DataManager;
import org.nd4j.linalg.learning.config.Adam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import demo.rl.supply_chain.model.agent.SupplyAgent;
import demo.rl.supply_chain.model.environment.ActionResult;
import demo.rl.supply_chain.model.state.SupplyState;



/**
 * Trainer class (Singleton)
 * 
 * this class kicks DQN training with set configurations.
 * 
 * 
 * @author gamita
 *
 */
@Service
public class Trainer {


	/** Agent */
	private SupplyAgent agent = null;


	/** Latch for training state */
	private AtomicBoolean isInTraining = new AtomicBoolean(false);


	/** Logger */
	private Logger logger = LoggerFactory.getLogger(Trainer.class);


	public static final int MAX_STEP_BY_EPOCH = 100;



	/**
	 * Start DQN
	 *
	 * As reference:
	 * 
	 * https://github.com/eclipse/deeplearning4j-examples/tree/master/rl4j-examples (Thank you, it's helpful.)
	 * 
	 * if you want to save (and play) your model after a training, please see above example.
	 * 
	 */
	@Async
	public void startDQNTraining() {


		// check in training now or not.
		if (this.isInTraining.getAndSet(true)) {
			logger.info("Training has already started, and so skip a start request. ");
			return;
		}


		// create a agent
		this.agent = new SupplyAgent();


		// initialize q-learning configuration
		QLearning.QLConfiguration qlConfig =

			new QLearning.QLConfiguration(

				1234, // Random seed

				MAX_STEP_BY_EPOCH, // Max step By epoch

				20000, // Max step

				5000, // Max size of experience replay

				32, // size of batches

				100, // target update (hard)

				0, // num step noop warmup

				1, // reward scaling

				0.95, // gamma

				3, // td-error clipping

				0.01f, // min epsilon

				10000, // num step for eps greedy anneal

				true // double DQN

			);



		// initialize neural network configuration for DQN
		DQNFactoryStdDense.Configuration qlNet =

			new DQNFactoryStdDense.Configuration(

				3, // number of layers

				16, // number of hidden nodes

				0.0001, // l2 regularization

				new Adam(),

				null);


		try {

			// DQN training starts (if set a arg of DataManager "true", training info is saved at directories under System.getProperty("user.home") path.)
			QLearningDiscreteDense<SupplyState> dql = new QLearningDiscreteDense<>(this.agent, qlNet, qlConfig, new DataManager());

			dql.train();

			this.agent.close();


		} catch (Exception e) {

			throw new RuntimeException(e); // my bad, rough handling.

		} finally {

			this.isInTraining.set(false);

		}

	}





	/**
	 * Return accumulated step transitions
	 * 
	 * @param reportSize
	 *            report size indicates to report last n-size steps
	 * 
	 * @return list of action-result(including order qty, inventory qty, reward) in each step
	 * 
	 */
	public List<ActionResult> getStepHistory(int reportSize) {


		if (Objects.isNull(this.agent) || agent.getStepHistory().size() < reportSize) {

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


		if (Objects.isNull(this.agent) || agent.getStepHistory().isEmpty()) {

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
