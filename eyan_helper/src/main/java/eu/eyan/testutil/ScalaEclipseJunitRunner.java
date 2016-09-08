package eu.eyan.testutil;

import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class ScalaEclipseJunitRunner extends BlockJUnit4ClassRunner {

	public ScalaEclipseJunitRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected void validateTestMethods(List<Throwable> errors) {
	}

	@Override
	protected void validateInstanceMethods(List<Throwable> errors) {
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
	}
}