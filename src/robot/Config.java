package robot;

public class Config {
	private boolean NeuralNetSupport;
	private double alpha;
	private double gamma;
	private boolean onpolicy;
	private boolean expoloration;
	private boolean stateReduction;
	
	public Config() {
		this.NeuralNetSupport=true;
	}

	public boolean isNeuralNetSupport() {
		return NeuralNetSupport;
	}

	public void setNeuralNetSupport(boolean neuralNetSupport) {
		NeuralNetSupport = neuralNetSupport;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getGamma() {
		return gamma;
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
	}

	public boolean isOnpolicy() {
		return onpolicy;
	}

	public void setOnpolicy(boolean onpolicy) {
		this.onpolicy = onpolicy;
	}

	public boolean isExpoloration() {
		return expoloration;
	}

	public void setExpoloration(boolean expoloration) {
		this.expoloration = expoloration;
	}

	public boolean isStateReduction() {
		return stateReduction;
	}

	public void setStateReduction(boolean stateReduction) {
		this.stateReduction = stateReduction;
	}


}
