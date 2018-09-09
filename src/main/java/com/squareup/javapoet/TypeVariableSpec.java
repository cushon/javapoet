/*
 * Copyright (C) 2018 The JavaPoet Authors.
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

import static com.squareup.javapoet.TypeName.OBJECT;
import static com.squareup.javapoet.TypeName.VOID;
import static com.squareup.javapoet.Util.checkArgument;
import static com.squareup.javapoet.Util.checkNotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;

/** The declaration of a type variable. */
public class TypeVariableSpec {

  public final String name;
  public final List<TypeName> bounds;
  public final List<AnnotationSpec> annotations;

  private TypeVariableSpec(String name, List<TypeName> bounds) {
    this(name, bounds, new ArrayList<>());
  }

  private TypeVariableSpec(String name, List<TypeName> bounds, List<AnnotationSpec> annotations) {
    this.name = checkNotNull(name, "name == null");
    this.bounds = bounds;
    this.annotations = annotations;

    for (TypeName bound : this.bounds) {
      checkArgument(!bound.isPrimitive() && bound != VOID, "invalid bound: %s", bound);
    }
  }

  void emit(CodeWriter codeWriter) throws IOException {
    codeWriter.emitAnnotations(annotations, true);
    codeWriter.emit("$L", name);
    boolean firstBound = true;
    for (TypeName bound : bounds) {
      codeWriter.emit(firstBound ? " extends $T" : " & $T", bound);
      firstBound = false;
    }
  }

  public TypeVariableSpec annotated(AnnotationSpec... annotations) {
    return annotated(Arrays.asList(annotations));
  }

  public TypeVariableSpec annotated(List<AnnotationSpec> annotations) {
    return new TypeVariableSpec(name, bounds, annotations);
  }

  public TypeVariableSpec withoutAnnotations() {
    return new TypeVariableSpec(name, bounds, Collections.<AnnotationSpec>emptyList());
  }

  public TypeVariableSpec withBounds(Type... bounds) {
    return withBounds(TypeName.list(bounds));
  }

  public TypeVariableSpec withBounds(TypeName... bounds) {
    return withBounds(Arrays.asList(bounds));
  }

  public TypeVariableSpec withBounds(List<? extends TypeName> bounds) {
    ArrayList<TypeName> newBounds = new ArrayList<>();
    newBounds.addAll(this.bounds);
    newBounds.addAll(bounds);
    return new TypeVariableSpec(name, newBounds, annotations);
  }

  private static TypeVariableSpec of(String name, List<TypeName> bounds) {
    // Strip java.lang.Object from bounds if it is present.
    List<TypeName> boundsNoObject = new ArrayList<>(bounds);
    boundsNoObject.remove(OBJECT);
    return new TypeVariableSpec(name, Collections.unmodifiableList(boundsNoObject));
  }

  /**
   * Returns type variable named {@code name} without bounds.
   */
  public static TypeVariableSpec get(String name) {
    return TypeVariableSpec.of(name, Collections.emptyList());
  }

  /**
   * Returns type variable named {@code name} with {@code bounds}.
   */
  public static TypeVariableSpec get(String name, TypeName... bounds) {
    return TypeVariableSpec.of(name, Arrays.asList(bounds));
  }

  /**
   * Returns type variable named {@code name} with {@code bounds}.
   */
  public static TypeVariableSpec get(String name, Type... bounds) {
    return TypeVariableSpec.of(name, TypeName.list(bounds));
  }

  /**
   * Returns type variable equivalent to {@code mirror}.
   */
  public static TypeVariableSpec get(TypeVariable mirror) {
    return get((TypeParameterElement) mirror.asElement());
  }

  /**
   * Returns type variable equivalent to {@code element}.
   */
  public static TypeVariableSpec get(TypeParameterElement element) {
    String name = element.getSimpleName().toString();
    List<? extends TypeMirror> boundsMirrors = element.getBounds();

    List<TypeName> boundsTypeNames = new ArrayList<>();
    for (TypeMirror typeMirror : boundsMirrors) {
      boundsTypeNames.add(TypeName.get(typeMirror));
    }

    return TypeVariableSpec.of(name, boundsTypeNames);
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder();
    try {
      CodeWriter codeWriter = new CodeWriter(out);
      emit(codeWriter);
      return out.toString();
    } catch (IOException e) {
      throw new AssertionError();
    }
  }


  @Override
  public int hashCode() {
    return Objects.hash(name, bounds, annotations);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof TypeVariableSpec)) {
      return false;
    }
    TypeVariableSpec that = (TypeVariableSpec) obj;
    return name.equals(that.name) && bounds.equals(that.bounds) && annotations
        .equals(that.annotations);
  }
}
