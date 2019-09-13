package src.test.files.checks.B;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Component
class B1 {} // Compliant

@Service
class B2 {} // Compliant

@Controller
class B3 {} // Compliant

@RestController
class B4 {} // Compliant
