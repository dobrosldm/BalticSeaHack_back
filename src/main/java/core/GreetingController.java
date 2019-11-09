package core;

import entities.Something;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("greeting")
@Api(value="Greeting System", description="The most important one")
public class GreetingController {
    
    @GetMapping(produces = "application/json")
    @ApiOperation(value = "Returns a json of parametrs you've written in url", response = Something.class)
    public Something greeting(@ApiParam(value = "Name, default - World", required = false)
        @RequestParam(name="name", required=false, defaultValue="World") String name, 
        @ApiParam(value = "Id", required = false)
        @RequestParam(name="id", required=false) Integer id) {

        Something something = new Something();
        something.setId(id);
        something.setName(name);
        return something;
    }
}