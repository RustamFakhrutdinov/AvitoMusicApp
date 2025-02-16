# 📱 MyAndroidApp

AvitoMusicApp — это Android-приложение, которое позволяет прослушивать и искать музыку, как загруженную на телефон, так и в интернете.

## 🛠️ Основные функции

- Прослушивание треков
- Поиск треков через Deezer Api
- Поиск треков на телефоне 
- Переключение треков
- Перемотка трека
- Поддерживается темная тема

## 📸 Скриншоты  
![image](https://github.com/user-attachments/assets/bb47ae0e-667f-4e53-b085-05691698112c)

![image](https://github.com/user-attachments/assets/7f3e4f19-3139-4eeb-b17a-050b7e9d53f2)

## 🧩 Используемые технологии  

- **Kotlin** — Основной язык разработки   
- **Coroutines** — Асинхронное программирование
- **Koin** — Dependency injection
- **Retrofit** — Работа с сетью
- **Activity, Fragment, Jetpack Navigation** — Навигация(используется принцип Single Activity)
- **MVVM** — Архитектура

## 🛡️ Разрешения  

Приложение запрашивает следующие разрешения:  

- **Интернет** — для поиска треков  
- **Аудио** — для прослушивания загруженных треков

## 🗺️ Архитектура проекта  

Проект следует принципам **MVVM** (Model-View-ViewModel) и разделен на следующие модули:
- Data - работа с данными(Api, работа с хранилищем данных устройства)
- Domain - описывает модели данных, связывает слои ui и data
- Ui - работа с ui, viewmodel 

