package com.example.cine360.DataBase.Manager

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.cine360.DataBase.DataBaseHelper
import com.example.cine360.DataBase.Tablas.Actor
import com.example.cine360.DataBase.Tablas.Director
import com.example.cine360.DataBase.Tablas.Pelicula
import java.sql.SQLException

class PeliculaManager(val dbHelper: DataBaseHelper) {
    private val TAG = "PeliculaManager"

    fun insertarPeliculas(db: SQLiteDatabase, peliculas: List<Pelicula>): List<Long> {
        val ids = mutableListOf<Long>()
        try {
            db.beginTransaction()
            peliculas.forEach { pelicula ->
                val values = ContentValues().apply {
                    put(DataBaseHelper.COLUM_TITULO, pelicula.titulo)
                    put(DataBaseHelper.COLUMN_DESCRIPCION, pelicula.descripcion)
                    put(DataBaseHelper.COLUMN_GENERO, pelicula.genero)
                    put(DataBaseHelper.COLUMN_FECHA_LANZAMIENTO, pelicula.fechaLanzamiento)
                    put(DataBaseHelper.COLUMN_DURACION, pelicula.duracion)
                    put(DataBaseHelper.COLUMN_PELICULA_IMAGEN, pelicula.imagen)
                    put(DataBaseHelper.COLUMN_TRAILER, pelicula.trailer)
                    put(DataBaseHelper.COLUMN_SEMANA, pelicula.semana)
                }
                val id = db.insert(DataBaseHelper.TABLE_PELICULA, null, values)
                if (id == -1L) {
                    throw SQLException("Error al insertar película: ${pelicula.titulo}")
                }
                ids.add(id)
            }
            db.setTransactionSuccessful()
        } catch (e: SQLException) {
            Log.e(TAG, "Error al insertar películas: ${e.message}")
        } finally {
            db.endTransaction()
        }
        return ids
    }

