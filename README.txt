SHIFT CHALLENGE - Fabric 1.21.11 (client-side)

Syarat: JDK 21, Gradle 9.2+ (atau buka folder ini di IntelliJ, biarkan dia sync)
Project ini pakai Mojang mappings (sama seperti dokumentasi Fabric untuk 1.21.11).

Build:
  gradle wrapper --gradle-version 9.2.1     (sekali saja, untuk bikin ./gradlew)
  ./gradlew build
  Jar: build/libs/shiftchallenge-1.0.0.jar -> taruh di folder mods
  (butuh Fabric Loader 0.18+ dan Fabric API untuk 1.21.11)

Hotkey (bisa diganti di Options > Controls > Key Binds > Shift Challenge):
  K = buka menu pengaturan (target, waktu, preset)
  J = mulai / stop challenge pakai setting terakhir

Setting & rekor tersimpan di config/shiftchallenge.properties
