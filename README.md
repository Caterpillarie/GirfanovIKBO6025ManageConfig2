# Визуализатор графа зависимостей пакетов (Вариант №6)

## 1. Общее описание

CLI-инструмент на языке Kotlin для анализа и визуализации графа зависимостей пакетов Alpine Linux (`apk`) 

## 2. Описание функций и настроек

* Разбор опций командной строки
* Валидация корректности переданных значений и типов данных
* Вывод сообщений об ошибке при некорректном вводе
* Работа с реальным репозиторием Alpine Linux
* Построение графа зависимостей (рекурсивный DFS)
* Режим тестового репозитория
* Режим вывода на экран обратных зависимостей (этап 4)
* Вывод в формате ASCII дерева
* Формирование текстового представления графа зависимостей на языке диаграм PlantUML

### Настраиваемые параметры 
* `--package` — имя анализируемого пакета   
* `--repo` — URL-адрес репозитория Alpine Linux или путь к файлу тестового репозитория
* `--test` — режим работы с тестовым репозиторием
* `--ascii` — режим вывода зависимостей в формате ASCII-дерева
* `--depth` — максимальная глубина анализа зависимостей
* `--reverse` — режим вывода обратных зависимостей (этап 4)

## 3. Сборка проекта и запуск тестов

Запуск приложения осуществляется через скрипт `run.bat` (необходим Gradle 8.x и JDK 21)
```bat
run.bat [параметры]
```

Запуск модульных тестов JUnit5
```bat
gradle clean test
```

## 4. Примеры запуска программы

1. Анализ пакета из реального репозитория Alpine Linux

Команда запуска с реальным репозиторием:

```bat
run.bat --package curl --repo "https://dl-cdn.alpinelinux.org/alpine/latest-stable/main/x86_64/" --depth 4
```

Пример вывода:
```text
Dependency graph for a package 'curl':
- curl
  - libcurl
    - ca-certificates-bundle
    - brotli-libs
      - musl
    - musl
    - c-ares
      - musl
    - libcrypto3
      - musl
    - libidn2
      - musl
      - libunistring
        - musl
    - nghttp2-libs
      - musl
    - libpsl
      - musl
      - libidn2
        - musl
        - libunistring
      - libunistring
        - musl
    - libssl3
      - libcrypto3
        - musl
      - musl
    - zlib
      - musl
    - zstd-libs
      - musl
  - musl
  - zlib
    - musl

Graph description on PlantUML:
@startuml
skinparam componentStyle rectangle
top to bottom direction
rectangle "curl" as pkg_curl #D5E8D4
rectangle "libcurl" as pkg_libcurl 
rectangle "ca-certificates-bundle" as pkg_ca_certificates_bundle 
rectangle "brotli-libs" as pkg_brotli_libs 
rectangle "musl" as pkg_musl 
rectangle "c-ares" as pkg_c_ares 
rectangle "libcrypto3" as pkg_libcrypto3 
rectangle "libidn2" as pkg_libidn2 
rectangle "libunistring" as pkg_libunistring 
rectangle "nghttp2-libs" as pkg_nghttp2_libs 
rectangle "libpsl" as pkg_libpsl 
rectangle "libssl3" as pkg_libssl3 
rectangle "zlib" as pkg_zlib 
rectangle "zstd-libs" as pkg_zstd_libs 
pkg_curl --> pkg_libcurl
pkg_libcurl --> pkg_ca_certificates_bundle
pkg_libcurl --> pkg_brotli_libs
pkg_brotli_libs --> pkg_musl
pkg_libcurl --> pkg_musl
pkg_libcurl --> pkg_c_ares
pkg_c_ares --> pkg_musl
pkg_libcurl --> pkg_libcrypto3
pkg_libcrypto3 --> pkg_musl
pkg_libcurl --> pkg_libidn2
pkg_libidn2 --> pkg_musl
pkg_libidn2 --> pkg_libunistring
pkg_libunistring --> pkg_musl
pkg_libcurl --> pkg_nghttp2_libs
pkg_nghttp2_libs --> pkg_musl
pkg_libcurl --> pkg_libpsl
pkg_libpsl --> pkg_musl
pkg_libpsl --> pkg_libidn2
pkg_libpsl --> pkg_libunistring
pkg_libcurl --> pkg_libssl3
pkg_libssl3 --> pkg_libcrypto3
pkg_libssl3 --> pkg_musl
pkg_libcurl --> pkg_zlib
pkg_zlib --> pkg_musl
pkg_libcurl --> pkg_zstd_libs
pkg_zstd_libs --> pkg_musl
pkg_curl --> pkg_musl
pkg_curl --> pkg_zlib
@enduml
```

Команда запуска с реальным репозиторием в режиме ASCII дерева:

```bat
run.bat --package libcurl --repo "https://dl-cdn.alpinelinux.org/alpine/latest-stable/main/x86_64/" --depth 2 --ascii true
```

Пример вывода:

```text
Dependency graph for a package 'libcurl':
libcurl
├── ca-certificates-bundle
├── brotli-libs
│   └── musl
├── musl
├── c-ares
│   └── musl
├── libcrypto3
│   └── musl
├── libidn2
│   ├── musl
│   └── libunistring
├── nghttp2-libs
│   └── musl
├── libpsl
│   ├── musl
│   ├── libidn2
│   └── libunistring
├── libssl3
│   ├── libcrypto3
│   └── musl
├── zlib
│   └── musl
└── zstd-libs
    └── musl

Graph description on PlantUML:
@startuml
skinparam componentStyle rectangle
top to bottom direction
rectangle "libcurl" as pkg_libcurl #D5E8D4
rectangle "ca-certificates-bundle" as pkg_ca_certificates_bundle 
rectangle "brotli-libs" as pkg_brotli_libs 
rectangle "musl" as pkg_musl 
rectangle "c-ares" as pkg_c_ares 
rectangle "libcrypto3" as pkg_libcrypto3 
rectangle "libidn2" as pkg_libidn2 
rectangle "libunistring" as pkg_libunistring 
rectangle "nghttp2-libs" as pkg_nghttp2_libs 
rectangle "libpsl" as pkg_libpsl 
rectangle "libssl3" as pkg_libssl3 
rectangle "zlib" as pkg_zlib 
rectangle "zstd-libs" as pkg_zstd_libs 
pkg_libcurl --> pkg_ca_certificates_bundle
pkg_libcurl --> pkg_brotli_libs
pkg_brotli_libs --> pkg_musl
pkg_libcurl --> pkg_musl
pkg_libcurl --> pkg_c_ares
pkg_c_ares --> pkg_musl
pkg_libcurl --> pkg_libcrypto3
pkg_libcrypto3 --> pkg_musl
pkg_libcurl --> pkg_libidn2
pkg_libidn2 --> pkg_musl
pkg_libidn2 --> pkg_libunistring
pkg_libcurl --> pkg_nghttp2_libs
pkg_nghttp2_libs --> pkg_musl
pkg_libcurl --> pkg_libpsl
pkg_libpsl --> pkg_musl
pkg_libpsl --> pkg_libidn2
pkg_libpsl --> pkg_libunistring
pkg_libcurl --> pkg_libssl3
pkg_libssl3 --> pkg_libcrypto3
pkg_libssl3 --> pkg_musl
pkg_libcurl --> pkg_zlib
pkg_zlib --> pkg_musl
pkg_libcurl --> pkg_zstd_libs
pkg_zstd_libs --> pkg_musl
@enduml
```

Команда с реальным репозиторием без зависимостей в режиме ASCII дерева:

```bat
run.bat --package musl --repo "https://dl-cdn.alpinelinux.org/alpine/latest-stable/main/x86_64/" --depth 4 --ascii true
```

Пример вывода:

```text
Dependency graph for a package 'musl':
musl

Graph description on PlantUML:
@startuml
skinparam componentStyle rectangle
top to bottom direction
rectangle "musl" as pkg_musl #D5E8D4
@enduml
```

2. Анализ графа с циклами в тестовом режиме

Содержимое файла `test_repo.txt`:

```text
A: B E
B: C D
C: A
D: 
E: D
F: F
```

Команда запуска в режиме тестирования:

```bat
run.bat --package A --repo test_repo.txt --test true --depth 4
```

Пример вывода: 

```text
Dependency graph for a package 'A':
- A
  - B
    - C
      - A [cycle]
    - D
  - E
    - D

Graph description on PlantUML:
@startuml
skinparam componentStyle rectangle
top to bottom direction
rectangle "A" as pkg_A #D5E8D4
rectangle "B" as pkg_B 
rectangle "C" as pkg_C 
rectangle "D" as pkg_D 
rectangle "E" as pkg_E 
pkg_A --> pkg_B
pkg_B --> pkg_C
pkg_C ..> pkg_A : cycle
pkg_B --> pkg_D
pkg_A --> pkg_E
pkg_E --> pkg_D
@enduml
```

Команда запуска в режиме вывода обратных зависимостей (этап 4):

```bat
run.bat --package A --repo test_repo.txt --test true --depth 4 --reverse true
```

Пример вывода: 

```text
Dependency graph for a package 'A':
- A
  - C
    - B
      - A [cycle]
```

Команда запуска в режиме ASCII дерева:

```bat
run.bat --package A --repo test_repo.txt --test true --depth 4 --ascii true
```

Пример вывода:

```text
Dependency graph for a package 'A':
A
├── B
│   ├── C
│   │   └── A [cycle]
│   └── D
└── E
    └── D

Graph description on PlantUML:
@startuml
skinparam componentStyle rectangle
top to bottom direction
rectangle "A" as pkg_A #D5E8D4
rectangle "B" as pkg_B 
rectangle "C" as pkg_C 
rectangle "D" as pkg_D 
rectangle "E" as pkg_E 
pkg_A --> pkg_B
pkg_B --> pkg_C
pkg_C ..> pkg_A : cycle
pkg_B --> pkg_D
pkg_A --> pkg_E
pkg_E --> pkg_D
@enduml
```