    fun insertarPeliculasPrecargadas(db: SQLiteDatabase) {
        val peliculas = listOf(
            /*Peliculas de la semana 1 COMEDIA*/
            Pelicula(1, "Solo en casa", "Un niño de 8 años que se queda abandonado en casa mientras su familia esta de vacaciones", "Comedia", "21/12/1990", 100, "soloencasa.jpg", "https://www.youtube.com/watch?v=4nXRb-1-n60&ab_channel=CineFantasia", "1"),
            Pelicula(2, "Intouchables", "Un millonario se a quedado tretrapléjico debido a un accidente y contrata a un imigrante recien salido de la carcel.", "Comedia", "9/03/2012", 109, "intouchables.jpg", "trailer2.mp4", "1"),
            Pelicula(3, "Dos tontos muy tontos", "Dos amigos con una gran estupidez, Lloyd se enamora de una chica que olvida su maletin y emprenden un viaje por el país para devolverselo.", "Comedia", "30/03/1995", 106, "dostontos.jpg", "trailer3.mp4", "1"),
            Pelicula(4, "The Dictator", "DEl dictador de Wadiya viaja a Estados Unidos siguiendo el consejo de su tio con la intención de impedir el establecimiento de la democracio en su país", "Comedia", "13/07/2012", 110, "dictador.jpg", "trailer4.mp4", "1"),
            Pelicula(5, "Ali G", "Ali G tiene una vida tranquila en su barrio, pero todo cambia cuando se entera de que el centro de ocio de Staines está a punto de cerrar.", "Comedia", "22/03/2002", 88, "alig.jpg", "trailer5.mp4", "1"),
            Pelicula(6, "Ted", "John Bennett deseaba que su oso de peluche fuera de verdad, más tarde ese sueño se hace realidad.", "Comedia", "10/08/2012", 106, "ted.jpg", "trailer6.mp4", "1"),
            Pelicula(7, "Campeones", "Un entrenador profesional de baloncesto se encuentra en medio de una crisis personal, entrena un equipo de personas con discapacidad intelectual, pero este problema acaba siendo una lección de vida.", "Comedia", "06/04/2018", 124, "campeones.jpg", "trailer7.mp4", "1"),
            /*Peliculas de la semana 2 ROMANCE*/
            Pelicula(8, "Todos los días de mi vida", "Un accidente de tránsito deja a Paige en coma y al despertar ha sufrido una gran pérdida de memoria, olvidando a su esposo y este debera esforzarse para ganar su corazón otra vez.", "Romance", "23/03/2012", 104, "todoslosdias.jpg", "trailer8.mp4", "2"),
            Pelicula(9, "Querido John", "Un soldado que se enamora de un estudiante universitario conservador mientras está de vaciones en casa", "Romance", "23/04/2010", 105, "queridojohn.jpg", "trailer9.mp4", "2"),
            Pelicula(10, "Bajo la misma estrella", "Dos pacientes de cáncer inician un viaje de afirmación de vida para visitar a un autor recluido en Ámsterdam", "Romance", "04/07/2014", 125, "bajolamisma.jpg", "trailer9.mp4", "2"),
            Pelicula(11, "Titanic", "Una aristóctrata de dicisiete años se enamora de un amable pero pobre artista a bordo del lujoso y desafortunado R.M.S Titanic", "Romance", "08/01/1998", 195, "titanic.jpg", "trailer9.mp4", "2"),
            Pelicula(12, "Perdona si te llamo amor", "Alex es un ejecutivo de éxito en busca de la estabilidad emocional.", "Romance", "19/06/2014", 105, "perdonasitellamoamor.jpg", "trailer9.mp4", "2"),
            Pelicula(13, "3 metros sobre el cielo", "Una mujer privilegiada y un hombre temerario se enamoran a pesar de la diferencia de clase entre ambos.", "Romance", "03/12/2010", 120, "metros.jpg", "trailer9.mp4", "2"),
            Pelicula(14, "Un pedacito de cielo", "Marley es una bella mujer que padece cáncer, pero se enamora de su médico.", "Romance", "04/05/2012", 120, "unpedacito.jpg", "trailer9.mp4", "2"),

            /*Peliculas de la semana 3 TERROR*/
            Pelicula(15, "Terrifier", "El payaso psicopata Art aterroriza a dos chicas durante la noche de hallowen matando a todos aquellos que se interponen en su camino", "Terror", "15/03/2018", 85, "terrifier.jpg", "trailer9.mp4", "3"),
            Pelicula(16, "Smile", "Después de presenciar un accidente extraño y traumático que involucra un paciente, la Dra comienza a experimentar sucesos aterradores que no puede explicar.", "Terror", "22/09/2022", 115, "smile.jpg", "trailer9.mp4", "3"),
            Pelicula(17, "Relic", "Una hija, una madre y una abuela son acosadas por un tipo de demencia que esta consumiendo a la familia.", "Terror", "03/07/2020", 86, "relic.jpg", "trailer9.mp4", "3"),
            Pelicula(18, "Audition", "Un cuarentón viudo, a propuesta de un amigo, convoca un castin para una inexistente pelicula con la intención de encontrar una nueva esposa.", "Terror", "19/07/2002", 115, "audition.jpg", "trailer9.mp4", "3"),
            Pelicula(19, "La matanza de Texas", "Cinco adolescentes visitan la tumba, supuestamente profanada, del abuelo de uno de ellos.Cuando llegan se encuentran con un siniestro matadero.", "Terror", "01/10/1999", 83, "lamatanza.jpg", "trailer9.mp4", "3"),
            Pelicula(20, "Arrástrame al infierno", "Una joven trabaja en un banco dando préstamos hipotecarios, una dia decide negarle ese prestamo a un señora mayor, está pierde su casa y decide vengarse de ella lanzandole una maldición", "Terror", "31/07/2009", 95, "arrastrame.jpg", "trailer9.mp4", "3"),
            Pelicula(21, "Anticristo", "Un psicologo, que quiere ayudar a su mujer a superar la muerte de su hijo en un accidente, la lleva a una cabaña pérdida en medio del bosque, esto no funciona y ella comienza a comportarse extraño", "Terror", "21/08/2009", 104, "anticristo.jpg", "trailer9.mp4", "3"),
            /*Peliculas de la semana 4 ACCIÓN*/
            Pelicula(22, "Jungla de cristal", "En lo alto de la ciudad de Los Ángeles un grupo terrorista se a apoderado de un edifico tomando a un grupo de personas como rehenes.Un policia solo y fuera de servicio se enfrentara a los seciestradores.", "Acción", "30/09/1988", 131, "jungla.jpg", "trailer9.mp4", "4"),
            Pelicula(23, "Terminator 2:El juicio final", "John es entrenado por su madre como un guerrero, debido a que su madre fue informada por un viajero del tiempo que este seria el salvador del mundo, un androide mejorado llega a la época para matar a john, pero un androide T-800 es enviado para protegerle.", "Acción", "05/12/1991", 135, "terminator2.jpg", "trailer9.mp4", "4"),
            Pelicula(24, "Kill Bill", "El día de su bida una asesina prfofesional sufre un ataque de algunos miembros de su propia banda.Ella logra sobreviir al ataque aunque queda en coma, cuatro años depues despierta dominada por un gran deseo de venganza.", "Acción", "10/10/2003", 110, "killbill.jpg", "trailer9.mp4", "4"),
            Pelicula(25, "Mad Max: Furia en la carretera", "Mad max cree que la mejor forma de sobrevivir es ir solo por el mundo.Se ve arrastrado a formar parte de un grupo que huye a traves del desierto en un War Rig conducido por una empreatriz de Élite.", "Acción", "15/05/2015", 120, "maxmax.jpg", "trailer9.mp4", "4"),
            Pelicula(26, "Matrix", "Thomas Anderson es un brillante programador de uan respetable compañia de software.Pero fuera de su trabajo es Neo, un hacker que un día recibe una misteriosa visita...", "Acción", "23/06/1999", 131, "matrix.jpg", "trailer9.mp4", "4"),
            Pelicula(27, "Misión imposible", "Ethan Hunt es un super espíacapaz de llevar a cabo la misión más peligrosa con la máxima eficacia y elegancia.", "Acción", "05/07/1996", 110, "misionimposible.jpg", "trailer9.mp4", "4"),
            Pelicula(28, "SuperMan", "Desde una galaxia remota, un recién nacido es enviado por sus padres al espacio debido a la inminente destrucción de su planeta.La nave aterriza en en la tierra y es adoptado por unos granjeros que le inculcan mejores valores humanos.", "Acción", "08/02/1979", 143, "superman.jpg", "trailer9.mp4", "4"),
        )
        insertarPeliculas(db, peliculas)
    }

