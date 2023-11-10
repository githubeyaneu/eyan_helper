package eu.eyan.util.jdbc


import eu.eyan.util.java.lang.ClassPlus.ClassPlusImplicit
import eu.eyan.util.jdbc.annotations.SqlTable
import eu.eyan.util.scala.TryCatchFinallyClose2

import java.lang.reflect.Parameter
import java.sql.DriverManager
import java.sql.Statement

class Database(host: String, databaseName: String, username: String, password: String) {


  Class.forName("com.mysql.cj.jdbc.Driver")
  private val DB_URL = "jdbc:mysql://" + host + "/" + databaseName + "?serverTimezone=Europe/Berlin"

  private val connection = DriverManager.getConnection(DB_URL, username, password)

  private def valueInSql(typ: Class[_], value: Object) = {
    if (typ.equals(classOf[String])) "'" + value + "'" else value + ""
  }


  def tableName(clazz: Class[_]): String = Option(clazz.getAnnotation(classOf[SqlTable])).map(_.name()).getOrElse(clazz.getSimpleName)

  def parameters(clazz: Class[_]): Array[Parameter] = clazz.firstConstructorParameters

  def columnName(parameter: Parameter): String = parameter.getName

  def columnSqlType(parameter: Parameter): String = {
    if (parameter.getType.equals(classOf[String])) {
      //    val clazzAnnotation =
      //      println("Class.@SampleAnnotation.name", clazzAnnotation.tableName())
      //      val clazzFields = clazz.getDeclaredFields
      //      clazzFields.foreach(field => {
      //        println("Class.field", field.getName, "Class.field.@FieldAnnotation.prop", field.getDeclaredAnnotation(classOf[SqlVarchar]))
      //      })
      "VARCHAR(25)"
    } else if (parameter.getType.equals(classOf[Long])) {
      "BIGINT"
    } else {
      "INT"
    }
  }

  def columns(clazz: Class[_]): List[String] = parameters(clazz).map(columnName).toList

  def insert[T](clazz: Class[T], itemsToInsert: Seq[T], retry: Int = 0): Boolean = {
    val nowSec = System.currentTimeMillis() / 1000

    itemsToInsert.map(item => {
      val values = parameters(clazz).map(param => valueInSql(param.getType, clazz.getValue(item, param)))
      val insertStmt = "INSERT INTO " + tableName(clazz) + " (timestamp, " + columns(clazz).mkString(", ") + ") VALUES (" + nowSec + ',' + values.mkString(", ") + ")"
      executeUpdate(
        insertStmt,
        t => {
          if (retry < 1) {
            createTable(clazz)
            insert(clazz, itemsToInsert, retry + 1)
          } else {
            throw t
          }
        })
    })
      .reduceLeft((b1, b2) => b1 && b2)


  }

  def createTable(clazz: Class[_]) = {
    val table = tableName(clazz)
    val metadata = connection.getMetaData

    val res = metadata.getTables(null, null, table, Array())
    if (!res.next()) executeUpdate(s"CREATE TABLE $table (id INTEGER not NULL AUTO_INCREMENT, timestamp BIGINT(19)  , PRIMARY KEY ( id ))")

    parameters(clazz).foreach(parameter => {
      val column = columnName(parameter)
      val colType = columnSqlType(parameter)
      val rs = metadata.getColumns(null, null, table, column)
      if (!rs.next) executeUpdate(s"ALTER TABLE $table ADD $column $colType")
    })
  }

  private def executeUpdate(sql: String, errorHandler: Throwable => Boolean = _ => false) = TryCatchFinallyClose2[Boolean, Statement](
    connection.createStatement(),
    st => {
      println("Execute: " + sql)
      val ret = st.executeUpdate(sql) > -1
      println("Execute done")
      ret
    },
    t => errorHandler(t),
    _.close()
  )

}
