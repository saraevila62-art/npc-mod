# Исправление ошибки processResources

В проекте был продублирован файл `META-INF/neoforge.mods.toml`:
- шаблон находится в `src/main/templates/META-INF/neoforge.mods.toml` и обрабатывается задачей `generateModMetadata`;
- второй файл находился в `src/main/resources/META-INF/neoforge.mods.toml`.

Оставлен шаблонный файл, потому что `build.gradle` уже настраивает его обработку и подстановку значений из `gradle.properties`. Дублирующий статический файл удалён.

После замены файлов запусти GitHub Actions заново. Ошибка `Entry META-INF/neoforge.mods.toml is a duplicate` должна исчезнуть. Это исправляет именно текущую ошибку упаковки ресурсов; возможные следующие ошибки компиляции Java или конфигурации NeoForge нужно будет рассматривать отдельно.
