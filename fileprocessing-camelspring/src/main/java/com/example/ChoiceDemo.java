package com.example;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ChoiceDemo extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        //size based segration
//        from("file:data/input")
//                .choice()
//                .when(simple("${file:size} > 1000000"))
//                .log("Large File : ${header.CamelFileName}")
//                .to("file:data/large-files")
//                .otherwise()
//                .log("Small File : ${header.CamelFileName}")
//                .to("file:data/small-files")
//                .end();
        //file extension based segration
        from("file:data/input")
                .choice()
                .when(simple("${file:ext} == 'pdf'"))
                .log("PDF Document: ${header.CamelFileName}")
                .to("file:data/documents")
                .when(simple("${file:ext} == 'png'"))
                .log("Image File : ${header.CamelFileName}")
                .to("file:data/images")
                .when(simple("${file:ext} == 'mkv'"))
                .log("Video File : ${header.CamelFileName}")
                .to("file:data/videos")
                .otherwise()
                .log("Other Extensions : ${header.CamelFileName}")
                .to("file:data/others")
                .end();
    }
}
