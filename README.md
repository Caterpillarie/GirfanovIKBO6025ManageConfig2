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
* Режим вывода на экран обратных зависимостей

### Настраиваемые параметры 
* `--package` — имя анализируемого пакета   
* `--repo` — URL-адрес репозитория Alpine Linux или путь к файлу тестового репозитория
* `--test` — режим работы с тестовым репозиторием
* `--ascii` — режим вывода зависимостей в формате ASCII-дерева
* `--depth` — максимальная глубина анализа зависимостей
* `--reverse` — режим вывода обратных зависимостей

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
```bat
run.bat --package curl --repo "https://dl-cdn.alpinelinux.org/alpine/latest-stable/main/x86_64/" --depth 4
```

Пример выводв:
```text
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
```

2. Анализ графа с циклами с тестовом режиме

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
```

Команда запуска в режиме вывода обратных зависимостей:

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
