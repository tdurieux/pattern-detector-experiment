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
import com.google.common.collect.ImmutableSet;
import com.squareup.javawriter.TypeName;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.lang.model.element.Modifier;

import static com.google.common.base.Preconditions.checkNotNull;

/** A generated field declaration. */
public final class FieldSpec {
  public final ImmutableList<AnnotationSpec> annotations;
  public final ImmutableSet<Modifier> modifiers;
  public final TypeName type;
  public final Name name;
  public final Snippet initializer;

  private FieldSpec(Builder builder) {
    this.annotations = ImmutableList.copyOf(builder.annotations);
    this.modifiers = ImmutableSet.copyOf(builder.modifiers);
    this.type = checkNotNull(builder.type);
    this.name = checkNotNull(builder.name);
    this.initializer = checkNotNull(builder.initializer);
  }

  void emit(CodeWriter codeWriter) {
    codeWriter.emitAnnotations(annotations);
    codeWriter.emitModifiers(modifiers);
    codeWriter.emit("$T $L", type, name);
    if (initializer != null) {
      codeWriter.emit(" = ");
      codeWriter.emit(initializer);
    }
    codeWriter.emit(";\n");
  }

  public static final class Builder {
    private final List<AnnotationSpec> annotations = new ArrayList<>();
    private final List<Modifier> modifiers = new ArrayList<>();
    private TypeName type;
    private Name name;
    private Snippet initializer;

    public Builder addAnnotation(AnnotationSpec annotationSpec) {
      this.annotations.add(annotationSpec);
      return this;
    }

    public Builder addAnnotation(Class<? extends Annotation> annotation) {
      this.annotations.add(AnnotationSpec.of(annotation));
      return this;
    }

    public Builder addModifiers(Modifier... modifiers) {
      this.modifiers.addAll(Arrays.asList(modifiers));
      return this;
    }

    public Builder type(TypeName type) {
      this.type = type;
      return this;
    }

    public Builder name(Name name) {
      this.name = name;
      return this;
    }

    public Builder name(String name) {
      return name(new Name(name));
    }

    public Builder initializer(String format, Object... args) {
      this.initializer = new Snippet(format, args);
      return this;
    }

    public FieldSpec build() {
      return new FieldSpec(this);
    }
  }
}
