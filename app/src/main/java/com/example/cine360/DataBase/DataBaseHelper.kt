package com.example.cine360.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.cine360.DataBase.Manager.ActorManager
import com.example.cine360.DataBase.Manager.ComidaManager
import com.example.cine360.DataBase.Manager.DirectorManager
import com.example.cine360.DataBase.Manager.PeliculaManager
import com.example.cine360.DataBase.Manager.PromocionesManager
import com.example.cine360.DataBase.Manager.SalaManager


class DataBaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "cine360.db"
        const val DATABASE_VERSION = 42
        private const val tag = "DataBaseHelper"

        /*Tabla Usuarios*/
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_IS_ADMIN = "is_admin"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_EMAIL = "email"

        /*Tabla Pelicula*/
        const val TABLE_PELICULA = "pelicula"
        const val COLUMN_Pelicula_ID = "pelicula_id"
        const val COLUM_TITULO = "titulo"
        const val COLUMN_DESCRIPCION = "descripcion"
        const val COLUMN_GENERO = "genero"
        const val COLUMN_FECHA_LANZAMIENTO = "fechaLanzamiento"
        const val COLUMN_DURACION = "duracion"
        const val COLUMN_PELICULA_IMAGEN = "pelicula_imagen"
        const val COLUMN_TRAILER = "pelicula_trailer"
        const val COLUMN_SEMANA = "semana"

        /*Tabla Director*/
        const val TABLE_DIRECTOR = "director"
        const val COLUMN_DIRECTOR_ID = "director_id"
        const val COLUMN_DIRECTOR_NOMBRE = "director_nombre"
        const val COLUMN_DIRECTOR_APELLIDO = "director_apellido"
        const val COLUMN_PELICULAID = "pelicula_id"

        /*Tabla Actor*/
        const val TABLE_ACTOR = "actor"
        const val COLUMN_ACTOR_ID = "actor_id"
        const val COLUMN_ACTOR_NOMBRE = "actor_nombre"
        const val COLUMN_ACTOR_APELLIDO = "actor_apellido"
        const val COLUMN_PELICULA_ID = "pelicula_id"

        /*Tabla Semana*/
        const val TABLE_SEMANA = "semana"
        const val COLUMN_SEMANA_ID = "semana_id"
        const val COLUMN_SEMANA_NOMBRE = "semana_nombre"

        /*Tabla Sala*/
        const val TABLE_SALA = "sala"
        const val COLUMN_SALA_ID = "sala_id"
        const val COLUMN_SALA_NOMBRE = "sala_nombre"
        const val COLUMN_MAXIMOASIENTOS = "maximoAsientos"
        const val COLUMN_MAXIMOFILAS = "maximoFilas"
        const val COLUMN_PRECIO_SALA = "precio_sala"
        const val COLUMN_HORARIO = "horario"
        const val COLUMN_PELICULA = "peliculaId"

        /*Tabla Promociones*/
        const val TABLE_PROMOCIONES = "promociones"
        const val COLUMN_PROMOCION_ID = "promocion_id"
        const val COLUMN_PROMOCION_NOMBRE = "promocion_nombre"
        const val COLUMN_PROMOCION_DESCRIPCION = "promocion_descripcion"
        const val COLUMN_PROMOCION_IMAGEN = "promocion_imagen" // Correcto, esta vez se usa.
        const val COLUMN_PROMOCION_PRECIO = "promocion_precio"

        /*Tabla comida*/
        const val TABLE_COMIDA = "comida"
        const val COLUMN_COMIDA_ID = "comida_id"
        const val COLUMN_COMIDA_NOMBRE = "comida_nombre"
        const val COLUMN_COMIDA_IMAGEN = "comida_imagen"
        const val COLUMN_COMIDA_DESCRIPCION = "comida_descripcion"
        const val COLUMN_COMIDA_PRECIO = "comida_precio"

        /*Tabla Entrada*/
        const val TABLE_ENTRADA = "entrada"
        const val COLUMN_ENTRADA_ID = "entrada_id"
        const val COLUMN_USERID = "user_id"
        const val COLUMN_PELI_ID = "pelicula_id"
        const val COLUMN_SALA = "sala"
        const val COLUMN_ASIENTO = "asiento"
        const val COLUMN_FILA = "fila"
        const val COLUMN_SALA_HORA = "sala_horario"
        const val COLUMN_PELICULA_NOMBRE = "nombrePelicula"

        /*Tabla EntradaPromociones*/
        const val TABLE_ENTRADA_PROMOCIONES = "entrada_promociones"
        const val COLUMN_ENTRADA_PROMOCIONES_ID = "id"
        const val COLUMN_ENTRADA_PROMOCIONES_USUARIO_ID = "usuarioId"
        const val COLUMN_ENTRADA_PROMOCIONES_PROMOCION_ID = "promocionId"
        const val COLUMN_ENTRADA_PROMOCIONES_NOMBRE_PROMOCION = "nombrePromocion"
        const val COLUMN_ENTRADA_PROMOCIONES_DESCRIPCION_PROMOCION = "descripcionPromocion"
        const val COLUMN_ENTRADA_PROMOCIONES_PRECIO_PROMOCION = "precioPromocion"
        const val COLUMN_ENTRADA_PROMOCIONES_IMAGEN_PROMOCION = "imagenPromocion"

        /*Tabla EntradaPromociones*/
        const val TABLE_ENTRADA_COMIDA = "entrada_comida"
        const val COLUMN_ENTRADA_COMIDA_ID = "id"
        const val COLUMN_ENTRADA_COMIDA_USUARIO_ID = "usuarioId"
        const val COLUMN_ENTRADA_COMIDA_COMIDA_ID = "comidaId"
        const val COLUMN_ENTRADA_COMIDA_NOMBRE_COMIDA = "nombreComida"
        const val COLUMN_ENTRADA_COMIDA_DESCRIPCION_COMIDA = "descripcionComida"
        const val COLUMN_ENTRADA_COMIDA_PRECIO_COMIDA = "precioComida"
        const val COLUMN_ENTRADA_COMIDA_IMAGEN_COMIDA = "imagenComida"


        private val CREATE_TABLE_USERS = ("CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_IS_ADMIN + " INTEGER,"
                + COLUMN_NOMBRE + " TEXT,"
                + COLUMN_EMAIL + " TEXT" + ")")

        private val CREATE_TABLE_Pelicula = ("CREATE TABLE " + TABLE_PELICULA + "("
                + COLUMN_Pelicula_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUM_TITULO + " TEXT,"
                + COLUMN_DESCRIPCION + " TEXT,"
                + COLUMN_GENERO + " TEXT,"
                + COLUMN_FECHA_LANZAMIENTO + " TEXT,"
                + COLUMN_DURACION + " INTEGER,"
                + COLUMN_PELICULA_IMAGEN + " TEXT,"
                + COLUMN_TRAILER + " TEXT,"
                + COLUMN_SEMANA + " TEXT" + ")")

        private val CREATE_TABLE_Actor = ("CREATE TABLE " + TABLE_ACTOR + "("
                + COLUMN_ACTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ACTOR_NOMBRE + " TEXT,"
                + COLUMN_ACTOR_APELLIDO + " TEXT,"
                + COLUMN_PELICULA_ID + " INTEGER"
                + ")")

        private val CREATE_TABLE_Director = ("CREATE TABLE " + TABLE_DIRECTOR + "("
                + COLUMN_DIRECTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DIRECTOR_NOMBRE + " TEXT,"
                + COLUMN_DIRECTOR_APELLIDO + " TEXT,"
                + COLUMN_PELICULAID + " INTEGER"
                + ")")

        private val CREATE_TABLE_Semana = ("CREATE TABLE " + TABLE_SEMANA + "("
                + COLUMN_SEMANA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SEMANA_NOMBRE + " TEXT"
                + ")")

        private val CREATE_TABLE_Sala = ("CREATE TABLE " + TABLE_SALA + "("
                + COLUMN_SALA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SALA_NOMBRE + " TEXT,"
                + COLUMN_MAXIMOASIENTOS + " INTEGER,"
                + COLUMN_MAXIMOFILAS + " INTEGER,"
                + COLUMN_HORARIO + " TEXT,"
                + COLUMN_PRECIO_SALA + " DOUBLE,"
                + COLUMN_PELICULA + " INTEGER"
                + ")")

        private val CREATE_TABLE_Promociones = ("CREATE TABLE " + TABLE_PROMOCIONES + "("
                + COLUMN_PROMOCION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PROMOCION_NOMBRE + " TEXT NOT NULL, "  // NOT NULL agregado
                + COLUMN_PROMOCION_DESCRIPCION + " TEXT, "
                + COLUMN_PROMOCION_PRECIO + " REAL NOT NULL, "    // Corrección de tipo y NOT NULL
                + COLUMN_PROMOCION_IMAGEN + " TEXT" // Aquí está la columna correcta
                + ")")

        private val CREATE_TABLE_Comida = ("CREATE TABLE " + TABLE_COMIDA + "("
                + COLUMN_COMIDA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_COMIDA_NOMBRE + " TEXT NOT NULL,"
                + COLUMN_COMIDA_DESCRIPCION + " TEXT,"
                + COLUMN_COMIDA_PRECIO + " REAL NOT NULL,"
                + COLUMN_COMIDA_IMAGEN + " TEXT"
                + ")")

        private val CREATE_TABLE_Entrada = ("CREATE TABLE " + TABLE_ENTRADA + "("
                + COLUMN_ENTRADA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERID + " INTEGER,"
                + COLUMN_PELI_ID + " INTEGER,"
                + COLUMN_SALA + " TEXT,"
                + COLUMN_ASIENTO + " INTEGER,"
                + COLUMN_FILA + " INTEGER,"
                + COLUMN_SALA_HORA + " TEXT,"
                + COLUMN_PELICULA_NOMBRE + " TEXT"
                + ")")

        private val CREATE_TABLE_ENTRADA_COMIDA= ("CREATE TABLE " + TABLE_ENTRADA_COMIDA + "("
                + COLUMN_ENTRADA_COMIDA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ENTRADA_COMIDA_USUARIO_ID + " INTEGER,"
                + COLUMN_ENTRADA_COMIDA_COMIDA_ID + " INTEGER,"
                + COLUMN_ENTRADA_COMIDA_NOMBRE_COMIDA + " TEXT,"
                + COLUMN_ENTRADA_COMIDA_DESCRIPCION_COMIDA + " TEXT,"
                + COLUMN_ENTRADA_COMIDA_PRECIO_COMIDA + " REAL,"
                + COLUMN_ENTRADA_COMIDA_IMAGEN_COMIDA + " TEXT"
                + ")")



        private val CREATE_TABLE_ENTRADA_PROMOCIONES = ("CREATE TABLE " + TABLE_ENTRADA_PROMOCIONES + "("
                + COLUMN_ENTRADA_PROMOCIONES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ENTRADA_PROMOCIONES_USUARIO_ID + " INTEGER,"
                + COLUMN_ENTRADA_PROMOCIONES_PROMOCION_ID + " INTEGER,"
                + COLUMN_ENTRADA_PROMOCIONES_NOMBRE_PROMOCION + " TEXT,"
                + COLUMN_ENTRADA_PROMOCIONES_DESCRIPCION_PROMOCION + " TEXT,"
                + COLUMN_ENTRADA_PROMOCIONES_PRECIO_PROMOCION + " REAL,"
                + COLUMN_ENTRADA_PROMOCIONES_IMAGEN_PROMOCION + " TEXT"
                + ")")


    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(tag, "Iniciando creación de todas las tablas")

        try {
            /*CREACION DE USER*/
            val CREATE_TABLE_USERS = ("CREATE TABLE " + TABLE_USERS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_USERNAME + " TEXT UNIQUE,"
                    + COLUMN_PASSWORD + " TEXT,"
                    + COLUMN_IS_ADMIN + " INTEGER,"
                    + COLUMN_NOMBRE + " TEXT,"
                    + COLUMN_EMAIL + " TEXT" + ")")
            db.execSQL(CREATE_TABLE_USERS)
            Log.d(tag, "Tabla $TABLE_USERS creada exitosamente")

            /*CREACION DE PELICULAS*/
            val CREATE_TABLE_Pelicula = ("CREATE TABLE " + TABLE_PELICULA + "("
                    + COLUMN_Pelicula_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUM_TITULO + " TEXT,"
                    + COLUMN_DESCRIPCION + " TEXT,"
                    + COLUMN_GENERO + " TEXT,"
                    + COLUMN_FECHA_LANZAMIENTO + " TEXT,"
                    + COLUMN_DURACION + " INTEGER,"
                    + COLUMN_PELICULA_IMAGEN + " TEXT,"
                    + COLUMN_TRAILER + " TEXT,"
                    + COLUMN_SEMANA + " TEXT" + ")")
            db.execSQL(CREATE_TABLE_Pelicula)
            Log.d(tag, "Tabla $TABLE_PELICULA creada exitosamente")

            /*CREACION DE ACTORES*/
            val CREATE_TABLE_Actor = ("CREATE TABLE " + TABLE_ACTOR + "("
                    + COLUMN_ACTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ACTOR_NOMBRE + " TEXT,"
                    + COLUMN_ACTOR_APELLIDO + " TEXT,"
                    + COLUMN_PELICULA_ID + " INTEGER"
                    + ")")
            db.execSQL(CREATE_TABLE_Actor)
            Log.d(tag, "Tabla $TABLE_ACTOR creada exitosamente")

            /*CREACION DE DIRECTORES*/
            val CREATE_TABLE_Director = ("CREATE TABLE " + TABLE_DIRECTOR + "("
                    + COLUMN_DIRECTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_DIRECTOR_NOMBRE + " TEXT,"
                    + COLUMN_DIRECTOR_APELLIDO + " TEXT,"
                    + COLUMN_PELICULAID + " INTEGER"
                    + ")")
            db.execSQL(CREATE_TABLE_Director)
            Log.d(tag, "Tabla $TABLE_DIRECTOR creada exitosamente")

            /*CREACION TABLA SEMANAS*/
            val CREATE_TABLE_Semana = ("CREATE TABLE " + TABLE_SEMANA + "("
                    + COLUMN_SEMANA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_SEMANA_NOMBRE + " TEXT"
                    + ")")
            db.execSQL(CREATE_TABLE_Semana)
            Log.d(tag, "Tabla $TABLE_SEMANA creada exitosamente")

            /*CREACION DE Sala*/
            val CREATE_TABLE_Sala = ("CREATE TABLE " + TABLE_SALA + "("
                    + COLUMN_SALA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_SALA_NOMBRE + " TEXT,"
                    + COLUMN_MAXIMOASIENTOS + " INTEGER,"
                    + COLUMN_MAXIMOFILAS + " INTEGER,"
                    + COLUMN_HORARIO + " TEXT,"
                    + COLUMN_PRECIO_SALA + " DOUBLE,"
                    + COLUMN_PELICULA + " INTEGER"
                    + ")")
            db.execSQL(CREATE_TABLE_Sala)
            Log.d(tag, "Tabla $TABLE_SALA creada exitosamente")

            /*CREACION PROMOCIONES*/
            val CREATE_TABLE_Promociones = ("CREATE TABLE " + TABLE_PROMOCIONES + "("
                    + COLUMN_PROMOCION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_PROMOCION_NOMBRE + " TEXT NOT NULL,"
                    + COLUMN_PROMOCION_DESCRIPCION + " TEXT,"
                    + COLUMN_PROMOCION_PRECIO + " REAL NOT NULL,"
                    + COLUMN_PROMOCION_IMAGEN + " TEXT"
                    + ")")
            db.execSQL(CREATE_TABLE_Promociones)
            Log.d(tag, "Tabla $TABLE_PROMOCIONES creada exitosamente")

            /*CREACION Comida*/
            val CREATE_TABLE_Comida = ("CREATE TABLE " + TABLE_COMIDA + "("
                    + COLUMN_COMIDA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_COMIDA_NOMBRE + " TEXT NOT NULL,"
                    + COLUMN_COMIDA_DESCRIPCION + " TEXT,"
                    + COLUMN_COMIDA_PRECIO + " REAL NOT NULL,"
                    + COLUMN_COMIDA_IMAGEN + " TEXT"
                    + ")")
            db.execSQL(CREATE_TABLE_Comida)
            Log.d(tag, "Tabla $TABLE_COMIDA creada exitosamente")

            /*CREACION Entrada*/
            val CREATE_TABLE_Entrada = ("CREATE TABLE " + TABLE_ENTRADA + "("
                    + COLUMN_ENTRADA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_USERID + " INTEGER,"
                    + COLUMN_PELI_ID + " INTEGER,"
                    + COLUMN_SALA + " TEXT,"
                    + COLUMN_ASIENTO + " INTEGER,"
                    + COLUMN_FILA + " INTEGER,"
                    + COLUMN_SALA_HORA + " TEXT,"
                    + COLUMN_PELICULA_NOMBRE + " TEXT"
                    + ")")
            db.execSQL(CREATE_TABLE_Entrada)
            Log.d(tag, "Tabla $TABLE_ENTRADA creada exitosamente")

            /* CREACION DE ENTRADA_PROMOCIONES */
            val CREATE_TABLE_ENTRADA_COMIDA= ("CREATE TABLE " + TABLE_ENTRADA_COMIDA + "("
                    + COLUMN_ENTRADA_COMIDA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ENTRADA_COMIDA_USUARIO_ID + " INTEGER,"
                    + COLUMN_ENTRADA_COMIDA_COMIDA_ID + " INTEGER,"
                    + COLUMN_ENTRADA_COMIDA_NOMBRE_COMIDA + " TEXT,"
                    + COLUMN_ENTRADA_COMIDA_DESCRIPCION_COMIDA + " TEXT,"
                    + COLUMN_ENTRADA_COMIDA_PRECIO_COMIDA + " REAL,"
                    + COLUMN_ENTRADA_COMIDA_IMAGEN_COMIDA + " TEXT"
                    + ")")
            db.execSQL(CREATE_TABLE_ENTRADA_COMIDA)
            Log.d(tag, "Tabla $TABLE_ENTRADA_COMIDA creada exitosamente")



            /* CREACION DE ENTRADA_PROMOCIONES */
            val CREATE_TABLE_ENTRADA_PROMOCIONES = ("CREATE TABLE " + TABLE_ENTRADA_PROMOCIONES + "("
                    + COLUMN_ENTRADA_PROMOCIONES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ENTRADA_PROMOCIONES_USUARIO_ID + " INTEGER,"
                    + COLUMN_ENTRADA_PROMOCIONES_PROMOCION_ID + " INTEGER,"
                    + COLUMN_ENTRADA_PROMOCIONES_NOMBRE_PROMOCION + " TEXT,"
                    + COLUMN_ENTRADA_PROMOCIONES_DESCRIPCION_PROMOCION + " TEXT,"
                    + COLUMN_ENTRADA_PROMOCIONES_PRECIO_PROMOCION + " REAL,"
                    + COLUMN_ENTRADA_PROMOCIONES_IMAGEN_PROMOCION + " TEXT"
                    + ")")
            db.execSQL(CREATE_TABLE_ENTRADA_PROMOCIONES)
            Log.d(tag, "Tabla $TABLE_ENTRADA_PROMOCIONES creada exitosamente")

            insertDefaultAdmin(db)

            Log.d(tag, "Todas las tablas creadas exitosamente")
        } catch (e: Exception) {
            Log.e(tag, "Error al crear tablas: ${e.message}")
            e.printStackTrace()
        }

        val peliculaManager = PeliculaManager(this)
        peliculaManager.insertarPeliculasPrecargadas(db)

        val actorManager = ActorManager(this)
        actorManager.insertarActoresPrecargados(db)

        val directorManager = DirectorManager(this)
        directorManager.insertarDirectoresPrecargados(db)

        peliculaManager.asignarSemanasAPeliculas(db)

        val salaManager = SalaManager(this)

        val peliculas = peliculaManager.obtenerPeliculaConDb(db)

        val promocionesManager = PromocionesManager(this)
        promocionesManager.insertarPromocionesPrecargadas(db)

        val comidaManager= ComidaManager(this)
        comidaManager.insertarCOmidaPrecargada(db)

        for (pelicula in peliculas) {
            salaManager.crearSalaParaPelicula(db, pelicula)
        }
    }

    private fun insertDefaultAdmin(db: SQLiteDatabase) {
        try {
            val values = ContentValues()
            values.put(COLUMN_USERNAME, "admin")
            values.put(
                COLUMN_PASSWORD,
                "admin123"
            )
            values.put(COLUMN_IS_ADMIN, 1)
            values.put(COLUMN_NOMBRE, "Administrador")
            values.put(COLUMN_EMAIL, "admin@cine360.com")

            val result = db.insert(TABLE_USERS, null, values)
            if (result == -1L) {
                Log.e(tag, "Error al insertar administrador por defecto")
            } else {
                Log.d(tag, "Administrador por defecto insertado correctamente con ID: $result")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error al insertar administrador por defecto: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(tag, "Actualizando base de datos de versión $oldVersion a $newVersion")

        db.execSQL("DROP TABLE IF EXISTS $TABLE_ENTRADA_PROMOCIONES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ENTRADA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COMIDA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PROMOCIONES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SALA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SEMANA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DIRECTOR")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACTOR")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PELICULA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ENTRADA_COMIDA")
        onCreate(db)
    }
}
