package eu.eyan.util.java.lang

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Parameter

object ClassPlus {

  implicit class ClassPlusImplicit[T](clazz: Class[T]) {
    def firstConstructor: Constructor[_] = clazz.getDeclaredConstructors.toList.head

    def firstConstructorParameters: Array[Parameter] = clazz.firstConstructor.getParameters

    def createNewWithParams(params: Object*): T = clazz.firstConstructor.newInstance(params: _*).asInstanceOf[T]

    def getValue(item: T, param: Parameter):Object = clazz.getDeclaredField(param.getName).asAccessible.get(item)

    def classToString(instance: T): String = clazz.getSimpleName + "(" + firstConstructorParameters.map(param => param.getName + "=" + getValue(instance, param)).mkString(", ") + ")"
  }

  implicit class FieldPlusImplicit(field: Field) {
    def asAccessible: Field = {
      field.setAccessible(true)
      field
    }
  }
}
