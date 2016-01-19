package pl.edu.agh.mandelbrot;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.ErrorHandler;

import java.io.IOException;
import java.sql.SQLException;

public class MandelbrotGenerationWorker {

    public static void main(String[] args) {
        final ApplicationContext rabbitConfig = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
        final ConnectionFactory rabbitConnectionFactory = rabbitConfig.getBean(ConnectionFactory.class);
        final Queue rabbitQueue = rabbitConfig.getBean(Queue.class);
        final MessageConverter messageConverter = new SimpleMessageConverter();

        final ApplicationContext dbConfig = new AnnotationConfigApplicationContext(DBConfiguration.class);
        final MandelbrotResultDAO mandelbrotResultDAO = dbConfig.getBean(MandelbrotResultDAO.class);

        // create a listener container, which is required for asynchronous message consumption.
        // AmqpTemplate cannot be used in this case
        final SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
        listenerContainer.setConnectionFactory(rabbitConnectionFactory);
        listenerContainer.setQueueNames(rabbitQueue.getName());

        // set the callback for message handling
        listenerContainer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                final MandelbrotGenerationRequest generationRequest =
                        (MandelbrotGenerationRequest) messageConverter.fromMessage(message);

                int id = generationRequest.getId();
                int tag = generationRequest.getTag();
                long submitTime = generationRequest.getSubmitTime();
                int precision = generationRequest.getPrecision();
                int width = generationRequest.getWidth();
                int height = generationRequest.getHeight();
                double top = generationRequest.getTop();
                double right = generationRequest.getRight();
                double bottom = generationRequest.getBottom();
                double left = generationRequest.getLeft();

                try {
                    long startOfTime = System.currentTimeMillis();
                    int[][] mandelbrotSet = new MandelbrotComputer(precision).compute(width, height, top,
                            right, bottom, left);
                    String image = new MandelbrotRenderer().render(mandelbrotSet, precision);
                    long endOfTime = System.currentTimeMillis();

                    long time = endOfTime - startOfTime;
                    long totalTime = endOfTime - submitTime;
                    mandelbrotResultDAO.insertGenerationResult(id,tag, (int) time, (int) totalTime, image);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // simply printing out the operation, but expensive computation could happen here
                System.out.println("Received from RabbitMQ: " + generationRequest);
            }
        });

        // set a simple error handler
        listenerContainer.setErrorHandler(new ErrorHandler() {
            public void handleError(Throwable t) {
                t.printStackTrace();
            }
        });

        // register a shutdown hook with the JVM
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Shutting down MandelbrotGenerationWorker");
                listenerContainer.shutdown();
            }
        });

        // start up the listener. this will block until JVM is killed.
        listenerContainer.start();
        System.out.println("MandelbrotGenerationWorker started");
    }
}
