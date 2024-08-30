En nuestro proyecto, hemos establecido una organización de ramas que facilita el desarrollo, la prueba y la implementación de nuevas funcionalidades de manera estructurada. Hay tres tipos principales de ramas que utilizamos:

Rama MAIN (anteriormente conocida como master):

Esta rama es la rama principal y representa nuestra rama de producción.
En MAIN, mantenemos la versión estable y lista para el entorno de producción.
No se permiten desarrollos directos en esta rama para garantizar la estabilidad del producto.


Rama Develop:

La rama Develop es nuestra rama de pruebas.
Todas las nuevas características y desarrollos comienzan en esta rama antes de ser integradas en la rama principal.
Actúa como un área de prueba para asegurar que los cambios no afecten negativamente a la versión de producción.


Rama Feature:

Las ramas Feature son donde los desarrolladores crean nuevas funcionalidades o realizan modificaciones.
Cada rama Feature debe ser creada a partir de la rama Develop.
Una vez que el desarrollo en la rama Feature está completo, se integra de nuevo en la rama Develop para las pruebas de calidad (QA).
Esta estructura de ramas nos permite trabajar de manera eficiente, garantizando que cada cambio se someta a pruebas exhaustivas antes de llegar a la versión de producción. Además, ayuda a mantener una clara separación entre el desarrollo en curso y la versión estable en producción.

Ejemplo de las 3 ramas

![image](https://github.com/DEVIS0306/InterPost/assets/80374089/40c43fd7-ac30-4ba8-83dc-3f695c0428fc)
