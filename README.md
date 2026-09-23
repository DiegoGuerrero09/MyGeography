# 🌍 MyGeography

**MyGeography** (v1.2.2) es una aplicación moderna para Android desarrollada con **Kotlin** y **Jetpack Compose** orientada a aprender, practicar y poner a prueba conocimientos de geografía mundial: banderas, capitales, modo mixto, estadísticas regionales, filtros analíticos y atlas de naciones.

---

## ✨ Características Principales

### 1. 🌐 Ámbito de Juego: Global o por Continentes
Al pulsar sobre cualquiera de los modos de juego, la aplicación despliega un diálogo limpio y directo para elegir el ámbito del test:
- 🌐 **Global** (todas las naciones del modo seleccionado)
- 🇪🇺 **Europa**
- 🌍 **África**
- 🌏 **Asia**
- 🌊 **Oceanía**
- 🏔️ **Sudamérica & Antártida**
- 🌎 **Norteamérica & Centroamérica**

> **Opciones globales en tests por continente:** Cuando se juega un test por continente, las preguntas corresponden a ese continente pero los distractores se seleccionan del universo global, ofreciendo una dificultad y variedad completas.

### 2. 🏳️ Test de banderas (254 territorios o por continente)
- En modo global abarca las **254 banderas del mundo** correspondientes a [banderas-mundo.es/examen](https://www.banderas-mundo.es/examen), o el subconjunto específico del continente elegido.
- Para cada pregunta se muestra el **nombre del país** (con ajuste automático de tamaño para nombres largos en una sola línea) y abajo una **cuadrícula de 6 filas x 2 columnas (12 opciones)** de banderas.
- Las opciones se expanden proporcionalmente hasta ocupar todo el espacio vertical disponible de la pantalla.
- Banderas sin rebordes artificiales para representar fielmente banderas con proporciones no estándar (Suiza, Nepal, Ciudad del Vaticano, etc.).
- **Filtro de banderas idénticas o confusas:** No permite que territorios con la misma bandera gráfica (como Noruega y Svalbard y Jan Mayen, o EE.UU. e Islas Ultramarinas Menores) aparezcan juntos en las opciones de una pregunta.
- Feedback visual interactivo en tiempo real:
  - Se ilumina en **verde** al seleccionar la opción correcta.
  - Se ilumina en **rojo** si se elige una opción errónea, iluminando simultáneamente la opción correcta en **verde**.
- Transición automática fluida a la siguiente pregunta tras la evaluación.
- Botón de retroceso para volver al menú inicial en cualquier momento con diálogo de confirmación.
- Marcador en vivo de preguntas respondidas, aciertos y fallos con barra de progreso.

### 3. 🏛️ Test de capitales (254 territorios o por continente)
- Abarca los **254 territorios y naciones del mundo** (o el subconjunto del continente seleccionado), permitiendo practicar tanto capitales de países soberanos como de territorios autónomos y de ultramar.
- Presenta el país en una cabecera dividida: **bandera fija a la izquierda sin reborde** y **nombre/continente a la derecha**.
- **12 opciones de capitales únicas ordenadas alfabéticamente en español** en una columna vertical (12 filas x 1 columna) con nombres ajustados para garantizar una sola línea y evitar desbordamientos.
- Las 12 tarjetas de capital se expanden ocupando uniformemente toda la pantalla.
- Mismo feedback visual interactivo y transición automática.

### 4. 🔀 Test mixto (254 países • Bandera + Capital)
- Nuevo modo accesible desde el menú principal con **borde rojo distintivo (`#EF4444`)** y etiqueta indicativa `254 PAÍSES`.
- **Cabecera estilizada de 3 líneas:** Muestra arriba en color rojo la etiqueta `BANDERA Y CAPITAL DE`, en el centro el nombre del país (con ajuste dinámico de tamaño) y abajo el continente, todo perfectamente centrado verticalmente.
- Cuadrícula de **8 banderas parecidas en 4 filas y 2 columnas** (1 correcta y 7 distractores seleccionados inteligentemente por familias visuales y cercanía geográfica: escandinavas, stanes, crucíferas británicas, tricolores eslavas, africanas, árabes, etc.).
- **Emparejamiento de banderas de alta similitud:** Prioridad automática para pares clave como Micronesia y Somalia, Taiwán y Samoa, o Afganistán y Arabia Saudita.
- **Exclusión estricta de banderas idénticas:** Se evita que banderas idénticas (p. ej. Noruega y Svalbard y Jan Mayen) aparezcan juntas.
- **Mecánica ágil de evaluación en dos fases:**
  1. **Selección de bandera:** El usuario elige primero la bandera con indicador `Elige una bandera...`. Si falla, se contabiliza de inmediato como error en bandera y se revela la capital correcta.
  2. **Escritura de capital:** Al acertar la bandera, el test pasa de forma automática e inmediata a la caja de texto de la capital, sin necesidad de pulsar ningún botón adicional de comprobación.
- **Campo de capital estilizado:** Caja de texto con elegante reborde celeste, compatible con la apertura del teclado virtual manteniendo en todo momento accesibles los botones de navegación y rendirse.
- **Validador inteligente de capitales (`ValidadorCapital`):**
  - Insensible a mayúsculas, minúsculas, tildes (p. ej. *Valparaiso* por *Valparaíso*) y espacios sobrantes.
  - Soporte automático para capitales con denominación "Ciudad de X" (p. ej. admitiendo *Panamá* en lugar de *Ciudad de Panamá*, *México* por *Ciudad de México*, *Guatemala*, *Kuwait*, etc.).
  - Admite sustitución de caracteres nórdicos o especiales y guiones por espacios.
  - Valida nombres compuestos con sus espacios correspondientes (p. ej. *Pago Pago*).
  - Exclusiones estrictas verificadas: Kiribati solo admite *Tarawa Sur* (rechazando *Tarawa* sola), Suiza solo admite *Berna* (rechazando *Bern*), Andorra solo admite *Andorra la Vieja* o *Andorra la Vella* (rechazando *Andorra* a secas) y Estados Unidos no admite solo *Washington* (requiere *Washington DC* o *Washington D.C.*).
  - En países con múltiples capitales oficiales (Sudáfrica, Bolivia, Países Bajos, etc.), se valida como correcta cualquiera de ellas.
- **Feedback enriquecido en una sola línea:** Tanto al acertar como al fallar o rendirse, se muestra la capital oficial (`¡Correcto! La capital era: ...`), adaptando el tamaño tipográfico con `TextoAjustable` para que siempre quepa íntegramente en una única línea, incluso en países con múltiples capitales.
- **Botón de Rendirse:** Centrado verticalmente con icono y texto en una sola línea; permite avanzar mostrando la capital correcta si no se recuerda, contando como fallo en capital.
- **Cascada de estadísticas:** Al alcanzar un porcentaje en el Test Mixto, las puntuaciones de Banderas y Capitales se elevan automáticamente a dicho porcentaje como mínimo.

### 5. 🌐 Globo terráqueo 3D interactivo
- Acceso directo desde el botón `[ 🌐 Globo 3D ]` en la cabecera del menú principal.
- **Proyección ortográfica esférica 3D en tiempo real:**
  - Renderizado nativo por GPU en Compose Canvas a 60/120 FPS.
  - Masas continentales y costas del planeta detalladas con líneas esmeralda.
  - Atmósfera exterior celeste con halo de dispersión y océano profundo sombreado.
  - Cuadrícula geográfica de graticules con ecuador, paralelos y meridianos.
- **Navegación e interacción total:**
  - **Rotación libre 360°:** Desliza el dedo horizontal y verticalmente para rotar el planeta en cualquier dirección.
  - **Zoom In / Zoom Out:** Mediante gesto de pellizco con dos dedos o mediante los botones flotantes laterales `+` y `-`.
  - **Auto-giro:** Botón para activar o pausar una rotación orbital continua automática.
  - **Botón de centrado:** Restablece la vista original o centra el planeta sobre el país seleccionado.
- **Vista previa de nombres de países clara y legible:**
  - En la superficie del globo, los países visibles proyectan marcadores luminosos y etiquetas con fondo oscuro de alto contraste y texto blanco nítido.
- **Buscador integrado con autocompletado:**
  - Permite escribir el nombre de cualquier país o capital y el globo rotará automáticamente para centrarlo y seleccionarlo con animación suave.
- **Tarjeta de detalle de país:**
  - Al pulsar cualquier país en el globo, se despliega una tarjeta inferior con su **bandera en alta resolución**, **nombre oficial**, **capital oficial**, badges de continente/región y soberanía, coordenadas geográficas y botón para centrar la cámara.

### 6. 📊 Resumen del test, filtros y desglose de fallos
- Pantalla de resumen detallado con recálculo dinámico de métricas:
  - Tarjeta de resumen con gran porcentaje, aciertos y fallos.
  - **Filtro por categorías ordenado prioritariamente:**
    - En tests globales: *Global*, seguido inmediatamente por *Independientes* (Dorado) y *Dependientes* (Gris), y a continuación los continentes (*África*, *Asia*, *Europa*, *Norteamérica & Centroamérica*, *Oceanía*, *Sudamérica & Antártida*) con su paleta de colores unificada.
    - En tests regionales: por *Todos*, *Independientes* y *Dependientes*.
  - **Filtro por tipo de fallo en Test Mixto:** Estructura idéntica y simétrica a los otros tests con pestañas dedicadas:
    - `Todas`, `Acertadas`, `Falladas`
    - Subpestañas de fallos: `Todos los fallos`, `Fallos de bandera`, `Fallos de capital`
    - Cajas de fallos de bandera y de capital integradas sin fondos rojos estridentes.
  - **Mensajes de felicitación adaptables:** Textos *"¡Excelente! No cometiste ningún fallo en las banderas."* y *"¡Excelente! No cometiste ningún fallo en las capitales."* con autoajuste de tipografía garantizando siempre una sola línea.
  - Detalle individual de cada respuesta con estado, bandera elegida y capital escrita/correcta.
  - **Propagación de récords:** Al completar un test global, se calculan y actualizan automáticamente las puntuaciones para cada continente.
- **Apartado de estadísticas:** Tarjeta de 3 columnas en menú principal para consultar récords de Banderas, Capitales y Mixto en todos los ámbitos.

### 7. 📖 Listado de países (País • Capital • Bandera)
- Atlas con las 254 naciones ordenadas alfabéticamente en español (incluyendo *República Democrática del Congo*).
- **Ajuste automático de tipografía (`NombrePaisAutoAjustable`, `CapitalAutoAjustable` y `DependienteDeAutoAjustable`):** Mide dinámicamente el ancho y reduce el tamaño de letra para que jamás se corte ningún texto de dependencias ni nombres largos.
- Buscador en tiempo real por nombre de país, capital o código ISO.
- **Consistencia cromática total:** Filtros y badges de región y soberanía con los mismos tonos que el menú principal (África en ámbar cálido, Europa en celeste, Asia en rojo coral, etc.).
- Badges uniformes de estado (**Soberano/Territorio**) y región.
- Filtros rápidos por continente y grado de soberanía.

### 8. 🎨 Interfaz responsiva y menú principal
- Menú principal responsivo con los 5 bloques expandidos uniformemente ocupando la pantalla completa.
- Diálogo de confirmación para salir del test con botón **Continuar** destacado en azul brillante para máxima visibilidad.
- Iconografía clara y tema oscuro moderno (*Dark Geography*).
- 100% Offline sin dependencias externas en tiempo de ejecución.

---

## 🛠️ Arquitectura y Tecnologías

- **Lenguaje:** Kotlin 1.9.23
- **UI:** Jetpack Compose con Material Design 3 (Material 3)
- **Navegación:** Jetpack Navigation Compose
- **Persistencia:** SharedPreferences para registro de mejores puntuaciones por modo y región
- **Arquitectura:** MVVM (Model-View-ViewModel) + StateFlow + Repository Pattern
- **Splash Screen:** Pantalla de inicio animada
- **Icono:** Icono adaptativo personalizado con líneas longitudinales azules
- **Paleta Visual:** Tema oscuro moderno (*Dark Geography*) con acentos en azul cielo, cian, ámbar, púrpura y esmeralda

---

## 📁 Estructura del Proyecto

```text
MyGeography/
├── app/
│   ├── src/main/
│   │   ├── assets/
│   │   │   └── flags/              # 254 banderas en formato PNG optimizado
│   │   ├── java/com/diegoguerrero/mygeography/
│   │   │   ├── MainActivity.kt     # Grafo de navegación y ciclo de vida
│   │   │   ├── data/
│   │   │   │   ├── datasource/     # PaisesData.kt, CoordenadasPaises.kt, TierrasData.kt
│   │   │   │   ├── model/          # Pais, Continente, RegionQuiz, QuizPregunta, etc.
│   │   │   │   ├── preferences/    # EstadisticasManager.kt (récords globales y regionales)
│   │   │   │   └── repository/     # PaisesRepository con 12 opciones y soporte regional
│   │   │   ├── ui/
│   │   │   │   ├── components/     # BanderaImage sin reborde
│   │   │   │   ├── screens/
│   │   │   │   │   ├── splash/     # SplashScreen animada
│   │   │   │   │   ├── menu/       # MenuScreen (4 botones expandidos, botón Globo 3D, stats)
│   │   │   │   │   ├── quiz/       # QuizScreen (6x2 banderas, 12x1 capitales, auto-advance)
│   │   │   │   │   ├── resumen/    # ResultadosScreen (mensajes vacíos, actualización por continente)
│   │   │   │   │   ├── listado/    # ListadoPaisesScreen (fuente autoajustable, badges de color)
│   │   │   │   │   └── globo/      # GloboTerraqueoScreen (globo 3D interactivo, rotación, zoom)
│   │   │   │   └── theme/          # Paleta Color.kt y Theme
│   │   └── res/
│   │       ├── drawable/           # Icono vectorial ic_launcher_foreground.xml (líneas azules)
│   │       └── mipmap-*/           # Iconos adaptativos de la aplicación
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── .gitignore                      # Limpio de archivos residuales
└── README.md
```

---

## 🚀 Compilación y Ejecución

Para compilar el proyecto en modo depuración desde la terminal:

```bash
./gradlew compileDebugKotlin
```

O generar el APK instalable:

```bash
./gradlew assembleDebug
```
El archivo APK generado se ubica en: `app/build/outputs/apk/debug/app-debug.apk`.
