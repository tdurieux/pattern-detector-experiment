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

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.squareup.javawriter.ClassName;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;

/** A generated annotation on a declaration. */
public final class AnnotationSpec {
  public static final AnnotationSpec OVERRIDE = new Builder()
      .type(ClassName.fromClass(Override.class))
      .build();

  public final ClassName type;
  public final ImmutableSortedMap<String, Snippet> members;

  private AnnotationSpec(Builder builder) {
    this.type = checkNotNull(builder.type);
    this.members = ImmutableSortedMap.copyOf(builder.members);
  }

  void emit(CodeWriter codeWriter) {
    if (members.isEmpty()) {
      // @Singleton
      codeWriter.emit("@$T\n", type);
    } else if (members.keySet().equals(ImmutableSortedSet.of("value"))) {
      // @Named("foo")
      codeWriter.emit("@$T(");
      codeWriter.emit(getOnlyElement(members.values()));
      codeWriter.emit(")\n");
    } else {
      // @Column(
      //   name = "updated_at",
      //   nullable = false
      // )
      codeWriter.emit("@$T(\n", type);
      codeWriter.indent();
      for (Iterator<Map.Entry<String, Snippet>> i = members.entrySet().iterator(); i.hasNext();) {
        Map.Entry<String, Snippet> entry = i.next();
        codeWriter.emit("$L = ", entry.getKey());
        codeWriter.emit(entry.getValue());
        codeWriter.emit(i.hasNext() ? ",\n" : "\n");
      }
      codeWriter.unindent();
      codeWriter.emit(")\n");
    }
  }

  public static AnnotationSpec of(Class<? extends Annotation> annotation) {
    return new Builder().type(ClassName.fromClass(annotation)).build();
  }

  @Override public boolean equals(Object o) {
    return o instanceof AnnotationSpec
        && ((AnnotationSpec) o).type.equals(type)
        && ((AnnotationSpec) o).members.equals(members);
  }

  @Override public int hashCode() {
    return type.hashCode() + 37 * members.hashCode();
  }

  public static final class Builder {
    private ClassName type;
    private final SortedMap<String, Snippet> members = Maps.newTreeMap();

    public Builder type(ClassName type) {
      this.type = type;
      return this;
    }

    public Builder addMember(String name, String format, Object... args) {
      members.put(name, new Snippet(format, args));
      return this;
    }

    public AnnotationSpec build() {
      return new AnnotationSpec(this);
    }
  }
}
