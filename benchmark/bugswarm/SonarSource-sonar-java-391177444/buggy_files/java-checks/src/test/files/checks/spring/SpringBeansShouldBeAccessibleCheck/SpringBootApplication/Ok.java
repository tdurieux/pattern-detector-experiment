package src.test.files.checks.spring.SpringBootApplication.Ok;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

// Sub-package of the root package.

@Component
class Ok1 {} // Compliant

@Service
class Ok2 {} // Compliant

@Controller
class Ok3 {} // Compliant

@RestController
class Ok4 {} // Compliant
