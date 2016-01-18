package pl.edu.agh.mandelbrot;

import static java.lang.System.getenv;

public class ConfigurationHelpers {
    public static String getEnvOrThrow(String name) {
        final String env = getenv(name);
        if (env == null) {
            throw new IllegalStateException("Environment variable [" + name + "] is not set.");
        }
        return env;
    }
}
