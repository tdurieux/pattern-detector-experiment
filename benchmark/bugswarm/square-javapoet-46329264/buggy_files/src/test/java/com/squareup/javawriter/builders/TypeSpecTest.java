/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.javawriter.builders;

import com.google.common.collect.ImmutableList;
import com.squareup.javawriter.ClassName;
import com.squareup.javawriter.ParameterizedTypeName;
import com.squareup.javawriter.WildcardName;
import javax.lang.model.element.Modifier;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class TypeSpecTest {
  @Test public void basic() throws Exception {
    TypeSpec taco = new TypeSpec.Builder()
        .name(ClassName.create("com.squareup.tacos", "Taco"))
        .addMethod(new MethodSpec.Builder()
            .name("toString")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .returns(String.class)
            .addCode("return $S;\n", "taco")
            .build())
        .build();
    assertThat(toString(taco)).isEqualTo(""
        + "package com.squareup.tacos;\n"
        + "\n"
        + "import java.lang.Override;\n"
        + "import java.lang.String;\n"
        + "\n"
        + "class Taco {\n"
        + "  @Override public final String toString() {\n"
        + "    return \"taco\";\n"
        + "  }\n"
        + "}\n");
  }

  @Test public void anonymousInnerClass() throws Exception {
    ClassName foo = ClassName.create("com.squareup.tacos", "Foo");
    ClassName bar = ClassName.create("com.squareup.tacos", "Bar");
    ClassName thingThang = ClassName.create(
        "com.squareup.tacos", ImmutableList.of("Thing"), "Thang");
    ParameterizedTypeName thingThangOfFooBar
        = ParameterizedTypeName.create(thingThang, foo, bar);
    ClassName thung = ClassName.create("com.squareup.tacos", "Thung");
    ClassName simpleThung = ClassName.create("com.squareup.tacos", "SimpleThung");
    ParameterizedTypeName thungOfSuperBar
        = ParameterizedTypeName.create(thung, WildcardName.createWithLowerBound(bar));
    ParameterizedTypeName thungOfSuperFoo
        = ParameterizedTypeName.create(thung, WildcardName.createWithLowerBound(foo));
    ParameterizedTypeName simpleThungOfBar = ParameterizedTypeName.create(simpleThung, bar);

    ParameterSpec thungParameter = new ParameterSpec.Builder()
        .addModifiers(Modifier.FINAL)
        .type(thungOfSuperFoo)
        .name("thung")
        .build();
    TypeSpec aSimpleThung = new TypeSpec.Builder()
        .supertype(simpleThungOfBar)
        .anonymousTypeArguments("$N", thungParameter)
        .addMethod(new MethodSpec.Builder()
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .name("doSomething")
            .addParameter(bar, "bar")
            .addCode("/* code snippets */\n")
            .build())
        .build();
    TypeSpec aThingThang = new TypeSpec.Builder()
        .supertype(thingThangOfFooBar)
        .anonymousTypeArguments("")
        .addMethod(new MethodSpec.Builder()
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(thungOfSuperBar)
            .name("call")
            .addParameter(thungParameter)
            .addCode("return $L;\n", aSimpleThung)
            .build())
        .build();
    TypeSpec taco = new TypeSpec.Builder()
        .name(ClassName.create("com.squareup.tacos", "Taco"))
        .addField(new FieldSpec.Builder()
            .addModifiers(Modifier.STATIC, Modifier.FINAL)
            .type(thingThangOfFooBar)
            .name("NAME")
            .initializer("$L", aThingThang)
            .build())
        .build();

    // TODO: import Thing, and change references from "Thang" to "Thing.Thang"
    assertThat(toString(taco)).isEqualTo(""
        + "package com.squareup.tacos;\n"
        + "\n"
        + "import com.squareup.tacos.Bar;\n"
        + "import com.squareup.tacos.Foo;\n"
        + "import com.squareup.tacos.SimpleThung;\n"
        + "import com.squareup.tacos.Thing.Thang;\n"
        + "import com.squareup.tacos.Thung;\n"
        + "import java.lang.Override;\n"
        + "\n"
        + "class Taco {\n"
        + "  static final Thang<Foo, Bar> NAME = new Thang<Foo, Bar>() {\n"
        + "    @Override public Thung<? super Bar> call(final Thung<? super Foo> thung) {\n"
        + "      return new SimpleThung<Bar>(thung) {\n"
        + "        @Override public void doSomething(Bar bar) {\n"
        + "          /* code snippets */\n"
        + "        }\n"
        + "      };\n"
        + "    }\n"
        + "  };\n"
        + "}\n");
  }

  private String toString(TypeSpec typeSpec) {
    return new JavaFile.Builder()
        .classSpec(typeSpec)
        .build()
        .toString();
  }
}
