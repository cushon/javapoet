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
package com.squareup.javapoet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeVariable;

import static com.squareup.javapoet.Util.checkNotNull;

/** The name of a type variable. */
public final class TypeVariableName extends TypeName {
  public final String name;

  private TypeVariableName(String name) {
    this(name, new ArrayList<>());
  }

  private TypeVariableName(String name, List<AnnotationSpec> annotations) {
    super(annotations);
    this.name = checkNotNull(name, "name == null");
  }

  @Override public TypeVariableName annotated(List<AnnotationSpec> annotations) {
    return new TypeVariableName(name, annotations);
  }

  @Override public TypeName withoutAnnotations() {
    return new TypeVariableName(name);
  }

  private static TypeVariableName of(String name) {
    return new TypeVariableName(name);
  }

  @Override CodeWriter emit(CodeWriter out) throws IOException {
    emitAnnotations(out);
    return out.emitAndIndent(name);
  }

  /**
   * Returns type variable named {@code name}.
   */
  public static TypeVariableName get(String name) {
    return TypeVariableName.of(name);
  }

  /** Returns type variable equivalent to {@code mirror}. */
  public static TypeVariableName get(TypeVariable mirror) {
    return get((TypeParameterElement) mirror.asElement());
  }

  /** Returns type variable equivalent to {@code element}. */
  public static TypeVariableName get(TypeParameterElement element) {
    return TypeVariableName.of(element.getSimpleName().toString());
  }

  /** Returns type variable equivalent to {@code type}. */
  public static TypeVariableName get(java.lang.reflect.TypeVariable<?> type) {
    return new TypeVariableName(type.getName());
  }
}
