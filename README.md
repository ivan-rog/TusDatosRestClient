# TusDatosRestClient

Tus-Datos-Client

Cliente HTTP reactivo para el consumo de la API REST de validación documental de TusDatos.

## Descripción

Este proyecto implementa un cliente reactivo para el procesamiento y validación de documentos. Utiliza Spring WebFlux para realizar operaciones asíncronas y no bloqueantes.

### Características principales

- Procesamiento asíncrono de documentos
- Implementación reactiva usando Project Reactor
- Reintentos automáticos para errores específicos
- Sistema de polling para verificación de estado de trabajos
- Generación de reportes en formato JSON

### Flujo de procesamiento

1. **Lanzamiento del trabajo**: Inicia el proceso de validación documental
2. **Verificación de estado**: Realiza consultas periódicas para verificar el estado del trabajo
3. **Sistema de reintentos**: Ejecuta hasta 3 reintentos automáticos si se detectan errores específicos
4. **Generación de reporte**: Obtiene el resultado final del procesamiento en formato JSON

![img.png](img.png)

### Tecnologías utilizadas

- Java
- Spring Boot
- Spring WebFlux
- Project Reactor
- Maven

## Configuración

El servicio requiere las siguientes configuraciones en el archivo de propiedades:

- `configuration.tusdatos.endpoint.launch`: Endpoint para iniciar el proceso
- `configuration.tusdatos.endpoint.job.status`: Endpoint para consultar estado
- `configuration.tusdatos.endpoint.job.retry`: Endpoint para reintentos
- `configuration.tusdatos.endpoint.report.json`: Endpoint para reportes
- `configuration.tusdatos.job.status.initial.delay.seconds`: Delay inicial para polling
- `configuration.tusdatos.job.status.polling.interval.seconds`: Intervalo de polling
- `configuration.tusdatos.job.status.timeout.seconds`: Tiempo máximo de espera
- `configuration.tusdatos.retry.errors`: Lista de errores que permiten reintento
