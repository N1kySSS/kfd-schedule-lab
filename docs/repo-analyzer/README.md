# Анализатор репозитория

## Коммиты и CI

- [Монолит](https://github.com/N1kySSS/kfd-schedule-lab/commit/c711745985575616b504114eb324872816d350df)
- [Модули](https://github.com/N1kySSS/kfd-schedule-lab/commit/a23bb04eb56fcf11de81f31cc9564b2706644156)
- [Успешный прогон CI](https://github.com/N1kySSS/kfd-schedule-lab/actions/runs/45838746375)

## Запуск

```
./gradlew :cli:run --args="docs/repo-analyzer/sample" --console=plain -q
```

Вывод на примере из четырёх файлов:

```
By extension:
kt: 2 file, 4 line
md: 1 file, 0 line

Longest files:
src/A.kt (2)
src/B.kt (2)
README.md (0)
```

## Эксперимент со сборкой

Правка: в `countByExtension` (`:core`) цикл подсчёта заменён на `sumOf`.

Прогноз до правки:
- `:core:compileKotlin`: UP-TO-DATE
- `:cli:compileKotlin`: UP-TO-DATE

Статусы после правки (`./gradlew build --console=plain`, без clean):

- `:core:compileKotlin`: ничего (то есть задача выполнилась)
- `:cli:compileKotlin`: UP-TO-DATE

Задача :core:compileKotlin выполнилась заново, так как исходный код модуля :core изменился (Gradle обнаружил изменение входных файлов).
При этом :cli:compileKotlin осталась UP-TO-DATE благодаря избеганию компиляции в Gradle.
