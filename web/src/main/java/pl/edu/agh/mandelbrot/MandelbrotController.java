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
import java.util.Date;
import java.util.List;
import java.util.Random;

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
            @RequestParam(value = "slices", required = false, defaultValue = "50") int slices
    ) throws IOException, SQLException {
        ApplicationContext context = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
        AmqpTemplate amqpTemplate = context.getBean(AmqpTemplate.class);

        int tag = new Random().nextInt(1 << 30);
        int sliceHeight = height / slices;
        double doubleHeightPerSlice = (top - bottom) / slices;

        for(int i = 0; i < slices; ++i)
        {
            double sliceBottom = bottom + i * doubleHeightPerSlice;
            double sliceTop = sliceBottom +  doubleHeightPerSlice;

            long submitTime = new Date().getTime();
            int generationRequestId =
                    mandelbrotResultDAO.insertGenerationRequest(tag,submitTime, width, sliceHeight, sliceTop, right, sliceBottom, left, precision);
            MandelbrotGenerationRequest request =
                    new MandelbrotGenerationRequest(generationRequestId, tag, submitTime, width, sliceHeight, sliceTop, right, sliceBottom, left, precision);
            amqpTemplate.convertAndSend(request);

            System.out.println("GENERATION REQUEST ID = " + generationRequestId);
            System.out.println("Sent to RabbitMQ: " + request);
        }

        return "redirect:/mandelbrot/result?tag=" + tag;
    }

    @RequestMapping("result")
    public String getResult(@RequestParam(value = "tag", required = true) int tag, Model model)
            throws SQLException {
        if (mandelbrotResultDAO.checkIfGenerationHasEnded(tag)) {
            model.addAttribute("calculated", true);
            List<MandelbrotGenerationResult> generationResult = mandelbrotResultDAO.getGenerationResult(tag);
            model
                .addAttribute("results", generationResult);
        } else {
            model.addAttribute("calculated", false);
        }

        return "mandelbrot";
    }

}
