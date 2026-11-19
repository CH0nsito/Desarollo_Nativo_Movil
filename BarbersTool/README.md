# Barber's Tool


## Equipaso

- Muro Lopez Sean Josue
- Cervantes Lopez Raul
- Osorio Encinas Leonel Fernando
- Villegas Caballero Daniel Tadeo
- Montes Garcia David Iram

## Funcionalidad

- Ver la lista de citas ya agendadas (barbero, servicio, fecha y hora).
- Agendar una nueva cita eligiendo barbero, servicio, fecha y hora.
- Ver el detalle de una cita específica (cliente, barbero, servicio, duración y precio).

## Ficha técnica

- **IDE:** Android Studio (Koala | 2024.1.x).
- **SDK:** Android SDK 34 (compileSdk / targetSdk), minSdk 24
- **Lenguaje:** Kotlin 1.9.24 (probablemente por la compatibilidad)
- **Gestor de dependencias:** Gradle (Kotlin DSL - `build.gradle.kts`) 8.5.2
- **UI Toolkit:** Jetpack Compose (BOM 2024.06.00) + Navigation Compose

## Dependencias esenciales

| Dependencia | Versión |
|---|---|
| androidx.core:core-ktx | 1.13.1 |
| androidx.lifecycle:lifecycle-runtime-ktx | 2.8.4 |
| androidx.lifecycle:lifecycle-viewmodel-compose | 2.8.4 |
| androidx.activity:activity-compose | 1.9.1 |
| androidx.compose (BOM) | 2024.06.00 |
| androidx.navigation:navigation-compose | 2.7.7 |
| org.jetbrains.kotlinx:kotlinx-coroutines-android | 1.8.1 |
| Android Gradle Plugin | 8.5.2 |
| Kotlin Gradle Plugin | 1.9.24 |
| junit (testing) | 4.13.2 |

## Estructura del proyecto

```
app/src/main/java/com/chon/agendabarberia/
├── data/            # Fuentes de datos
│   └── repository/  # BarberiaRepository: barberos, servicios y citas
├── domain/          # Lógica de negocio
│   ├── model/       # Barbero, Servicio, Cita
│   └── usecase/     # GetCitasUseCase, AgendarCitaUseCase
├── ui/              # Interfaz de usuario
│   └── screens/     # HomeScreen, NuevaCitaScreen, DetailScreen
├── di/              # Inyección de dependencias (a futuro)
└── utils/           # Utilidades y extensiones
```

