package demo.rl.supply_chain.model.agent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import demo.rl.supply_chain.Application;
import demo.rl.supply_chain.model.environment.ActionResult;
import demo.rl.supply_chain.model.environment.SupplyEnvironment;
import demo.rl.supply_chain.model.state.SupplyObservationSpace;
import demo.rl.supply_chain.model.state.SupplyState;


/**
 * 
 * Agent class for RL
 * 
 * @author gamita
 *
 */
public class SupplyAgent implements MDP<SupplyState, Integer, DiscreteSpace> {


	/** Supply Environment */
	private SupplyEnvironment environment = new SupplyEnvironment();


	/** Observation Space */
	private ObservationSpace<SupplyState> observationSpace = new SupplyObservationSpace<>();


	/** Action Space */
	private DiscreteSpace actionSpace = new DiscreteSpace(4);


	/** Step Report */
	private List<ActionResult> stepHistory = new CopyOnWriteArrayList<>();


	/** Messaging Template for Web Socket */
	private SimpMessagingTemplate messagingTemplate = Application.getContext().getBean(SimpMessagingTemplate.class);


	/** Logger */
	private Logger logger = LoggerFactory.getLogger(SupplyAgent.class);




	/**
	 * In this method,
	 * 
	 * agent executes a action, and get reward and next state as a result of the action.
	 * 
	 * @see org.deeplearning4j.rl4j.mdp.MDP#step(java.lang.Object)
	 * 
	 */
	@Override
	public StepReply<SupplyState> step(Integer action) {

		// execute a given action.
		ActionResult actionResult = this.environment.receiveAction(action);


		// acquired reward value after action
		double reward = actionResult.getReward();

		// next state after an action  (i.e. time point = t + 1) 
		SupplyState nextState = actionResult.getNextState();


		StepReply<SupplyState> stepReply = new StepReply<SupplyState>(

				nextState,

				reward,

				this.isDone(),

				null);

		// here is a unrelated method with RL. Just logging, web socket, etc for demo.
		doSomethingForDemo(actionResult);


		return stepReply;

	}



	/**
	 * return observation space
	 * 
	 * shape of observation space = [4] (see below)
	 * 
	 * [ this time order, next time order, retailer's inventory, factory's inventory ]
	 * 
	 * @see org.deeplearning4j.rl4j.mdp.MDP#getObservationSpace()
	 */
	@Override
	public ObservationSpace<SupplyState> getObservationSpace() {

		return this.observationSpace;
	}



	/**
	 * return action space
	 * 
	 * discrete space:
	 * 
	 * #0 nothing to do
	 * 
	 * #1 purchase
	 * 
	 * #2 produce
	 * 
	 * #3 both purchase and produce
	 * 
	 * @see org.deeplearning4j.rl4j.mdp.MDP#getActionSpace()
	 */
	@Override
	public DiscreteSpace getActionSpace() {

		return this.actionSpace;
	}



	/**
	 * this method is a initializing method at the start of new episode.
	 * 
	 * @see org.deeplearning4j.rl4j.mdp.MDP#reset()
	 */
	@Override
	public SupplyState reset() {

		logger.info("--- new episode started. ---");

		this.environment = new SupplyEnvironment();

		return new SupplyState(this.environment.getCustomerOrderState(), this.environment.getRetailerInventoryQuantity(),
				this.environment.getFactoryInventoryQuantity());

	}



	/**
	 * handling for post-training
	 * 
	 * @see org.deeplearning4j.rl4j.mdp.MDP#close()
	 */
	@Override
	public void close() {

		logger.info("--- training ended. ---");
	}



	/**
	 * the condition of stopping a episode in progress.
	 * 
	 * Regarding this demo, always "false"
	 * 
	 * @see org.deeplearning4j.rl4j.mdp.MDP#isDone()
	 */
	@Override
	public boolean isDone() {

		return false;
	}



	/**
	 * Probably, when using A3C algorithm, new agents are created via this method.
	 * 
	 * @see org.deeplearning4j.rl4j.mdp.MDP#newInstance()
	 */
	@Override
	public MDP<SupplyState, Integer, DiscreteSpace> newInstance() {

		logger.info("--- new agent was created. ---");
		return new SupplyAgent();

	}



	/**
	 * Logging and Publishing web socket info
	 * 
	 * @param actionResult
	 * 
	 */
	private void doSomethingForDemo(ActionResult actionResult) {

		this.logger.info("step # {}  Selected Action: {}  Result | {}", stepHistory.size(), actionResult.getAction(), actionResult);

		this.stepHistory.add(actionResult);

		if (Application.isLiveMode()) {
			this.publishActionResult(actionResult);
		}


	}



	/**
	 * publish web socket info to clients for live-display
	 * 
	 * @param actionResult
	 */
	private void publishActionResult(ActionResult actionResult) {

		Map<String, Object> publishInfo = new HashMap<>();

		try {

			// publish a web socket info for live-display
			publishInfo.put("stepNo", this.getStepHistory().size());
			publishInfo.put("actionResult", actionResult);
			this.messagingTemplate.convertAndSend("/topic/action-result", publishInfo);

			TimeUnit.MILLISECONDS.sleep(500); // for controlling send-messages speed...

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}



	/**
	 * return a list of accumulated action result.
	 * 
	 * @return step history (list of action result)
	 * 
	 */
	public List<ActionResult> getStepHistory() {

		return this.stepHistory;
	}


}
