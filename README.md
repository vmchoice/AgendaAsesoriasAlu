
**PARA : MC ROSSAINS**

**DE : VLADIMIR ANDRIEYNKO 201336828**

**ASUNTO: Instrucción para abrir y evaluar el PF de APPWEB, CODIGO FUENTE viene en el WAR**

-------------

PASO 1: Instalar o Tener Instalado Tomcat 8.5 y MySQL más resiente ambos con configuración de fabrica.

PASO 2: Descargar el adjunto `PFVladAPPWEB2019.zip` en `C:\Temp` para fu fácil acceso

PASO 3: Extraer los archivos del `.zip`: `vladAWebAPPRoss.war` y `ProyectoFinalBD_4NF_Plus_Insert.sql`

PASO 4: Detener el Servicio de Tomcar por medio de la aplicación Configure Tomcat / Configurar Tomacat o
directamente en Servicios de Windows

PASO 5: Mover el archivo `.war` adentro de la carpeta webapps: `C:\Program Files(Archivo de Programas)\Apache Software Foundation\Tomcat 8.5\webapps\`

PASO 6: Iniciar el Servicio de Tomcar por medio de la aplicación Configure Tomcat / Configurar Tomacat o
directamente en Servicios de Windows

PASO 7: Despues del PASO 6 te nomos en la carpeta `<TomCatDir>\webapps` una nueva carpeta vladAWebAPPRoss con su CÓDIGO
FUENTE al lado de los compilados en la carpeta src. La pagina aun no funciona del todo.

PASO 8: Para que funcione completamente falta la base de datos. Abrimos <Applicacion de MySQL: MySQL 8.0 
Command Line Client - Unicode> o su equivalente en el idioma instalado. Proporcionamos la contraseña
del administrador y ejecutamos el siguiente comando para generar la base de datos: `source C:\Temp\ProyectoFinalBD_4NF_Plus_Insert.sql`

PASO 9: Abrir la pagina `localhost:8080/vladAWebAPPRoss/inicio.jsp` y Evaluar.
