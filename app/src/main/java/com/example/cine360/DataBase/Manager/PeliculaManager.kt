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

    /*Funcion para insertar una lista en la base de datos*/
    fun insertarPeliculas(db: SQLiteDatabase, peliculas: List<Pelicula>): List<Long> {
        /*Creamos una variable que contendra una lista con el id de las peliculaas*/
        val ids = mutableListOf<Long>()
        try {
            /*Creamos una instancia con la base de datos*/
            db.beginTransaction()
            /*Usamos un for each para iterar sobre la lista de peliculas*/
            peliculas.forEach { pelicula ->
                /*Creamos un objeto content values con el cual almacenamos los valores que se van a insertar en
                * una fila de la base de datos llamando a los nombres de las columnas con sus valores*/
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
                /*Llamamos a la base de datos y insertamos en la base de datos los valores en la tabla pelicula*/
                val id = db.insert(DataBaseHelper.TABLE_PELICULA, null, values)
                /*Si el id no existe mostramos un error*/
                if (id == -1L) {
                    throw SQLException("Error al insertar película: ${pelicula.titulo}")
                }
                ids.add(id)
            }
            /*Indicamos que se a realizado correctamente la operacion*/
            db.setTransactionSuccessful()
        } catch (e: SQLException) {
            Log.e(TAG, "Error al insertar películas: ${e.message}")
        } finally {
            db.endTransaction()
        }
        return ids
    }

    /*Funcion apra insertar las peliculas en la base de datos a la hora de actualizar o iniciar de 0 la base de datos*/
    fun insertarPeliculasPrecargadas(db: SQLiteDatabase) {
        val peliculas = listOf(
            Pelicula(1, "Solo en casa", "Un niño de 8 años se queda abandonado en casa mientras su familia está de vacaciones.", "Comedia", "21/12/1990", 100, "soloencasa.jpg", "https://www.youtube.com/watch?v=4nXRb-1-n60&ab_channel=CineFantasia", "1"),
            Pelicula(2, "Intocable", "Un millonario que ha quedado tetrapléjico debido a un accidente contrata a un inmigrante recién salido de la cárcel.", "Comedia", "9/03/2012", 109, "intouchables.jpg", "trailer2.mp4", "1"),
            Pelicula(3, "Dos tontos muy tontos", "Dos amigos con una gran estupidez. Lloyd se enamora de una chica que olvida su maletín y emprenden un viaje por el país para devolvérselo.", "Comedia", "30/03/1995", 106, "dostontos.jpg", "trailer3.mp4", "1"),
            Pelicula(4, "El Dictador", "El dictador de Wadiya viaja a Estados Unidos siguiendo el consejo de su tío con la intención de impedir el establecimiento de la democracia en su país.", "Comedia", "13/07/2012", 110, "dictador.jpg", "trailer4.mp4", "1"),
            Pelicula(5, "Ali G", "Ali G tiene una vida tranquila en su barrio, pero todo cambia cuando se entera de que el centro de ocio de Staines está a punto de cerrar.", "Comedia", "22/03/2002", 88, "alig.jpg", "trailer5.mp4", "1"),
            Pelicula(6, "Ted", "John Bennett deseaba que su oso de peluche fuera de verdad. Más tarde, ese sueño se hace realidad.", "Comedia", "10/08/2012", 106, "ted.jpg", "trailer6.mp4", "1"),
            Pelicula(7, "Campeones", "Un entrenador profesional de baloncesto se encuentra en medio de una crisis personal. Entrena a un equipo de personas con discapacidad intelectual, y esta experiencia acaba siendo una lección de vida.", "Comedia", "06/04/2018", 124, "campeones.jpg", "trailer7.mp4", "1"),
            /*Películas de la semana 2 ROMANCE*/
            Pelicula(8, "Todos los días de mi vida", "Un accidente de tránsito deja a Paige en coma y, al despertar, ha sufrido una gran pérdida de memoria, olvidando a su esposo. Este deberá esforzarse para ganar su corazón otra vez.", "Romance", "23/03/2012", 104, "todoslosdias.jpg", "trailer8.mp4", "2"),
            Pelicula(9, "Querido John", "Un soldado se enamora de una estudiante universitaria conservadora mientras está de vacaciones en casa.", "Romance", "23/04/2010", 105, "queridojohn.jpg", "trailer9.mp4", "2"),
            Pelicula(10, "Bajo la misma estrella", "Dos pacientes de cáncer inician un viaje de afirmación de vida para visitar a un autor recluido en Ámsterdam.", "Romance", "04/07/2014", 125, "bajolamisma.jpg", "trailer9.mp4", "2"),
            Pelicula(11, "Titanic", "Una aristócrata de diecisiete años se enamora de un amable pero pobre artista a bordo del lujoso y desafortunado R.M.S. Titanic.", "Romance", "08/01/1998", 195, "titanic.jpg", "trailer9.mp4", "2"),
            Pelicula(12, "Perdona si te llamo amor", "Alex es un ejecutivo de éxito en busca de la estabilidad emocional.", "Romance", "19/06/2014", 105, "perdonasitellamoamor.jpg", "trailer9.mp4", "2"),
            Pelicula(13, "3 metros sobre el cielo", "Una mujer privilegiada y un hombre temerario se enamoran a pesar de la diferencia de clase entre ambos.", "Romance", "03/12/2010", 120, "metros.jpg", "trailer9.mp4", "2"),
            Pelicula(14, "Un pedacito de cielo", "Marley es una bella mujer que padece cáncer, pero se enamora de su médico.", "Romance", "04/05/2012", 120, "unpedacito.jpg", "trailer9.mp4", "2"),

            /*Películas de la semana 3 TERROR*/
            Pelicula(15, "Terrifier", "El payaso psicópata Art aterroriza a dos chicas durante la noche de Halloween, matando a todos aquellos que se interponen en su camino.", "Terror", "15/03/2018", 85, "terrifier.jpg", "trailer9.mp4", "3"),
            Pelicula(16, "Smile", "Después de presenciar un accidente extraño y traumático que involucra a un paciente, la Dra. comienza a experimentar sucesos aterradores que no puede explicar.", "Terror", "22/09/2022", 115, "smile.jpg", "trailer9.mp4", "3"),
            Pelicula(17, "Relic", "Una hija, una madre y una abuela son acosadas por un tipo de demencia que está consumiendo a la familia.", "Terror", "03/07/2020", 86, "relic.jpg", "trailer9.mp4", "3"),
            Pelicula(18, "Audition", "Un cuarentón viudo, a propuesta de un amigo, convoca un casting para una inexistente película con la intención de encontrar una nueva esposa.", "Terror", "19/07/2002", 115, "audition.jpg", "trailer9.mp4", "3"),
            Pelicula(19, "La matanza de Texas", "Cinco adolescentes visitan la tumba, supuestamente profanada, del abuelo de uno de ellos. Cuando llegan, se encuentran con un siniestro matadero.", "Terror", "01/10/1999", 83, "lamatanza.jpg", "trailer9.mp4", "3"),
            Pelicula(20, "Arrástrame al infierno", "Una joven trabaja en un banco dando préstamos hipotecarios. Un día decide negarle ese préstamo a una señora mayor; esta pierde su casa y decide vengarse de ella lanzándole una maldición.", "Terror", "31/07/2009", 95, "arrastrame.jpg", "trailer9.mp4", "3"),
            Pelicula(21, "Anticristo", "Un psicólogo, que quiere ayudar a su mujer a superar la muerte de su hijo en un accidente, la lleva a una cabaña perdida en medio del bosque. Esto no funciona y ella comienza a comportarse extraño.", "Terror", "21/08/2009", 104, "anticristo.jpg", "trailer9.mp4", "3"),
            /*Películas de la semana 4 ACCIÓN*/
            Pelicula(22, "Jungla de cristal", "En lo alto de la ciudad de Los Ángeles, un grupo terrorista se ha apoderado de un edificio, tomando a un grupo de personas como rehenes. Un policía solo y fuera de servicio se enfrentará a los secuestradores.", "Acción", "30/09/1988", 131, "jungla.jpg", "trailer9.mp4", "4"),
            Pelicula(23, "Terminator 2: El juicio final", "John es entrenado por su madre como un guerrero, debido a que su madre fue informada por un viajero del tiempo que este sería el salvador del mundo. Un androide mejorado llega a la época para matar a John, pero un androide T-800 es enviado para protegerle.", "Acción", "05/12/1991", 135, "terminator2.jpg", "trailer9.mp4", "4"),
            Pelicula(24, "Kill Bill", "El día de su boda, una asesina profesional sufre un ataque de algunos miembros de su propia banda. Ella logra sobrevivir al ataque, aunque queda en coma. Cuatro años después despierta dominada por un gran deseo de venganza.", "Acción", "10/10/2003", 110, "killbill.jpg", "trailer9.mp4", "4"),
            Pelicula(25, "Mad Max: Furia en la carretera", "Mad Max cree que la mejor forma de sobrevivir es ir solo por el mundo. Se ve arrastrado a formar parte de un grupo que huye a través del desierto en un War Rig conducido por una emperatriz de élite.", "Acción", "15/05/2015", 120, "maxmax.jpg", "trailer9.mp4", "4"),
            Pelicula(26, "Matrix", "Thomas Anderson es un brillante programador de una respetable compañía de software. Pero fuera de su trabajo es Neo, un hacker que un día recibe una misteriosa visita...", "Acción", "23/06/1999", 131, "matrix.jpg", "trailer9.mp4", "4"),
            Pelicula(27, "Misión imposible", "Ethan Hunt es un superespía capaz de llevar a cabo la misión más peligrosa con la máxima eficacia y elegancia.", "Acción", "05/07/1996", 110, "misionimposible.jpg", "trailer9.mp4", "4"),
            Pelicula(28, "Superman", "Desde una galaxia remota, un recién nacido es enviado por sus padres al espacio debido a la inminente destrucción de su planeta. La nave aterriza en la Tierra y es adoptado por unos granjeros que le inculcan los mejores valores humanos.", "Acción", "08/02/1979", 143, "superman.jpg", "trailer9.mp4", "4"),
        )
            insertarPeliculas(db, peliculas)
    }

    /*Funcion para obtener las peliculas de la base de datos a partir de una lista*/
    fun obtenerPeliculaConDb(db: SQLiteDatabase): List<Pelicula> {
        /*Creamos una variable que contendra una lista de los datos de las peliculas*/
        val peliculas = mutableListOf<Pelicula>()
        /*Usamos un cursos para realizar una sentencia query con la cual llamamos a obtener todos los datos
        * de la tabla pelicula*/
        val cursor = db.rawQuery("SELECT * FROM ${DataBaseHelper.TABLE_PELICULA}", null)

        cursor.use {
            /*Indicamos que coga el primer dato de todo*/
            if (it.moveToFirst()) {
                /*Declaramos una variable y le indicamos que obtenga los datos especificados y obtenemos el indice de cada uno*/
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
                    /*Llamamos a la tabla pelicula y le pasamos los datos obtenidos*/
                    val pelicula = Pelicula(id, titulo, descripcion, genero, fechaLanzamiento, duracion, imagen, trailer, semana)
                    /*Llamamos a peliculas y le pasamos todos los datos para que estos sean añadidos*/
                    peliculas.add(pelicula)
                } while (it.moveToNext())
            }
        }
        return peliculas
    }

    /*Funcion para obtener las peliculas por id*/
    fun obtenerPeliculaPorId(peliculaId: Int): Pelicula? {
        var pelicula: Pelicula? = null
        /*Creamos una instancia*/
        dbHelper.readableDatabase.use { db ->
            /*Llamamos a la base de datos y hacemos una consulta indicando que obtenga todos los datos
            * de la tabla pelicula por id*/
            val cursor = db.rawQuery(
                "SELECT * FROM ${DataBaseHelper.TABLE_PELICULA} WHERE ${DataBaseHelper.COLUMN_Pelicula_ID} = ?",
                arrayOf(peliculaId.toString())
            )
            /*Cogemos los datos, indicamos que coga todos y obtenemos el indice de cada uno*/
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

                    /*Llamamos a actores y indicamos que use la funcion para obtener los actores
                    * y directores mediante el id de la pelicula a la que estan asociados*/
                    val actores = obtenerActoresPorPelicula(peliculaId)
                    val directores = obtenerDirectoresPorPeliculaId(peliculaId)
                    /*Creamos un mapeado de los directores para obtener toods los datos de los directores*/
                    val listaDirectores = directores.mapNotNull { nombreCompleto ->
                        if (nombreCompleto != null) {
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
                    /*Creamos un mapeado de los actores para obtener toods los datos de los actores*/
                    val listaActores = actores.mapNotNull { nombreCompleto ->
                        val partes = nombreCompleto.trim().split(" ")
                        if (partes.size >= 2) {
                            Actor(nombre = partes[0], apellido = partes[1], peliculaId = peliculaId.toLong())
                        } else {
                            Log.e("DetallesActivity", "Nombre de actor inesperado: $nombreCompleto")
                            null
                        }
                    }
                    /*Llamamos a pelicula y le pasamos todos los datos, tanto los de pelicula como los de lista de actores y directores*/
                    pelicula = Pelicula(id, titulo, descripcion, genero, fechaLanzamiento, duracion, imagen, trailer, semana, listaDirectores, listaActores)
                }
            }
        }
        return pelicula
    }

    /*Funcion para asignar las peliculas a la semanas*/
    fun asignarSemanasAPeliculas(db: SQLiteDatabase) {
        /*Llamamos a la funcion con la cual obtenemos las peliculas a raiz de la base de datos*/
        val peliculas = obtenerPeliculaConDb(db)

        try {
            /*Creamos un for each para iterar y obtener todos los datos de las peliculas*/
            peliculas.forEach { pelicula ->
                val semana = pelicula.semana
                /*Creamos un concten values para obtener los datos de las semanas*/
                val values = ContentValues()
                values.put(DataBaseHelper.COLUMN_SEMANA, semana)
                /*Realizamos una consulta para obtener el id de las peliculas
                * y almacenarla*/
                val whereClause = "${DataBaseHelper.COLUMN_Pelicula_ID} = ?"
                val whereArgs = arrayOf(pelicula.id.toString())
                /*Actualizamos la tabla peliculas asignadole las semanas*/
                db.update(DataBaseHelper.TABLE_PELICULA, values, whereClause, whereArgs)
            }
        } catch (e: Exception) {
            Log.e("PeliculaManager", "Error al asignar semanas a películas: ${e.message}")
        }
    }

    /*Funcion para obtener los actores por pelicula*/
    fun obtenerActoresPorPelicula(peliculaId: Int): List<String> {
        /*Lista para guardar todos los datos*/
        val actores = mutableListOf<String>()
        /*Creamos una instancia con la base de datos en la cual hacemos una consulta,
        * mediante la cual obtenemos los datos del actor llamando a su tabla y filtrando todo
        por el id de  la pelicula*/
        dbHelper.readableDatabase.use { db ->
            val query = """
            SELECT 
                ${DataBaseHelper.COLUMN_ACTOR_NOMBRE} || ' ' || ${DataBaseHelper.COLUMN_ACTOR_APELLIDO} AS nombre_completo 
            FROM ${DataBaseHelper.TABLE_ACTOR} 
            WHERE ${DataBaseHelper.COLUMN_PELICULA_ID} = ?
        """
            /*Asignamos los datos a la lista anteriormente creada*/
            val cursor = db.rawQuery(query, arrayOf(peliculaId.toString()))
            /*Usamos un cursor para pasar el nombre completo*/
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
    /*Funcion para obtener los directores por el id de la pelicula*/
    fun obtenerDirectoresPorPeliculaId(peliculaId: Int): List<String> {
        /*Creamos una lista para guardar todos los datos*/
        val directores = mutableListOf<String>()

        /*Creamos una instancia con la base de datos en la cual hacemos una consulta,
       * mediante la cual obtenemos los datos del director llamando a su tabla y filtrando todo
       por el id de  la pelicula*/
        dbHelper.readableDatabase.use { db ->
            val query = """
            SELECT 
                ${DataBaseHelper.COLUMN_DIRECTOR_NOMBRE} || ' ' || ${DataBaseHelper.COLUMN_DIRECTOR_APELLIDO} AS nombre_completo 
            FROM ${DataBaseHelper.TABLE_DIRECTOR} 
            WHERE ${DataBaseHelper.COLUMN_PELICULAID} = ?
        """
            /*Asignamos los datos a la lista anteriormente creada*/
            val cursor = db.rawQuery(query, arrayOf(peliculaId.toString()))
            /*Usamos un cursor para pasar el nombre completo*/
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