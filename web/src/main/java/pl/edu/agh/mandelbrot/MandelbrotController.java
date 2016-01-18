package pl.edu.agh.mandelbrot;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.sql.SQLException;

@Controller
@RequestMapping("/mandelbrot")
public class MandelbrotController {

    // load RabbitMQ configuration provided by Spring
    @Autowired
    private RabbitTemplate amqpTemplate;
    @Autowired
    private Queue rabbitQueue;
    @Autowired
    private MandelbrotResultDAO mandelbrotResultDAO;

    @RequestMapping()
    public String mandelbrot(
            @RequestParam(value = "width", required = false, defaultValue = "1024") int width,
            @RequestParam(value = "height", required = false, defaultValue = "1024") int height,
            @RequestParam(value = "top", required = false, defaultValue = "1.25") double top,
            @RequestParam(value = "right", required = false, defaultValue = "0.5") double right,
            @RequestParam(value = "bottom", required = false, defaultValue = "-1.25") double bottom,
            @RequestParam(value = "left", required = false, defaultValue = "-2") double left,
            @RequestParam(value = "precision", required = false, defaultValue = "1024") int precision,
            Model model) throws IOException, SQLException {
        ApplicationContext context = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
        AmqpTemplate amqpTemplate = context.getBean(AmqpTemplate.class);

        int generationRequestId =
                mandelbrotResultDAO.insertGenerationRequest(width, height, top, right, bottom, left, precision);

        MandelbrotGenerationRequest request =
                new MandelbrotGenerationRequest(generationRequestId, width, height, top, right, bottom, left, precision);
        amqpTemplate.convertAndSend(request);

        System.out.println("GENERATION REQUEST ID = " + generationRequestId);
        System.out.println("Sent to RabbitMQ: " + request);

        return "redirect:/mandelbrot/result?id=" + generationRequestId;
    }

    @RequestMapping("result")
    public String getResult(@RequestParam(value = "id", required = true) int id, Model model)
            throws SQLException {
        if (mandelbrotResultDAO.checkIfGenerationHasEnded(id)) {
            model.addAttribute("calculated", true);
            MandelbrotGenerationResult generationResult = mandelbrotResultDAO.getGenerationResult(id);
            model
                .addAttribute("renderedImage", generationResult.getImage())
                .addAttribute("width", generationResult.getWidth())
                .addAttribute("height", generationResult.getHeight())
                .addAttribute("top", generationResult.getTop())
                .addAttribute("right", generationResult.getRight())
                .addAttribute("bottom", generationResult.getBottom())
                .addAttribute("left", generationResult.getLeft())
                .addAttribute("precision", generationResult.getPrecision());
        } else {
            model.addAttribute("calculated", false);
        }

        return "mandelbrot";
    }

}
