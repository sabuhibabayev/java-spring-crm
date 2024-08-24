package az.websuper.crm.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

public class TestController {

    @GetMapping("/test")
    @PreAuthorize("hasAnyAuthority('ADMIN',USER)")
    public String test(Principal principal ){
        String user = principal.getName();
        return "Salam";

    }
}
