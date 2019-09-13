package src.test.files.checks.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class Bar {

}

class Foo {
  private final Bar bar;

  public Foo(Bar bar) {
    this.bar = bar;
  }
}

@Configuration
class A {

  @Autowired private Bar bar; // Noncompliant

  @Bean
  public Foo method() {
    return new Foo(this.bar);
  }
}

@Configuration
class B {

  @Bean
  public Foo method(Bar bar) {
    return new Foo(bar);
  }
}
