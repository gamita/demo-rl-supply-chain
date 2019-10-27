package demo.rl.supply_chain;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import demo.rl.supply_chain.model.environment.ActionResult;
import demo.rl.supply_chain.training.Trainer;



/**
 * Application-Starter and End-points class
 * 
 * @author gamita
 *
 */
@SpringBootApplication
@EnableAsync
@Controller
public class Application {


	@Autowired
	private Trainer trainer;


	private static ConfigurableApplicationContext context;


	private static boolean liveMode = false;


	private static final Map<String, Object> EMPTY = new HashMap<String, Object>();



	/**
	 * Application starter
	 * 
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		context = SpringApplication.run(Application.class, args);
	}



	public static ConfigurableApplicationContext getContext() {
		return context;
	}



	/**
	 * Return index page
	 * 
	 * @return index page
	 * 
	 */
	@GetMapping(value = { "/", "/index" })
	public String visitIndexPage() {

		return "./index";

	}



	/**
	 * End-point: Start DQN Training
	 * 
	 * @return empty
	 * 
	 */
	@PostMapping("/training")
	@ResponseBody
	public Map<String, Object> startTraining() {

		this.trainer.startDQNTraining();

		return EMPTY;

	}




	/**
	 * End-point: Switch Live-on or Live-off
	 * 
	 * @param liveMode
	 * @return empty
	 * @throws IOException
	 * 
	 */
	@PostMapping("/live")
	@ResponseBody
	public Map<String, Object> switchLiveMode(@RequestParam("liveMode") boolean _liveMode) throws IOException {

		Application.liveMode = _liveMode;

		return EMPTY;

	}





	/**
	 * End-point: Report of Step Transition
	 * 
	 * @param size
	 * @return step transition list
	 * 
	 */
	@GetMapping("/step-report")
	@ResponseBody
	public List<ActionResult> getStepReport(@RequestParam(name = "size", defaultValue = "100") Integer size) {

		return this.trainer.getStepHistory(size);

	}





	/**
	 * End-point: Report of Reward History
	 * 
	 * @return reward history (by each episode)
	 * 
	 */
	@GetMapping("/reward-report")
	@ResponseBody
	public List<Double> getRewardReport() {

		return this.trainer.getRewardHistory();

	}



	public static boolean isLiveMode() {
		return liveMode;
	}


}
