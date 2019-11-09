package core;

import entities.Something;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("greeting")
public class GreetingController {
    @GetMapping(produces = "application/json")
    public Something greeting() {
        Something something = new Something();
        something.setId(10);
        something.setName("John");
        return something;
    }
}