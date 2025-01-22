# protectoraAnimalesAPI
Primera Actividad de Aprendizaje de la asignatura Acceso a Datos en Centro San Valero.



+---------------------------------------------------------------+
                          INTRODUCCIÓN
+---------------------------------------------------------------+

Esta API es una herramienta para gestionar una protectora de animales. En este caso se le ha dado título: tiene como nombre "Refugio Bastet" en honor a la diosa egipcia de nombre homónimo con forma de gato que, entre otras cosas, representa la protección (base sobre la que se debería erigir cualquier protectora de animales). Por lo tanto, ponemos a disposición esta aplicación para facilitar la gestión de un centro de estas características.



+----------------------------------------------------------+
                    CARACTERÍSTICAS TÉCNICAS
+----------------------------------------------------------+

Utiliza java, por lo que se necesita tener instalado un JRE (Java Runtime Environment) o JDK (Java Development Kit) para ejecutarse.

Para iniciar la API se ha estado utilizando IntelliJ IDEA. Hay que abrir el terminal que ofrece el IDE y escribir: mvn spring-boot:run para ejecutar la API (si hubiese algún error, probar a escribir mvn clean y/o limpiar cachés desde el menú de herramientas). Para pararla, ir al proyecto y utilizar el atajo de teclado Ctrl + C.

No hay que preocuparse por la base de datos al trabajarse con H2, por lo que generará un fichero en el dispositivo en el que irá guardando los datos.

Si por alguna circunstancia hubiese problemas para ejecutar la API, también se podría probar a cambiar el puerto de escucha del servidor, ya que podría suceder que estuviese utilizándolo algún proceso externo a esta API.

Se podría utilizar una colección Postman para probar las llamadas a los endpoints.
