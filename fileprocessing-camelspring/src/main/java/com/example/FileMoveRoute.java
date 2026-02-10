package com.example;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FileMoveRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        //moving a file from data/input folder to data/outputfolder
//        from("file:data/input?move=../output")
//                .log("Moved File : ${header.CamelFileName}");
        //dont replace existing file just keep the same file
        from("file:data/input?moveExisting=../output/archive&move=../output")
                .log("Moved File : ${header.CamelFileName}");
    }
}
