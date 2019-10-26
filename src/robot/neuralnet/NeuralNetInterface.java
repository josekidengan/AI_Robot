package robot.robocode.src.neuralnet;

public interface NeuralNetInterface extends CommonInterface {

	public double sigmoid(double x);
	public void initializeWeights();
	public void zeroWeights();
}
