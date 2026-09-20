# 🌍 MyGeography

**MyGeography** es una aplicación moderna para Android desarrollada con **Kotlin** y **Jetpack Compose** orientada a aprender, practicar y poner a prueba conocimientos de geografía mundial: banderas, capitales, estadísticas regionales y atlas de naciones.

---

## ✨ Características Principales

### 1. 🌐 Ámbito de Juego: Global o por Continentes
Al pulsar sobre cualquiera de los dos modos de juego, la aplicación despliega un diálogo limpio y directo para elegir el ámbito del test:
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
- Feedback visual interactivo en tiempo real:
  - Se ilumina en **verde** al seleccionar la opción correcta.
  - Se ilumina en **rojo** si se elige una opción errónea, iluminando simultáneamente la opción correcta en **verde**.
- Transición automática fluida a la siguiente pregunta tras la evaluación.
- Botón de retroceso para volver al menú inicial en cualquier momento con diálogo de confirmación en una sola línea.
- Marcador en vivo de preguntas respondidas, aciertos y fallos con barra de progreso.

### 3. 🏛️ Test de capitales (254 territorios o por continente)
- Ampliado para abarcar los **254 territorios y naciones del mundo** (o el subconjunto del continente seleccionado), permitiendo practicar tanto capitales de países soberanos como de territorios autónomos y de ultramar.
- Presenta el país en una cabecera dividida: **bandera fija a la izquierda sin reborde** y **nombre/continente a la derecha**.
- **12 opciones de capitales únicas ordenadas alfabéticamente en español** en una columna vertical (12 filas x 1 columna) con nombres ajustados para garantizar una sola línea y evitar desbordamientos o movimientos indeseados.
- Las 12 tarjetas de capital se expanden ocupando uniformemente toda la pantalla.
- Mismo feedback visual interactivo y transición automática.

### 4. 🌐 Globo terráqueo 3D interactivo
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
  - En la superficie del globo, los países visibles proyectan marcadores luminosos y etiquetas con fondo oscuro de alto contraste y texto blanco nítido, permitiendo identificar claramente las naciones antes y después de pulsar.
- **Buscador integrado con autocompletado:**
  - Permite escribir el nombre de cualquier país o capital y el globo rotará automáticamente para centrarlo y seleccionarlo con animación suave.
- **Tarjeta de detalle de país:**
  - Al pulsar cualquier país en el globo, se despliega una tarjeta inferior con su **bandera en alta resolución**, **nombre oficial**, **capital oficial**, badges de continente/región y soberanía, coordenadas geográficas (latitud y longitud) y botón para centrar la cámara.

### 5. 📊 Resumen del test y estadísticas dinámicas
- Pantalla de resumen detallado al finalizar el test:
  - Cajas de **Acertadas** y **Falladas** con texto centrado tanto vertical como horizontalmente.
  - Porcentaje de acierto global o regional.
  - Pestañas de filtrado: **Todas**, **Acertadas** y **Falladas**. Si una categoría no tiene elementos (por ejemplo, 0 fallos), muestra un mensaje descriptivo en una sola línea felicitando o explicando el resultado.
  - Detalle individual de cada pregunta con bandera, país y respuestas con ajuste dinámico de tamaño de texto.
  - **Propagación de récords:** Al completar un test global, la aplicación calcula y actualiza de forma automática las mejores puntuaciones para cada uno de los continentes individuales.
  - Botones de acción directa: **Volver al menú** o **Repetir**.
- **Apartado de estadísticas:** Botón en menú principal con reborde amarillo, franja superior indicativa `PUNTUACIONES` y selector interactivo con chips rápidos identificados por su paleta de colores para consultar las mejores puntuaciones obtenidas en **Global**, **Europa**, **África**, **Asia**, **Oceanía**, **Sudamérica & Antártida** y **Norteamérica & Centroamérica**.

### 6. 📖 Listado de países (País • Capital • Bandera)
- Botón en menú principal con reborde verde, franja superior indicativa `LISTADO` y contenido centrado verticalmente.
- Atlas de consulta con las 254 naciones ordenadas alfabéticamente en español (por ejemplo, Yibuti en la 'Y').
- **Ajuste automático de tipografía (`NombrePaisAutoAjustable` y `CapitalAutoAjustable`):** Mide dinámicamente el ancho horizontal en pantalla y reduce suavemente la fuente tanto del nombre del país como de la capital para que quepan íntegros en una sola línea sin cortarse jamás.
- Buscador en tiempo real por nombre de país, capital o código ISO.
- Distinción regional detallada en el continente americano: **Norteamérica** (Cian), **Centroamérica** (Verde lima tropical `#84CC16`) y **Sudamérica** (Púrpura), distinguiéndose claramente de **Asia** (Rojo intenso `#EF4444`).
- Cajas de estado (**Soberano/Territorio**) y **Continente/Región** de idéntico tamaño uniforme (92x24 dp), ambas con reborde y color dorado distintivo para Soberano que no coincide con ningún continente.
- Filtros rápidos por categoría: *Todos (254)*, *Soberanos (195)*, *Territorios (59)*, *Europa*, *Norteamérica*, *Centroamérica*, *Sudamérica*, *Asia*, *África*, *Oceanía* y *Antártida*.
- Banderas en tamaño amplio y sin marcos/bordes para una visualización limpia de banderas cuadradas o irregulares.

### 7. 🎨 Interfaz responsiva y menú principal
- Menú principal donde los 4 botones principales se expanden ocupando el 100% de la altura de la pantalla, con tipografía ampliada para títulos (23 sp), subtítulos (13.5 sp), descripciones (15 sp) y estadísticas (17.5 sp).
- **Cabecera equilibrada:** Título e icono de MyGeography perfectamente centrados horizontalmente en pantalla con botón interactivo `[ 🌐 Globo 3D ]` fijado en la esquina superior derecha.
- Iconografía clara y degradados visuales para cada funcionalidad.
- 100% Offline: todas las banderas empaquetadas localmente en `assets/flags/`, coordenadas geográficas y polígonos continentales integrados sin dependencia de internet.

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
