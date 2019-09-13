import javax.persistence.Entity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Entity
public class Foo {
  String foo;
}

@Controller
class FooController {

  @PostMapping(path = "/foo1")
  public void foo1(Foo foo) { // Noncompliant
  }

  @RequestMapping(path = "/foo2", method = RequestMethod.POST)
  public void foo2(Foo foo) {  // Noncompliant
  }

  @PostMapping(path = "/ok1")
  public Foo ok1(String s) {
    Foo foo = new Foo();
    return foo; // it is ok to return
  }

  public void ok2(Foo foo) {
  }
}

public class Bar {
  String bar;
}

@Controller
class BarController {

  @PostMapping(path = "/bar1")
  public void bar1(Bar bar) {
  }

  @RequestMapping(path = "/bar2", method = RequestMethod.POST)
  public void bar2(Bar bar) {
  }
}
