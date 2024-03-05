# **VoiceHandler**

<div align="center">
  <img src="https://github.com/TerreDHermes/TerreDHermes/blob/main/assets/voicehandler.png" alt="Описание изображения" style="width: 70%;">
</div>

Поддержка полной работоспособности скомпилированного приложения (apk) отсутствует, так как нужно произвести заного настройку на сайте Firebase. 


## Приложение
Было создано приложение VoiceHandler, которое написано под Android. Его цель - улучшение речи человека (пользователя). Пользователь входит в приложение, записывает голос (на выступлении, при разговоре с другом и т.д.), приложение анализирует речь и высылает сводку о качестве речи (процентное содержание слов-паразитов, матерных слов и самых популярных слов конкретного пользователя, эмоциональная подача).

Есть возможность скачать и протестировать приложение. Файл apk. лежит в Releases - [ссылка](https://github.com/TerreDHermes/VoiceHandler/releases/tag/VoiceHandler.apk). Но расшифровку по аудиозаписи получить не получится, так как для этого нужно, чтобы сервер был запущен (можно запустить его самостоятельно). Без сервера вы сможете: зарегестрироваться, войти в аккаунт, сделать заметки, записать аудио, сохранить аудио в Firebase, скачать аудио и прослушать.  
____
## Паттерн
При реализации Android приложения использовался паттерн MVVM (Model-View-ViewModel). MVVM — это паттерн проектирования, который используется в разработке программного обеспечения, включая мобильные приложения, для эффективного управления пользовательским интерфейсом и логикой приложения. MVVM разделяет компоненты приложения на три основных части: Model, View и ViewModel.
____

## Firebase
В качестве базы данных используется Firebase. Firebase — это платформа для разработки мобильных и веб-приложений, предоставляемая Google. Firebase включает в себя широкий спектр инструментов и сервисов, которые помогают разработчикам ускорить и упростить процесс создания приложений, а также улучшить их функциональность и безопасность. Вот некоторые из основных возможностей Firebase:

1.	Firebase Realtime Database. Этот сервис предоставляет базу данных в реальном времени для хранения и синхронизации данных между клиентской и серверной частями приложения. Это идеально подходит для приложений, где важна мгновенная обновляемость данных.
2.	Firebase Authentication. Сервис для аутентификации пользователей, который позволяет разработчикам управлять аутентификацией и авторизацией в приложении, включая поддержку различных методов входа, таких как электронная почта, социальные сети и другие.
3.	Firebase Cloud Functions. С помощью этого сервиса можно создавать облачные функции, которые выполняются на сервере и реагируют на различные события в приложении. Это позволяет добавлять логику на стороне сервера без необходимости управления собственными серверами.
4.	Firebase Cloud Storage. Сервис для хранения и управления медиафайлами, такими как изображения, видео и другие файлы, предоставляя простой доступ к облачному хранилищу.

___
 ##	Логика приложения и функционал
 После выбора паттерна и базы данных была прописана логика приложения и возможности пользователя:
1)	Пользователь не имеет доступа к функционалу, пока не пройдёт двухфакторную аутентификацию (после регистрации);
2)	После входа открываются следующие возможности:
	* Сделать заметку, которая будет храниться в Realtime Database.
    * Записать аудиозапись и сохранить в Storage.
    * Прослушать записанную аудиозапись.
    * Скачать и просмотреть расшифровку (анализ) записанной аудиозаписи (в PDF формате).
    * Выйти из аккаунта. 
3)	Каждый пользователь имеет доступ только к своим аудиозаписям и заметкам.

___
## Сервер
Вся обработка аудиозаписи происходит на сервере, который написан на Python. Он состоит из двух модулей - [Server_Voice_Handler-Firebase](https://github.com/TerreDHermes/Server_Voice_Handler-Firebase-) и [Big_Server_Voice_Handler](https://github.com/TerreDHermes/Big_Server_Voice_Handler). Суть в том, что он слушает базу данных Firebase Storage, и как только появляется запись, он её начинает преобразовывать.
Алгоритм работы сервера:
1.	Скачивание новой аудиозаписи в формате 3.gp;
2.	Перевод в формат wav;
3.	Распознавание речи;
4.	Выявление слов паразитов, матерных слов и составление списка популярных слов;
5.	Формирование “ALL_TEXT.pdf” и “name.pdf”;
6.	Отправка двух pdf в Firebase.
Сервер запоминает файлы, которые обрабатывал, поэтому по второму кругу обработки происходить не будет.

На самом деле, очень важной частью сервера является качество распознавания речи. Качественные распознаватели речи стоят денег, поэтому была выбрана бесплатная библиотека. Если реализовать более качественный распознаватель слов в речи, то получится более качественный продукт.

___
## Фишинговое приложение VoiceHandler
Было создано [фишинговое приложение VoiceHandler](https://github.com/TerreDHermes/Phishing_VoiceHandler).
Алгоритм входа злоумышленника в аккаунт жертвы:
1. На первом этапе злоумышленник отправляет фишинговое письмо, с ссылкой на автоматическое скачивание поддельного APK. При попытке пользователя войти, вместо отправки данных на сервер Firebase, осуществляется отправка на почту злоумышленника.
2. Далее злоумышленник вводит полученные данные в реальное приложение, которое посылает код для входа на почту Пользователя. Ничего не подозревающий пользователь заходит на свою почту и вводит полученный код в поддельное приложение, которое выглядит полностью идентично оригинальному. Но вместо входа, данные опять посылаются атакующему, и он получает доступ к аккаунту пользователя в легальном приложении.

Ссылка на видео, где просиходит вход злоумышленника в аккаунт жертвы - [ссылка](https://www.youtube.com/watch?v=i3P0gMLD_OA)
