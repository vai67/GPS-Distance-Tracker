# GPS Distance Tracker

An Android application built with **Kotlin** that tracks real-time GPS movement and computes distance, speed, and elapsed time using Android’s Location APIs. Designed with a modern **Material UI** and optimized for frequent, accurate location updates.

## Features
- Real-time GPS distance tracking
- Live speed calculation based on location updates
- Elapsed time tracking during active sessions
- Accurate distance computation using latitude and longitude coordinates
- Responsive Material Design UI
- Optimized for frequent location updates with minimal performance overhead

## Tech Stack
- **Language:** Kotlin
- **Platform:** Android
- **APIs:** Android Location Services (LocationManager, LocationListener)
- **UI:** Material Design components
- **Build System:** Gradle (Kotlin DSL)

## How It Works
1. Requests runtime location permissions from the user
2. Continuously listens for GPS location updates
3. Calculates incremental distance between successive coordinates
4. Computes speed and elapsed time in real time
5. Updates the UI dynamically as the user moves

## Getting Started

### Prerequisites
- Android Studio
- Android device or emulator with GPS enabled

### Run Locally
1. Clone the repository:
   ```bash
   git clone https://github.com/vai67/GPS-Distance-Tracker.git
   ```
2. Open the project in Android Studio
3. Build and run the app on a device or emulator
4. Grant location permissions when prompted

## Use Cases
- Fitness and walking distance tracking
- Learning Android location services
- Real-time sensor and system API integration
- Mobile systems and GPS-based applications

## Future Improvements
- Map visualization using Google Maps or Mapbox
- Route history and session saving
- Background tracking support
- Unit switching (miles/km)
- Improved battery optimization

**Vaibhavi Srivastava**  
Linkedin: www.linkedin.com/in/vai-srivastava
Email: vai.sriv12@gmail.com
