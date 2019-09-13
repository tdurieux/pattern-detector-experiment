package foo;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@ComponentScan({"src.test.files.checks.spring.A","src.test.files.checks.spring.B"})
class Foo {

}

@Component
class Bar1 { } // Noncompliant

@Service
class Bar2 { } // Noncompliant

@Controller
class Bar3 { } // Noncompliant

@RestController
class Bar4 { } // Noncompliant
