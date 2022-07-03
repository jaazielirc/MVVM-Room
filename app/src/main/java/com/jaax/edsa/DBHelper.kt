package com.jaax.edsa

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper (
    context: Context?,
    nombre: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
): SQLiteOpenHelper(context, nombre, factory, version) {

    private lateinit var db: SQLiteDatabase
    private lateinit var cv: ContentValues
    private var resultInsertar = 0L
    private var resultActualizar = 0
    private var resultEliminar = 0

    //DB y TABLAS
    private val tablaUsuario = "USUARIOS"
    private val tablaEmail = "EMAILS"
    private val tablaCuenta = "CUENTAS"

    //columnas de c/tabla
    private val usuarioColumna1 = "NOMBRE"
    private val usuarioColumna2 = "PASSWORD"
    private val usuarioColumna3 = "KEYWORD"

    private val emailColumna1 = "ID"
    private val emailColumna2 = "NOMBRE"
    private val emailColumna3 = "PASSWORD"

    private val cuentaColumna1 = "ID"
    private val cuentaColumna2 = "NOMBRE"
    private val cuentaColumna3 = "PASSWORD"
    private val cuentaColumna4 = "TIPO"

    companion object {
        const val nombreDB = "EDSA.db"
        const val version = 1
    }

    override fun onCreate(sqldb: SQLiteDatabase?) {
        sqldb!!.execSQL(
            "CREATE TABLE $tablaUsuario " +
                    "($usuarioColumna1 TEXT NOT NULL PRIMARY KEY," +
                    " $usuarioColumna2 TEXT NOT NULL," +
                    " $usuarioColumna3 TEXT NOT NULL)"
        )

        sqldb.execSQL(
            "CREATE TABLE $tablaEmail " +
                    "($emailColumna1 TEXT NOT NULL," +
                    " $emailColumna2 TEXT NOT NULL," +
                    " $emailColumna3 TEXT NOT NULL)"
        )

        sqldb.execSQL(
            "CREATE TABLE $tablaCuenta " +
                    "($cuentaColumna1 TEXT NOT NULL," +
                    " $cuentaColumna2 TEXT," +
                    " $cuentaColumna3 TEXT," +
                    " $cuentaColumna4 TEXT NOT NULL)"
        )
    }
    override fun onUpgrade(sqldb: SQLiteDatabase?, p1: Int, p2: Int) {
        sqldb!!.execSQL("DROP TABLE IF EXISTS $tablaUsuario")
        sqldb.execSQL("DROP TABLE IF EXISTS $tablaEmail")
        sqldb.execSQL("DROP TABLE IF EXISTS $tablaCuenta")
    }

    //--------- INSERTAR ---------//
    fun insertarUsuario(nombre: String, psswrd: String, keyword: String): Boolean {
        db = this.writableDatabase
        cv = ContentValues()
        cv.put(usuarioColumna1, nombre) //ID
        cv.put(usuarioColumna2, psswrd)
        cv.put(usuarioColumna3, keyword)

        resultInsertar = db.insert(tablaUsuario, null, cv)
        db.close()
        return resultInsertar != -1L
    }

    fun insertarEmail(id: String, nombre: String, psswrd: String): Boolean {
        db = this.writableDatabase
        cv = ContentValues()
        cv.put(emailColumna1, id) //debe ser el nombre de usuario
        cv.put(emailColumna2, nombre)
        cv.put(emailColumna3, psswrd)

        resultInsertar = db.insert(tablaEmail, null, cv)
        db.close()
        return resultInsertar != -1L
    }

    fun insertarCuenta(id: String, nombre: String, psswrd: String, tipo: String): Boolean {
        db = this.writableDatabase
        cv = ContentValues()
        cv.put(cuentaColumna1, id)
        cv.put(cuentaColumna2, nombre)
        cv.put(cuentaColumna3, psswrd)
        cv.put(cuentaColumna4, tipo)

        resultInsertar = db.insert(tablaCuenta, null, cv)
        db.close()
        return resultInsertar != -1L
    }

    //--------- LEER ---------//
    fun getAllUsuarios(): Cursor {
        db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $tablaUsuario", null)
    }

    /*fun getUsuarioById(ID: String): Cursor {
        db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $tablaUsuario WHERE $usuarioColumna1 =?", arrayOf(ID))
    }*/

    fun getEmailsById(ID: String): Cursor { //datos de email por usuario
        db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $tablaEmail WHERE $emailColumna1 =?", arrayOf(ID))
    }
    fun getDatosEmailById(ID: String, nombre: String): Cursor { //datos de email individuales
        db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $tablaEmail WHERE $emailColumna1 =? AND $emailColumna2 =?", arrayOf(ID, nombre))
    }

    fun getCuentasById(ID: String): Cursor {
        db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $tablaCuenta WHERE $cuentaColumna1 =?", arrayOf(ID))
    }
    fun getDatosCuentaById(ID: String, tipo: String): Cursor {
        db = this.writableDatabase
        return db.rawQuery(
            "SELECT * FROM $tablaCuenta WHERE $cuentaColumna1 =? AND $cuentaColumna4 =?", arrayOf(ID, tipo)
        )
    }

    //--------- ACTUALIZAR ---------//
    fun updtPasswordUsuario(
        nombreActual: String,
        psswrdNuevo: String,
        keyActual: String): Boolean {
        db = this.writableDatabase
        cv = ContentValues()
        cv.put(usuarioColumna2, psswrdNuevo)

        resultActualizar = db.update(
            tablaUsuario, cv, "$usuarioColumna1 =? AND $usuarioColumna3 =?", arrayOf(nombreActual, keyActual)
        )
        return resultActualizar > 0
    }

    fun updtKeywordUsuario(
        nombreActual: String,
        psswrdActual: String,
        keyNuevo: String): Boolean {
        db = this.writableDatabase
        cv = ContentValues()
        cv.put(usuarioColumna3, keyNuevo)

        resultActualizar = db.update(
            tablaUsuario, cv, "$usuarioColumna1 =? AND $usuarioColumna2 =?", arrayOf(nombreActual, psswrdActual)
        )
        return resultActualizar > 0
    }

    fun updtDatosEmail(
        id: String,
        nombreActual: String,
        nombreNuevo: String,
        psswrdNuevo: String,
    ): Boolean {
        db = this.writableDatabase
        cv = ContentValues()
        cv.put(emailColumna2, nombreNuevo)
        cv.put(emailColumna3, psswrdNuevo)

        resultActualizar = db.update(
            tablaEmail, cv,
            "$emailColumna1 =? AND $emailColumna2 =?",
            arrayOf(id, nombreActual)
        ) //sólo es necesario verificar ID y Nombre del email
        return resultActualizar > 0
    }

    fun updtDatosCuenta(
        id: String,
        nombreActual: String,
        nombreNuevo: String,
        psswrdNuevo: String,
        tipoActual: String,
        tipoNuevo: String
    ): Boolean {
        db = this.writableDatabase
        cv = ContentValues()
        cv.put(cuentaColumna2, nombreNuevo)
        cv.put(cuentaColumna3, psswrdNuevo)
        cv.put(cuentaColumna4, tipoNuevo)

        resultActualizar = db.update(
            tablaCuenta, cv,
            "$cuentaColumna1 =? AND $cuentaColumna2 =? AND $cuentaColumna4 =?",
            arrayOf(id, nombreActual, tipoActual) //sólo se requieren ID y Nombre de la cuenta
        )
        return resultActualizar > 0
    }

    fun updateChildIdPerParentId(idActual: String, newId: String, table: Int): Boolean{
        db = this.writableDatabase
        cv = ContentValues()
        when(table){
            0 -> {
                cv.put(cuentaColumna1, newId)
                resultActualizar = db.update( tablaCuenta, cv,"$cuentaColumna1 =?", arrayOf(idActual) )
                return resultActualizar>0
            }
            1 -> {
                cv.put(emailColumna1, newId)
                resultActualizar = db.update( tablaEmail, cv,"$emailColumna1 =?", arrayOf(idActual) )
                return resultActualizar>0
            }
        }
        return false
    }

    //--------- ELIMINAR ---------//
    /*fun delUsuario(nombre: String, psswrd: String, keyword: String): Int{
        return db.delete(tablaUsuario, "$usuarioColumna1 = $? AND " +
                "$usuarioColumna2 =? AND " +
                "$usuarioColumna3 =?", arrayOf(nombre, psswrd, keyword))
    }*/
    fun delEmail(id: String, nombre: String): Boolean{
        db = this.writableDatabase
        resultEliminar = db.delete(tablaEmail, "$emailColumna1 =? AND $emailColumna2 =?", arrayOf(id, nombre))
        return resultEliminar != 0
    }
    fun delCuenta( id: String, tipo: String ): Boolean{
        db = this.writableDatabase
        resultEliminar = db.delete(tablaCuenta, "$cuentaColumna1 =? AND $cuentaColumna4 =?", arrayOf(id, tipo))
        return resultEliminar != 0
    }
    fun truncateTablePerParentDeleted(idParent: String, table: Int): Boolean{
        db = this.writableDatabase
        when(table){
            0 -> { resultEliminar = db.delete(tablaCuenta, "$cuentaColumna1 =?", arrayOf(idParent)) }
            1 -> { resultEliminar = db.delete(tablaEmail, "$emailColumna1 =?", arrayOf(idParent)) }
        }
        return resultEliminar != 0
    }
}