    fun obtenerPeliculaConDb(db: SQLiteDatabase): List<Pelicula> {
        val peliculas = mutableListOf<Pelicula>()
        val cursor = db.rawQuery("SELECT * FROM ${DataBaseHelper.TABLE_PELICULA}", null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_Pelicula_ID))
                    val titulo = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUM_TITULO))
                    val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DESCRIPCION))
                    val genero = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_GENERO))
                    val fechaLanzamiento = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_FECHA_LANZAMIENTO))
                    val duracion = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DURACION))
                    val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PELICULA_IMAGEN))
                    val trailer = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_TRAILER))
                    val semana = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_SEMANA))

                    val pelicula = Pelicula(id, titulo, descripcion, genero, fechaLanzamiento, duracion, imagen, trailer, semana)
                    peliculas.add(pelicula)
                } while (it.moveToNext())
            }
        }
        return peliculas
    }

    fun obtenerPeliculaPorId(peliculaId: Int): Pelicula? {
        var pelicula: Pelicula? = null
        dbHelper.readableDatabase.use { db ->
            val cursor = db.rawQuery(
                "SELECT * FROM ${DataBaseHelper.TABLE_PELICULA} WHERE ${DataBaseHelper.COLUMN_Pelicula_ID} = ?",
                arrayOf(peliculaId.toString())
            )
            cursor.use {
                if (it.moveToFirst()) {
                    val id = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_Pelicula_ID))
                    val titulo = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUM_TITULO))
                    val descripcion = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DESCRIPCION))
                    val genero = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_GENERO))
                    val fechaLanzamiento = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_FECHA_LANZAMIENTO))
                    val duracion = it.getInt(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_DURACION))
                    val imagen = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PELICULA_IMAGEN))
                    val trailer = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_TRAILER))
                    val semana = it.getString(it.getColumnIndexOrThrow(DataBaseHelper.COLUMN_SEMANA))


                    val actores = obtenerActoresPorPelicula(peliculaId)
                    val directores = obtenerDirectoresPorPeliculaId(peliculaId)

                    val listaDirectores = directores.mapNotNull { nombreCompleto ->
                        if (nombreCompleto != null) { // Agrega verificación de null
                            val partes = nombreCompleto.trim().split(" ")
                            if (partes.size >= 2) {
                                Director(nombre = partes[0], apellido = partes[1], peliculaId = peliculaId.toLong())
                            } else {
                                Log.e("DetallesActivity", "Nombre de director inesperado: $nombreCompleto")
                                null
                            }
                        } else {
                            Log.e("DetallesActivity", "Nombre de director nulo")
                            null
                        }
                    }

                    val listaActores = actores.mapNotNull { nombreCompleto ->
                        val partes = nombreCompleto.trim().split(" ")
                        if (partes.size >= 2) {
                            Actor(nombre = partes[0], apellido = partes[1], peliculaId = peliculaId.toLong())
                        } else {
                            Log.e("DetallesActivity", "Nombre de actor inesperado: $nombreCompleto")
                            null
                        }
                    }

                    pelicula = Pelicula(id, titulo, descripcion, genero, fechaLanzamiento, duracion, imagen, trailer, semana, listaDirectores, listaActores)
                }
            }
        }
        return pelicula
    }

    fun asignarSemanasAPeliculas(db: SQLiteDatabase) {
        val peliculas = obtenerPeliculaConDb(db)

        try {
            peliculas.forEach { pelicula ->
                val semana = pelicula.semana

                val values = ContentValues()
                values.put(DataBaseHelper.COLUMN_SEMANA, semana)

                val whereClause = "${DataBaseHelper.COLUMN_Pelicula_ID} = ?"
                val whereArgs = arrayOf(pelicula.id.toString())

                db.update(DataBaseHelper.TABLE_PELICULA, values, whereClause, whereArgs)
            }
        } catch (e: Exception) {
            Log.e("PeliculaManager", "Error al asignar semanas a películas: ${e.message}")
        }
    }



    fun obtenerDetallesPeliculaCompletos(peliculaId: Int): Map<String, Any> {
        val pelicula = obtenerPeliculaPorId(peliculaId)

        return mapOf(
            "pelicula" to pelicula!!,
            "actores" to pelicula.actores.map { "${it.nombre} ${it.apellido}" },
            "directores" to pelicula.directores.map { "${it.nombre} ${it.apellido}" }
        )
    }
    fun obtenerActoresPorPelicula(peliculaId: Int): List<String> {
        val actores = mutableListOf<String>()

        dbHelper.readableDatabase.use { db ->
            val query = """
            SELECT 
                ${DataBaseHelper.COLUMN_ACTOR_NOMBRE} || ' ' || ${DataBaseHelper.COLUMN_ACTOR_APELLIDO} AS nombre_completo 
            FROM ${DataBaseHelper.TABLE_ACTOR} 
            WHERE ${DataBaseHelper.COLUMN_PELICULA_ID} = ?
        """

            val cursor = db.rawQuery(query, arrayOf(peliculaId.toString()))

            cursor.use {
                if (it != null && it.moveToFirst()) {
                    do {
                        val nombreCompleto = it.getString(it.getColumnIndexOrThrow("nombre_completo"))
                        actores.add(nombreCompleto)
                    } while (it.moveToNext())
                }
            }
        }

        return actores
    }

    fun obtenerDirectoresPorPeliculaId(peliculaId: Int): List<String> { // Cambia Long a Int
        val directores = mutableListOf<String>()

        dbHelper.readableDatabase.use { db ->
            val query = """
            SELECT 
                ${DataBaseHelper.COLUMN_DIRECTOR_NOMBRE} || ' ' || ${DataBaseHelper.COLUMN_DIRECTOR_APELLIDO} AS nombre_completo 
            FROM ${DataBaseHelper.TABLE_DIRECTOR} 
            WHERE ${DataBaseHelper.COLUMN_PELICULAID} = ?
        """

            val cursor = db.rawQuery(query, arrayOf(peliculaId.toString()))

            cursor.use {
                if (it != null && it.moveToFirst()) {
                    do {
                        val nombreCompleto = it.getString(it.getColumnIndexOrThrow("nombre_completo"))
                        directores.add(nombreCompleto)
                    } while (it.moveToNext())
                }
            }
        }

        return directores
    }
}