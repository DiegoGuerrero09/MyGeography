# 🌍 MyGeography

**MyGeography** es una aplicación moderna para Android desarrollada con **Kotlin** y **Jetpack Compose** orientada a aprender, practicar y poner a prueba conocimientos de geografía mundial: banderas, capitales y atlas de naciones.

---

## ✨ Características Principales

### 1. 🏳️ Test de Banderas (254 Naciones y Territorios)
- Cubre las **254 banderas del mundo** correspondientes a la recopilación de [banderas-mundo.es/examen](https://www.banderas-mundo.es/examen).
- Para cada pregunta se muestra en la parte superior el **nombre del país** y abajo una **cuadrícula 3x3 con 9 opciones de banderas**.
- Feedback visual interactivo en tiempo real:
  - Se ilumina en **verde** al seleccionar la opción correcta.
  - Se ilumina en **rojo** si se elige una opción errónea, iluminando simultáneamente la opción correcta en **verde** para facilitar el aprendizaje.
- Permite **volver al menú inicial en cualquier momento** mediante el botón de retroceso con confirmación.
- Marcador en vivo de preguntas respondidas, aciertos y fallos con barra de progreso.

### 2. 🏛️ Test de Capitales (195 Países Soberanos)
- Modo específico enfocado en los **195 estados soberanos** reconocidos internacionalmente (193 miembros de la ONU + Ciudad del Vaticano + Palestina).
- Presenta el **país junto a su bandera** en alta definición y una **cuadrícula 3x3 con 9 opciones de capitales**.
- Feedback visual idéntico (verde / rojo) y capacidad de volver al menú en todo momento.

### 3. 📊 Resumen de Resultados
- Al completar cualquiera de los dos tests, se despliega una pantalla detallada con:
  - Puntuación total (aciertos vs. fallos) y porcentaje de acierto.
  - Pestañas de filtrado: **Todas**, **Acertadas** y **Falladas**.
  - Detalle individual de cada pregunta con la bandera, país, tu respuesta elegida y la respuesta correcta.
  - Botones de acción directa: **Volver al Menú Principal** o **Repetir Test**.

### 4. 📖 Listado de Países (País • Capital • Bandera)
- Atlas de consulta accesible directamente desde el menú inicial con las 254 naciones.
- Buscador en tiempo real por nombre de país, capital o código.
- Filtros rápidos por categoría: *Todos (254)*, *Soberanos (195)*, *Europa*, *América*, *Asia*, *África* y *Oceanía*.
- Etiquetas visuales para distinguir entre países soberanos y territorios dependientes.

### 5. ⚡ 100% Offline y Carga Instantánea
- Las 254 banderas se encuentran empaquetadas en alta resolución dentro de los recursos locales (`assets/flags/`).
- No requiere conexión a internet para jugar ni consultar el listado.
- Transiciones fluidas a 60 fps sin parpadeos ni demoras de red.

---

## 🛠️ Arquitectura y Tecnologías

- **Lenguaje:** Kotlin 1.9.23
- **UI:** Jetpack Compose con Material Design 3 (Material 3)
- **Navegación:** Jetpack Navigation Compose
- **Arquitectura:** MVVM (Model-View-ViewModel) + StateFlow + Repository Pattern
- **Splash Screen:** Pantalla de inicio animada con transición suave hacia el menú principal
- **Icono:** Icono adaptativo personalizado con vectores del globo terráqueo y bandera
- **Paleta Visual:** Tema oscuro moderno (*Dark Geography*) con acentos en azul cielo, cian, ámbar y esmeralda

---

## 📁 Estructura del Proyecto

```text
MyGeography/
├── app/
│   ├── src/main/
│   │   ├── assets/
│   │   │   └── flags/              # 254 banderas en formato PNG optimizado
│   │   ├── java/com/diegoguerrero/mygeography/
│   │   │   ├── MainActivity.kt     # Punto de entrada y grafo de navegación
│   │   │   ├── data/
│   │   │   │   ├── datasource/     # Base de datos local (PaisesData.kt con 254 naciones)
│   │   │   │   ├── model/          # Pais, Continente, QuizPregunta, RespuestaQuiz, TipoQuiz
│   │   │   │   └── repository/     # PaisesRepository con generador de preguntas aleatorias
│   │   │   ├── ui/
│   │   │   │   ├── components/     # BanderaImage con caché en memoria y fallback
│   │   │   │   ├── screens/
│   │   │   │   │   ├── splash/     # SplashScreen animada
│   │   │   │   │   ├── menu/       # MenuScreen con botones principales
│   │   │   │   │   ├── quiz/       # QuizScreen & QuizViewModel (Grid 3x3 interactivo)
│   │   │   │   │   ├── resumen/    # ResultadosScreen (acertadas vs falladas)
│   │   │   │   │   └── listado/    # ListadoPaisesScreen con buscador y filtros
│   │   │   │   └── theme/          # Paleta Color.kt y MyGeographyTheme
│   │   └── res/
│   │       ├── drawable/           # Iconos vectoriales y recursos gráficos
│   │       └── mipmap-*/           # Iconos adaptativos de la aplicación
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
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
El archivo APK resultante se generará en `app/build/outputs/apk/debug/app-debug.apk`.
