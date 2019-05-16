# agar_io
Integrantes:
Luis Fernando Muñoz
Leonardo Franco
Camilo Sepúlveda

base de datos http:
localhost:8081

iniciar clase Server del paquete server, HiloAtencionServidores del paquete Server_DB primero para poder ejecutar los llamados de los clientes por la página o por el juego.
Después de 40 segundos de iniciado el servidor, si la partida logra iniciarse, el juego finalizará y se guardarán los puntajes siempre que ambos servidores (DB y Server) estén encendidos.
Si existe alguna excepción en el método startSSL, cambiar la ruta quitando el inicio de "agar.io/" en los System.setProperty.
Cuando se inicia la clase Server, no carga los usuarios creados en partidas pasadas, pero sí quedan registrados para llamar en la página con su correo o su Nickname y contraseña.